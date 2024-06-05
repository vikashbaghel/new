package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GstInfoResponseModel(

	@field:SerializedName("data")
	val data: GstInfoData? = null,

	@field:SerializedName("message")
	var message: String? = null,

	@field:SerializedName("error")
    var error: Boolean? = null
) : Parcelable

@Parcelize
data class GstInfoData(

	@field:SerializedName("pincode")
	val pincode: String? = null,

	@field:SerializedName("registered_address")
	val registeredAddress: String? = null,

	@field:SerializedName("entity_type")
	val entityType: String? = null,

	@field:SerializedName("primary_gstin")
	val primaryGstin: String? = null,

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("gstin_date_of_registration")
	val gstinDateOfRegistration: String? = null,

	@field:SerializedName("address_line_1")
	val addressLine1: String? = null,

	@field:SerializedName("gstin_status")
	val gstinStatus: String? = null,

	@field:SerializedName("legal_name")
	val legalName: String? = null,

	@field:SerializedName("state")
	val state: String? = null,

	@field:SerializedName("trade_name")
	val tradeName: String? = null
) : Parcelable
