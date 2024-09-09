package com.app.rupyz.model_kt

import android.os.Parcelable
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderTakenByAdminModel(

    @field: SerializedName("data")
    var data: List<AdminData>? = null,


    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null,

    var errorCode: Int? = null
) : Parcelable

@Parcelize
data class AdminData(

    @field: SerializedName("user_id")
    var userId: Int? = null,

    @field:SerializedName("name")
    var name: String? = null,

    @field:SerializedName("pic_url")
    var pic_url: String? = null,


) : Parcelable