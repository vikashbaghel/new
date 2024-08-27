package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AttendanceResponseModel(

	@field:SerializedName("data")
	var data: List<AttendanceDataItem>? = null,

	@field:SerializedName("message")
	var message: String? = null,

	@field:SerializedName("error")
    var error: Boolean? = null
) : Parcelable

@Parcelize
data class UpdateAttendanceResponseModel(

	@field:SerializedName("data")
	val data: AttendanceDataItem? = null,

	@field:SerializedName("message")
	var message: String? = null,

	@field:SerializedName("error")
	var error: Boolean? = null
) : Parcelable

@Parcelize
data class AttendanceDataItem(

	var calenderDate: String? = null,

	@field:SerializedName("date")
    var date: String? = null,

    var apiDate: String? = null,

	@field:SerializedName("comments")
	var comments: String? = null,

	@field:SerializedName("created_at")
	var createdAt: String? = null,

	@field:SerializedName("is_edited")
	val isEdited: Boolean? = null,

	var isWeekEnd: Boolean? = null,

	@field:SerializedName("staff")
	val staff: Int? = null,

	@field:SerializedName("created_by_name")
	val createdByName: String? = null,

	@field:SerializedName("created_by")
	val createdBy: Int? = null,

	@field:SerializedName("time_out")
	var timeOut: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("organization")
	val organization: Int? = null,

	@field:SerializedName("time_in")
	var timeIn: String? = null,

	@field:SerializedName("updated_by")
	val updatedBy: Int? = null,

	@field:SerializedName("week_day")
	var weekDay: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("total_time")
	val totalTime: Int? = null,

	@field:SerializedName("attendance_type")
    var attendanceType: String? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable
