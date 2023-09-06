package com.anksystems.fenomy_sys.api

import com.anksystems.fenomy_sys.api.exceptions.InvalidRequestParametersAtLeastException
import com.anksystems.fenomy_sys.api.exceptions.NoAppKeyException
import com.anksystems.fenomy_sys.api.exceptions.NoTokenException
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
    fun getBlockchainPrefs(@RequestParam(name = "token") token: String?,
                           @RequestParam(name = "user_id") userId: String?,
                           @RequestHeader("sys-key") sysKey: String?=null): String {

        checkAuthorization(sysKey)
        if(token==null && userId==null)
            throw InvalidRequestParametersAtLeastException("token, user_id")
        return try {
            val res = pgService.getBlockchainPrefs(token, userId)
            log.d("blockchain $res $token")
            if(res.isEmpty())
                throw NoTokenException(token!!)
             res
        } catch (e: NoTokenException) {
            throw e
        } catch (e: Exception) {            "{"+"\"error\":"+"\""+e.message+"\"}"
        }
    }

}