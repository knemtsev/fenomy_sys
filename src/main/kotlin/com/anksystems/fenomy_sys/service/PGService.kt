package com.anksystems.fenomy_sys.service

import com.anksystems.fenomy_pushk.db.dao.PushTable
import com.anksystems.fenomy_sys.FenomySysApplication
import com.anksystems.fenomy_sys.MyProperties
import com.anksystems.fenomy_sys.api.exceptions.PostgresErrorException
import com.anksystems.fenomy_sys.api.model.PushData
import com.anksystems.fenomy_sys.api.model.UserIdTypes
import com.anksystems.fenomy_sys.api.request.FenomyUserIdRequest
import com.anksystems.fenomy_sys.api.request.GroupAddRequest
import com.anksystems.fenomy_sys.api.request.GroupMemberRequest
import com.anksystems.fenomy_sys.extensions.toJson
import com.fasterxml.jackson.databind.JsonMappingException
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.SQLException
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.UUID
import java.util.stream.IntStream
import javax.lang.model.type.NullType


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

    fun execQuery(query: String): String {
        var result = ""
        transaction(Database.connect(ds)) {
            val statement = connection.prepareStatement(query, false)

            val res = statement.executeQuery()

            if (res.next()) {
                result = res.getString(1)
            }

        }
        checkPostgresError(result)
        return result
    }

    fun screenApostrophe(string: String): String =
        string.replace("'", "''")

    fun execQueryToJsonArray(query: String): String {
        var result = ""
        try {
            Database.connect(ds)
            transaction {
                val statement = connection.prepareStatement(query, false)

                val res = statement.executeQuery()

                result = resultSetToJsonArray(res).toString()

            }
        } catch (e: Exception) {
            result = e.toJson(log)
        }
        return result
    }

    fun execQueryToJsonObject(query: String): String {
        var result = ""
        transaction(Database.connect(ds)) {
            val statement = connection.prepareStatement(query, false)

            val res = statement.executeQuery()

            result = resultSetRowToJsonObject(res).toString()

        }
        return result
    }


    private val clientCache: HashMap<String, String> = HashMap(10000)
    fun getClientId(fenomyUserIdRequest: FenomyUserIdRequest): String {
        log.i("clientCache: " + clientCache.toList().joinToString { it.first + "->" + it.second })
        val userId = fenomyUserIdRequest.getUserIdByRequest()
        return clientCache[userId.id] ?: when (userId.type) {
            UserIdTypes.FENOMY_ID -> execQuery("select id::text from db.client where code='${userId.id}'")
            UserIdTypes.USER_ID -> execQuery("select id::text from db.client where userid='${userId.id}'")
            UserIdTypes.CLIENT_ID -> userId.id
        }.also { if (!it.isNullOrEmpty()) clientCache[userId.id] = it }
    }

    private val groupCache: HashMap<String, String> = HashMap(100)

    fun getGroupId(groupFenomyId: String): String {
        log.i("groupCache: " + groupCache.toList().joinToString { it.first + "->" + it.second })
        return groupCache[groupFenomyId]
            ?: execQuery("select id::text from db.user where username='$groupFenomyId' and type='G';")
                .also { if (it.isNotEmpty()) groupCache[groupFenomyId] = it }
    }

    fun sendPushToGroup(groupFenomyId: String, pushData: PushData, subject: String, content: String) {
        Database.connect(ds)
        transaction {
            val statement = connection.prepareStatement(
                "select d.address as address from db.member_group mg\n" +
                        "    inner join db.\"user\" u on mg.member = u.id\n" +
                        "    inner join db.client c on u.id = c.userid\n" +
                        "    inner join db.device d on c.id = d.client\n" +
                        "where\n" +
                        "      mg.userid=getgroup('$groupFenomyId')\n" +
                        "    and d.address is not null\n" +
                        ";", false
            )

            val addressSet = statement.executeQuery()

            val addressList: MutableList<String> = mutableListOf()
            while (addressSet.next()) {
                addressList.add(addressSet.getString("address"))
            }

            PushTable.batchInsert(addressList) { address ->
                this[PushTable.address] = address
                this[PushTable.subject] = subject
                this[PushTable.content] = content
                this[PushTable.data] = Json.encodeToString(pushData)
                this[PushTable.id] = UUID.randomUUID()
                this[PushTable.status] = "new"
                this[PushTable.cdate] = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("UTC")).toLocalDateTime()
                this[PushTable.udate] = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("UTC")).toLocalDateTime()
                this[PushTable.priority] = "normal"
                this[PushTable.collapseKey] = null

            }
        }
    }

    fun sendPushToGroupByTopic(groupFenomyId: String, pushData: PushData, subject: String, content: String) {
        Database.connect(ds)
        transaction {
            PushTable.insert {
                it[address] = groupFenomyId
                it[PushTable.subject] = subject
                it[PushTable.content] = content
                it[priority] = "high"
                it[collapseKey] = "groups"
                it[data] = Json.encodeToString(pushData)
            }
        }
    }


    fun resultSetToJsonArray(resultSet: ResultSet): JsonArray {

        val result: MutableList<JsonElement> = mutableListOf()

        while (resultSet.next()) {
            result.add(rowToJsonObject(resultSet))
        }
        return JsonArray(result)
    }

    fun resultSetRowToJsonObject(resultSet: ResultSet, rowNumber: Int = 1): JsonObject {
        return if (resultSet.absolute(rowNumber)) {
            rowToJsonObject(resultSet)
        } else
            Json.decodeFromString("{}")
    }

    fun getColumnNames(resultSet: ResultSet): List<String> {
        val md: ResultSetMetaData = resultSet.metaData
        val numCols: Int = md.columnCount
        return IntStream.range(0, numCols)
            .mapToObj { i ->
                try {
                    return@mapToObj md.getColumnLabel(i + 1)
                } catch (e: SQLException) {
                    e.printStackTrace()
                    return@mapToObj "?"
                }
            }
            .toList()
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun rowToJsonObject(resultSet: ResultSet): JsonObject {
        return JsonObject(JsonObject(mapOf()).toMutableMap().apply {
            getColumnNames(resultSet).forEach { cn ->
                try {
                    val obj = resultSet.getObject(cn)
                    //log.d("obj = $obj ${try { obj.javaClass } catch(e: Exception){ "null"} }")
                    if (obj != null)
                        when (obj) {
                            is String -> {
                                val s = resultSet.getString(cn)
                                if (s.startsWith("{") && s.endsWith("}")) {
                                    put(cn, JsonObject(Json.decodeFromString(s)))
                                } else
                                    put(cn, JsonPrimitive(s))
                            }
                            is Timestamp -> put(
                                cn,
                                JsonPrimitive(resultSet.getTimestamp(cn).toLocalDateTime().toString())
                            )
                            is BigDecimal -> put(cn, JsonPrimitive(resultSet.getBigDecimal(cn) as Number))
                            is Boolean -> put(cn, JsonPrimitive(resultSet.getBoolean(cn)))
                            is Int -> put(cn, JsonPrimitive(resultSet.getLong(cn) as Number))
                            else -> put(cn, JsonPrimitive(resultSet.getString(cn)))
                        }
                    else
                        put(cn, JsonPrimitive(null))

                } catch (e: JsonMappingException) {
                    e.printStackTrace()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
            }
        })
    }


    fun getBlockchainPrefs(token: String): String {
        log.d("get prefs $token")
        val res =execQuery(
            "SELECT row_to_json(sel) FROM (\n" +
                    "         SELECT (substring(c.code,2,4) || '***' || substring(c.code,21,6)) as fyid, c.userid as user_id, (ext_flags & B'00000100' = B'00000100') as use_blockchain, load_blockchain, reputation FROM db.participant_ext pe\n" +
                    "            inner join db.client c on c.id=pe.id\n" +
                    "            inner join db.session s on s.userid=c.userid\n" +
                    "            inner join db.token_header th on th.session=s.code\n" +
                    "            inner join db.token t on th.id = t.header\n" +
                    "         WHERE  t.token = '${screenApostrophe(token)}'\n" +
                    "         ) sel;"
        )
        log.d("get prefs res = $res")
        return res
    }

    fun newGroup(groupAddRequest: GroupAddRequest): String {
        return execQuery("select api.groups_new_group('${screenApostrophe(groupAddRequest.name)}', ' ');")
    }

    fun addGroupMember(request: GroupMemberRequest): String {
        return execQuery(
            "select api.groups_add_member('${screenApostrophe(request.groupFenomyId)}', '${
                screenApostrophe(request.userFenomyId)}');"
        )
    }

    fun delGroupMember(request: GroupMemberRequest): String {
        return execQuery(
            "select api.groups_del_member('${screenApostrophe(request.groupFenomyId)}', '${
                screenApostrophe(
                    request.userFenomyId
                )
            }');"
        )
    }

    protected fun checkPostgresError(result: String) {
        if (result.startsWith("ERR-"))
            throw PostgresErrorException(result)
    }

}