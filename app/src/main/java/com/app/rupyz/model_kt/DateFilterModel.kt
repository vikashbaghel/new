package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DateFilterModel(
    @field:SerializedName("title")
    var title: String? = null,
    @field:SerializedName("start_date")
    var startDate: String? = null,
    @field:SerializedName("end_date")
    var end_date: String? = null,
    @field:SerializedName("is_selected")
    var isSelected: Boolean? = null,
    @field:SerializedName("filter_type")
    var filter_type: String? = null
) : Parcelable