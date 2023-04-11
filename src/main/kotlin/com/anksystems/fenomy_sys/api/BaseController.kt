package com.anksystems.fenomy_sys.api

import com.anksystems.fenomy_sys.MyProperties
import com.anksystems.fenomy_sys.api.exceptions.InvalidRequestParametersAtLeastException
import com.anksystems.fenomy_sys.api.exceptions.NoAppKeyException
import com.anksystems.fenomy_sys.api.exceptions.NoTokenException
import com.anksystems.fenomy_sys.api.response.ResultResponse
import com.anksystems.fenomy_sys.service.LogService
import com.anksystems.fenomy_sys.service.PGService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

open class BaseController() {
    @Autowired protected lateinit var pgService: PGService
    @Autowired protected lateinit var log: LogService
    @Autowired protected lateinit var myProperties: MyProperties

    companion object {
        val RESULT_OK = ResultResponse("ok")
    }
    fun checkAuthorization(sysKey: String?) {
        if(sysKey==null || sysKey!=myProperties.sysKey)
            throw NoAppKeyException()
    }

    @ExceptionHandler(value = [NoAppKeyException::class])
    @ResponseBody
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    fun handleNoAuthorized(e: Exception): MutableMap<String, String> {
        log.e("unauthorized Access to the API: ${e.message}")
        return composeException(401, e)
    }


    @ExceptionHandler(value =
    [InvalidRequestParametersAtLeastException::class,
        HttpMessageNotReadableException::class,
        NoTokenException::class
    ])
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    fun handle400Error(e: Exception): MutableMap<String, String> {
        log.e("Error: ${e.message}")
        return composeException(400, e)
    }

    private fun composeException(code: Int, e: Exception): MutableMap<String, String> {
        val exception: MutableMap<String, String> = mutableMapOf()

        exception["code"] = code.toString()
        exception["reason"] = e.message!!

        return exception

    }


}