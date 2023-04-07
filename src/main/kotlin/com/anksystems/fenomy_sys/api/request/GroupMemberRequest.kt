package com.anksystems.fenomy_sys.api.request


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroupMemberRequest(
    @SerialName("group_fenomy_id")    val groupFenomyId: String,
    @SerialName("user_fenomy_id")     val userFenomyId: String
)