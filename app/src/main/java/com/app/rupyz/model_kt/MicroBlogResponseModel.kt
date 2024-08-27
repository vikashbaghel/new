package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MicroBlogResponseModel(

	@field:SerializedName("data")
	val data: List<BlogDataItem>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("error")
	val error: Boolean? = null
) : Parcelable

@Parcelize
data class BlogDataItem(

	@field:SerializedName("author")
	val author: String? = null,

	@field:SerializedName("viewers_count")
	val viewersCount: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("label")
	val label: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("tags")
	val tags: List<String>? = null,

	@field:SerializedName("promotion_image_url")
	val promotionImageUrl: String? = null,

	@field:SerializedName("read_time")
	val readTime: Int? = null,

	@field:SerializedName("thumbnail_image_url")
	val thumbnailImageUrl: String? = null,

	@field:SerializedName("subtitle")
	val subtitle: String? = null,

	@field:SerializedName("blog_for")
	val blogFor: String? = null,

	@field:SerializedName("category")
	val category: String? = null,

	@field:SerializedName("slug")
	val slug: String? = null
) : Parcelable
