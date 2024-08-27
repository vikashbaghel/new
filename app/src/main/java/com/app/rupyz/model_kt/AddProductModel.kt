package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class AddProductModel(

    @field:SerializedName("id")
    var id: Int? = null,

    @field:SerializedName("display_pic")
    var displayPic: Int? = null,

    @field:SerializedName("unit")
    var unit: String? = null,

    @field:SerializedName("mrp_unit")
    var mrpUnit: String? = null,

    @field:SerializedName("packaging_unit")
    var packagingUnit: String? = null,

    @field:SerializedName("max_price")
    var maxPrice: Int? = null,

    @field:SerializedName("min_price")
    var minPrice: Int? = null,

    @field:SerializedName("packaging_size")
    var packagingSize: Int? = null,

    @field:SerializedName("gst")
    var gst: Double? = null,

    @field:SerializedName("is_published")
    var isPublished: Boolean? = null,

    @field:SerializedName("gst_exclusive")
    var gstExclusive: Boolean? = null,

    @field:SerializedName("name")
    var name: String? = null,

    @field:SerializedName("description")
    var description: String? = null,

    var specification: @RawValue Any? = null,

    @field:SerializedName("category")
    var category: String? = null,

    @field:SerializedName("code")
    var code: String? = null,

    @field:SerializedName("hsn_code")
    var hsnCode: String? = null,

    @field:SerializedName("brand")
    var brand: String? = null,

    @field:SerializedName("price")
    var price: Double? = null,

    @field:SerializedName("mrp_price")
    var mrpPrice: Double? = null,

    @field:SerializedName("pics")
    var pics: List<Int?>? = null,

    @field:SerializedName("avaliableStock")
    var avaliableStock: String? = null,

    @field:SerializedName("is_out_of_stock")
    var isOutOfStock: Boolean? = null,

    @field:SerializedName("packaging_level")
    var packagingLevel: ArrayList<PackagingLevelModel>? = null

) : Parcelable

@Parcelize
data class PackagingLevelModel(
    @field:SerializedName("unit")
    var unit: String? = null,

    @field:SerializedName("buyers_unit")
    var buyersUnit: String? = null,

    @field:SerializedName("size")
    var size: Double? = null,
) : Parcelable

@Parcelize
data class ProductUnitModel(
    var name: String? = null,
    var isSelected: Boolean? = null,
    var position: Int? = null,
) : Parcelable