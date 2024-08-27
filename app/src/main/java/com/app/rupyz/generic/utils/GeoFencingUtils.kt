package com.app.rupyz.generic.utils

import android.location.Location


fun findUserIsInGeoFencingArea(
    customerLocationLat: Double,
    customerLocationLong: Double,
    myLocationLat: Double,
    myLocationLong: Double
): Pair<Boolean, Float> {

    val startPoint = Location("locationA")
    startPoint.latitude = customerLocationLat
    startPoint.longitude = customerLocationLong

    val endPoint = Location("locationA")
    endPoint.latitude = myLocationLat
    endPoint.longitude = myLocationLong

    val distance: Float = startPoint.distanceTo(endPoint)

    return Pair(distance < 200.0, distance)
}