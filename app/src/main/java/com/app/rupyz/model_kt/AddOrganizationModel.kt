package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddOrganizationModel(
    @field:SerializedName("pan_id")
    var panId: String? = null,

    @field:SerializedName("mobile")
    var mobile: String? = null,

    @field:SerializedName("legal_name")
    var legalName: String? = null,

    @field:SerializedName("otp")
    var otp: String? = null,

    @field:SerializedName("otp_ref")
    var otpRef: String? = null
) : Parcelable