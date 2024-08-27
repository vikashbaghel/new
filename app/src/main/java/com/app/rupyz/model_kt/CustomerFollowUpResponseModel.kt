package com.app.rupyz.model_kt

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.app.rupyz.databse.NameAndValueSetInfoListConverter
import com.app.rupyz.databse.customer.DeviceInfoTypeConverter
import com.app.rupyz.databse.customer.IntListConverter
import com.app.rupyz.databse.order.PicMapListTypeConverter
import com.app.rupyz.databse.staff.StaffInfoListConverter
import com.app.rupyz.generic.model.product.PicMapModel
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomerFollowUpResponseModel(

        @field:SerializedName("data")
        var data: CustomerFollowUpDataItem? = null,

        @field:SerializedName("message")
        var message: String? = null,

        @field:SerializedName("error")
        var error: Boolean? = null,

        var errorCode: Int? = null

) : Parcelable

@Parcelize
data class CustomerFollowUpListResponseModel(

        @field:SerializedName("data")
        var data: List<CustomerFollowUpDataItem>? = null,

        @field:SerializedName("message")
        var message: String? = null,

        @field:SerializedName("error")
        var error: Boolean? = null
) : Parcelable

@Parcelize
data class CustomerFollowUpSingleResponseModel(
        
        @field:SerializedName("data")
        var data: CustomerFollowUpDataItem? = null,
        
        @field:SerializedName("message")
        var message: String? = null,
        
        @field:SerializedName("error")
        var error: Boolean? = null
                                            ) : Parcelable

@TypeConverters(IntListConverter::class, PicMapListTypeConverter::class,
        StaffInfoListConverter::class, DeviceInfoTypeConverter::class,
        NameAndValueSetInfoListConverter::class)
@Entity(tableName = "record_activity_table")
@Parcelize
data class CustomerFollowUpDataItem(
        @field:SerializedName("business_name")
        var businessName: String? = null,

        @field:SerializedName("geo_location")
        var geoLocation: String? = null,

        @field:SerializedName("comments")
        var comments: String? = null,

        @field:SerializedName("sub_module_type")
        var subModuleType: String? = null,

        @field:SerializedName("geo_location_long")
        var geoLocationLong: Double? = 0.0,

        @field:SerializedName("module_type")
        var moduleType: String? = null,

        @field:SerializedName("created_at")
        var createdAt: String? = null,

        @field:SerializedName("due_datetime")
        var dueDateTime: String? = null,

        @field:SerializedName("staff")
        var staff: Int? = null,

        @field:SerializedName("created_by_name")
        var createdByName: String? = null,

        @field:SerializedName("address_line_1")
        var addressLine1: String? = "",

        @field:SerializedName("state")
        var state: String? = "",

        @field:SerializedName("city")
        var city: String? = "",

        @field:SerializedName("pincode")
        var pincode: String? = "",

        @field:SerializedName("created_by")
        var createdBy: Int? = null,

        @field:SerializedName("module_id")
        var moduleId: Int? = null,

        @field:SerializedName("sub_module_id")
        var subModuleId: Int? = null,

        @field:SerializedName("updated_at")
        var updatedAt: String? = null,

        @field:SerializedName("geo_location_lat")
        var geoLocationLat: Double? = 0.0,

        @field:SerializedName("organization")
        var organization: Int? = null,

        @field:SerializedName("pics_urls")
        var picsUrls: ArrayList<PicMapModel>? = null,
        
        @field:SerializedName("start_day_images_info")
        var startDayImagesInfo: ArrayList<PicMapModel>? = null,
        
        @field:SerializedName("end_day_images_info")
        var endDayImagesInfo: ArrayList<PicMapModel>? = null,

        @field:SerializedName("action")
        var action: String? = null,

        @PrimaryKey
        @field:SerializedName("id")
        var id: Int? = null,

        @field:SerializedName("customer_name")
        var customerName: String? = null,

        @field:SerializedName("pics")
        var pics: ArrayList<Int>? = null,

        @field:SerializedName("joint_staff_ids_info")
        var jointStaffIdsInfo: ArrayList<JointStaffInfoModel>? = null,

        @field:SerializedName("feedback_type")
        var feedbackType: String? = null,

        @field:SerializedName("message")
        var message: String? = null,

        @field:SerializedName("is_auto_time_out")
        var isAutoTimeOut: Boolean? = null,

        var label: String? = null,

        @field:SerializedName("customer")
        var customer: Int? = null,

        var isCustomerIdUpdated: Boolean? = null,

        @field:SerializedName("battery_optimization")
        var batteryOptimisation: Boolean? = null,

        @field:SerializedName("location_permission")
        var locationPermission: Boolean? = null,

        @field:SerializedName("device_information")
        var deviceInformation: DeviceInformationModel? = null,

        @field:SerializedName("battery_percent")
        var batteryPercent: Int? = null,

        @field:SerializedName("geo_address")
        var geoAddress: String? = null,
        
        @field:SerializedName("time_in")
        var timeIn: String? = null,
        
        @field:SerializedName("time_out")
        var timeOut: String? = null,
        
        @field:SerializedName("attendance_type")
        var attendanceType: String? = null,
        
        @field:SerializedName("activity_type")
        var activityType: String? = null,
        
        @field:SerializedName("start_day_comments")
        var startDayComments: String? = null,
        
        @field:SerializedName("end_day_comments")
        var endDayComments: String? = null,
        
        @field:SerializedName("custom_form_data")
        var customFormData: ArrayList<NameAndValueSetInfoModel>? = null

) : Parcelable

@Parcelize
data class DeviceInformationModel(
        @field:SerializedName("model")
        var model: String? = null,

        @field:SerializedName("os")
        var os: String? = null,

        @field:SerializedName("brand")
        var brand: String? = null,

        @field:SerializedName("manufacturer")
        var manufacturer: String? = null,

        @field:SerializedName("device")
        var device: String? = null,

        @field:SerializedName("product")
        var product: String? = null,

        @field:SerializedName("ram")
        var ram: Long? = null
) : Parcelable


@Parcelize
data class UploadOfflineAttendanceModel(

        @field:SerializedName("attendance_list")
        var attendanceList: ArrayList<OfflineAttendanceModel>? = null,

        @field:SerializedName("message")
        var message: String? = null,

        @field:SerializedName("error")
        var error: Boolean? = null
) : Parcelable

@Parcelize
data class OfflineAttendanceModel(

        @field:SerializedName("Check In")
        var checkIn: CheckInOutModel? = null,

        @field:SerializedName("Check Out")
        var checkOut: CheckInOutModel? = null,

        @field:SerializedName("date")
        var date: String? = null,

        ) : Parcelable

@Parcelize
data class CheckInOutModel(

        @field:SerializedName("geo_location_lat")
        var geoLocationLat: Double? = null,

        @field:SerializedName("geo_location_long")
        var geoLocationLong: Double? = null,

        @field:SerializedName("timestamp")
        var timestamp: String? = null,

        @field:SerializedName("start_day_images")
        var startDayImages: ArrayList<Int>? = null,

        var startDayImagesInfo: ArrayList<PicMapModel>? = null,

        @field:SerializedName("end_day_images")
        var endDayImages: ArrayList<Int>? = null,

        var endDayImagesInfo: ArrayList<PicMapModel>? = null,

        @field:SerializedName("joint_staff_ids")
        var jointStaffIds: ArrayList<Int?>? = null,

        @field:SerializedName("start_day_comments")
        var startDayComments: String? = null,

        @field:SerializedName("end_day_comments")
        var endDayComments: String? = null,

        @field:SerializedName("attendance_type")
        var attendanceType: String? = null,

        @field:SerializedName("activity_type")
        var activityType: String? = null,

        @field:SerializedName("action")
        var action: String? = null,

        var isImageIdListUploaded: Boolean? = null

) : Parcelable

@Parcelize
data class JointStaffInfoModel(

        @field:SerializedName("id")
        var id: Int? = null,

        @field:SerializedName("user_id")
        var userId: Int? = null,

        @field:SerializedName("name")
        var name: String? = null,

        @field:SerializedName("pic_url")
        var picUrl: String? = null
) : Parcelable