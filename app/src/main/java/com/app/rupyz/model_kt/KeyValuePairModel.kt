package com.app.rupyz.model_kt

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KeyValuePairModel(
    var name: String? = null,
    var value: String? = null,
): Parcelable
