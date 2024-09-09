package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeviceActivityLogsPostModel(

    @field:SerializedName("device_activity_list")
    var deviceActivityList: ArrayList<DeviceActivityListItem?>? = null
) : Parcelable

@Parcelize
data class DeviceActivityLogsResponseModel(

    @field:SerializedName("data")
    var data: ArrayList<DeviceActivityListItem>? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null
) : Parcelable

@Parcelize
data class DeviceActivityListItem(

    @field:SerializedName("activity_timestamp")
    var activityTimeStamp: String? = null,

    @field:SerializedName("activity_type")
    var activityType: String? = null,

    @field:SerializedName("location_permission_type")
    var locationPermissionType: String? = null,

    @field:SerializedName("mock_location")
    var mockLocation: Boolean? = null,

    @field:SerializedName("battery_percent")
    var batteryPercent: Int? = null,

    @field:SerializedName("device_information")
    var deviceInformation: DeviceInformationModel? = null,

    @field:SerializedName("is_system_power_saving")
    var isSystemPowerSaving: Boolean? = null,

    @field:SerializedName("is_app_power_saving")
    var isAppPowerSaving: Boolean? = null,

    @field:SerializedName("location_permission")
    var locationPermission: Boolean? = null,

    @field:SerializedName("internet_status")
    var internetStatus: Boolean? = null
) : Parcelable

@Parcelize
data class LogOutModel(

    @field:SerializedName("device_activity_list")
    var data: ArrayList<DeviceActivityListItem>? = null,

    @field:SerializedName("device_type")
    var deviceType: String? = null,
) : Parcelable

