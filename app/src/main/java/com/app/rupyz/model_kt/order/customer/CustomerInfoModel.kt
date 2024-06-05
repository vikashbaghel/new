package com.app.rupyz.model_kt.order.customer

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.app.rupyz.databse.customer.CustomerTypeConverters
import com.app.rupyz.databse.customer.DeviceInfoTypeConverter
import com.app.rupyz.databse.customer.IntListConverter
import com.app.rupyz.model_kt.DeviceInformationModel
import com.app.rupyz.model_kt.NameAndIdSetInfoModel
import com.app.rupyz.model_kt.order.sales.UpdateMappingModel
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomerInfoModel(

        @field: SerializedName("data")
        var data: List<CustomerData>? = null,

        @field:SerializedName("message")
        var message: String? = null,

        @field:SerializedName("error")
        var error: Boolean? = null,

        var errorCode: Int? = null
) : Parcelable

@Entity(tableName = "customer_table")
@TypeConverters(IntListConverter::class,
        CustomerTypeConverters::class,
        DeviceInfoTypeConverter::class)
@Parcelize
data class CustomerData(

        @PrimaryKey
        @field:SerializedName("id")
        var id: Int? = null,

        @field:SerializedName("lead")
        var lead: Int? = null,

        var beatId: Int? = null,

        @field:SerializedName("name")
        var name: String? = null,

        @field:SerializedName("mobile")
        var mobile: String? = null,

        @field:SerializedName("customer_level")
        var customerLevel: String? = null,

        @field:SerializedName("customer_level_name")
        var customerLevelName: String? = null,

        @field:SerializedName("customer_parent")
        var customerParent: Int? = null,

        @field:SerializedName("level_3_customer_count")
        var level_3_customer_count: Int? = null,

        @field:SerializedName("level_2_customer_count")
        var level_2_customer_count: Int? = null,

        @field:SerializedName("customer_parent_name")
        var customerParentName: String? = null,

        @field:SerializedName("pricing_group_name")
        var pricingGroupName: String? = null,

        @field:SerializedName("pricing_group")
        var pricingGroup: Int? = null,

        @field:SerializedName("customer_count")
        var customerCount: Int? = null,

        @field:SerializedName("credit_limit")
        var creditLimit: Double? = null,

        @field:SerializedName("outstanding_amount")
        var outstandingAmount: Double? = null,

        @field:SerializedName("total_amount_sales")
        var totalAmountSales: Double? = null,

        @field:SerializedName("total_payment_amount_received")
        var totalPaymentAmountReceived: Double? = null,

        @field:SerializedName("last_order_date")
        var lastOrderDate: String? = "",

        @field:SerializedName("address_line_1")
        var addressLine1: String? = "",

        @field:SerializedName("state")
        var state: String? = "",

        @field:SerializedName("city")
        var city: String? = "",

        @field:SerializedName("pincode")
        var pincode: String? = "",

        @field:SerializedName("customer_type")
        var customer_type: String? = "",

        @field:SerializedName("distributor_id")
        var distributor_id: Int? = null,

        @field:SerializedName("contact_person_name")
        var contactPersonName: String? = "",

        @field:SerializedName("pan_id")
        var panId: String? = "",

        @field:SerializedName("gstin")
        var gstin: String? = "",

        @field:SerializedName("email")
        var email: String? = "",

        @field:SerializedName("payment_term")
        var paymentTerm: String? = "",

        @field:SerializedName("logo_image")
        var imageLogo: Int? = null,

        @field:SerializedName("product_category")
        var productCategory: ArrayList<Int?>? = null,

        @Ignore
        @field:SerializedName("beats")
        var beats: List<Int>? = null,

        @Ignore
        @field:SerializedName("beat_list")
        var beat_list: List<String>? = null,

        @field:SerializedName("logo_image_url")
        var logoImageUrl: String? = null,

        @field:SerializedName("segment_name")
        var segment_name: String? = null,

        @field:SerializedName("segment_discount_unit")
        var segment_discount_unit: String? = null,

        @field:SerializedName("segment_discount_value")
        var segment_discount_value: Double? = null,

        @field:SerializedName("whatsapp_opt")
        var whatsappOpt: Boolean? = false,

        @Ignore
        @field:SerializedName("add_set")
        var addStaffSet: List<Int>? = null,

        @Ignore
        @field:SerializedName("remove_set")
        var removeStaffSet: List<Int>? = null,

        @Ignore
        @field:SerializedName("exclude_set")
        var exclude_set: List<Int>? = null,

        @Ignore
        @field:SerializedName("add_cat")
        var addCat: List<Int>? = null,

        @Ignore
        @field:SerializedName("remove_cat")
        var removeCat: List<Int>? = null,

        @Ignore
        @field:SerializedName("staff_set_info")
        var staffSetInfo: List<NameAndIdSetInfoModel>? = null,

        @field:SerializedName("select_category")
        var selectCategory: UpdateMappingModel? = null,

        @field:SerializedName("select_staff")
        var selectStaff: UpdateMappingModel? = null,

        @field:SerializedName("select_beat")
        var selectBeat: UpdateMappingModel? = null,

        @field:SerializedName("allow_all_staff")
        var allow_all_staff: Boolean? = null,

        @field:SerializedName("disallow_all_staff")
        var disallow_all_staff: Boolean? = null,

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

        @field:SerializedName("created_at")
        var createdAt: String? = null,

        @field:SerializedName("source")
        var source: String? = null,

        var isSyncedToServer: Boolean? = null,

        @field:SerializedName("is_selected")
        var isSelected: Boolean? = false,

        var isPartOfParentCustomer: Boolean? = false,

        var errorMessage: String? = null,

        @field:SerializedName("battery_optimization")
        var batteryOptimisation: Boolean? = null,

        @field:SerializedName("location_permission")
        var locationPermission: Boolean? = null,

        @field:SerializedName("device_information")
        var deviceInformation: DeviceInformationModel? = null,

        @field:SerializedName("battery_percent")
        var batteryPercent: Int? = null
        
) : Parcelable


@Parcelize
data class CustomerDataWhatsAppOpt(
        @field:SerializedName("customer_consent")
        var customerConsent: Boolean? = false
) : Parcelable

@Parcelize
data class CustomerListWithStaffMappingModel(

        @field: SerializedName("data")
        val data: List<NameAndIdSetInfoModel>? = null,

        @field:SerializedName("message")
        var message: String? = null,

        @field:SerializedName("error")
        var error: Boolean? = null,
) : Parcelable

@Parcelize
data class CustomerListForBeatModel(

        @field: SerializedName("data")
        val data: List<CustomerData>? = null,

        @field:SerializedName("message")
        var message: String? = null,

        @field:SerializedName("error")
        var error: Boolean? = null,
) : Parcelable


