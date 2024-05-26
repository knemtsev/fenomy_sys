package com.anksystems.fenomy_sys.api.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SecretRequest(
    @SerialName("email") val email: String,
)