package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NetworkConnectResponseModel(

	@field:SerializedName("data")
	val data: ConnectionData? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("error")
	val error: Boolean? = null
) : Parcelable

@Parcelize
data class ConnectionData(

	@field:SerializedName("active_connection_request_sent")
	val activeConnectionRequestSent: Int? = null,

	@field:SerializedName("connections_count")
	val connectionsCount: Int? = null,

	@field:SerializedName("results")
	val results: List<String>? = null,

	@field:SerializedName("active_connection_request_received")
	val activeConnectionRequestReceived: Int? = null
) : Parcelable
