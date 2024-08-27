package com.app.rupyz.model_kt

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class PostalOfficeResponseModel(
	@field:SerializedName("Status")
	var status: String? = null,

	@field:SerializedName("Message")
	var message: String? = null,

	@field:SerializedName("PostOffice")
	val postOffice: List<PostOfficeItem>? = null
) : Parcelable

@Parcelize
data class PostOfficeItem(

	@field:SerializedName("Circle")
	val circle: String? = null,

	@field:SerializedName("Description")
	val description: String? = null,

	@field:SerializedName("BranchType")
	val branchType: String? = null,

	@field:SerializedName("State")
	val state: String? = null,

	@field:SerializedName("DeliveryStatus")
	val deliveryStatus: String? = null,

	@field:SerializedName("Region")
	val region: String? = null,

	@field:SerializedName("Country")
	val country: String? = null,

	@field:SerializedName("Division")
	val division: String? = null,

	@field:SerializedName("District")
	val district: String? = null,

	@field:SerializedName("Name")
	val name: String? = null
) : Parcelable
