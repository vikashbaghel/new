package com.app.rupyz.model_kt

import android.os.Parcelable
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BeatRoutePlanResponseModel(

    @field:SerializedName("data")
    val data: BeatPlanModel? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null
) : Parcelable

@Parcelize
data class BeatRoutePlanListResponseModel(

    @field:SerializedName("data")
    val data: List<BeatPlanModel>? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null,

    var errorCode: Int? = null,
) : Parcelable

@Parcelize
data class BeatPlanModel(

    @field:SerializedName("end_date")
    var endDate: String? = null,

    @field:SerializedName("comments")
    var comments: String? = null,

    @field:SerializedName("visited_days")
    val visitedDays: Int? = null,

    @field:SerializedName("reject_reason")
    var rejectReason: String? = null,

    @field:SerializedName("created_by_name")
    val createdByName: String? = null,

    @field:SerializedName("user_name")
    val user_name: String? = null,

    @field:SerializedName("profile_pic_url")
    val profilePicUrl: String? = null,

    @field:SerializedName("total_customers_visited")
    val totalCustomersVisited: Int? = null,

    @field:SerializedName("no_days_of_plan")
    val noDaysOfPlan: Int? = null,

    @field:SerializedName("organization")
    val organization: Int? = null,

    @field:SerializedName("name")
    var name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("remaining_days")
    val remainingDays: Int? = null,

    @field:SerializedName("user")
    val user: Int? = null,

    @field:SerializedName("start_date")
    var startDate: String? = null,

    @field:SerializedName("is_used")
    var isUsed: Boolean? = null,

    @field:SerializedName("is_active")
    var isActive: Boolean? = null,

    @field:SerializedName("status")
    var status: String? = null
) : Parcelable


@Parcelize
data class BeatRouteDayListModel(

    @field:SerializedName("date")
    var date: String? = null,

    var isUpdate: Boolean? = null,

    var isFirstTime: Boolean? = null,

    @field:SerializedName("is_cancelled")
    var isCancelled: Boolean? = null,

    @field:SerializedName("cancel_reason")
    var cancelReason: String? = null,

    @field:SerializedName("night_stay")
    var nightStay: String? = null,

    @field:SerializedName("beatrouteplan")
    val beatrouteplan: Int? = null,

    @field:SerializedName("module_type")
    var moduleType: String? = null,

    @field:SerializedName("purpose")
    var purpose: String? = null,

    @field:SerializedName("is_active")
    var isActive: Boolean? = null,

    @field:SerializedName("is_duplicate")
    var isDuplicate: Boolean? = null,

    @field:SerializedName("target_leads_count")
    var targetLeadsCount: Int? = null,

    @field:SerializedName("allow_all_customers")
    var allowAllCustomers: Boolean? = null,

    @field:SerializedName("achieved_customers_count")
    var achievedCustomersCount: Int? = null,

    @field:SerializedName("achieved_leads_count")
    var achievedLeadsCount: Int? = null,

    @field:SerializedName("target_customer_ids")
    var targetCustomerIds: ArrayList<Int>? = null,

    @field:SerializedName("beat_name")
    var beatName: String? = null,

    @field:SerializedName("organization")
    val organization: Int? = null,

    @field:SerializedName("beat")
    var beatId: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("module_name")
    var moduleName: String? = null,

    @field:SerializedName("target_customers_count")
    var targetCustomersCount: Int? = null,

    @field:SerializedName("select_day_beat")
    var selectDayBeat: BeatCustomerResponseModel? = null,

    var orgBeatList: List<OrgBeatModel>? = null
) : Parcelable


@Parcelize
data class ActiveBeatRoutePlanResponseModel(

    @field:SerializedName("data")
    val data: ActiveBeatRouteInfoAndDayListModel? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null
) : Parcelable

@Parcelize
data class ActiveBeatRouteInfoAndDayListModel(

    @field:SerializedName("beat_route_info")
    var beatRouteInfo: BeatRouteInfoModel? = null,

    @field:SerializedName("days_list")
    var beatRouteDayPlan: BeatRouteDayListModel? = null

) : Parcelable


@Parcelize
data class CreateBeatRoutePlanModel(

    @field:SerializedName("start_date")
    var startDate: String? = null,

    @field:SerializedName("end_date")
    var endDate: String? = null,

    @field:SerializedName("name")
    var name: String? = null,

    @field:SerializedName("beatplan_duplicate_from_id")
    var beatPlanDuplicateFromId: Int? = null,

    @field:SerializedName("is_active")
    var isActive: Boolean = false,

    @field:SerializedName("beat_route_day_plan")
    var beatRouteDayPlan: ArrayList<BeatRouteDayListModel>? = null,

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
data class BeatRouteDailyPlanResponseModel(

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null,

    @field:SerializedName("data")
    var beatRouteInfoAndDayListModel: BeatRouteInfoAndDayListModel? = null,

    ) : Parcelable

@Parcelize
data class BeatRouteInfoAndDayListModel(

    @field:SerializedName("beat_route_info")
    var beatRouteInfo: BeatPlanModel? = null,

    @field:SerializedName("days_list")
    var beatRouteDayPlan: ArrayList<BeatRouteDayListModel>? = null

) : Parcelable

@Parcelize
data class BeatRouteInfoModel(
    @field:SerializedName("start_date")
    var startDate: String? = null,

    @field:SerializedName("end_date")
    var endDate: String? = null,

    @field:SerializedName("name")
    var name: String? = null,

    @field:SerializedName("status")
    var status: String? = null,

    @field:SerializedName("comments")
    var comments: String? = null

) : Parcelable

@Parcelize
data class BeatRouteCustomerInfoModel(
    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null,

    @field:SerializedName("data")
    var beatRouteCustomerDataModel: BeatRouteCustomerDataModel? = null,

) : Parcelable
@Parcelize
data class BeatRouteCustomerDataModel(
    @field:SerializedName("date")
    var date: String? = null,

    @field:SerializedName("customers_list")
    var customersList: List<CustomerData>? = null,

    @field:SerializedName("leads_list")
    var leadList: List<LeadLisDataItem>? = null,

    ) : Parcelable

