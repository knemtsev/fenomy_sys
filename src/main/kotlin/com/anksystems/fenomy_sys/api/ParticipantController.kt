package com.anksystems.fenomy_sys.api

import com.anksystems.fenomy_sys.api.BaseController
import com.anksystems.fenomy_sys.api.exceptions.InvalidRequestParametersAtLeastException
import com.anksystems.fenomy_sys.api.model.UserId
import com.anksystems.fenomy_sys.api.model.UserIdTypes
import com.anksystems.fenomy_sys.api.request.FenomyUserIdRequest
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.elementNames
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/sys/v1/participant"], produces = ["application/json"])
@ResponseBody
class ParticipantController: BaseController() {
    @GetMapping(path = ["/avatar"])
    fun getAvatar(
        @RequestBody fenomyUserIdRequest: FenomyUserIdRequest,
        @RequestHeader("sys-key") sysKey: String? = null
    ): String {
        checkAuthorization(sysKey)

        val clientId = pgService.getClientId(fenomyUserIdRequest)
        return pgService.execQuery("select p.picture from db.profile p inner join db.client c on c.userid=p.userid where c.id='$clientId';")
    }

    @GetMapping(path = ["/location"])
    fun getLocation(
        @RequestBody fenomyUserIdRequest: FenomyUserIdRequest,
        @RequestHeader("sys-key") sysKey: String? = null
    ): String {

        checkAuthorization(sysKey)

        val clientId = pgService.getClientId(fenomyUserIdRequest)

        return pgService.execQueryToJsonObject(
            "select oc.validfromdate, oc.validtodate, oc.latitude, oc.longitude, oc.accuracy,\n" +
                    "oc.data->'device'->>'serial' as device, oc.data->'device'->>'battery' as battery\n" +
                    "from db.object_coordinates oc where oc.object='${clientId}' order by validfromdate desc limit 1;"
        )
    }


}

