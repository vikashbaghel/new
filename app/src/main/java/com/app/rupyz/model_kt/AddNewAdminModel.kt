package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddNewAdminModel(
    @field:SerializedName("first_name")
    var firstName: String? = null,

    @field:SerializedName("mobile")
    var mobile: String? = null,

    @field:SerializedName("last_name")
    var lastName: String? = null,

    @field:SerializedName("email")
    var email: String? = null,

    @field:SerializedName("otp")
    var otp: String? = null,

    @field:SerializedName("otp_ref")
    var otpRef: String? = null
) : Parcelable