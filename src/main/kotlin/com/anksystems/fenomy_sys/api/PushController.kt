package com.anksystems.fenomy_sys.api

import com.anksystems.fenomy_sys.api.model.PushData
import com.anksystems.fenomy_sys.api.request.SendGroupPushRequest
import com.anksystems.fenomy_sys.api.request.SendPushRequest
import com.anksystems.fenomy_sys.api.response.ResultResponse
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping(path = ["/sys/v1/push"], produces = ["application/json"])
@ResponseBody
class PushController : BaseController() {
    @PostMapping("/send")
    fun sendPush(
        @RequestBody request: SendPushRequest,
        @RequestHeader("sys-key") sysKey: String? = null
    ): ResultResponse {
        checkAuthorization(sysKey)

        val pushData = PushData(
            type = request.type,
            action = request.action,
            body = "",
            title = "",
            objectX = request.objectId,
            participant = "",
            timestamp = Instant.now().toEpochMilli().toString(),
            groupFenomyId = ""
        )

        return try {
            //pgService.sendPushToGroup(groupFenomyId, pushData, request.type+" "+request.action, "")
            //pgService.sendPushToGroupByTopic(groupFenomyId, pushData, request.type, request.action)
            RESULT_OK
        } catch (e: Exception) {
            ResultResponse(result = e.message.toString())
        }

    }
}