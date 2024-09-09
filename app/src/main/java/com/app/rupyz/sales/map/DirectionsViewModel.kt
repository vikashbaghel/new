package com.app.rupyz.sales.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.JsonObject

class DirectionsViewModel : ViewModel() {

    private val repository = DirectionsRepository()

    private val _routeData  = MutableLiveData<Pair<List<LatLng>, Double>>()
    val routeData: LiveData<Pair<List<LatLng>, Double>> get() = _routeData

    fun getDirections(origin: LatLng, dest: LatLng, waypoints: List<LatLng>, apiKey: String) {
        val url = getDirectionURL(origin, dest, waypoints, apiKey)
        repository.fetchDirections(url) { response ->
            response?.let {
                val (routePoints, totalDistance) = parseDirectionsResponse(it)
                _routeData.postValue(Pair(routePoints, totalDistance))
            }

        }
    }

    private fun getDirectionURL(
        origin: LatLng,
        dest: LatLng,
        waypoints: List<LatLng>,
        secret: String
    ): String {
        val waypointsStr = waypoints.joinToString("|") { "${it.latitude},${it.longitude}" }

        return "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}" +
                "&waypoints=$waypointsStr" +
                "&sensor=false" +
                "&mode=driving" +
                "&key=$secret"
    }

    private fun parseDirectionsResponse(responseData: String):  Pair<List<LatLng>, Double> {
        val json = Gson().fromJson(responseData, JsonObject::class.java)
        val routes = json.getAsJsonArray("routes")
        val legs = routes[0].asJsonObject.getAsJsonArray("legs")
        var totalDistanceMeters = 0.0
        for (leg in legs) {
            val distance = leg.asJsonObject.getAsJsonObject("distance").get("value").asDouble
            totalDistanceMeters += distance
        }


        val polyline = routes[0].asJsonObject
            .getAsJsonObject("overview_polyline")
            .get("points")
            .asString

        val routePoints = decodePoly(polyline)

        // Convert total distance from meters to kilometers
        val totalDistanceKilometers = totalDistanceMeters / 1000

        return Pair(routePoints, totalDistanceKilometers)

    }

    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }

        return poly
    }
}