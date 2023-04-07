package com.anksystems.fenomy_sys

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("sys")
data class MyProperties constructor(
    var pgTimeout: Int = 1000,
    var sysKeyName: String = "sys-key",
    var sysKey: String = ""
)