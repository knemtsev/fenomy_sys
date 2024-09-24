package com.anksystems.fenomy_sys.api.response

@kotlinx.serialization.Serializable
data class BalanceResponse(
    val fyid: String,
    val balance: Double,
)
