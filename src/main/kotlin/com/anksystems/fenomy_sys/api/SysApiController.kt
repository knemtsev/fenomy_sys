package com.anksystems.fenomy_sys.api

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class SysApiController {
    @RequestMapping("/sys/v1/blockchain/prefs")
    fun blockchainPrefs(): String {
        return "hello"
    }
}