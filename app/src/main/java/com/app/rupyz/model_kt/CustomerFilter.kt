package com.app.rupyz.model_kt

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomerFilter(
		val selectedBeatList : MutableSet<Int> = mutableSetOf(),
		val selectedCustomerLevel : MutableSet<String> = mutableSetOf(),
		val selectedCustomerType : MutableSet<String> = mutableSetOf(),
		val selectedStaff : MutableSet<Int> = mutableSetOf(),
                         ) : Parcelable