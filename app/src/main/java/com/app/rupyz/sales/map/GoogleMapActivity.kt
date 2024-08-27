package com.app.rupyz.sales.map

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityGoogleMapBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.ActivityMapPointsModel
import com.app.rupyz.model_kt.CustomerFollowUpDataItem
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


class GoogleMapActivity : BaseActivity(), OnMapReadyCallback,
    ActivityLocationListAdapter.ICustomerFeedbackActionListener {
    private lateinit var binding: ActivityGoogleMapBinding

    private var activityMapPointsModel: ActivityMapPointsModel? = null
    private var staffName: String = ""
    private var filterDate: String = ""

    private lateinit var mMap: GoogleMap

    private lateinit var adapter: ActivityLocationListAdapter

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private var isFakeLocationDetectedFromThisDate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoogleMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(AppConstant.STAFF_NAME)) {
            staffName = intent.getStringExtra(AppConstant.STAFF_NAME) ?: ""

            if (staffName.isNotEmpty()) {
                binding.tvToolbarTitle.text = staffName
            } else {
                binding.tvToolbarTitle.text =
                    SharedPref.getInstance().getString(AppConstant.USER_NAME)
            }
        }


        isFakeLocationDetectedFromThisDate =
            intent.getBooleanExtra(AppConstant.FAKE_LOCATION_DETECTED, false)

        if (intent.hasExtra(AppConstant.DATE)) {
            filterDate = intent.getStringExtra(AppConstant.DATE) ?: ""
            binding.tvDate.text = DateFormatHelper.getMonthDate(filterDate)
        }

        if (intent.hasExtra(AppConstant.LOCATION)) {
            activityMapPointsModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(AppConstant.LOCATION, ActivityMapPointsModel::class.java)
            } else {
                intent.getParcelableExtra(AppConstant.LOCATION)
            }

            val mapFragment =
                supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment!!.getMapAsync(this)

            launch {
                if (intent.hasExtra(AppConstant.ACTIVITY_TYPE)
                    && intent.getStringExtra(AppConstant.ACTIVITY_TYPE)
                        .equals(AppConstant.MY_ACTIVITY)
                ) {
                    if (activityMapPointsModel?.activityPoints.isNullOrEmpty().not()) {
                        activityMapPointsModel?.activityPoints?.let {
                            if (it.size > 1) {
                                val pointsList = ArrayList<CustomerFollowUpDataItem>()
                                pointsList.addAll(it)
                                openPolyLineMap(activityMapPointsModel?.activityPoints!!)
                                addWaypointMarkers(activityMapPointsModel?.activityPoints!!)
                                setUpBottomSheetListener()
                            }
                        }
                    }
                } else if (activityMapPointsModel?.liveLocationPoints.isNullOrEmpty().not()) {
                    openLiveLocationPolyLineMap(activityMapPointsModel?.liveLocationPoints!!)
                }
            }
        }

        binding.ivBack.setOnClickListener { finish() }
    }


    private fun setUpBottomSheetListener() {
        val bottomSheetView = findViewById<ConstraintLayout>(R.id.activity_location_bottom_sheet)
        val tvHeading = findViewById<TextView>(R.id.tv_heading)
        val ivLiveLocationTrack = findViewById<ImageView>(R.id.iv_live_location_track)
        val fakeLocationTv = findViewById<TextView>(R.id.tv_fake_location_detected)
        val rvActivityLocation = findViewById<RecyclerView>(R.id.rv_activity_location)


        if (bottomSheetView != null) {
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
            bottomSheetBehavior.isHideable = false
        }

        if (isFakeLocationDetectedFromThisDate) {
            fakeLocationTv.visibility = View.VISIBLE
        }

        if (activityMapPointsModel?.lastLiveLocationPoints != null) {
            ivLiveLocationTrack.visibility = View.VISIBLE
            ivLiveLocationTrack.setOnClickListener {
                getFeedbackDetails(activityMapPointsModel?.lastLiveLocationPoints!!)
            }
        } else {
            ivLiveLocationTrack.visibility = View.GONE
        }

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        tvHeading.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }


        rvActivityLocation.layoutManager = LinearLayoutManager(this)
        adapter = ActivityLocationListAdapter(activityMapPointsModel?.activityPoints!!.asReversed(), this)
        rvActivityLocation.adapter = adapter
    }

    private fun bitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        // below line is use to generate a drawable.
        val vectorDrawable: Drawable? = ContextCompat.getDrawable(
            context, vectorResId
        )

        // below line is use to set bounds to our vector
        // drawable.
        vectorDrawable?.setBounds(
            0, 0, 120,
            120
        )

        // below line is use to create a bitmap for our
        // drawable which we have added.
        val bitmap = Bitmap.createBitmap(
            120, 120,
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

    private fun addWaypointMarkers(waypoints: ArrayList<CustomerFollowUpDataItem>) {

        val boundsBuilder = LatLngBounds.Builder()

        val mapView = (supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?)!!.view

        waypoints.forEachIndexed { index, waypoint ->
            val position = LatLng(waypoint.geoLocationLat!!, waypoint.geoLocationLong!!)
            mMap.addMarker(
                MarkerOptions()
                    .position(position)
                    .icon(
                        BitmapDescriptorFactory.fromBitmap(
                            getMarkerBitmapFromView(
                                waypoint,
                                "${index + 1}"
                            )
                        )
                    )
                    .visible(true)
                    .title(getActivityLabelForMarker(waypoint))
            )
            boundsBuilder.include(position)
        }

        // Build the bounds
        val bounds = boundsBuilder.build()

        mapView?.viewTreeObserver?.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Remove the listener to prevent being called multiple times
                mapView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // Move and zoom the camera to fit the bounds
                val padding = 100 // offset from edges of the map in pixels
                val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)

                mMap.moveCamera(cu)
            }
        })
    }


    private fun getActivityLabelForMarker(model: CustomerFollowUpDataItem): String {
        var label = ""
        if (model.moduleType != null) {
            if (model.moduleType == AppConstant.ATTENDANCE) {

                if (model.action == AppConstant.ATTENDANCE_CHECK_IN) {
                    label = resources.getString(R.string.start_day)
                } else if (model.action == AppConstant.ATTENDANCE_CHECK_OUT) {
                    label = resources.getString(R.string.end_day)
                }
            } else {

                var customerAndBusinessName = ""
                if (model.businessName.isNullOrEmpty().not()) {
                    customerAndBusinessName = model.businessName!!
                } else if (model.customerName.isNullOrEmpty().not()) {
                    customerAndBusinessName = model.customerName!!
                }

                when (model.moduleType) {
                    AppConstant.CUSTOMER_FEEDBACK -> {
                        label = "${model.feedbackType}, $customerAndBusinessName"
                    }

                    AppConstant.LEAD_FEEDBACK -> {
                        label = "${model.feedbackType}, $customerAndBusinessName"
                    }

                    AppConstant.ORDER_DISPATCH -> {
                        label = "${model.moduleType}, $customerAndBusinessName"
                    }

                    AppConstant.PAYMENT -> {
                        label =
                            "${resources.getString(R.string.payment_collected)}, $customerAndBusinessName"
                    }

                    else -> {
                        label =
                            "${
                                model.action?.lowercase()?.replaceFirstChar(Char::titlecase)
                            } ${model.moduleType}, $customerAndBusinessName"
                    }
                }
            }
        }

        return label
    }

    private fun getMarkerBitmapFromView(model: CustomerFollowUpDataItem, label: String): Bitmap {
        val customMarkerView = layoutInflater.inflate(R.layout.custom_marker_layout, null)
        val markerTextView =
            customMarkerView.findViewById<AppCompatTextView>(R.id.custom_marker_text)
        val markerImageView = customMarkerView.findViewById<AppCompatImageView>(R.id.custom_marker)

        markerTextView.text = label

        if (model.moduleType != null) {
            when (model.moduleType) {
                AppConstant.ATTENDANCE -> {
                    if (model.action == AppConstant.ATTENDANCE_CHECK_IN) {
                        markerImageView.imageTintList = ColorStateList.valueOf(
                            resources.getColor(R.color.google_map_day_started, theme)
                        )
                        markerTextView.backgroundTintList = ColorStateList.valueOf(
                            resources.getColor(R.color.google_map_day_started)
                        )
                    } else if (model.action == AppConstant.ATTENDANCE_CHECK_OUT) {
                        markerTextView.backgroundTintList = ColorStateList.valueOf(
                            resources.getColor(R.color.google_map_day_ended)
                        )
                        markerImageView.imageTintList = ColorStateList.valueOf(
                            resources.getColor(R.color.google_map_day_ended)
                        )
                    }
                }

                AppConstant.CUSTOMER_FEEDBACK,
                AppConstant.LEAD_FEEDBACK -> {
                    markerTextView.backgroundTintList = ColorStateList.valueOf(
                        resources.getColor(R.color.google_map_activity)
                    )
                    markerImageView.imageTintList = ColorStateList.valueOf(
                        resources.getColor(R.color.google_map_activity)
                    )
                }

                AppConstant.ORDER -> {
                    markerTextView.backgroundTintList = ColorStateList.valueOf(
                        resources.getColor(R.color.google_map_order)
                    )
                    markerImageView.imageTintList = ColorStateList.valueOf(
                        resources.getColor(R.color.google_map_order)
                    )
                }

                AppConstant.PAYMENT -> {
                    markerTextView.backgroundTintList = ColorStateList.valueOf(
                        resources.getColor(R.color.google_map_order)
                    )
                    markerImageView.imageTintList = ColorStateList.valueOf(
                        resources.getColor(R.color.google_map_order)
                    )
                }

                AppConstant.CUSTOMER -> {
                    markerTextView.backgroundTintList = ColorStateList.valueOf(
                        resources.getColor(R.color.google_map_feedback)
                    )
                    markerImageView.imageTintList = ColorStateList.valueOf(
                        resources.getColor(R.color.google_map_feedback)
                    )
                }

                AppConstant.LEAD -> {
                    markerTextView.backgroundTintList = ColorStateList.valueOf(
                        resources.getColor(R.color.google_map_feedback)
                    )
                    markerImageView.imageTintList = ColorStateList.valueOf(
                        resources.getColor(R.color.google_map_feedback)
                    )
                }

                else -> {
                    markerTextView.backgroundTintList = ColorStateList.valueOf(
                        resources.getColor(R.color.google_map_order)
                    )
                    markerImageView.imageTintList = ColorStateList.valueOf(
                        resources.getColor(R.color.google_map_order)
                    )
                }
            }
        } else {
            markerTextView.backgroundTintList = ColorStateList.valueOf(
                resources.getColor(R.color.google_map_day_started)
            )
            markerImageView.imageTintList = ColorStateList.valueOf(
                resources.getColor(R.color.google_map_day_started)
            )
        }

        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        customMarkerView.layout(
            0,
            0,
            customMarkerView.measuredWidth,
            customMarkerView.measuredHeight
        )
        customMarkerView.buildDrawingCache()

        val returnedBitmap = Bitmap.createBitmap(
            customMarkerView.measuredWidth,
            customMarkerView.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
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

            if (intent.hasExtra(AppConstant.ACTIVITY_TYPE)
                && intent.getStringExtra(AppConstant.ACTIVITY_TYPE)
                    .equals(AppConstant.MY_ACTIVITY)
            ) {
                if (activityMapPointsModel?.activityPoints.isNullOrEmpty().not()) {
                    activityMapPointsModel?.activityPoints?.let {
                        if (it.size == 1) {
                            openSingleDotMap(mMap, it[0])
                        }
                        setUpBottomSheetListener()
                    }
                }
            } else {
                binding.clBottomSheet.visibility = View.GONE
            }

            if (activityMapPointsModel?.lastLiveLocationPoints != null) {
                activityMapPointsModel?.lastLiveLocationPoints?.let {
                    openLiveDotMap(mMap, it)
                }
            }
        }
    }


    private fun openPolyLineMap(points: ArrayList<CustomerFollowUpDataItem>) {

        val wayPointsList = ArrayList<LatLng>()
        points.forEach {
            wayPointsList.add(LatLng(it.geoLocationLat!!, it.geoLocationLong!!))
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

    private fun openLiveLocationPolyLineMap(points: ArrayList<CustomerFollowUpDataItem>) {

        val origin = LatLng(
            points[0].geoLocationLat ?: 0.0,
            points[0].geoLocationLong ?: 0.0
        )

        val destination = LatLng(
            points[points.lastIndex].geoLocationLat ?: 0.0,
            points[points.lastIndex].geoLocationLong ?: 0.0
        )

        val wayPointsList = ArrayList<LatLng>()
        points.forEach {
            wayPointsList.add(LatLng(it.geoLocationLat!!, it.geoLocationLong!!))
        }

        mMap.addMarker(
            MarkerOptions().position(origin).title("start")
                .icon(bitmapFromVector(this, R.drawable.ic_start_location_marker))
        )
        mMap.addMarker(
            MarkerOptions().position(destination)
                .title("End").icon(bitmapFromVector(this, R.drawable.ic_end_location_marker))
        )

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

    private fun openSingleDotMap(googleMap: GoogleMap, latLng: CustomerFollowUpDataItem) {
        val location = LatLng(latLng.geoLocationLat!!, latLng.geoLocationLong!!)

        googleMap.addMarker(
            MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(latLng, "1")))
                .visible(true)
                .title(getActivityLabelForMarker(latLng))
        )

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
    }


    private fun openLiveDotMap(googleMap: GoogleMap, latLng: CustomerFollowUpDataItem) {
        val location = LatLng(latLng.geoLocationLat!!, latLng.geoLocationLong!!)

        googleMap.addMarker(
            MarkerOptions().position(location)
                .icon(bitmapFromVector(this, R.drawable.ic_live_location_points))
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    override fun getFeedbackDetails(model: CustomerFollowUpDataItem) {
        val bounds = LatLngBounds.builder().apply {
            include(LatLng(model.geoLocationLat!!, model.geoLocationLong!!))
        }.build()
        val padding = 50 // Padding in pixels
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
    }
}