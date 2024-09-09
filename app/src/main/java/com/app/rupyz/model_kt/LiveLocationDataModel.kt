package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LiveLocationListModel(
        @field:SerializedName("batch_id")
        var batchId: Int? = null,
        @field:SerializedName("location_data")
        var locationData: ArrayList<LiveLocationDataModel>? = null,
) : Parcelable

@Parcelize
data class LiveLocationDataModel(
        @field:SerializedName("user_id")
        var userId: Int? = null,
        @field:SerializedName("organization_id")
        var organizationId: Int? = null,
        @field:SerializedName("geo_lat")
        var latitude: Double? = null,
        @field:SerializedName("geo_long")
        var longitude: Double? = null,
        @field:SerializedName("create_timestamp")
        var dateTime: String? = null
) : Parcelable

@Parcelize
data class LiveLocationResponseModel(
        @field:SerializedName("data")
        var data: ArrayList<LiveLocationDataModel>? = null,

        @field:SerializedName("message")
        var message: String? = null,

        @field:SerializedName("error")
        var error: Boolean? = null
) : Parcelable

@Parcelize
data class SaveLiveLocationModel(
        @field:SerializedName("data")
        var data: ArrayList<LiveLocationListModel>? = null,
) : Parcelable


@Parcelize
data class LocationActivityRecordDataModel(
        @field:SerializedName("module_type")
        var moduleType: String? = null,
        @field:SerializedName("sub_module_type")
        var subModuleType: String? = null,
        @field:SerializedName("source")
        var source: String? = null,
        @field:SerializedName("geo_address")
        var geoAddress: String? = null,
        @field:SerializedName("battery_percent")
        var batteryPercent: Int? = null,
        @field:SerializedName("location_permission")
        var locationPermission: Boolean? = null,
        @field:SerializedName("battery_optimization")
        var batteryOptimization: Boolean? = null,
        @field:SerializedName("device_information")
        var deviceInformation: DeviceInformationModel? = null,
        @field:SerializedName("geo_lat")
        var latitude: Double? = null,
        @field:SerializedName("geo_long")
        var longitude: Double? = null,
        @field:SerializedName("create_timestamp")
        var dateTime: String? = null
) : Parcelable