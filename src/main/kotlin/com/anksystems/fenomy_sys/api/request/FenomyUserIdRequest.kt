package com.anksystems.fenomy_sys.api.request

import com.anksystems.fenomy_sys.api.exceptions.InvalidRequestParametersAtLeastException
import com.anksystems.fenomy_sys.api.model.UserId
import com.anksystems.fenomy_sys.api.model.UserIdTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.elementNames

@Serializable
open class FenomyUserIdRequest(
    @SerialName("fenomy_id") val fenomyId: String? = null,
    @SerialName("user_id")   val userId: String? = null,
    @SerialName("client_id") val clientId: String? = null,
) {
    fun getUserIdByRequest(): UserId = if(!fenomyId.isNullOrEmpty()) {
        UserId( id= fenomyId, type = UserIdTypes.FENOMY_ID)
    } else if(!userId.isNullOrEmpty()) {
        UserId( id= userId, type = UserIdTypes.USER_ID)
    } else if(!clientId.isNullOrEmpty()) {
        UserId( id= clientId, type = UserIdTypes.CLIENT_ID)
    } else
        throw InvalidRequestParametersAtLeastException(serializer().descriptor.elementNames.joinToString { it })

}