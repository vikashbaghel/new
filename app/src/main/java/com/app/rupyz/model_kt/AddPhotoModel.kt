package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddPhotoModel(
    @field:SerializedName("image_url")
    var imageUrl: Int? = null,
) : Parcelable

@Parcelize
data class AddedPhotoModel(
    @field:SerializedName("imageId")
    var imageId: Int? = null,

    @field:SerializedName("imagePath")
    var imagePath: String? = null,

    var type: String? = null,

    @field:SerializedName("isUploading")
    var isUploading: Boolean = false,

    @field:SerializedName("timeStamp")
    var timeStamp: Long? = null,

    @field:SerializedName("onEditProduct")
    var onEditProduct: Boolean = false,

    var isSelect: Boolean = false,

    var isDisplayPicEnable: Boolean  = true
) : Parcelable