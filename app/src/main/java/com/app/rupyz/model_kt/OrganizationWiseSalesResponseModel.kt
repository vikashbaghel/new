package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrganizationWiseSalesResponseModel(

	@field:SerializedName("data")
	val data: ArrayList<OrganizationWiseSalesDataItem>? = null,

	@field:SerializedName("message")
	var message: String? = null,

	@field:SerializedName("error")
	var error: Boolean? = null
) : Parcelable

@Parcelize
data class OrganizationWiseSalesDataItem(

	@field:SerializedName("amount_payment_received")
	val totalAmountPaymentReceived: Double? = null,

	@field:SerializedName("count_orders")
	val totalCountOrders: Int? = null,

	@field:SerializedName("year")
	val year: Int? = null,

	@field:SerializedName("month")
	val month: Int? = null,

	@field:SerializedName("user_name")
	val userName: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("amount_sales")
	val totalAmountSales: Double? = null
) : Parcelable
