package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ActivityMapPointsModel(
    val lastLiveLocationPoints: CustomerFollowUpDataItem?,
    val activityPoints: ArrayList<CustomerFollowUpDataItem>,
    val liveLocationPoints: ArrayList<CustomerFollowUpDataItem>
) : Parcelable

@Parcelize
data class WayPointsModel(
    val tag: String, val location: LatLng
) : Parcelable
