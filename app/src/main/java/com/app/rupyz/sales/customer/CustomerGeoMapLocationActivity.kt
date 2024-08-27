package com.app.rupyz.sales.customer

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.app.rupyz.BuildConfig
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityCustomerGeoMapLocationBinding
import com.app.rupyz.dialog.MockLocationDetectedDialogFragment
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.LocationPermissionUtils
import com.app.rupyz.generic.utils.MyLocation
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.util.Locale


class CustomerGeoMapLocationActivity : BaseActivity(),
    MockLocationDetectedDialogFragment.IMockLocationActionListener,
    LocationPermissionUtils.ILocationPermissionListener {
    private lateinit var binding: ActivityCustomerGeoMapLocationBinding

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationPermissionUtils: LocationPermissionUtils

    private var currentGeoLocationLat: Double = 0.00
    private var currentGeoLocationLong: Double = 0.00
    private var completeAddress: String = ""

    private var customerData = CustomerData()
    private var mGoogleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerGeoMapLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        locationPermissionUtils = LocationPermissionUtils(this, this)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        with(binding.mapView) {
            // Initialise the MapView
            onCreate(null)
            isClickable = false
            // Set the map ready callback to receive the GoogleMap object
            getMapAsync {
                MapsInitializer.initialize(applicationContext)
            }
        }

        setupMap()

        if (intent.hasExtra(AppConstant.CUSTOMER)) {
            customerData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(AppConstant.CUSTOMER, CustomerData::class.java)!!
            } else {
                intent.getParcelableExtra(AppConstant.CUSTOMER)!!
            }

            if (customerData.mapLocationLat != null && customerData.mapLocationLat != 0.0
                && customerData.mapLocationLong != null && customerData.mapLocationLong != 0.0
            ) {
                with(binding.mapView) {
                    getMapAsync { map ->
                        setMapLocation(
                            map,
                            LatLng(
                                customerData.mapLocationLat!!,
                                customerData.mapLocationLong!!
                            ),
                            false
                        )
                    }
                }
            }
        }

        binding.tvSearch.setOnClickListener {
            val fields = listOf(Place.Field.LAT_LNG, Place.Field.NAME)

            // Start the autocomplete intent.
            val intent: Intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN,
                fields
            )
                .build(this)
            startAutocomplete.launch(intent)
        }

        setUpdatedLocationListener()

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnAdd.setOnClickListener {
            val intent = Intent()
            intent.putExtra(
                AppConstant.LOCATION,
                LatLng(currentGeoLocationLat, currentGeoLocationLong)
            )
            intent.putExtra(AppConstant.ADDRESS, completeAddress)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    @SuppressLint("MissingPermission", "ClickableViewAccessibility")
    private fun setupMap() {
        binding.mapView.getMapAsync { googleMap ->
            mGoogleMap = googleMap
            mGoogleMap?.isMyLocationEnabled = false
            mGoogleMap?.uiSettings.let {
                it?.isScrollGesturesEnabled = true
                it?.isZoomGesturesEnabled = true
                it?.isMyLocationButtonEnabled = false
                it?.isMapToolbarEnabled = false
            }

            googleMap.setOnCameraIdleListener {
                binding.btnAdd.isEnabled = false
                val latitude = mGoogleMap?.cameraPosition?.target?.latitude
                val longitude = mGoogleMap?.cameraPosition?.target?.longitude
                latitude?.let {
                    longitude?.let {
                        if (latitude != 0.0 && longitude != 0.0) {
                            currentGeoLocationLat = latitude
                            currentGeoLocationLong = longitude
                            completeAddress = getCompleteAddressString(
                                currentGeoLocationLat,
                                currentGeoLocationLong
                            )
                            binding.btnAdd.isEnabled = true
                        }
                    }
                }
            }
        }
    }

    private fun getCompleteAddressString(LATITUDE: Double, LONGITUDE: Double): String {
        var strAdd = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
                val returnedAddress: Address = addresses[0]
                val strReturnedAddress = StringBuilder("")
                for (i in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString()
            } else {
                Log.w("My Current location address", "No Address returned!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w("My Current location address", "Can not get Address!")
        }
        return strAdd
    }

    @SuppressLint("MissingPermission")
    private fun setUpdatedLocationListener() {
        // for getting the current location update after every 2 seconds with high accuracy
        if (locationPermissionUtils.hasPermission()) {
            if (locationPermissionUtils.isGpsEnabled(this)) {
                getCurrentLocation()
            } else {
                locationPermissionUtils.showEnableGpsDialog()
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun getCurrentLocation() {
        val myLocation = MyLocation()
        myLocation.getLocation(this, locationResult)
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            getCurrentLocation()
        } else {
            showEnablePermissionDialog()
        }
    }

    private fun showEnablePermissionDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_delete_dialog)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.setCancelable(false)

        val tvHeading = dialog.findViewById<TextView>(R.id.tv_heading)
        val tvTitle = dialog.findViewById<TextView>(R.id.tv_title)
        val ivClose = dialog.findViewById<ImageView>(R.id.iv_close)
        val ivActionImage = dialog.findViewById<ImageView>(R.id.iv_action_image)
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val tvDelete = dialog.findViewById<TextView>(R.id.tv_delete)

        tvHeading.text = resources.getString(R.string.location_permission_required)
        tvTitle.text = resources.getString(R.string.you_need_grant_location_permission)

        ivActionImage.visibility = View.VISIBLE
        ivActionImage.setImageResource(R.drawable.ic_location_blue)

        tvCancel.visibility = View.GONE

        ivClose.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        tvCancel.setOnClickListener {
            dialog.dismiss()
        }

        tvDelete.text = resources.getString(R.string.grant_permission)

        tvDelete.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
            intent.data = uri
            startActivity(intent)
            dialog.dismiss()
        }

        dialog.show()
    }

    private var locationResult: MyLocation.LocationResult = object : MyLocation.LocationResult() {
        override fun gotLocation(myLocation: Location?) {

            if (Utils.isMockLocation(myLocation)) {
                val fragment =
                    MockLocationDetectedDialogFragment.getInstance(this@CustomerGeoMapLocationActivity)
                fragment.isCancelable = false
                fragment.show(
                    supportFragmentManager,
                    MockLocationDetectedDialogFragment::class.java.name
                )
            } else {
                myLocation?.let {
                    currentGeoLocationLat = it.latitude
                    currentGeoLocationLong = it.longitude

                    val position = LatLng(currentGeoLocationLat, currentGeoLocationLong)
                    if (customerData.mapLocationLat == null || customerData.mapLocationLat == 0.0) {
                        with(binding.mapView) {
                            runOnUiThread {
                                getMapAsync { map ->
                                    setMapLocation(map, position, false)
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private val startAutocomplete = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    val place = Autocomplete.getPlaceFromIntent(intent)

                    place.latLng?.let { latLong ->
                        with(binding.mapView) {
                            getMapAsync { map ->
                                setMapLocation(map, latLong, true)
                            }
                        }
                    }
                }
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
                Log.e(tag, "User canceled autocomplete")
            }
        }

    private fun setMapLocation(map: GoogleMap, position: LatLng, updateLeadLocation: Boolean) {
        if (updateLeadLocation) {
            currentGeoLocationLat = position.latitude
            currentGeoLocationLong = position.longitude

            completeAddress =
                getCompleteAddressString(currentGeoLocationLat, currentGeoLocationLong)
        }
        with(map) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17f))
            mapType = GoogleMap.MAP_TYPE_NORMAL
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onPermissionsGiven() {
        super.onPermissionsGiven()
        getCurrentLocation()
    }

    override fun onPermissionsDenied() {
        super.onPermissionsDenied()
        getCurrentLocation()
    }
}