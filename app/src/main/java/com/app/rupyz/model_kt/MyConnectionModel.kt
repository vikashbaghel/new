package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MyConnectionModel(

	@field:SerializedName("data")
	val data: List<MyConnectionDataItem>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("error")
	val error: Boolean? = null
) : Parcelable

@Parcelize
data class MyConnectionDataItem(

	@field:SerializedName("profile_image")
	val profileImage: String? = null,

	@field:SerializedName("org_id")
	val orgId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("legal_name")
	val legalName: String? = null
) : Parcelable
