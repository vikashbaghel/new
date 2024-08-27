package com.app.rupyz.model_kt

import android.os.Parcelable
import com.app.rupyz.model_kt.order.sales.UpdateMappingModel
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddBeatModel(
    @field:SerializedName("id")
    var id: Int? = null,

    @field:SerializedName("name")
    var name: String? = null,

    @field:SerializedName("locality")
    var locality: String? = null,

    @field:SerializedName("parent_customer")
    var parentCustomer: Int? = null,

    var parentCustomerName: String? = null,

    @field:SerializedName("select_staff")
    var selectStaff: UpdateMappingModel? = null,

    @field:SerializedName("select_customer")
    var selectCustomer: BeatCustomerResponseModel? = null,

    var isUpdate: Boolean? = null,

    @field:SerializedName("allow_all")
    var allowAll: Boolean? = null,

    var isFirstTime: Boolean? = null

    ) : Parcelable
