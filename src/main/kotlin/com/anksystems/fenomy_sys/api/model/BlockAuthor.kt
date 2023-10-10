package com.anksystems.fenomy_sys.api.model

@kotlinx.serialization.Serializable
data class BlockAuthor(
    val userId: String,
    val hashId: String
)
