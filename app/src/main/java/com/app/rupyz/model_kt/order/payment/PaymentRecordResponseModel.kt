package com.app.rupyz.model_kt.order.payment

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentRecordResponseModel(

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null,

    var errorCode: Int? = null
) : Parcelable


