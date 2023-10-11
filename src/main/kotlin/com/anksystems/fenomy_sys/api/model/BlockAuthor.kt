package com.anksystems.fenomy_sys.api.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class BlockAuthor(
    @SerialName("user_id")
    val userId: String,
    @SerialName("block_id")
    val blockId: String
)
