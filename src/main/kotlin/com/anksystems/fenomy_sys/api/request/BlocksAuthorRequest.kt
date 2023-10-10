package com.anksystems.fenomy_sys.api.request

import com.anksystems.fenomy_sys.api.model.BlockAuthor
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class BlocksAuthorRequest(
    @SerialName("blocks")
    val blocks: List<BlockAuthor>
)
