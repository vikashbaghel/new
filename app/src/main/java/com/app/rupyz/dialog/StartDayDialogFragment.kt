package com.app.rupyz.dialog

import android.Manifest
import android.annotation.SuppressLint
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.app.rupyz.R
import com.app.rupyz.databinding.MarkAttendanceForTodayDialogBinding
import com.app.rupyz.generic.helper.Actions
import com.app.rupyz.generic.helper.ButtonStyleHelper
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.helper.log
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Connectivity
import com.app.rupyz.generic.utils.LocationPermissionUtils
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utility
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.generic.utils.Utils.isGpsOn
import com.app.rupyz.generic.utils.isTimeLessThanTo8PM
import com.app.rupyz.model_kt.CustomerFollowUpDataItem
import com.app.rupyz.model_kt.SaveAttendanceModel
import com.app.rupyz.sales.staffactivitytrcker.StaffActivityViewModel
import com.app.rupyz.service.EndlessService
import com.app.rupyz.service.ServiceState
import com.app.rupyz.service.StopLiveTrackingJobService
import com.app.rupyz.service.getServiceState
import java.util.Calendar

class StartDayDialogFragment : DialogFragment(),
        MockLocationDetectedDialogFragment.IMockLocationActionListener,
        LocationPermissionUtils.ILocationPermissionListener {
    private val activityViewModel: StaffActivityViewModel by viewModels()
    private lateinit var binding: MarkAttendanceForTodayDialogBinding
    private var locationManager: LocationManager? = null
    private lateinit var locationPermissionUtils: LocationPermissionUtils

    companion object {
        var listener: IStartDayActionListener? = null
        private var geoLocationLat: Double = 0.00
        private var geoLocationLong: Double = 0.00

        fun getInstance(
                listener: IStartDayActionListener?, geoLocationLat: Double,
                geoLocationLong: Double
        ): StartDayDialogFragment {
            this.listener = listener
            this.geoLocationLat = geoLocationLat
            this.geoLocationLong = geoLocationLong
            return StartDayDialogFragment()
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = MarkAttendanceForTodayDialogBinding.inflate(layoutInflater)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationManager =
                this.context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationPermissionUtils = LocationPermissionUtils(this, requireActivity())

        initObservers()
        binding.btnContinue.setOnClickListener {
            val location = Location("")
            location.latitude = geoLocationLat
            location.longitude = geoLocationLong

            if (Utils.isMockLocation(location).not()) {
                val wasGPSOn: Boolean = Utils.isGpsOn(locationManager)
                if (!wasGPSOn) {
                    locationPermissionUtils.showEnableGpsDialog()
                } else if (geoLocationLat == 0.0 || geoLocationLong == 0.0) {
                    if (isStaffUser() && SharedPref.getInstance()
                                    .getBoolean(AppConstant.LIVE_LOCATION, false)
                    ) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            activityResultLauncher.launch(
                                    arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION,
                                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                    )
                            )
                        } else {
                            locationPermissionUtils.requestPermission(101)
                        }
                    } else
                        if (locationPermissionUtils.hasPermission()) {
                            locationManager?.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    1100,
                                    10f,
                                    locationListenerNetworkInException,
                                    Looper.getMainLooper()
                            )
                        } else {
                            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                } else {
                    markAttendance()
                }
            } else {
                openMockLocationDetectionDialog()
            }
        }

        binding.ivClose.setOnClickListener {
            listener?.onDismissDialogForStartDay()
            dismiss()
        }
    }

    fun isStaffUser(): Boolean {
        val appAccessType = SharedPref.getInstance().getString(AppConstant.APP_ACCESS_TYPE)
        return appAccessType != AppConstant.ACCESS_TYPE_MASTER
    }

    private val activityResultLauncher =
            registerForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                var isGranted = false

                permissions.entries.forEach {
                    isGranted = it.value
                }

                if (isGranted) {
                    markAttendance()
                } else {
                    dismiss()
                    listener?.requiredBackgroundLocationFromPopUp()
                }
            }

    private fun markAttendance() {
        ButtonStyleHelper(requireContext()).initCustomButton(
                false,
                binding.btnContinue,
                resources.getString(R.string.start_day),
                R.drawable.check_score_button_style
        )

        val model = CustomerFollowUpDataItem(moduleType = AppConstant.ATTENDANCE)
        model.moduleType = AppConstant.ATTENDANCE

        model.action = AppConstant.ATTENDANCE_CHECK_IN
        model.geoLocationLong = geoLocationLong
        model.geoLocationLat = geoLocationLat

//        activityViewModel.addAttendance(model, hasInternetConnection())

    }

    fun hasInternetConnection(): Boolean {
        return Connectivity.hasInternetConnection(requireContext())
    }

    private var locationListenerNetworkInException: LocationListener = LocationListener {
        if (Utils.isMockLocation(it)) {
            openMockLocationDetectionDialog()
        } else {
            geoLocationLat = it.latitude
            geoLocationLong = it.longitude
        }
    }

    @SuppressLint("MissingPermission")
    private val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            locationManager?.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1100, 10f, locationListenerNetworkInException, Looper.getMainLooper()
            )
        } else {
            dismiss()
        }
    }


    private fun openMockLocationDetectionDialog() {
        val fragment = MockLocationDetectedDialogFragment.getInstance(this)
        fragment.show(
                childFragmentManager, MockLocationDetectedDialogFragment::class.simpleName
        )
    }

    private fun initObservers() {
        activityViewModel.addFeedbackFollowUpLiveData.observe(requireActivity()) {
            if (isAdded) {
                ButtonStyleHelper(requireContext()).initCustomButton(
                        true,
                        binding.btnContinue,
                        resources.getString(R.string.start_day),
                        R.drawable.check_score_button_style
                )

                if (it.error == true) {
                    if (it.errorCode == 403) {
                        Utility(requireContext()).logout()
                    } else {
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    SharedPref.getInstance().putBoolean(AppConstant.START_DAY, true)
                    if (isStaffUser() && SharedPref.getInstance()
                                    .getBoolean(AppConstant.LIVE_LOCATION, false)
                    ) {
                        if (isGpsOn(locationManager) && isTimeLessThanTo8PM() && locationPermissionUtils.hasPermission()
                                && locationPermissionUtils.hasBackgroundLocationPermission()
                        ) {
                            actionOnService(Actions.START)
                        }
                        addScheduleWorkerForStopService()
                    }
                    SharedPref.getInstance().putModelClass(AppConstant.SAVE_ATTENDANCE_PREF,
                            SaveAttendanceModel(
                                    date = DateFormatHelper.convertDateToIsoFormat(Calendar.getInstance().time),
                                    checkIn = true,
                                    checkOut = null
                            )
                    )


                    dismiss()
                    listener?.onSuccessfullyStartDay()
                }
            }
        }
    }

    private fun addScheduleWorkerForStopService() {
        val componentName = ComponentName(requireContext(), StopLiveTrackingJobService::class.java)
        val builder = JobInfo.Builder(0, componentName)

        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, AppConstant.LIVE_LOCATION_END_TIME) // 8:00 PM
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)

        val triggerTime = cal.timeInMillis

        builder.setPersisted(true)
        builder.setMinimumLatency(triggerTime - System.currentTimeMillis())
        builder.setOverrideDeadline(triggerTime - System.currentTimeMillis() + 1000) // 1 second window

        val jobScheduler = requireActivity().getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(builder.build())

    }

    private fun actionOnService(action: Actions) {
        if (getServiceState(requireContext()) == ServiceState.STOPPED && action == Actions.STOP) return
        Intent(requireContext(), EndlessService::class.java).also {
            it.action = action.name
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                log("Starting the service in >=26 Mode")
                ContextCompat.startForegroundService(requireContext(), it)
                return
            }
            log("Starting the service in < 26 Mode")
            requireContext().startService(it)
        }
    }

    interface IStartDayActionListener {
        fun onDismissDialogForStartDay() {}
        fun onSuccessfullyStartDay() {}
        fun requiredBackgroundLocationFromPopUp() {}
    }
}