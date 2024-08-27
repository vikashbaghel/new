package com.app.rupyz.model_kt

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StaffActivityFilterModel(
    var filterRoleList: ArrayList<String?>? = null,
    var filterStaffId: Int? = null,
) : Parcelable
