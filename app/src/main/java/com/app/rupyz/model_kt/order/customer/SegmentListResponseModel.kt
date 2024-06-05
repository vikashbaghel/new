package com.app.rupyz.model_kt.order.customer

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SegmentListResponseModel(

	@field:SerializedName("data")
	val data: List<SegmentDataItem>? = null,

	@field:SerializedName("message")
    var message: String? = null,

	@field:SerializedName("error")
    var error: Boolean? = null
) : Parcelable

@Parcelize
data class SegmentDataItem(

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("discount_unit")
	val discountUnit: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("updated_by")
	val updatedBy: String? = null,

	@field:SerializedName("discount_value")
	val discountValue: Double? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

//	@field:SerializedName("created_by")
//	val createdBy: Int? = null
) : Parcelable
