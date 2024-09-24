package com.anksystems.fenomy_sys.api

import com.anksystems.fenomy_sys.api.exceptions.NoAppKeyException
import com.anksystems.fenomy_sys.api.exceptions.NoTokenException
import com.anksystems.fenomy_sys.api.request.TransactionRequest
import com.anksystems.fenomy_sys.api.response.BalanceResponse
import com.anksystems.fenomy_sys.service.LogService
import com.anksystems.fenomy_sys.service.PGService
import kotlinx.serialization.json.JsonObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException


@RestController
@RequestMapping(path = ["/sys/v1/account"], produces = ["application/json"])
@ResponseBody
class AccountController(
) : BaseController() {

    @PostMapping(path = ["/transaction"])
    fun transaction(
        @RequestBody request: TransactionRequest,
        @RequestHeader("sys-key") sysKey: String? = null
    ): String {

        checkAuthorization(sysKey)

        val res = pgService.transaction(request.debit, request.credit, request.amount, request.currency)

        log.d("/transaction $request $res")

        return res
    }

    @GetMapping(path = ["/balance/fnm/{fenomyId}"])
    fun balance(
        @PathVariable fenomyId: String,
        @RequestHeader("sys-key") sysKey: String? = null
    ): String {
        checkAuthorization(sysKey)

        val res = pgService.getBalance(fenomyId)

        log.d("/balance/fnm/${fenomyId} $res")

        return res
    }
}