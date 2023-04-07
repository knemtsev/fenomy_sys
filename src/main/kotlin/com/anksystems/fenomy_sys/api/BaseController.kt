package com.anksystems.fenomy_sys.api

import com.anksystems.fenomy_sys.MyProperties
import com.anksystems.fenomy_sys.api.exceptions.NoAppKeyException
import com.anksystems.fenomy_sys.service.LogService
import com.anksystems.fenomy_sys.service.PGService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

open class BaseController() {
    @Autowired protected lateinit var pgService: PGService
    @Autowired protected lateinit var log: LogService
    @Autowired protected lateinit var myProperties: MyProperties

    fun checkAuthorization(sysKey: String?) {
        log.d("sysKey=$sysKey - config sysKey=${myProperties.sysKey}")
        if(sysKey==null || sysKey!=myProperties.sysKey)
            throw NoAppKeyException()
    }

    @ExceptionHandler(value = [NoAppKeyException::class])
    @ResponseBody
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    fun handleNoAuthorized(e: Exception): MutableMap<String, String> {
        //throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        val exception: MutableMap<String, String> = mutableMapOf()

        log.e("unauthorized Access to the API: ${e.message}")

        exception["code"] = "401"
        exception["reason"] = e.message!!

        return exception
    }

}