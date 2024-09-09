package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PreferencesResponseModel(
    @field:SerializedName("data")
    val data: PreferenceData? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null,

    var errorCode: Int? = null
) : Parcelable

@Parcelize
data class PreferenceData(

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("organization")
    val organization: Int? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("minimum_order_amount")
    var minimumOrderAmount: Int? = null,

    @field:SerializedName("staff_customer_mapping")
    var staffCustomerMapping: Boolean? = null,

    @field:SerializedName("location_tracking")
    var locationTracking: Boolean? = null,

    @field:SerializedName("live_location_tracking")
    var liveLocationTracking: Boolean? = null,

    @field:SerializedName("activity_geo_fencing")
    var activityGeoFencing: Boolean? = null,

    @field:SerializedName("disable_gallery_photo")
    var disableGalleryPhoto: Boolean? = null,

    @field:SerializedName("activity_check_in_required")
    var activityCheckInRequired: Boolean? = null,

    @field:SerializedName("activity_check_in_image_required")
    var activityCheckInImageRequired: Boolean? = null,

    @field:SerializedName("activity_check_in_show_image_input")
    var activityCheckInShowImageInput: Boolean? = null,

    @field:SerializedName("enable_roles_permission")
    var enableRolesPermission: Boolean? = null,

    @field:SerializedName("daily_report_email_addresses")
    var dailyReportEmailAddresses: ArrayList<String>? = null,

    @field:SerializedName("daily_report_whatsapp_mobiles")
    var dailyReportWhatsappMobiles: ArrayList<String>? = null,

    @field:SerializedName("weekly_report_email_addresses")
    var weeklyReportEmailAddresses: ArrayList<String>? = null,

    @field:SerializedName("weekly_report_whatsapp_mobiles")
    var weeklyReportWhatsappMobiles: ArrayList<String>? = null,

    @field:SerializedName("monthly_report_email_addresses")
    var monthlyReportEmailAddresses: ArrayList<String>? = null,

    @field:SerializedName("monthly_report_whatsapp_mobiles")
    var monthlyReportWhatsappMobiles: ArrayList<String>? = null,

    @field:SerializedName("enable_analytics_calculation")
    var enableAnalyticsCalculation: Boolean? = null,

    @field:SerializedName("enable_customer_level_order")
    var enableCustomerLevelOrder: Boolean? = null,

    @field:SerializedName("enable_customer_category_mapping")
    var enableCustomerCategoryMapping: Boolean? = null,

    @field:SerializedName("auto_assign_customers_in_hierarchy")
    var autoAssignCustomersInHierarchy: Boolean? = null,

    @field:SerializedName("block_screenshots_in_products")
    var blockScreenshotsInProducts: Boolean? = null,

    @field:SerializedName("auto_approve_orders")
    var autoApproveOrders: Boolean? = null,

    @field:SerializedName("auto_dispatch_orders")
    var autoDispatchOrders: Boolean? = null,

    @field:SerializedName("allow_offline_mode")
    var allowOfflineMode: Boolean? = null,

    @field:SerializedName("enable_hierarchy_management")
    var enableHierarchyManagement: Boolean? = null,

    @field:SerializedName("auto_approve_beat_plan")
    var autoApproveBeatPlan: Boolean? = null,
    
    @field:SerializedName("activity_allow_telephonic_order")
    var activityAllowTelephonicOrder: Boolean? = null,

    @field:SerializedName("attendance_start_day_image_required")
    var mandatePhotoOnStartDay: Boolean? = null,

    @field:SerializedName("attendance_end_day_image_required")
    var mandatePhotoOnEndDay: Boolean? = null,

    @field:SerializedName("customer_level_config")
    var customerLevelConfig: CustomerLevelConfigModel? = null,

    ) : Parcelable

@Parcelize
data class CustomerLevelConfigModel(
    @field:SerializedName("LEVEL-1")
    var LEVEL_1: String? = null,

    @field:SerializedName("LEVEL-2")
    var LEVEL_2: String? = null,

    @field:SerializedName("LEVEL-3")
    var LEVEL_3: String? = null
) : Parcelable
