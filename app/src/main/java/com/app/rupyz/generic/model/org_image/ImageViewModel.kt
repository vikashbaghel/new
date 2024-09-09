package com.app.rupyz.generic.model.org_image

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageViewModel(
    @field:SerializedName("id")
    var id: Int? = null,
    @field:SerializedName("organization")
    var organization: Int? = null,
    @field:SerializedName("image_url")
    var image_url: String? = null
) : Parcelable