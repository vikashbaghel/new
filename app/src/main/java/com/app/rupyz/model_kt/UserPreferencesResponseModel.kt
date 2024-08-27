package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserPreferencesResponseModel(

	@field:SerializedName("data")
	val data: UserPreferenceData? = null,

	@field:SerializedName("message")
	var message: String? = null,

	@field:SerializedName("error")
	var error: Boolean? = null
) : Parcelable

@Parcelize
data class UserPreferenceData(

	@field:SerializedName("push_notifications")
	var pushNotifications: Boolean? = null,

	@field:SerializedName("user_type")
	val userType: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("whatsapp_opt_in")
	val whatsappOptIn: Boolean? = null,

	@field:SerializedName("organization")
	val organization: Int? = null,

	@field:SerializedName("whatsapp_emi")
	var whatsappEmi: Boolean? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("created_by_name")
	val createdByName: String? = null,

	@field:SerializedName("user")
	val user: Int? = null,

	@field:SerializedName("created_by")
	val createdBy: Int? = null
) : Parcelable
