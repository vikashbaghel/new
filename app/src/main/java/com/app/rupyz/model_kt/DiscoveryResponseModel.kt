package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class DiscoveryResponseModel(

    @field:SerializedName("data")
    val data: DiscoveryData? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: Boolean? = null,
)

data class DiscoveryData(

    @field:SerializedName("product")
    val product: List<ProductItem>? = null,

    @field:SerializedName("org")
    val org: List<OrgItem>? = null,
)

data class ProductItem(

    @field:SerializedName("_index")
    val index: String? = null,

    @field:SerializedName("_type")
    val type: String? = null,

    @field:SerializedName("_source")
    val source: ProductSource? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("_score")
    val score: Double? = null,
)

data class OrgItem(

    @field:SerializedName("_index")
    val index: String? = null,

    @field:SerializedName("_type")
    val type: String? = null,

    @field:SerializedName("_source")
    val source: OrgSource? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("_score")
    val score: Double? = null,
)

@Parcelize
data class ProductSource(
    @field:SerializedName("pincode")
    val pincode: String? = null,

    @field:SerializedName("like_count")
    val likeCount: Int? = null,

    @field:SerializedName("city")
    val city: String? = null,

    @field:SerializedName("business_nature")
    val businessNature: String? = null,

    @field:SerializedName("compliance_rating")
    val complianceRating: Double? = null,

    @field:SerializedName("max_price")
    val maxPrice: Int? = null,

    @field:SerializedName("nano_id")
    val nanoId: String? = null,

    @field:SerializedName("min_price")
    val minPrice: Int? = null,

    @field:SerializedName("org_id")
    val orgId: Int? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("org_slug")
    val orgSlug: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("legal_name")
    val legalName: String? = null,

    @field:SerializedName("state")
    val state: String? = null,

    @field:SerializedName("pics")
    val pics: String? = null,

    @field:SerializedName("view_count")
    val viewCount: Int? = null,

    ) : Parcelable

@Parcelize
data class OrgSource(
    @field:SerializedName("compliance_rating")
    val complianceRating: Double? = null,

    @field:SerializedName("pincode")
    val pincode: String? = null,

    @field:SerializedName("like_count")
    val likeCount: Int? = null,

    @field:SerializedName("city")
    val city: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("legal_name")
    val legalName: String? = null,

    @field:SerializedName("state")
    val state: String? = null,

    @field:SerializedName("logo_image")
    val logoImage: String? = null,

    @field:SerializedName("pics")
    val pics: String? = null,

    @field:SerializedName("view_count")
    val viewCount: Int? = null,

    @field:SerializedName("short_description")
    val shortDescription: String? = null,

    @field:SerializedName("business_nature")
    val businessNature: String? = null,

    @field:SerializedName("banner_image")
    val bannerImage: String? = null,

    @field:SerializedName("org_slug")
    val orgSlug: String? = null,

    ) : Parcelable

@Parcelize
data class LocationFilterItem(

    val name: String? = null,
    var isSelected: Boolean,
    var position: Int? = null,

    ) : Parcelable

@Parcelize
data class BadgeFilterItem(

    val name: String? = null,
    var isSelected: Boolean,
    var position: Int? = null,

    ) : Parcelable
