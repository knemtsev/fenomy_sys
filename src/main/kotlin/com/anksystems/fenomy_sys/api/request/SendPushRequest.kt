package com.anksystems.fenomy_sys.api.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SendPushRequest(
    @SerialName("type")     val type: String,   // message, member, invite
    @SerialName("action")   val action: String, // new, update, delete
    @SerialName("object_id")val objectId: String,
)
