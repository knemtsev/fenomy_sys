package com.anksystems.fenomy_sys.api.request

import com.fasterxml.jackson.databind.BeanDescription
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroupAddRequest(
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
)
