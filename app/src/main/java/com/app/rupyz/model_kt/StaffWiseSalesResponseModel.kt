package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StaffWiseSalesResponseModel(

	@field:SerializedName("data")
	val data: List<StaffWiseSalesDataItem>? = null,

	@field:SerializedName("message")
	var message: String? = null,

	@field:SerializedName("error")
	var error: Boolean? = null
) : Parcelable

@Parcelize
data class StaffWiseSalesDataItem(

	@field:SerializedName("total_amount_payment_received")
	val totalAmountPaymentReceived: Double? = null,

	@field:SerializedName("total_amount_dispatched")
	val totalAmountdispatched: Double? = null,

	@field:SerializedName("total_count_orders")
	val totalCountOrders: Int? = null,

	@field:SerializedName("user_name")
	val userName: String? = null,

	@field:SerializedName("staff_id")
	val staffId: Int? = null,

	@field:SerializedName("total_amount_sales")
	val totalAmountSales: Double? = null
) : Parcelable
