package com.anksystems.fenomy_sys.api.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZonedDateTime

@Serializable
data class PushData(
    @SerialName("type")         val type: String,
    @SerialName("action")       val action: String,
    @SerialName("body")         val body: String,
    @SerialName("object")       val objectX: String,
    @SerialName("participant")  val participant: String,
    @SerialName("timestamp")    val timestamp: String = Instant.now().toEpochMilli().toString(),
    @SerialName("title")        val title: String,
)