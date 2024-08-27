package com.app.rupyz.model_kt

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StaffActivitySortingModel(
    var tcFilter: Pair<String, String?>? = null,
    var pcFilter: Pair<String, String?>? = null,
    var orderValueFilter: Pair<String, String?>? = null,
    var durationFilter: Pair<String, String?>? = null
) : Parcelable
