package com.anksystems.fenomy_sys.api

import com.anksystems.fenomy_sys.api.request.GroupAddRequest
import com.anksystems.fenomy_sys.api.request.GroupMemberRequest
import com.anksystems.fenomy_sys.api.response.GroupAddResponse
import com.anksystems.fenomy_sys.api.response.ResultResponse
import com.anksystems.fenomy_sys.extensions.toJson
import com.anksystems.fenomy_sys.service.LogService
import com.anksystems.fenomy_sys.service.PGService
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/sys/v1/groups"], produces = ["application/json"])
@ResponseBody
class GroupsController (
    @Autowired val pgService: PGService,
    @Autowired val log: LogService
) {
    @PostMapping
    fun groupsAdd(@RequestBody request: GroupAddRequest): String {
        return try {
            Json.encodeToString(GroupAddResponse(fenomyId = pgService.newGroup(request)))
        } catch (e: Exception) {
            e.toJson(log)
        }
    }

    @PostMapping("/members")
    fun membersAdd(@RequestBody request: GroupMemberRequest): String {
        return try {
            Json.encodeToString(ResultResponse( result = pgService.addGroupMember(request)))
        } catch (e: Exception) {
            e.toJson(log)
        }
    }

    @DeleteMapping("/members")
    fun membersDel(@RequestBody request: GroupMemberRequest): String {
        return try {
            Json.encodeToString(ResultResponse( result = pgService.delGroupMember(request)))
        } catch (e: Exception) {
            e.toJson(log)
        }
    }

}