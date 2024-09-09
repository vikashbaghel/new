package com.app.rupyz.model_kt

import com.google.gson.annotations.SerializedName


data class CheckInRequest(
    var customer_id: Int? = null,
    var images: List<Int?>? = null,
    var geo_location_lat: Double? = null,
    var geo_location_long: Double? = null,
    var geo_address: String? = null,
)

data class CheckInResponse(
    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null,

    var errorCode: Int? = null
)

data class CheckoutRequest(
    val customer_id: Int?
)