package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DailySalesReportResponseModel(

    @field:SerializedName("data")
    val data: DailySalesReportData? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null
) : Parcelable

@Parcelize
data class DailySalesReportData(

    @field:SerializedName("date")
    val date: String? = null,

    @field:SerializedName("beat_list")
    val beatList: List<NameAndIdSetInfoModel>? = null,

    @field:SerializedName("tc_visited_customer_ids")
    val tcVisitedCustomerIds: List<Int>? = null,

    @field:SerializedName("tc_visited_lead_ids")
    val tcVisitedLeadIds: List<Int>? = null,

    @field:SerializedName("new_customer_data")
    val newCustomerData: NewCustomerData? = null,

    @field:SerializedName("user_name")
    val userName: String? = null,

    @field:SerializedName("profile_pic_url")
    val profilePicUrl: String? = null,

    @field:SerializedName("distributor")
    val distributor: String? = null,

    @field:SerializedName("pc_visited_customer_ids")
    val pcVisitedCustomerIds: List<Int?>? = null,

    @field:SerializedName("customer_metrics")
    val customerMetrics: List<CategoryMetricsItem>? = null,

    @field:SerializedName("beat_name")
    val beatName: String? = null,

    @field:SerializedName("total_activity_count")
    val totalActivityCount: Int? = null,

    @field:SerializedName("new_lead_ids")
    val newLeadIds: List<Int>? = null,

    @field:SerializedName("category_metrics")
    val categoryMetrics: List<CategoryMetricsItem>? = null,

    @field:SerializedName("organization")
    val organization: Int? = null,

    @field:SerializedName("total_order_count")
    val totalOrderCount: Int? = null,

    @field:SerializedName("distributor_name")
    val distributorName: String? = null,

    @field:SerializedName("total_order_value")
    val totalOrderValue: Double? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("user")
    val user: Int? = null,

    @field:SerializedName("product_metrics")
    val productMetrics: List<ProductMetricsItem>? = null
) : Parcelable

@Parcelize
data class NewCustomerData(

    @field:SerializedName("LEVEL-1")
    val lEVEL1: LEVEL1? = null,

    @field:SerializedName("LEVEL-2")
    val lEVEL2: LEVEL2? = null,

    @field:SerializedName("LEVEL-3")
    val lEVEL3: LEVEL3? = null
) : Parcelable

@Parcelize
data class LEVEL2(

    @field:SerializedName("count")
    val count: Int? = null,

    @field:SerializedName("customer_ids")
    val customerIds: List<Int>? = null
) : Parcelable

@Parcelize
data class ProductMetricsItem(

    @field:SerializedName("unit")
    val unit: String? = null,

    @field:SerializedName("amount")
    val amount: Double? = null,

    @field:SerializedName("qty")
    val qty: Double? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
) : Parcelable

@Parcelize
data class CategoryMetricsItem(

    @field:SerializedName("amount")
    val amount: Double? = null,

    @field:SerializedName("qty")
    val qty: Double? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("customer_name")
    val customerName: String? = null,

    @field:SerializedName("customer_id")
    val customerId: Int? = null
) : Parcelable

@Parcelize
data class LEVEL1(

    @field:SerializedName("count")
    val count: Int? = null,

    @field:SerializedName("customer_ids")
    val customerIds: List<Int>? = null
) : Parcelable

@Parcelize
data class LEVEL3(

    @field:SerializedName("count")
    val count: Int? = null,

    @field:SerializedName("customer_ids")
    val customerIds: List<Int>? = null
) : Parcelable
