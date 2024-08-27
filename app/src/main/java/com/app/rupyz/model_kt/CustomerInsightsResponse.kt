package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomerInsightsResponse(
    @field:SerializedName("data")
    val data: DataInsights? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null
) : Parcelable

@Parcelize

data class DataInsights(
    @field:SerializedName("avg_order_value_historic")
    var avgOrderValueHistoric: Double? = 0.0 ,
    @field:SerializedName("last_order_date")
    val lastOrderDate: String? = "",
    @field:SerializedName("last_order_value")
    val lastOrderValue: Double? = 0.0,
    @field:SerializedName("last_visit_date")
    val lastVisitDate: String? = "",
    @field:SerializedName("lifetime_pc_count")
    val lifetimePcCount: Int? = 0,
    @field:SerializedName("lifetime_tc_count")
    val lifetimeTcCount: Int? = 0,
    @field:SerializedName("pc_order_count")
    val pcOrderCount: Int? = 0,
    @field:SerializedName("productive_output_percentage")
    val productiveOutputPercentage: Double? = 0.0,
    @field:SerializedName("tc_meeting_count")
    val tcMeetingCount: Int? = 0 ,
    @field:SerializedName("total_order_amount")
    val totalOrderAmount: Double? = 0.0
) : Parcelable