package com.app.rupyz.model_kt.packagingunit

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PackagingUnitInfoModel(

    @field: SerializedName("data")
    val data: PackagingUnitData? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null,
) : Parcelable

@Parcelize
data class PackagingUnitData(

    @field:SerializedName("id")
    var id: Int? = null,

    @field:SerializedName("name")
    var name: String? = null,

    @field:SerializedName("created_at")
    var created_at: String? = null,

    @field:SerializedName("updated_at")
    val updated_at: String? = null,

    @field:SerializedName("organization")
    val organization: Int? = null,

    @field:SerializedName("created_by")
    val created_by: Int? = null,

    ) : Parcelable

