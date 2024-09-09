package com.app.rupyz.model_kt

import com.google.gson.annotations.SerializedName

data class TopProductResponseModel(
	@field:SerializedName("data")
	val data: List<TopProductDataItem>? = null,

	@field:SerializedName("message")
	var message: String? = null,

	@field:SerializedName("error")
    var error: Boolean? = null
)

data class TopProductDataItem(

	@field:SerializedName("total_price")
	val totalPrice: Double? = null,

	@field:SerializedName("dispatch_qty")
	val dispatchQty: Double? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("category")
	val category: String? = null
)
