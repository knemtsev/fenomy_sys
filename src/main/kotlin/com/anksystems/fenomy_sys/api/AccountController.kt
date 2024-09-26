package com.anksystems.fenomy_sys.api

import com.anksystems.fenomy_sys.api.exceptions.InvalidRequestParameterException
import com.anksystems.fenomy_sys.api.exceptions.InvalidRequestParametersAtLeastException
import com.anksystems.fenomy_sys.api.request.SetFNMIdRequest
import com.anksystems.fenomy_sys.api.request.TransactionRequest
import com.anksystems.fenomy_sys.api.response.ResultResponse
import org.springframework.web.bind.annotation.*


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
        var res: String? = ""

        if (fenomyId.startsWith("fy"))
            res = pgService.getBalanceByFyId(fenomyId)
        else
            res = pgService.getBalanceByUserId(fenomyId)

        log.d("/balance/fnm/${fenomyId} $res")

        return res
    }

    @PostMapping(path = ["/fnm_id"])
    fun setFNMId(
        @RequestBody req: SetFNMIdRequest,
        @RequestHeader("sys-key") sysKey: String? = null
    ): ResultResponse {
        checkAuthorization(sysKey)

        log.d("fyid=${req.fyid} fnm_id=${req.fnmId}")

        if (req.fyid != null)
            pgService.setFNMIdByFyId(req.fyid, req.fnmId)
        else if (req.user_id != null)
            pgService.setFNMIdByUserId(req.user_id, req.fnmId)
        else
            throw InvalidRequestParametersAtLeastException("fyid, user_id")

        return RESULT_OK
    }
}