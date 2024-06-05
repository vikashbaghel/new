package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StaffTrackingDetailsResponseModel(

    @field:SerializedName("data")
    val data: StaffTrackingData? = null,

    @field:SerializedName("message")
	var message: String? = null,

    @field:SerializedName("error")
	var error: Boolean? = null,

	var errorCode: Int? = null
) : Parcelable

@Parcelize
data class TeamTrackingDetailsResponseModel(

	@field:SerializedName("data")
	val data: List<StaffTrackingActivityModules>? = null,

	@field:SerializedName("message")
	var message: String? = null,

	@field:SerializedName("error")
	var error: Boolean? = null
) : Parcelable

@Parcelize
data class StaffTrackingData(

    @field:SerializedName("activity_modules")
    val activityModules: StaffTrackingActivityModules? = null,

    @field:SerializedName("activity_list")
    val activityList: List<CustomerFollowUpDataItem>? = null
) : Parcelable

@Parcelize
data class StaffTrackingActivityModules(

	@field:SerializedName("user_id")
	val staffId: Int? = null,

	@field:SerializedName("order_count")
	val orderCount: Int? = null,

	@field:SerializedName("distance_travelled")
	val distanceTravelled: Double? = null,

	@field:SerializedName("staff_name")
	val staffName: String? = null,

	@field:SerializedName("order_amount")
	val orderAmount: Double? = null,

	@field:SerializedName("total_activity")
	val totalActivity: Int? = null,

	@field:SerializedName("meetings")
	val meetings: Int? = null,

	@field:SerializedName("lead_count")
	val leadCount: Int? = null,

	@field:SerializedName("customer_count")
	val customerCount: Int? = null
) : Parcelable


