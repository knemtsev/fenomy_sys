package com.anksystems.fenomy_sys.api

import com.anksystems.fenomy_sys.api.exceptions.InvalidRequestParametersAtLeastException
import com.anksystems.fenomy_sys.api.exceptions.NoTokenException
import com.anksystems.fenomy_sys.api.model.PushData
import com.anksystems.fenomy_sys.api.request.BlockMembersRequest
import com.anksystems.fenomy_sys.api.request.BlockchainPrefsRequest
import com.anksystems.fenomy_sys.api.request.BlocksAuthorRequest
import com.anksystems.fenomy_sys.api.request.TransactionResultRequest
import com.anksystems.fenomy_sys.api.response.SuccessResponse
import org.springframework.web.bind.annotation.*


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

    @PostMapping(path = ["/transaction/result"])
    fun transactionResult(@RequestBody transactionResult: TransactionResultRequest,
                          @RequestHeader("sys-key") sysKey: String?=null): String
    {
        checkAuthorization(sysKey)
        return if(transactionResult.success) {
            pgService.doDisable(transactionResult.transactionId)
        } else {
            "{\"transaction\":\"off\"}"
        }
    }

    @PostMapping(path = ["/blocks/confirm/author"])
    fun blocksConfirmAuthor(@RequestBody blocksAuthor: BlocksAuthorRequest,
        @RequestHeader("sys-key") sysKey: String?=null
    ): SuccessResponse {
        checkAuthorization(sysKey)

        log.d("bc confirmation input = $blocksAuthor")

        blocksAuthor.blocks.forEach {
            val pushData = PushData(
                type = "confirmation.blockchain",
                action = "confirm_own_block",
                body = "",
                objectX = it.blockId,
                participant = "",
                title = "",
                groupFenomyId = ""
            )
            pgService.sendPushToUser(it.userId, pushData, "high")
        }

        return SuccessResponse(true)
    }

    @PostMapping(path = ["/blocks/confirm/member"])
    fun blocksConfirmMember(@RequestBody blockMembers: BlockMembersRequest,
                            @RequestHeader("sys-key") sysKey: String?=null): SuccessResponse {
        checkAuthorization(sysKey)

        log.d("bc confirmation input = $blockMembers")

        blockMembers.userIds.forEach {
            val pushData = PushData(
                type = "confirmation.blockchain",
                action = "calc_hash",
                body = blockMembers.prevHash+","+blockMembers.sourceHash,
                objectX = blockMembers.blockId,
                participant = "",
                title = "",
                groupFenomyId = ""
            )
            pgService.sendPushToUser(it, pushData, "high")
        }

        return SuccessResponse(true)
    }

}