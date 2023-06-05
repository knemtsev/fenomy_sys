package com.anksystems.fenomy_sys.api

import com.anksystems.fenomy_sys.api.exceptions.InvalidRequestParametersAtLeastException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(
    path = ["/sys/v1/client"], produces = ["application/json"],
)
@ResponseBody
class ClientController : BaseController() {
    @GetMapping(path = ["/data"])
    fun getAvatar(
        @RequestParam("fyid") fyid: String?,
        @RequestParam("group_fyid") groupFyid: String?,
        @RequestParam("offset") offset: Int?,
        @RequestParam("limit") limit: Int?,
        @RequestHeader("sys-key") sysKey: String? = null
    ): String {
        checkAuthorization(sysKey)

        val fields = "family_name as lastname, given_name as firstname, p.picture as avatar, u.email as email, u.phone as phone"

        return if (groupFyid != null)
            pgService.execQueryToJsonArray(
                "select $fields " +
                        "from db.member_group mg " +
                        "inner join db.profile p on mg.member=p.userid " +
                        "inner join db.client c on c.userid=p.userid " +
                        "inner join db.user u on u.id=p.userid " +
                        "inner join db.user ug on ug.id=mg.userid " +
                        "where ug.username='$groupFyid' " +
                        (if(limit!=null) "limit $limit " else " ") +
                        (if(offset!=null) "offset $offset " else " ") +
                        ";"
            )
        else if (fyid != null)
            pgService.execQueryToJsonObject(
                "select $fields " +
                        "from db.profile p\n" +
                        "inner join db.client c on c.userid=p.userid " +
                        "inner join db.user u on u.id=p.userid " +
                        "where c.code='$fyid';"
            )
        else
            throw InvalidRequestParametersAtLeastException("fyid, group_fyid")
    }

    @GetMapping(path = ["/location/last"])
    fun getLocation(
        @RequestParam("fyid") fyid: String?,
        @RequestParam("group_fyid") groupFyid: String?,
        @RequestParam("offset") offset: Int?,
        @RequestParam("limit") limit: Int?,
        @RequestHeader("sys-key") sysKey: String? = null
    ): String {

        checkAuthorization(sysKey)

        val fields = "oc.id, c.code as object, oc.code, oc.latitude, oc.longitude, oc.accuracy, oc.label, oc.description, oc.validfromdate, oc.validtodate, oc.data::jsonb as data"

        return if(groupFyid!=null)
                pgService.execQueryToJsonArray(
                    "select $fields " +
                            "from db.member_group mg " +
                            "inner join db.client c on c.userid=mg.member " +
                            "inner join db.object_coordinates oc on oc.object=c.id and oc.validtodate>now() " +
                            "inner join db.user ug on ug.id=mg.userid " +
                            "where ug.username='$groupFyid'" +
                            (if(limit!=null) "limit $limit " else " ") +
                            (if(offset!=null) "offset $offset " else " ") +
                            ";")
            else if(fyid!=null)
                pgService.execQueryToJsonObject(
            "select $fields " +
                    "from db.object_coordinates oc " +
                    "inner join db.client c on c.id=oc.object " +
                    "where c.code='${fyid}' order by validfromdate desc limit 1;")
            else
                throw InvalidRequestParametersAtLeastException("fyid, group_fyid")

    }

}

