package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddPhotoResponseModel(

	@field:SerializedName("data")
	val data: PhotoData? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("error")
	val error: Boolean? = null
) : Parcelable

@Parcelize
data class PhotoData(
	@field:SerializedName("image_url")
	var imageUrl: Int? = null,
) : Parcelable
