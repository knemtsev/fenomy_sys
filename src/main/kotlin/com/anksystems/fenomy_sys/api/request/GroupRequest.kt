package com.anksystems.fenomy_sys.api.request

import com.anksystems.fenomy_sys.api.exceptions.InvalidRequestParameterException
import kotlinx.serialization.SerialName
import java.time.ZonedDateTime

@kotlinx.serialization.Serializable
class GroupRequest(
    @SerialName("fyid") val fyid: String? = null,
    @SerialName("group_fyid") val groupFyid: String? = null,
) : BaseRequest() {


}