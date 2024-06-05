package com.app.rupyz.model_kt.order.customer

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomerAddResponseModel(

        @field: SerializedName("data")
        val data: CustomerData? = null,

        @field:SerializedName("message")
        var message: String? = null,

        @field:SerializedName("error")
        var error: Boolean? = null,

        var errorCode: Int? = null
) : Parcelable

@Parcelize
data class CustomerDeleteOptionModel(

        @field:SerializedName("is_customer_delete")
        var isCustomerDelete: Boolean? = null,

        @field:SerializedName("check_children")
        var checkChildren: Boolean? = null,

        @field:SerializedName("customer_parent_id")
        var customerParentId: Int? = null

) : Parcelable

@Parcelize
data class CustomerDeleteResponseModel(

        @field: SerializedName("data")
        val data: CustomerDeleteData? = null,

        @field:SerializedName("message")
        var message: String? = null,

        @field:SerializedName("error")
        var error: Boolean? = null

) : Parcelable

@Parcelize
data class CustomerDeleteData(

        @field:SerializedName("id")
        var id: Int? = null,

        @field:SerializedName("is_used")
        var isUsed: Boolean? = null,

        @field:SerializedName("child_count")
        var childCount: Int? = null

) : Parcelable


