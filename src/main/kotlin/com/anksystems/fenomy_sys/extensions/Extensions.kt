package com.anksystems.fenomy_sys.extensions

import com.anksystems.fenomy_sys.service.LogService
import org.springframework.beans.factory.annotation.Autowired

fun Exception.toJson(
    @Autowired log: LogService
): String {
    log.e("${message}\n${stackTrace}")
    return "{\"error\":\"$message\", \"stack\":\"${stackTrace}\"}"
}