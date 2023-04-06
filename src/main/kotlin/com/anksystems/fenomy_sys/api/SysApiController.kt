package com.anksystems.fenomy_sys.api

import com.anksystems.fenomy_sys.api.request.GroupAddRequest
import com.anksystems.fenomy_sys.api.response.GroupAddResponse
import com.anksystems.fenomy_sys.service.PGService
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.InvalidPropertiesFormatException


@RestController
class SysApiController(
    @Autowired val pgService: PGService
) {
    @RequestMapping(value = ["/sys/v1/blockchain/prefs"], produces = ["application/json"])
    @ResponseBody
    fun blockchainPrefs(@RequestParam(name = "token") token: String): String {
        return try {
            pgService.getBlockchainPrefs(token)
        } catch (e: Exception) {
            "{"+"\"error\":"+"\""+e.message+"\"}"
        }
    }

    @RequestMapping(value = ["/sys/v1/groups/new"], produces = ["application/json"])
    @ResponseBody
    fun groupsAdd(@RequestBody request: GroupAddRequest): GroupAddResponse? {
        return try {
            GroupAddResponse(pgService.newGroup(request))
        } catch (e: Exception) {
            null
        }
    }

}