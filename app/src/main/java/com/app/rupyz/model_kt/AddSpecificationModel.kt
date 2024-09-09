package com.app.rupyz.model_kt

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddSpecificationModel(
    var key: String? = null,
    var description: String? = null,
): Parcelable