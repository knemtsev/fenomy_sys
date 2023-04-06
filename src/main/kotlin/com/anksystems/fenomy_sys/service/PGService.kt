package com.anksystems.fenomy_sys.service

import com.anksystems.fenomy_sys.FenomySysApplication
import com.anksystems.fenomy_sys.MyProperties
import com.anksystems.fenomy_sys.api.request.GroupAddRequest
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
class PGService(
    @Autowired private val props: MyProperties,
    @Autowired private val env: Environment,
    @Autowired private val log: LogService,
    @Autowired private val app: FenomySysApplication
) {
    private val config by lazy {
        HikariConfig().apply {
            jdbcUrl = env.getProperty("spring.datasource.url") //props.dbUrl
            username = env.getProperty("spring.datasource.username")
            password = env.getProperty("spring.datasource.password")
            driverClassName = env.getProperty("spring.datasource.driver-class-name")
            //keepaliveTime = 60000

        }
    }

    private val ds by lazy {
        HikariDataSource(config)
    }

    private val serviceScope = CoroutineScope(Dispatchers.IO)


    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        coerceInputValues = true
    }

    init {
        initService()
    }

    private fun initService() {

    }

    fun getBlockchainPrefs(token: String): String {
        var result = "{}"
        Database.connect(ds)
        transaction {
            val query = "SELECT row_to_json(sel) FROM (\n" +
                    "         SELECT (substring(c.code,2,4) || '***' || substring(c.code,21,6)) as fyid, c.userid as user_id, (ext_flags & B'00000100' = B'00000100') as use_blockchain, load_blockchain, reputation FROM db.participant_ext pe\n" +
                    "            inner join db.client c on c.id=pe.id\n" +
                    "            inner join db.session s on s.userid=c.userid\n" +
                    "            inner join db.token_header th on th.session=s.code\n" +
                    "            inner join db.token t on th.id = t.header\n" +
                    "         WHERE  t.token = '$token'\n" +
                    "         ) sel;"
            val statement = connection.prepareStatement(query, false)

            val res = statement.executeQuery()

            if (res.next()) {
                result = res.getString(1)
            }

        }
        return result
    }

    fun newGroup(groupAddRequest: GroupAddRequest): String {
        var result = "?"
        Database.connect(ds)
        transaction {
            val query = "select api.groups_new_group('${groupAddRequest.name}', '${groupAddRequest.description}');"
            val statement = connection.prepareStatement(query, false)

            val res = statement.executeQuery()

            if (res.next()) {
                result = res.getString(1)
            }

        }
        return result
    }
}