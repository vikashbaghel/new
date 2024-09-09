package com.app.rupyz.model_kt

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class TeamAggregatedInfoResponseModel(
	@field:SerializedName("data")
	val data: TeamAggregatedInfoModel? = null,

	@field:SerializedName("message")
	var message: String? = null,

	@field:SerializedName("error")
	var error: Boolean? = null
) : Parcelable

@Parcelize
data class TeamAggregatedInfoModel(

	@field:SerializedName("tc_meeting_count")
	val tcMeetingCount: Int? = null,

	@field:SerializedName("leave_staff_count")
	val leaveStaffCount: Int? = null,

	@field:SerializedName("active_staff_count")
	val activeStaffCount: Int? = null,

	@field:SerializedName("pc_order_count")
	val pcOrderCount: Int? = null,

	@field:SerializedName("total_order_amount")
	val totalOrderAmount: Double? = null,

	@field:SerializedName("inactive_staff_count")
	val inactiveStaffCount: Int? = null
) : Parcelable
