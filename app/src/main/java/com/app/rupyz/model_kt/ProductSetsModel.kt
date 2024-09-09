package com.app.rupyz.model_kt

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class ProductSetsModel(

	@field:SerializedName("variant_id")
	val variantId: String? = null,

	@field:SerializedName("set_name")
	val setName: String? = null,

	@field:SerializedName("option_id")
	val optionId: String? = null,

	@field:SerializedName("variant_set_data")
	val variantSetData: List<VariantSetDataItem>? = null
) : Parcelable

@Parcelize
data class VariantSetDataItem(

	@field:SerializedName("identifier")
	val identifier: String? = null,

	@field:SerializedName("details")
	val details: Details? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class Details(

	@field:SerializedName("quantity")
	val quantity: Int? = null
) : Parcelable
