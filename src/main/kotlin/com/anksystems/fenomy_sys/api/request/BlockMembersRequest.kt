package com.anksystems.fenomy_sys.api.request

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class BlockMembersRequest(
    @SerialName("block_id")
    val blockId : String,
    @SerialName("prev_hash")
    val prevHash: String,
    @SerialName("source_hash")
    val sourceHash: String,
    @SerialName("user_ids")
    val userIds: List<String>
)
