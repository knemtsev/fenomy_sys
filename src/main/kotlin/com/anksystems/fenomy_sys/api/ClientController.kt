package com.anksystems.fenomy_sys.api

import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController

@RequestMapping(
    path = ["/sys/v1/client"], produces = ["application/json"],
)
@ResponseBody
@CrossOrigin(origins = ["https://localhost:3000"], maxAge = 3600)
class ClientController: BaseController() {
    @GetMapping(path = ["/data"])
    fun getAvatar(
        @RequestParam("fyid") fyid: String,
        @RequestHeader("sys-key") sysKey: String? = null
    ): String {
        checkAuthorization(sysKey)

        return pgService.execQueryToJsonObject(
            "select family_name as lastname, given_name as firstname, p.picture as avatar from db.profile p\n" +
                "inner join db.client c on c.userid=p.userid where c.code='$fyid';")
    }

    @GetMapping(path = ["/location/last"])
    fun getLocation(
        @RequestParam("fyid") fyid: String,
        @RequestHeader("sys-key") sysKey: String? = null
    ): ResponseEntity<String> {

        checkAuthorization(sysKey)

        val res = pgService.execQueryToJsonObject(
            "select oc.id, '$fyid' as object, oc.code, oc.latitude, oc.longitude, oc.accuracy, oc.label, " +
                    "oc.description, oc.validfromdate, oc.validtodate, oc.data::jsonb as data \n" +
                    "from db.object_coordinates oc " +
                    "inner join db.client c on c.id=oc.object " +
                    "where c.code='${fyid}' order by validfromdate desc limit 1;"
        )
        val responseHeaders = HttpHeaders()
        responseHeaders.set(
            "Access-Control-Request-Headers",
            "*"
        )

        return ResponseEntity.ok()
            .headers(responseHeaders)
            .body(res)
    }


}

