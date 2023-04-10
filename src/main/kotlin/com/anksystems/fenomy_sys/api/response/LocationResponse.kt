package com.anksystems.fenomy_sys.api.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationResponse(
    @SerialName("accuracy")
    val accuracy: Int,
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("location_data")
    val locationData: LocationData,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("validfromdate")
    val validFromDate: String,
    @SerialName("validtodate")
    val validToDate: String
)