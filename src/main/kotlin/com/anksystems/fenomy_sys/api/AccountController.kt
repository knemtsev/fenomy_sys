package com.anksystems.fenomy_sys.api

import com.anksystems.fenomy_sys.api.exceptions.NoAppKeyException
import com.anksystems.fenomy_sys.api.exceptions.NoTokenException
import com.anksystems.fenomy_sys.api.request.TransactionRequest
import com.anksystems.fenomy_sys.service.LogService
import com.anksystems.fenomy_sys.service.PGService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException


@RestController
@RequestMapping(path = ["/sys/v1/account"], produces = ["application/json"])
@ResponseBody
class AccountController (
): BaseController() {

    @PostMapping(path=["/transaction"])
    fun transaction(@RequestBody request: TransactionRequest,
                    @RequestHeader("sys-key") sysKey: String?=null): String {

        checkAuthorization(sysKey)
        return try {
            val res = pgService.transaction()
            log.d("blockchain $res $token")
            if(res.isEmpty())
                throw NoTokenException(token)
             res
        } catch (e: NoTokenException) {
            throw e
        } catch (e: Exception) {
            "{"+"\"error\":"+"\""+e.message+"\"}"
        }
    }

}