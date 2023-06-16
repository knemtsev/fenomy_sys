package com.anksystems.fenomy_sys.api

import com.anksystems.fenomy_sys.api.exceptions.InvalidRequestParameterException
import com.anksystems.fenomy_sys.api.exceptions.InvalidRequestParametersAtLeastException
import com.anksystems.fenomy_sys.api.exceptions.NoRowAffected
import com.anksystems.fenomy_sys.api.request.SetNftAlgoRequest
import com.anksystems.fenomy_sys.api.response.ResultResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/sys/v1/nft"], produces = ["application/json"])
@ResponseBody
class NftController: BaseController() {

    @PostMapping(path = ["/algo/set"])
    fun setNftAlgo(
        @RequestBody setNftAlgoRequest: SetNftAlgoRequest,
        @RequestHeader("sys-key") sysKey: String? = null
    ): ResultResponse {
        checkAuthorization(sysKey)

        if(setNftAlgoRequest.id.isNullOrEmpty() || setNftAlgoRequest.payoutDate.isNullOrEmpty())
            throw InvalidRequestParametersAtLeastException("Required fields: id, payout_date")

        val res = pgService.execUpdate("update db.nft_algorithm\n" +
                "    set payout_date = '${setNftAlgoRequest.payoutDate}'\n" +
                "where id='${setNftAlgoRequest.id}';")

        log.d("setNftAlgo res = $res")

        if(res==0) throw NoRowAffected()

        return RESULT_OK
    }
}