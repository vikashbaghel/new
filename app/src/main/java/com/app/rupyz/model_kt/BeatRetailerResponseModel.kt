package com.app.rupyz.model_kt

import android.os.Parcelable
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BeatRetailerResponseModel(

    @field:SerializedName("add_set")
    var addCustomer: ArrayList<CustomerData>? = null,

    @field:SerializedName("remove_set")
    var removeCustomer: ArrayList<CustomerData>? = null,

) : Parcelable

