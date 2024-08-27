package com.app.rupyz.model_kt

import android.os.Parcelable
import com.app.rupyz.generic.model.org_image.ImageViewModel
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrgImageListModel(

	@field:SerializedName("data")
	var data: List<ImageViewModel>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("error")
	val error: Boolean? = null
) : Parcelable

data class DeleteImageModel(
	var image_id: Int
)
