package com.anksystems.fenomy_sys.api.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Device(
    @SerialName("battery")
    val battery: Double,
    @SerialName("id")
    val id: String,
    @SerialName("identity")
    val identity: String,
    @SerialName("serial")
    val serial: String
)