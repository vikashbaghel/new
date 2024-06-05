package com.app.rupyz.model_kt

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StaffTargetModel(
	var title: String? = null,
	var target: Double? = null,
	var achieved: Double? = null,
	var logo: Int? = null
) : Parcelable
