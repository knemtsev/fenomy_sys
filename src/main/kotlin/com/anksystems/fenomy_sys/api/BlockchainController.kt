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
        return if(transactionResult.success) {
            pgService.doDisable(transactionResult.transactionId)
        } else {
            "{\"transaction\":\"off\"}"
        }
    }

    @PostMapping(path = ["/blocks/confirm/author"])
    fun blocksConfirmAuthor(@RequestBody blocksAuthor: BlocksAuthorRequest): SuccessResponse {
        blocksAuthor.blocks.forEach {
            val pushData = PushData(
                type = "blockchain",
                action = "confirm_author",
                body = "",
                objectX = it.hashId,
                participant = "",
                title = "",
                groupFenomyId = ""
            )
            pgService.sendPushToUser(it.userId, pushData, "high")
        }

        return SuccessResponse(true)
    }

    @PostMapping(path = ["/blocks/confirm/member"])
    fun blocksConfirmAuthor(@RequestBody blockMembers: BlockMembersRequest): SuccessResponse {
        blockMembers.userIds.forEach {
            val pushData = PushData(
                type = "blockchain",
                action = "confirm_member",
                body = "",
                objectX = blockMembers.hashId,
                participant = "",
                title = "",
                groupFenomyId = ""
            )
            pgService.sendPushToUser(it, pushData, "high")
        }

        return SuccessResponse(true)
    }


}