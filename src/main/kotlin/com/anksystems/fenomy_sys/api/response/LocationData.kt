package com.anksystems.fenomy_sys.api.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationData(
    @SerialName("device")
    val device: Device
)