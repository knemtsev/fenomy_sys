package com.anksystems.fenomy_sys.api.request

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class TransactionResultRequest(
    @SerialName("transaction_id")    val transactionId: String,
    @SerialName("success")    val success: Boolean
)
