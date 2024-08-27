package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class StaffTcPcInfoModel(

    @field:SerializedName("data")
    val data: TeamTcPcRecordsModel? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null,
) : Parcelable

@kotlinx.android.parcel.Parcelize
data class TeamTcPcRecordsModel(

    @field:SerializedName("records")
    val records: List<StaffTcPcInfoModelItem>? = null
) : Parcelable

@Parcelize
data class StaffTcPcInfoModelItem(

    @field:SerializedName("customer_type")
    val customerType: String? = null,

    @field:SerializedName("date")
    val date: String? = null,

    @field:SerializedName("geo_location")
    val geoLocation: String? = null,

    @field:SerializedName("comments")
    val comments: String? = null,

    @field:SerializedName("geo_location_long")
    val geoLocationLong: Double? = null,

    @field:SerializedName("module_type")
    val moduleType: String? = null,

    @field:SerializedName("check_in")
    val checkIn: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("staff")
    val staff: Int? = null,

    @field:SerializedName("created_by")
    val createdBy: Int? = null,

    @field:SerializedName("duration")
    val duration: Int? = null,

    @field:SerializedName("check_out")
    val checkOut: String? = null,

    @field:SerializedName("customer_logo_url")
    val customerLogoUrl: String? = null,

    @field:SerializedName("module_id")
    val moduleId: Int? = null,

    @field:SerializedName("geo_location_lat")
    val geoLocationLat: Double? = null,

    @field:SerializedName("is_pc")
    val isPc: Boolean? = null,

    @field:SerializedName("geo_address")
    val geoAddress: String? = null,

    @field:SerializedName("activity_type")
    val activityType: String? = null,

    @field:SerializedName("organization")
    val organization: Int? = null,

    @field:SerializedName("pics_urls")
    val picsUrls: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("customer_name")
    val customerName: String? = null,

    @field:SerializedName("order_value")
    val orderValue: Double? = null,

    @field:SerializedName("customer")
    val customer: Int? = null
) : Parcelable
