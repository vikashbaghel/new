package com.app.rupyz.model_kt

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.app.rupyz.databse.customer.DeviceInfoTypeConverter
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LeadListResponseModel(

        @field:SerializedName("data")
        var data: List<LeadLisDataItem>? = null,

        @field:SerializedName("message")
        var message: String? = null,

        @field:SerializedName("error")
        var error: Boolean? = null,

        var errorCode: Int? = null
) : Parcelable

@Parcelize
data class AddLeadResponseModel(

        @field:SerializedName("data")
        var data: LeadLisDataItem? = null,

        @field:SerializedName("message")
        var message: String? = null,

        @field:SerializedName("error")
        var error: Boolean? = null,

        var errorCode: Int? = null
) : Parcelable

@Entity(tableName = "lead_table")
@TypeConverters(DeviceInfoTypeConverter::class)
@Parcelize
data class LeadLisDataItem(

        @field:SerializedName("business_name")
        var businessName: String? = null,

        @field:SerializedName("pincode")
        var pincode: String? = null,

        @field:SerializedName("comments")
        var comments: String? = null,

        @field:SerializedName("city")
        var city: String? = null,

        @field:SerializedName("mobile")
        var mobile: String? = null,

        @field:SerializedName("alternate_mobile")
        var alternateMobile: String? = null,

        @field:SerializedName("created_at")
        var createdAt: String? = null,

        @field:SerializedName("gstin")
        var gstin: String? = null,

        @field:SerializedName("is_details_send_to_user")
        var isDetailsSendToUser: Boolean? = null,

        @field:SerializedName("created_by_name")
        var createdByName: String? = null,

        @field:SerializedName("created_by")
        var createdBy: Int? = null,

        @field:SerializedName("updated_at")
        var updatedAt: String? = null,

        @field:SerializedName("organization")
        var organization: Int? = null,

        @field:SerializedName("address_line_1")
        var addressLine1: String? = null,

        @PrimaryKey
        @field:SerializedName("id")
        var id: Int? = null,

        @field:SerializedName("designation")
        var designation: String? = null,

        @field:SerializedName("email")
        var email: String? = null,

        @field:SerializedName("follow_update")
        var follow_update: String? = null,

        @field:SerializedName("contact_person_name")
        var contactPersonName: String? = null,

        @field:SerializedName("state")
        var state: String? = null,

        @field:SerializedName("address_line_2")
        var addressLine2: String? = null,

        @field:SerializedName("contact_person_designation")
        var contactPersonDesignation: String? = null,

        @field:SerializedName("lead_category")
        var leadCategory: Int? = null,

        @field:SerializedName("lead_category_name")
        var leadCategoryName: String? = null,

        @field:SerializedName("source")
        var source: String? = null,

        @field:SerializedName("logo_image")
        var imageLogo: Int? = null,

        @field:SerializedName("logo_image_url")
        var logoImageUrl: String? = null,

        var logoImagePath: String? = null,

        @field:SerializedName("status")
        var status: String? = null,

        @field:SerializedName("geo_location_long")
        var geoLocationLong: Double? = null,

        @field:SerializedName("geo_location_lat")
        var geoLocationLat: Double? = null,

        @field:SerializedName("map_location_lat")
        var mapLocationLat: Double? = null,

        @field:SerializedName("map_location_long")
        var mapLocationLong: Double? = null,

        @field:SerializedName("geo_address")
        var geoAddress: String? = null,
        
        @field:SerializedName("activity_geo_address")
        var activityGeoAddress: String? = null,

        var isSyncedToServer: Boolean? = null,

        @field:SerializedName("battery_optimization")
        var batteryOptimisation: Boolean? = null,

        @field:SerializedName("location_permission")
        var locationPermission: Boolean? = null,

        @field:SerializedName("device_information")
        var deviceInformation: DeviceInformationModel? = null,

        @field:SerializedName("battery_percent")
        var batteryPercent: Int? = null,
        

) : Parcelable
