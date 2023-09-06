package com.anksystems.fenomy_sys.api

import com.anksystems.fenomy_sys.api.exceptions.InvalidRequestParametersAtLeastException
import com.anksystems.fenomy_sys.api.exceptions.NoAppKeyException
import com.anksystems.fenomy_sys.api.exceptions.NoTokenException
import com.anksystems.fenomy_sys.api.request.BlockchainPrefsRequest
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
            val res = pgService.getBlockchainPrefs(token, null)
            log.d("blockchain $res $token")
            if(res.isEmpty())
                throw NoTokenException(token!!)
             res
        } catch (e: NoTokenException) {
            throw e
        } catch (e: Exception) {            "{"+"\"error\":"+"\""+e.message+"\"}"
        }
    }

    @PostMapping(path=["/prefs"])
    fun getBlockchainPrefsByTokenOrUserId(@RequestBody blockchainPrefsRequest: BlockchainPrefsRequest,
                                          @RequestHeader("sys-key") sysKey: String?=null): String {

        checkAuthorization(sysKey)
        if(blockchainPrefsRequest.token==null && blockchainPrefsRequest.userId==null)
            throw InvalidRequestParametersAtLeastException("token, user_id")

        return try {
            val res = pgService.getBlockchainPrefs(blockchainPrefsRequest.token, blockchainPrefsRequest.userId)
            log.d("blockchain $res token=${blockchainPrefsRequest.token} user_id=${blockchainPrefsRequest.userId}")
            if(res.isEmpty())
                throw NoTokenException(blockchainPrefsRequest.token ?: blockchainPrefsRequest.userId ?: "")
            res
        } catch (e: NoTokenException) {
            throw e
        }
    }


}