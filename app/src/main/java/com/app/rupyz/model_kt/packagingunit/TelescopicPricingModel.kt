package com.app.rupyz.model_kt.packagingunit

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TelescopicPricingModel(

	@field:SerializedName("qty")
	var qty: Double? = null,

	@field:SerializedName("price")
	var price: Double? = null,

) : Parcelable
