package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BeatCustomerResponseModel(

    @field:SerializedName("add_set")
    var addCustomer: ArrayList<Int>? = null,

    @field:SerializedName("remove_set")
    var removeCustomer: ArrayList<Int>? = null,

    @field:SerializedName("allow_all")
    var selectAllCustomer: Boolean? = null,

    @field:SerializedName("disallow_all")
    var deSelectAllCustomer: Boolean? = null,

    @field:SerializedName("sublevel_set")
    var subLevelSet: ArrayList<Int>? = null,

    var isUpdatedFromSearch: Boolean = false,
    var isPaginationAvailable: Boolean = false,
    var selectedAllCustomerCount: Int? = null,
    var isUpdatedExternally: Boolean = false

) : Parcelable

