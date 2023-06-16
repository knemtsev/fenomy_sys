package com.anksystems.fenomy_sys.api.request


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SetNftAlgoRequest(
    @SerialName("id")    val id: String,
    @SerialName("fyid")  val fyid: String?=null,
    @SerialName("type")  val type: String?=null,
    @SerialName("status")    val status: String?=null,
    @SerialName("activate_date")  val activateDate: String?=null,
    @SerialName("activation_count")    val activationCount: Int?=null,
    @SerialName("activation_rules")    val activationRules: Int?=null,
    @SerialName("expired_date")    val expiredDate: String?=null,
    @SerialName("owners")    val owners: Int?=null,
    @SerialName("owners_rules")    val ownersRules: Int?=null,
    @SerialName("mode")    val mode: String?=null,
    @SerialName("mode_last_date")    val modeLastDate: String?=null,
    @SerialName("balance")    val balance: Int?=null,
    @SerialName("balance_rules")    val balanceRules: Boolean?=null,
    @SerialName("pk")    val pk: Int?=null,
    @SerialName("dk")    val dk: Double?=null,
    @SerialName("balance_refund")    val balanceRefund: Boolean?=null,
    @SerialName("count_balance_refund")    val countBalanceRefund: Int?=null,
    @SerialName("count_refunds")    val countRefunds: Int?=null,
    @SerialName("lock_period")    val lockPeriod: Int?=null,
    @SerialName("lock_period_rules")    val lockPeriodRules: Boolean?=null,
    @SerialName("payout_period")    val payoutPeriod: Int?=null,
    @SerialName("currency")    val currency: String?=null,
    @SerialName("reputation")    val reputation: Int?=null,
    @SerialName("reputation_rules")    val reputationRules: Boolean?=null,
    @SerialName("fixfnm")    val fixfnm: Double?=null,
    @SerialName("weather")    val weather: String?=null,
    @SerialName("contract")    val contract: String?=null,
    @SerialName("token_id")    val tokenId: String?=null,
    @SerialName("day_income")    val dayIncome: String?=null,
    @SerialName("payout_date")    val payoutDate: String?=null
)