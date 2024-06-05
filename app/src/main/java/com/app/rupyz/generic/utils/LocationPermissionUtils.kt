package com.app.rupyz.generic.utils

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.app.rupyz.R
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

class LocationPermissionUtils(
    private val mPermissionListener: ILocationPermissionListener,
    private val mActivity: Activity? = null
) {

    fun requestPermission(requestCode: Int) {
        mActivity?.let {
            ActivityCompat.requestPermissions(
                it,
                arrayOf(ACCESS_FINE_LOCATION),
                requestCode
            )
        }
    }

    fun hasPermission(): Boolean {
        mActivity?.let {
            return ContextCompat.checkSelfPermission(
                it, ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
        return false
    }

    fun hasBackgroundLocationPermission(): Boolean {
        mActivity?.let {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContextCompat.checkSelfPermission(
                    it, ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                return true
            }
        }
        return false
    }

    fun setPermissionResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        mActivity?.let {
            if (it.isFinishing.not() && it.isDestroyed.not()) {
                when (requestCode) {
                    AppConstant.LOCATION_TRACKING_TAG -> {
                        var isRationale = false
                        if (permissions.isNotEmpty()) {
                            isRationale =
                                ActivityCompat.shouldShowRequestPermissionRationale(
                                    it,
                                    permissions[0]
                                ).not()
                                        && ContextCompat.checkSelfPermission(it, permissions[0]) !=
                                        PackageManager.PERMISSION_GRANTED
                        }
                        if (isPermissionGranted(grantResults)) {
                            mPermissionListener.onPermissionsGiven()
                        } else if (isRationale) {
                            showRationaleDialog(requestCode)
                            mPermissionListener.onShowRationale()
                        } else {
                            mPermissionListener.onPermissionsDenied()
                        }
                    }
                }
            }
        }
    }

    private fun isPermissionGranted(grantResults: IntArray): Boolean {
        for (i in grantResults) {
            if (i != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun showRationaleDialog(code: Int) {
        mActivity?.let {
            AlertDialog.Builder(it)
                .setTitle(it.resources.getString(R.string.gps_enabled))
                .setPositiveButton(it.resources.getString(R.string.enable_now)) { _, _ ->
                    openSettings()
                }
                .setNegativeButton(it.resources.getString(R.string.not_now)) { _, _ ->
                    mPermissionListener.onPermissionsDenied()
                }
                .create()
                .show()
        }
    }

    private fun openSettings() {
        mActivity?.let {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", it.packageName, null)
            )
            //  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            it.startActivityForResult(intent, AppConstant.OPEN_LOCATION_SETTINGS)
        }
    }

    fun isGpsEnabled(activity: Activity): Boolean {
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun showEnableGpsDialog() {
        mActivity?.let {

            val locationRequest =
                LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, MILLISECONDS_INTERVAL)

            val locationSettingDialogBuilder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest.build())

            val task = LocationServices.getSettingsClient(it)
                .checkLocationSettings(locationSettingDialogBuilder.build())

            task.addOnSuccessListener {
                mPermissionListener.onGpsEnabled()
            }

            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        exception.startResolutionForResult(
                            it,
                            AppConstant.OPEN_GPS_SETTINGS
                        )
                        mPermissionListener.onGpsDisabled()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun setActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mActivity?.let {
            when (requestCode) {
                Activity.RESULT_OK -> {
                    mPermissionListener.onGpsEnabled()
                }

                AppConstant.OPEN_GPS_SETTINGS -> {
                    if (resultCode == Activity.RESULT_OK) {
                        mPermissionListener.onGpsEnabled()
                    }
                }

                AppConstant.OPEN_LOCATION_SETTINGS -> {
                    if (hasPermission()) {
                        mPermissionListener.onPermissionsGiven()
                    }
                }

                else -> {
                    mPermissionListener.onPermissionsDenied()
                }
            }
        }
    }

    interface ILocationPermissionListener {
        fun onPermissionsGiven() {}
        fun onPermissionsDenied() {}
        fun onShowRationale() {}
        fun onGpsEnabled() {}
        fun onGpsDisabled() {}
    }

    companion object {
        const val MILLISECONDS_INTERVAL = 1000L
    }
}