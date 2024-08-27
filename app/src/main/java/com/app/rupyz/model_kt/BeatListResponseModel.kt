package com.app.rupyz.model_kt

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class BeatListResponseModel(

    @field:SerializedName("data")
    val data: List<BeatListDataItem>? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null,

    var errorCode: Int? = null

) : Parcelable

@Parcelize
data class BeatListDataItem(

    @field:SerializedName("parent_customer_name")
    val parentCustomerName: String? = null,

    @field:SerializedName("locality")
    val locality: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("parent_customer_logo_image_url")
    val parentCustomerLogoImageUrl: String? = null,

    @field:SerializedName("parent_customer_level")
    val parentCustomerLevel: String? = null,

    @field:SerializedName("created_by_name")
    val createdByName: String? = null,

    @field:SerializedName("created_by")
    val createdBy: Int? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("organization")
    val organization: Int? = null,

    @field:SerializedName("staff_count")
    val staffCount: Int? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("updated_by")
    val updatedBy: Int? = null,

    @field:SerializedName("allow_all")
    val allowAll: Boolean? = null,

    @field:SerializedName("parent_customer")
    val parentCustomer: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("customer_count")
    val customerCount: Int? = null
) : Parcelable

@Parcelize
data class BeatDetailsResponseModel(

    @field:SerializedName("data")
    val data: BeatListDataItem? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null
) : Parcelable
