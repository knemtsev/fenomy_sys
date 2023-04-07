package com.anksystems.fenomy_sys.api

import com.anksystems.fenomy_sys.service.PGService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(path = ["/sys/v1/blockchain"], produces = ["application/json"])
@ResponseBody
class BlockchainController(
    @Autowired val pgService: PGService
) {
    @GetMapping(path=["/prefs"])
    fun getBlockchainPrefs(@RequestParam(name = "token") token: String): String {
        return try {
            pgService.getBlockchainPrefs(token)
        } catch (e: Exception) {
            "{"+"\"error\":"+"\""+e.message+"\"}"
        }
    }
}