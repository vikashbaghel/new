package com.app.rupyz.model_kt.order.customer

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PanDataInfoModel(

    @field: SerializedName("pan_id")
    var pan_id: String? = null,

    @field:SerializedName("org_id")
    var org_id: Int? = null,

    @field:SerializedName("in_details")
    var in_details: Boolean? = null,
) : Parcelable
