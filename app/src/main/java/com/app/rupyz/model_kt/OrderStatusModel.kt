package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderStatusModel(

    @field:SerializedName("name")
    var name: String? = null,

    @field:SerializedName("isSelected")
    var isSelected: Boolean? = null,

) : Parcelable

