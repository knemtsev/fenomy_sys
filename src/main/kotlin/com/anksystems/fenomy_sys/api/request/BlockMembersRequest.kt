package com.anksystems.fenomy_sys.api.request

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class BlockMembersRequest(
    @SerialName("hash_id")
    val hashId : String,
    @SerialName("hash_prev")
    val hashPrev: String,
    @SerialName("hash_source")
    val hashSource: String,
    @SerialName("user_ids")
    val userIds: List<String>
)
