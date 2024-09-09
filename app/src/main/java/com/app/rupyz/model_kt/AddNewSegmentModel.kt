package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddNewSegmentModel(

	@field:SerializedName("discount_unit")
	var discountUnit: String? = null,

	@field:SerializedName("name")
	var name: String? = null,

	@field:SerializedName("discount_value")
	var discountValue: Double? = null
) : Parcelable
