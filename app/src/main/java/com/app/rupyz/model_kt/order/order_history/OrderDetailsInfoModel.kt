package com.app.rupyz.model_kt.order.order_history

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderDetailsInfoModel(

    @field: SerializedName("data")
    var data: OrderData? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null,
) : Parcelable
