package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class AddProductResponseModel(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	var message: String? = null,

	@field:SerializedName("error")
    var error: Boolean? = null
) : Parcelable

@Parcelize
data class Data(

	@field:SerializedName("like_count")
	val likeCount: Int? = null,

	@field:SerializedName("is_published")
	val isPublished: Boolean? = null,

	@field:SerializedName("description")
	val description: String? = null,

	val specification: @RawValue Any? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("created_by")
	val createdBy: Int? = null,

	@field:SerializedName("nanoid")
	val nanoid: String? = null,

	@field:SerializedName("unit")
	val unit: String? = null,

	@field:SerializedName("max_price")
	val maxPrice: Int? = null,

	@field:SerializedName("min_price")
	val minPrice: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("organization")
	val organization: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("updated_by")
	val updatedBy: Int? = null,

	@field:SerializedName("pics_urls")
	val picsUrls: List<String?>? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("category")
	val category: String? = null,

	@field:SerializedName("pics")
	val pics: List<Int?>? = null,

	@field:SerializedName("avaliable_stock")
	val avaliableStock: String? = null,

    @field:SerializedName("price")
    val price: Double? = null,

	@field:SerializedName("view_count")
	val viewCount: Int? = null

) : Parcelable
