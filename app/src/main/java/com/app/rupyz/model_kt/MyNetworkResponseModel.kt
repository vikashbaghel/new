package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MyNetworkResponseModel(

	@field:SerializedName("data")
	val data: MyNetworkData? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("error")
	val error: Boolean? = null
) : Parcelable

@Parcelize
data class ResultsItem(

	@field:SerializedName("compliance_rating")
	val complianceRating: Double? = null,

	@field:SerializedName("short_description")
	val shortDescription: String? = null,

	@field:SerializedName("target_id")
	val targetId: Int? = null,

	@field:SerializedName("legal_name")
	val legalName: String? = null,

	@field:SerializedName("logo_image")
	val logoImage: String? = null,

	@field:SerializedName("slug")
	val slug: String? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class MyNetworkData(

	@field:SerializedName("active_connection_request_sent")
	val activeConnectionRequestSent: Int? = null,

	@field:SerializedName("connections_count")
	val connectionsCount: Int? = null,

	@field:SerializedName("results")
	val results: List<ResultsItem?>? = null,

	@field:SerializedName("active_connection_request_received")
	val activeConnectionRequestReceived: Int? = null
) : Parcelable
