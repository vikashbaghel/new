package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoginModel(

	@field:SerializedName("access_type")
	var accessType: String? = null,

	@field:SerializedName("otp_ref")
	var otpRef: String? = null,

	@field:SerializedName("preferences")
	var preferences: WhatsAppPreferences? = null,

	@field:SerializedName("is_smart_match")
	var isSmartMatch: Boolean? = null,

	@field:SerializedName("otp")
    var otp: String? = null,

	@field:SerializedName("terms_condition")
    var termsCondition: Boolean? = null,

	@field:SerializedName("username")
	var username: String? = null
) : Parcelable

@Parcelize
data class WhatsAppPreferences(

	@field:SerializedName("WHATSAPP_OTP_IN")
	var whatsappOtpIn: Boolean? = null
) : Parcelable
