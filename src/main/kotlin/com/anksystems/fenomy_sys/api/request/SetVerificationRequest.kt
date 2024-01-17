package com.anksystems.fenomy_sys.api.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class SetVerificationRequest(
    @SerialName("fyid")
    val fyid: String,
    @SerialName("verification_id")
    val verificationId: String
)
