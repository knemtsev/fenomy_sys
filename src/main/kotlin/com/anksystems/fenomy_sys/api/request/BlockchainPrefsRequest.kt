package com.anksystems.fenomy_sys.api.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BlockchainPrefsRequest(
    @SerialName("token")  val token: String? = null,
    @SerialName("user_id")  val userId: String? = null
)