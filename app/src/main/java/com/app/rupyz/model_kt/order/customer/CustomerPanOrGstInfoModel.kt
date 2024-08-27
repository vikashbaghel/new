package com.app.rupyz.model_kt.order.customer

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomerPanOrGstInfoModel(

    @field: SerializedName("data") val data: CustomerPrimaryInfoModel? = null,

    @field:SerializedName("message") var message: String? = null,

    @field:SerializedName("error") var error: Boolean? = null,
) : Parcelable

@Parcelize
data class CustomerPrimaryInfoModel(
    @field:SerializedName("legal_name")
    var legalName: String? = null,
    @field:SerializedName("primary_gstin")
    var primaryGstIn: String? = null,
    @field:SerializedName("pan_id")
    var panId: String? = null,
    @field:SerializedName("name")
    var name: String? = null,
    @field:SerializedName("first_name")
    var firstName: String? = null,
    @field:SerializedName("middle_name")
    var middleName: String? = null,
    @field:SerializedName("last_name")
    var lastName: String? = null,
    @field:SerializedName("email")
    var email: String? = null,
    @field:SerializedName("gender")
    var gender: String? = null,
    @field:SerializedName("dob")
    var dob: String? = null,
    @field:SerializedName("state")
    var state: String? = null,
    @field:SerializedName("city")
    var city: String? = null,
    @field:SerializedName("address_line_1")
    var addressLine1: String? = null,
    @field:SerializedName("address_line_2")
    var addressLine2: String? = null,
    @field:SerializedName("pincode")
    var pincode: String? = null
) : Parcelable
