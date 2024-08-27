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
    val data: TeamRecordsModel? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null
) : Parcelable

@Parcelize
data class TeamRecordsModel(

    @field:SerializedName("tc_meeting_count")
    val tcMeetingCount: Int? = null,

    @field:SerializedName("total_order_amount")
    val totalOrderAmount: Double? = null,

    @field:SerializedName("pc_order_count")
    val pcOrderCount: Int? = null,

    @field:SerializedName("records")
    val records: List<StaffTrackingActivityModules>? = null
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
    var staffId: Int? = null,

    @field:SerializedName("profile_pic_url")
    var picUrl: String? = null,

    @field:SerializedName("order_value")
    val orderCount: Double? = null,

    @field:SerializedName("tc_count")
    val tcCount: Int? = null,

    @field:SerializedName("pc_count")
    val pcCount: Int? = null,

    @field:SerializedName("distance_travelled")
    val distanceTravelled: Double? = null,

    @field:SerializedName("name")
    var staffName: String? = null,

    @field:SerializedName("start_day")
    val startDay: String? = null,

    @field:SerializedName("end_day")
    val endDay: String? = null,

    @field:SerializedName("order_amount")
    val orderAmount: Double? = null,

    @field:SerializedName("total_activity")
    val totalActivity: Int? = null,

    @field:SerializedName("meetings")
    val meetings: Int? = null,

    @field:SerializedName("lead_count")
    val leadCount: Int? = null,

    @field:SerializedName("duration")
    val duration: Int? = null,

    @field:SerializedName("is_fake_location_detected")
    val isFakeLocationDetected: Boolean? = null,

    @field:SerializedName("customer_count")
    val customerCount: Int? = null,

    @field:SerializedName("last_live_location")
    val lastLiveLocation: LastLiveLocationModel? = null
) : Parcelable

@Parcelize
data class LastLiveLocationModel(
    @field:SerializedName("lat")
    var lat: Double? = 0.0,
    @field:SerializedName("long")
    var long: Double? = 0.0
) : Parcelable


