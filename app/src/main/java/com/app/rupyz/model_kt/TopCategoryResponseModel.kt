package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TopCategoryResponseModel(

	@field:SerializedName("data")
	val data: List<TopCategoryDataItem>? = null,

	@field:SerializedName("message")
	var message: String? = null,

	@field:SerializedName("error")
	var error: Boolean? = null
) : Parcelable

@Parcelize
data class TopCategoryDataItem(

	@field:SerializedName("total_price")
	val totalPrice: Double? = null,

	@field:SerializedName("category")
	val category: String? = null
) : Parcelable
