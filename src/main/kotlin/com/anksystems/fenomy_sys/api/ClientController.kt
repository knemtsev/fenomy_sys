package com.anksystems.fenomy_sys.api

import com.anksystems.fenomy_sys.api.exceptions.InvalidRequestParameterException
import com.anksystems.fenomy_sys.api.exceptions.InvalidRequestParametersAtLeastException
import com.anksystems.fenomy_sys.api.request.BaseRequest
import com.anksystems.fenomy_sys.api.request.GroupRequest
import com.anksystems.fenomy_sys.api.request.SecretRequest
import org.springframework.web.bind.annotation.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@CrossOrigin(origins = ["https://localhost:3000", "https://fenomy.com"], allowedHeaders = ["content-type", "sys-key"])
@RestController
@RequestMapping(
    path = ["/sys/v1/client"], produces = ["application/json"],
)
@ResponseBody
class ClientController : BaseController() {
    @GetMapping(path = ["/data"])
    fun getData(
        @RequestParam("fyid") fyid: String?,
        @RequestParam("group_fyid") groupFyid: String?,
        @RequestParam("offset") offset: Int?,
        @RequestParam("limit") limit: Int?,
        @RequestParam("after") after: String?,
        @RequestParam("fyids") fyids: List<String>?,
        @RequestHeader("sys-key") sysKey: String? = null
    ): String {
        checkAuthorization(sysKey)

        log.d("fyIds=${fyids?.joinToString("|") { it }}")
        return getData(
            fyid = fyid,
            groupFyid = groupFyid,
            offset = offset,
            limit = limit,
            afterTime = BaseRequest.toTime(after),
            fyIds = fyids
        )
    }

    @PostMapping(path = ["/data"])
    fun getData(
        @RequestBody groupRequest: GroupRequest,
        @RequestHeader("sys-key") sysKey: String? = null
    ): String {
        checkAuthorization(sysKey)

        return getData(
            fyid = groupRequest.fyid, groupFyid = groupRequest.groupFyid,
            offset = groupRequest.offset, limit = groupRequest.limit, afterTime = groupRequest.afterToTime(),
            fyIds = groupRequest.fyids
        )
    }

    @PostMapping(path = ["/avatar"])
    fun getAvatar(
        @RequestBody groupRequest: GroupRequest,
        @RequestHeader("sys-key") sysKey: String? = null
    ): String {
        checkAuthorization(sysKey)

        return getData(
            fyid = groupRequest.fyid, groupFyid = groupRequest.groupFyid,
            offset = groupRequest.offset,
            limit = groupRequest.limit,
            afterTime = groupRequest.afterToTime(),
            rule = "and p.picture is not null ",
            fields = "c.code as fyid, p.picture as avatar"
        )
    }


    private fun getData(
        fyid: String? = null,
        groupFyid: String? = null,
        offset: Int? = null,
        limit: Int? = null,
        afterTime: ZonedDateTime? = null,
        rule: String? = null,
        fields: String = "c.code as fyid, family_name as lastname, given_name as firstname, p.picture as avatar, u.email as email, u.phone as phone",
        fyIds: List<String>? = null
    ): String {

        val res = if (groupFyid != null)
            pgService.execQueryToJsonArray(
                "select $fields " +
                        "from db.member_group mg " +
                        "inner join db.profile p on mg.member=p.userid " +
                        "inner join db.client c on c.userid=p.userid " +
                        "inner join db.user u on u.id=p.userid " +
                        "inner join db.user ug on ug.id=mg.userid " +
                        "inner join db.object o on o.id=c.id " +
                        "where ug.username='$groupFyid' " +
                        (rule?.let { it } ?: " ") +
                        (if (afterTime != null) " and o.udate>'${afterTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)}'" else " ") +
                        (if (limit != null) "limit $limit " else " ") +
                        (if (offset != null) "offset $offset " else " ") +
                        ";"
            )
        else if (fyIds != null)
            pgService.execQueryToJsonArray(
                "select $fields " +
                        "from db.profile p\n" +
                        "inner join db.client c on c.userid=p.userid " +
                        "inner join db.user u on u.id=p.userid " +
                        "where c.code = ANY(ARRAY[${fyIds.joinToString(",") { "'" + it + "'" }}])" +
                        ";"
            )
        else if (fyid != null)
            pgService.execQueryToJsonObject(
                "select $fields " +
                        "from db.profile p\n" +
                        "inner join db.client c on c.userid=p.userid " +
                        "inner join db.user u on u.id=p.userid " +
                        "where c.code='$fyid'" +
                        ";"
            )
        else
            throw InvalidRequestParametersAtLeastException("fyid, group_fyid")

        log.d("GET_DATA $groupFyid $afterTime")

        return res;
    }

    @GetMapping(path = ["/location/last"])
    fun getLocation(
        @RequestParam("fyid") fyid: String?,
        @RequestParam("group_fyid") groupFyid: String?,
        @RequestParam("offset") offset: Int?,
        @RequestParam("limit") limit: Int?,
        @RequestParam("after") after: String?,
        @RequestHeader("sys-key") sysKey: String? = null
    ): String {

        checkAuthorization(sysKey)

        return getLocation(fyid, groupFyid, offset, limit, BaseRequest.toTime(after))
    }

    @CrossOrigin(/*origins = ["https://localhost:3000"]*/)
    @PostMapping(path = ["/location/last"])
    fun getLocation(
        @RequestBody groupRequest: GroupRequest,
        @RequestHeader("sys-key") sysKey: String? = null
    ): String {

        checkAuthorization(sysKey)

        return getLocation(
            groupRequest.fyid, groupRequest.groupFyid,
            groupRequest.offset, groupRequest.limit, groupRequest.afterToTime()
        )

    }


    private fun getLocation(
        fyid: String? = null,
        groupFyid: String? = null,
        offset: Int? = null,
        limit: Int? = null,
        afterTime: ZonedDateTime? = null,
    ): String {

        val fields =
            "oc.id, c.code as object, oc.code, oc.latitude, oc.longitude, oc.accuracy, oc.label, oc.description, oc.validfromdate, oc.validtodate, oc.data::jsonb as data"

        val res = if (groupFyid != null) {
            pgService.execQueryToJsonArray(
                "select $fields " +
                        "from db.member_group mg " +
                        "inner join db.client c on c.userid=mg.member " +
                        "inner join db.object_coordinates oc on oc.object=c.id and oc.validtodate>now() " +
                        "inner join db.user ug on ug.id=mg.userid " +
                        "where ug.username='$groupFyid'" +
                        (if (afterTime != null) "and oc.validfromdate>'${afterTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)}' " else " ") +
                        (if (limit != null) "limit $limit " else " ") +
                        (if (offset != null) "offset $offset " else " ") +
                        ";"
            )
        } else if (fyid != null)
            pgService.execQueryToJsonObject(
                "select $fields " +
                        "from db.object_coordinates oc " +
                        "inner join db.client c on c.id=oc.object " +
                        "where c.code='${fyid}' order by validfromdate desc limit 1;"
            )
        else
            throw InvalidRequestParametersAtLeastException("fyid, group_fyid")

        log.d("GET_LOCATION $groupFyid $afterTime $res")

        return res
    }

    @CrossOrigin(/*origins = ["https://localhost:3000"]*/)
    @PostMapping(path = ["/secret/recovery"])
    fun getSecretRecovery(
        @RequestBody secretRequest: SecretRequest,
        @RequestHeader("sys-key") sysKey: String? = null
    ): String {

        checkAuthorization(sysKey)

        return pgService.execQuery("select api.recovery_secret('${pgService.screenApostrophe(secretRequest.email)}');")

    }

    @CrossOrigin(/*origins = ["https://localhost:3000"]*/)
    @GetMapping(path = ["/secret/recovery/{ticket}"])
    fun getSecretByTicket(
        @PathVariable ticket: String,
        @RequestHeader("sys-key") sysKey: String? = null
    ): String {

        checkAuthorization(sysKey)

        return pgService.execQuery("select api.get_new_secret_by_ticket('${pgService.screenApostrophe(ticket)}');")

    }


}

