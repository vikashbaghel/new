package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StaffCurrentlyActiveTargetResponseModel(

	@field:SerializedName("data")
	val data: StaffCurrentlyActiveDataModel? = null,

	@field:SerializedName("message")
	var message: String? = null,

	@field:SerializedName("error")
    var error: Boolean? = null
) : Parcelable

@Parcelize
data class StaffUpcomingAndClosedTargetResponseModel(

	@field:SerializedName("data")
	val data: List<StaffCurrentlyActiveDataModel>? = null,

	@field:SerializedName("message")
	var message: String? = null,

	@field:SerializedName("error")
    var error: Boolean? = null

) : Parcelable

@Parcelize
data class StaffCurrentlyActiveDataModel(

	@field:SerializedName("end_date")
	val endDate: String? = null,

	@field:SerializedName("user_name")
	val userName: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("created_by_name")
	val createdByName: String? = null,

	@field:SerializedName("current_new_leads")
	val currentNewLeads: Int? = null,

	@field:SerializedName("current_customer_visits")
	val currentCustomerVisits: Int? = null,

	@field:SerializedName("target_payment_collection")
	val targetPaymentCollection: Double? = null,

	@field:SerializedName("updated_by_name")
	val updatedByName: String? = null,

	@field:SerializedName("current_new_customers")
	val currentNewCustomers: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("target_sales_amount")
	val targetSalesAmount: Double? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("current_payment_collection")
	val currentPaymentCollection: Double? = null,

	@field:SerializedName("start_date")
	val startDate: String? = null,

	@field:SerializedName("is_active")
	val isActive: Boolean? = null,

	@field:SerializedName("recurring")
	val recurring: Boolean? = null,

	@field:SerializedName("created_by")
	val createdBy: Int? = null,

	@field:SerializedName("duration_string")
	val durationString: String? = null,

	@field:SerializedName("target_new_customers")
	val targetNewCustomers: Int? = null,

	@field:SerializedName("current_sales_amount")
	val currentSalesAmount: Double? = null,

	@field:SerializedName("organization")
	val organization: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("updated_by")
	val updatedBy: Int? = null,

	@field:SerializedName("target_new_leads")
	val targetNewLeads: Int? = null,

	@field:SerializedName("user")
	val user: Int? = null,

	@field:SerializedName("target_customer_visits")
	val targetCustomerVisits: Int? = null,

	@field:SerializedName("product_metrics")
	val productMetrics: List<ProductMetricsModel>? = null
) : Parcelable

@Parcelize
data class ProductMetricsModel(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("target_value")
	val targetValue: Double? = null,

	@field:SerializedName("current_value")
	val currentValue: Double? = null,

	@field:SerializedName("name")
	var name: String? = null,

	@field:SerializedName("unit")
	var unit: String? = null,

	@field:SerializedName("type")
	var type: String? = null

) : Parcelable
