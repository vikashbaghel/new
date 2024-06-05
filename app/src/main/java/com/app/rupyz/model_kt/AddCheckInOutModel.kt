package com.app.rupyz.model_kt

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.app.rupyz.databse.customer.IntListConverter
import com.app.rupyz.databse.order.PicMapListTypeConverter
import com.app.rupyz.generic.model.product.PicMapModel
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity(tableName = "offline_attendance")
@TypeConverters(IntListConverter::class, PicMapListTypeConverter::class)
@Parcelize
data class AddCheckInOutModel(

        @PrimaryKey(autoGenerate = true)
        var id: Int? = null,

        @field:SerializedName("geo_location_long")
        var geoLocationLong: Double? = null,

        @field:SerializedName("geo_location_lat")
        var geoLocationLat: Double? = null,

        @field:SerializedName("start_day_images")
        var startDayImages: ArrayList<Int>? = null,

        var startDayImagesInfo: ArrayList<PicMapModel>? = null,

        @field:SerializedName("end_day_images")
        var endDayImages: ArrayList<Int>? = null,

        var endDayImagesInfo: ArrayList<PicMapModel>? = null,

        @field:SerializedName("joint_staff_ids")
        var jointStaffIds: ArrayList<Int?>? = null,

        @field:SerializedName("action")
        var action: String? = null,

        @field:SerializedName("start_day_comments")
        var startDayComments: String? = null,

        @field:SerializedName("end_day_comments")
        var endDayComments: String? = null,

        @field:SerializedName("attendance_type")
        var attendanceType: String? = null,

        @field:SerializedName("activity_type")
        var activityType: String? = null,

        @field:SerializedName("created_at")
        var createdAt: String? = null,

        var createdByName: String? = null,

        @field:SerializedName("battery_optimization")
        var batteryOptimisation: Boolean? = null,

        @field:SerializedName("location_permission")
        var locationPermission: Boolean? = null,

        @field:SerializedName("device_information")
        var deviceInformation: DeviceInformationModel? = null,

        @field:SerializedName("battery_percent")
        var batteryPercent: Int? = null,
        
        @field:SerializedName("geo_address")
        var geoAddress: String? = null

) : Parcelable

@Parcelize
data class CheckInOutListResponseModel(

        @field:SerializedName("data")
        var data: List<AddCheckInOutModel>? = null,

        @field:SerializedName("message")
        var message: String? = null,

        @field:SerializedName("error")
        var error: Boolean? = null
) : Parcelable

@Parcelize
data class CheckInOutResponseModel(
        @field:SerializedName("data")
        val data: CheckInOutDataModel? = null,

        @field:SerializedName("message")
        var message: String? = null,

        @field:SerializedName("error")
        var error: Boolean? = null

) : Parcelable

@Parcelize
data class CheckInOutDataModel(
        @field:SerializedName("checkin_time")
        var checkinTime: String? = null,

        @field:SerializedName("checkout_time")
        var checkoutTime: String? = null,

        @field:SerializedName("attendance_type")
        var attendanceType: String? = null,

        @field:SerializedName("total_time")
        var totalTime: Int? = null

) : Parcelable


