package com.app.rupyz.generic.model.product

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PicMapModel(
    var id: Int? = null,
    var url: String? = null
): Parcelable
