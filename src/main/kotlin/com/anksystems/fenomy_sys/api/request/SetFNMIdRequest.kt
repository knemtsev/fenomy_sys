package com.anksystems.fenomy_sys.api.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SetFNMIdRequest(
    @SerialName("fyid")
    val fyid: String? = null,
    @SerialName("user_id")
    val user_id: String? = null,
    @SerialName("fnm_id")
    val fnmId: String
)
