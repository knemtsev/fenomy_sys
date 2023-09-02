package com.anksystems.fenomy_sys.api.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionRequest(
    @SerialName("debit") val debit: String,
    @SerialName("credit") val credit: String,
    @SerialName("amount") val amount: Double
) {
}