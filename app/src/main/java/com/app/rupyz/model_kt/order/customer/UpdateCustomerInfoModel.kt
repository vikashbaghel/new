package com.app.rupyz.model_kt.order.customer

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UpdateCustomerInfoModel(

    @field: SerializedName("data")
    var data: CustomerData? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null,

    var errorCode: Int? = null
) : Parcelable

