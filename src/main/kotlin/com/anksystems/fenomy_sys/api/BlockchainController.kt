package com.anksystems.fenomy_sys.api

import com.anksystems.fenomy_sys.api.exceptions.NoAppKeyException
import com.anksystems.fenomy_sys.service.LogService
import com.anksystems.fenomy_sys.service.PGService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException


@RestController
@RequestMapping(path = ["/sys/v1/blockchain"], produces = ["application/json"])
@ResponseBody
class BlockchainController (
): BaseController() {

    @GetMapping(path=["/prefs"])
    fun getBlockchainPrefs(@RequestParam(name = "token") token: String,
                           @RequestHeader("sys-key") sysKey: String?=null): String {

        checkAuthorization(sysKey)
        return try {
            pgService.getBlockchainPrefs(token)
        } catch (e: Exception) {
            "{"+"\"error\":"+"\""+e.message+"\"}"
        }
    }

}