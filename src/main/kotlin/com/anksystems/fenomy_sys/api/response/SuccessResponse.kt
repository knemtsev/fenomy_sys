package com.anksystems.fenomy_sys.api.response

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SuccessResponse (
    @SerialName("success")
    val success: Boolean
)