package com.app.rupyz.model_kt.packagingunit

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PackagingUnitResponseModel(

    @SerializedName("data"    ) var packUnitResponse    : ArrayList<String> = arrayListOf(),
    @SerializedName("message" ) var message : String?           = null,
    @SerializedName("error"   ) var error   : Boolean?          = null
) : Parcelable


