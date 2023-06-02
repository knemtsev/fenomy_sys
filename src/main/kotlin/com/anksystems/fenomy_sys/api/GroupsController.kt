package com.anksystems.fenomy_sys.api

import com.anksystems.fenomy_sys.api.exceptions.NoAppKeyException
import com.anksystems.fenomy_sys.api.model.PushData
import com.anksystems.fenomy_sys.api.model.UserId
import com.anksystems.fenomy_sys.api.model.UserIdTypes
import com.anksystems.fenomy_sys.api.request.FenomyUserIdRequest
import com.anksystems.fenomy_sys.api.request.GroupAddRequest
import com.anksystems.fenomy_sys.api.request.GroupMemberRequest
import com.anksystems.fenomy_sys.api.request.SendGroupPushRequest
import com.anksystems.fenomy_sys.api.response.GroupAddResponse
import com.anksystems.fenomy_sys.api.response.ResultResponse
import com.anksystems.fenomy_sys.extensions.toJson
import com.anksystems.fenomy_sys.service.LogService
import com.anksystems.fenomy_sys.service.PGService
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@RestController
@RequestMapping(path = ["/sys/v1/groups"], produces = ["application/json"])
@ResponseBody
class GroupsController (): BaseController() {
    @PostMapping
    fun groupsAdd(
        @RequestBody request: GroupAddRequest,
        @RequestHeader("sys-key") sysKey: String? = null
    ): String {
        checkAuthorization(sysKey)
        return try {
            Json.encodeToString(GroupAddResponse(fenomyId = pgService.newGroup(request)))
        } catch (e: Exception) {
            e.toJson(log)
        }
    }

    @PostMapping("/members")
    fun membersAdd(
        @RequestBody request: GroupMemberRequest,
        @RequestHeader("sys-key") sysKey: String? = null
    ): String {
        checkAuthorization(sysKey)
        return try {
            Json.encodeToString(ResultResponse(result = pgService.addGroupMember(request)))
        } catch (e: Exception) {
            e.toJson(log)
        }
    }

    @DeleteMapping("/members")
    fun membersDel(
        @RequestBody request: GroupMemberRequest,
        @RequestHeader("sys-key") sysKey: String? = null
    ): String {
        checkAuthorization(sysKey)
        return try {
            Json.encodeToString(ResultResponse(result = pgService.delGroupMember(request)))
        } catch (e: Exception) {
            "ERROR "+e.message//throw ResponseStatusException(HttpStatus.BAD_REQUEST,e.message)
        }
    }

    @PostMapping("/push/{groupFenomyId}")
    fun sendPush(
        @RequestBody request: SendGroupPushRequest,
        @PathVariable groupFenomyId: String,
        @RequestHeader("sys-key") sysKey: String? = null
    ): ResultResponse {
        checkAuthorization(sysKey)

        val pushData = PushData(
            type = request.type+".groups",
            action = request.action,
            body = "",
            title = "",
            objectX = request.objectId,
            participant = "",
            timestamp = Instant.now().toEpochMilli().toString(),
            groupFenomyId = groupFenomyId
        )

        return try {
            //pgService.sendPushToGroup(groupFenomyId, pushData, request.type+" "+request.action, "")
            pgService.sendPushToGroupByTopic(groupFenomyId, pushData, request.type, request.action)
            RESULT_OK
        } catch (e:Exception) {
            ResultResponse(result = e.message.toString())
        }

    }
}