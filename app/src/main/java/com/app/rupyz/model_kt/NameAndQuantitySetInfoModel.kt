package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NameAndQuantitySetInfoModel(

    @field:SerializedName("id")
    var qty: Double? = null,

    @field:SerializedName("name")
    var name: String? = null,

    @field:SerializedName("is_selected")
    var isSelected: Boolean = false

) : Parcelable
