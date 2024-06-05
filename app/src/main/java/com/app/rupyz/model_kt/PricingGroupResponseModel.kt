package com.app.rupyz.model_kt

import com.google.gson.annotations.SerializedName

data class PricingGroupResponseModel(

	@field:SerializedName("data")
	val data: List<NameAndIdSetInfoModel>? = null,

	@field:SerializedName("message")
	var message: String? = null,

	@field:SerializedName("error")
	var error: Boolean? = null
)
