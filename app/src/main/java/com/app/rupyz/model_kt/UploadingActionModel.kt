package com.app.rupyz.model_kt

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UploadingActionModel(
        var type: String? = null,
        var imageExist: Boolean? = null,
        var imageCount: Int? = null,
        val imageUploaded: Boolean? = null,
        val typeUploaded: Boolean? = null
) : Parcelable
