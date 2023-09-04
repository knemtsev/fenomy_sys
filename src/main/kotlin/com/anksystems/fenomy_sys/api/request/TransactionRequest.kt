package com.anksystems.fenomy_sys.api.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionRequest(
    @SerialName("debit") val debit: String? = null,
    @SerialName("credit") val credit: String? = null,
    @SerialName("amount") val amount: Double,
    @SerialName("currency") val currency: String? = null,
) {
}