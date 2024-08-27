package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomerWiseSalesResponseModel(

	@field:SerializedName("data")
	val data: List<CustomerWiseSalesDataItem>? = null,

	@field:SerializedName("message")
	var message: String? = null,

	@field:SerializedName("error")
	var error: Boolean? = null
) : Parcelable

@Parcelize
data class CustomerWiseSalesDataItem(

	@field:SerializedName("total_amount_payment_received")
	val totalAmountPaymentReceived: Double? = null,

	@field:SerializedName("total_amount_dispatched")
	val totalAmountDispatched: Double? = null,

	@field:SerializedName("total_count_orders")
	val totalCountOrders: Int? = null,

	@field:SerializedName("customer_name")
	val customerName: String? = null,

	@field:SerializedName("customer_id")
	val customerId: Int? = null,

	@field:SerializedName("total_amount_sales")
	val totalAmountSales: Double? = null
) : Parcelable
