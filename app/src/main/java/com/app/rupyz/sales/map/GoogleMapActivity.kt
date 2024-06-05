package com.app.rupyz.sales.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityGoogleMapBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.ActivityMapPointsModel
import com.app.rupyz.model_kt.WayPointsModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


class GoogleMapActivity : BaseActivity(), OnMapReadyCallback, CoroutineScope by MainScope() {
    private lateinit var binding: ActivityGoogleMapBinding

    private var activityMapPointsModel: ActivityMapPointsModel? = null

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoogleMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(AppConstant.LOCATION)) {
            activityMapPointsModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(AppConstant.LOCATION, ActivityMapPointsModel::class.java)
            } else {
                intent.getParcelableExtra(AppConstant.LOCATION)
            }

            val mapFragment =
                    supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment!!.getMapAsync(this)

            if (activityMapPointsModel?.point.isNullOrEmpty().not()) {
                activityMapPointsModel?.point?.let {
                    if (it.size > 1) {

                        val pointsList = ArrayList<WayPointsModel>()
                        pointsList.addAll(it)

                        pointsList.removeFirst()
                        pointsList.removeLast()

                        launch {
                            if (intent.hasExtra(AppConstant.ACTIVITY_TYPE)
                                    && intent.getStringExtra(AppConstant.ACTIVITY_TYPE)
                                            .equals(AppConstant.MY_ACTIVITY)
                            ) {
                                addWaypointMarkers(activityMapPointsModel?.point!!)
                            } else {
                                openPolyLineMap(it)
                            }
                        }
                    }
                }
            }
        }

        binding.ivBack.setOnClickListener { finish() }
    }

    private fun bitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        // below line is use to generate a drawable.
        val vectorDrawable: Drawable? = ContextCompat.getDrawable(
                context, vectorResId
        )

        // below line is use to set bounds to our vector
        // drawable.
        vectorDrawable?.setBounds(
                0, 0, 60,
                60
        )

        // below line is use to create a bitmap for our
        // drawable which we have added.
        val bitmap = Bitmap.createBitmap(
                60, 60,
                Bitmap.Config.ARGB_8888
        )

        // below line is use to add bitmap in our canvas.
        val canvas = Canvas(bitmap)

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable?.draw(canvas)

        // after generating our bitmap we are returning our
        // bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun addWaypointMarkers(waypoints: List<WayPointsModel>) {
        waypoints.forEachIndexed { index, waypoint ->
            mMap.addMarker(
                    MarkerOptions()
                            .position(waypoint.location)
                            .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView("${index + 1}")))
                            .visible(true)
                            .title(waypoint.tag)
            )
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(waypoints.first().location, 12f))
    }

    private fun getMarkerBitmapFromView(label: String): Bitmap {
        val customMarkerView = layoutInflater.inflate(R.layout.custom_marker_layout, null)
        val markerTextView = customMarkerView.findViewById<TextView>(R.id.marker_text)
        markerTextView.text = label

        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        customMarkerView.layout(0, 0, customMarkerView.measuredWidth, customMarkerView.measuredHeight)
        customMarkerView.buildDrawingCache()

        val returnedBitmap = Bitmap.createBitmap(customMarkerView.measuredWidth, customMarkerView.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
        customMarkerView.draw(canvas)
        return returnedBitmap
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (intent.hasExtra(AppConstant.LOCATION)) {
            activityMapPointsModel =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(
                                AppConstant.LOCATION,
                                ActivityMapPointsModel::class.java
                        )
                    } else {
                        intent.getParcelableExtra(AppConstant.LOCATION)
                    }
            if (activityMapPointsModel?.point.isNullOrEmpty().not()) {
                activityMapPointsModel?.point?.let {
                    if (it.size == 1) {
                        openSingleDotMap(mMap, it[0])
                    }
                }
            }
        }
    }


    private fun openPolyLineMap(points: ArrayList<WayPointsModel>) {

        val origin = points[0]
        val destination = points[points.lastIndex]

        mMap.addMarker(
                MarkerOptions().position(origin.location).title("start")
                        .icon(bitmapFromVector(this, R.drawable.ic_start_location_marker))
        )
        mMap.addMarker(
                MarkerOptions().position(destination.location)
                        .title("End").icon(bitmapFromVector(this, R.drawable.ic_end_location_marker))
        )

        val wayPointsList = ArrayList<LatLng>()
        points.forEach {
            wayPointsList.add(it.location)
        }

        mMap.setOnMapLoadedCallback {
            val polylineOptions = PolylineOptions().addAll(wayPointsList)
                    .color(resources.getColor(R.color.google_map_route_line_color)).width(10f)
            mMap.addPolyline(polylineOptions)
            val bounds = LatLngBounds.builder().apply {
                for (point in wayPointsList) {
                    include(point)
                }
            }.build()
            val padding = 100 // Padding in pixels
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
        }
    }

    private fun openSingleDotMap(googleMap: GoogleMap, latLng: WayPointsModel) {
        googleMap.addMarker(
                MarkerOptions().position(latLng.location)
        )?.title = latLng.tag

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng.location, 16f))
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}