package com.app.rupyz.model_kt

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OfflineProgressModel(
        var isOfflineDumpComplete: Boolean = false,
        var offlineProgressHashMap: HashMap<String, Pair<Boolean, Int?>>? = null
) : Parcelable

