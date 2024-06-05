package com.app.rupyz.service

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.os.SystemClock
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.app.rupyz.R
import com.app.rupyz.generic.base.FailureResponse
import com.app.rupyz.generic.base.NetworkCallback
import com.app.rupyz.generic.helper.Actions
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.helper.log
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.LocationPermissionUtils
import com.app.rupyz.generic.utils.SharePrefConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.generic.utils.generateUniqueId
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.model_kt.LiveLocationDataModel
import com.app.rupyz.model_kt.LiveLocationListModel
import com.app.rupyz.model_kt.SaveLiveLocationModel
import com.app.rupyz.retrofit.RetrofitClient
import com.app.rupyz.sales.home.SalesMainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import retrofit2.Call
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class EndlessService : Service(), LocationPermissionUtils.ILocationPermissionListener {

    private var wakeLock: PowerManager.WakeLock? = null
    private var isServiceStarted = false

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationListener: LocationListener? = null

    private var locationManager: LocationManager? = null
    private val notificationChannelId = "LOCATION SERVICE CHANNEL"
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private var minDistance = 10f // Minimum distance between location updates (meters)
    private val speedThreshold = 5.0f

    private val saveLocationDataTOLocalDelay: Long = 15 * 60 * 1000
//    private val saveLocationDataTOLocalDelay: Long = 60 * 1000

    override fun onBind(intent: Intent): IBinder? {
        log("Some component want to bind with the service")
        // We don't provide binding, so return null
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand executed with startId: $startId")
        if (intent != null) {
            val action = intent.action
            log("using an intent with action $action")
            when (action) {
                Actions.START.name -> startService()
                Actions.STOP.name -> saveDataToServer(true)
                else -> log("This should never happen. No action in the received intent")
            }
        } else {
            log("with a null intent. It has been probably restarted by the system.")
        }

        // by returning this we make sure the service is restarted if the system kills the service
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        val notification = createNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION);
        } else {
            startForeground(1, notification);
        }
        locationManager =
                this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        this.registerReceiver(
                gpsReceiver,
                IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        )
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onDestroy() {
        try {
            if (locationListener != null) {
                fusedLocationProviderClient?.removeLocationUpdates(locationListener!!)
            }
            this.unregisterReceiver(gpsReceiver)
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        val restartServiceIntent = Intent(applicationContext, EndlessService::class.java).also {
            it.setPackage(packageName)
        };
        val restartServicePendingIntent: PendingIntent =
                PendingIntent.getService(this, 1, restartServiceIntent, PendingIntent.FLAG_IMMUTABLE);
        applicationContext.getSystemService(Context.ALARM_SERVICE);
        val alarmService: AlarmManager =
                applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager;
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent
        )
    }

    private fun startService() {
        if (isServiceStarted) return
        Toast.makeText(this, "Start Live Location Tracking", Toast.LENGTH_SHORT).show()
        isServiceStarted = true

        setServiceState(this, ServiceState.STARTED)

        // we need this lock so our service gets not affected by Doze Mode
        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
                acquire()
            }
        }

        val locationRequest = LocationRequest.create().apply {
            interval = 5000 // Request location updates every 5 seconds
            fastestInterval = 1000 // Set the fastest interval for location updates
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY // Set the desired accuracy level
        }

        // Initialize location listener with filtering
        class LocationListenerWithFilter(private val speedThreshold: Float) : LocationListener {
            private var previousLocation: Location? = null
            override fun onLocationChanged(location: Location) {
                // This method will be called when the location changes
                log("getting location ${location.latitude} ${location.longitude}")

                if (SharedPref.getInstance().getBoolean(AppConstant.SHARE_LOCATION_DATA_FOR_FIRST_TIME, false).not()) {

                    val firebaseModel = LiveLocationDataModel()
                    firebaseModel.latitude = location.latitude
                    firebaseModel.longitude = location.longitude
                    firebaseModel.dateTime =
                            DateFormatHelper.convertDateToIsoUTCFormat(Calendar.getInstance().time)
                    addDataToSharedPreference(firebaseModel)
                    saveDataToServer(false)
                    SharedPref.getInstance().putBoolean(AppConstant.SHARE_LOCATION_DATA_FOR_FIRST_TIME, true)
                }

                if (Utils.isMockLocation(location).not() &&
                        location.speed > speedThreshold &&
                        previousLocation?.let { location.distanceTo(it) >= minDistance } == true
                ) {
                    previousLocation = location // Update previous location for distance check
                    log("getting live location with valid speed ${location.speed} and distance ${location.distanceTo(location)} :- ${location.latitude} ${location.longitude}")

                    val firebaseModel = LiveLocationDataModel()
                    firebaseModel.latitude = location.latitude
                    firebaseModel.longitude = location.longitude
                    firebaseModel.dateTime =
                            DateFormatHelper.convertDateToIsoUTCFormat(Calendar.getInstance().time)
                    addDataToSharedPreference(firebaseModel)
                }
                if (previousLocation == null) {
                    previousLocation = location
                }
            }
        }

        locationListener = LocationListenerWithFilter(speedThreshold)

        if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationProviderClient?.requestLocationUpdates(
                    locationRequest, locationListener!!, Looper.myLooper()
            )
        }

        handler = Handler(Looper.myLooper()!!)
        runnable = object : Runnable {
            override fun run() {
                CoroutineScope(Dispatchers.IO).launch {
                    saveDataToServer(false)
                }
                handler?.postDelayed(this, saveLocationDataTOLocalDelay)
            }
        }

        handler?.postDelayed(runnable!!, saveLocationDataTOLocalDelay)
    }

    private fun addDataToSharedPreference(firebaseModel: LiveLocationDataModel) {

        try {

            val liveLocationListModel = Gson().fromJson(
                    SharedPref.getInstance().getString(AppConstant.LOCATION),
                    SaveLiveLocationModel::class.java
            )

            if (liveLocationListModel?.data.isNullOrEmpty().not()) {
                val totalList = liveLocationListModel.data

                val locationModel = totalList!![totalList.size - 1]

                var list: ArrayList<LiveLocationDataModel> = ArrayList()

                if (locationModel.locationData.isNullOrEmpty().not()) {
                    val size = locationModel.locationData?.size
                    list = locationModel.locationData!!
                    if (DateFormatHelper.isDate1EqualThenDate2(list[size!!.minus(1)].dateTime,
                            DateFormatHelper.convertDateToCustomDateFormat(Calendar.getInstance().time,
                                    SimpleDateFormat("yyyy-MM-dd", Locale.US)))){

                        list = locationModel.locationData!!
                        list.add(firebaseModel)
                        locationModel.locationData = list

                        totalList[totalList.size - 1] = locationModel
                    } else {
                        val newLiveLocationListModel = LiveLocationListModel()
                        newLiveLocationListModel.batchId = generateUniqueId()
                        list.add(firebaseModel)
                        newLiveLocationListModel.locationData = list

                        totalList.add(newLiveLocationListModel)
                    }
                } else {
                    list.add(firebaseModel)
                    locationModel.locationData = list
                    totalList[totalList.size - 1] = locationModel
                }


                liveLocationListModel.data = totalList

                SharedPref.getInstance().putModelClass(AppConstant.LOCATION, liveLocationListModel)
            } else {
                val saveLiveLocationModel = SaveLiveLocationModel()
                val listOfLiveLocationData = ArrayList<LiveLocationListModel>()

                val newLiveLocationListModel = LiveLocationListModel()
                newLiveLocationListModel.batchId = generateUniqueId()

                val list = ArrayList<LiveLocationDataModel>()
                list.add(firebaseModel)

                newLiveLocationListModel.locationData = list

                listOfLiveLocationData.add(newLiveLocationListModel)

                saveLiveLocationModel.data = listOfLiveLocationData

                SharedPref.getInstance().putModelClass(AppConstant.LOCATION, saveLiveLocationModel)
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    private fun stopService() {
        try {
            Toast.makeText(this, "Stop Live Location Tracking", Toast.LENGTH_SHORT).show()
            isServiceStarted = false
            setServiceState(this, ServiceState.STOPPED)

            SharedPref.getInstance().putBoolean(AppConstant.SHARE_LOCATION_DATA_FOR_FIRST_TIME, false)

            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }

            if (fusedLocationProviderClient != null) {
                fusedLocationProviderClient?.removeLocationUpdates(locationListener!!)
                        ?.addOnSuccessListener {
                            // Location updates removed successfully
                            log("removing fusedLocationProviderClient")
                        }
                        ?.addOnFailureListener { e ->
                            // Failed to remove location updates
                            log("Failed to remove location updates: ${e.message}")
                        }
            } else {
                log("fusedLocationProviderClient is null")
            }

            handler?.removeCallbacks(runnable!!)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_REMOVE)
            } else {
                stopForeground(true)
            }

            stopSelf()
        } catch (e: Exception) {
            log("Service stopped without being started: ${e.message}")
            if (fusedLocationProviderClient != null && locationListener != null) {
                fusedLocationProviderClient?.removeLocationUpdates(locationListener!!)
            }
            stopSelf()
        }

    }

    private val gpsReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action!!.matches(LocationManager.PROVIDERS_CHANGED_ACTION.toRegex())) {
                val wasGPSOn: Boolean = Utils.isGpsOn(locationManager)
                if (wasGPSOn.not()) {
                    saveDataToServer(true)
                }
            }
        }
    }

    private fun saveDataToServer(destroyService: Boolean) {
        val liveLocationListModel = Gson().fromJson(
                SharedPref.getInstance().getString(AppConstant.LOCATION),
                SaveLiveLocationModel::class.java
        )

        if (liveLocationListModel != null) {
            if (liveLocationListModel.data.isNullOrEmpty().not()) {
                val counter = 0
                val totalList = liveLocationListModel.data

                if (totalList != null) {
                    updateDataOnServer(totalList[0], totalList, destroyService, counter)
                }

            }
        } else if (destroyService) {
            stopService()
        }
    }

    private fun updateDataOnServer(liveLocationListModel: LiveLocationListModel?, totalList: ArrayList<LiveLocationListModel>, destroyService: Boolean, counter: Int) {
        var newCounter: Int
        var userId = 0
        if (SharedPref.getInstance().getString(AppConstant.USER_ID).isNotEmpty()) {
            userId = SharedPref.getInstance().getString(AppConstant.USER_ID).toInt()
        }

        val id = SharedPref.getInstance().getInt(SharePrefConstant.ORG_ID)
        val uploadCred: Call<GenericResponseModel> =
                RetrofitClient.apiInterface.pushLiveLocationData(id, userId, liveLocationListModel)

        uploadCred.enqueue(object : NetworkCallback<GenericResponseModel?>() {
            override fun onSuccess(t: GenericResponseModel?) {
                log("updated data on server")
                newCounter = counter + 1

                if (totalList.size > newCounter) {
                    updateDataOnServer(totalList[newCounter], totalList, destroyService, newCounter)
                } else {
                    SharedPref.getInstance().putModelClass(AppConstant.LOCATION, null)
                    if (destroyService) {
                        log("stopping service after success from api")
                        stopService()
                    }
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                log("Getting failure from server $failureResponse")
                try {
                    if (failureResponse?.errorCode == 403) {
                        stopService()
                    }
                    if (destroyService) {
                        stopService()
                    } else {
                        addNewDataToPreference()
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }

            override fun onError(t: Throwable?) {
                log("Getting error from server $t")
                if (destroyService) {
                    stopService()
                } else {
                    addNewDataToPreference()
                }
            }
        })
    }

    private fun addNewDataToPreference() {
        val liveLocationListModel = Gson().fromJson(
                SharedPref.getInstance().getString(AppConstant.LOCATION),
                SaveLiveLocationModel::class.java
        )

        if (liveLocationListModel?.data.isNullOrEmpty().not()) {
            val totalList = liveLocationListModel.data

            val locationModel = LiveLocationListModel()
            locationModel.batchId = generateUniqueId()

            totalList?.add(locationModel)

            liveLocationListModel.data = totalList

            SharedPref.getInstance().putModelClass(AppConstant.LOCATION, liveLocationListModel)
        }
    }

    private fun createNotification(): Notification {
        // depending on the Android API that we're dealing with we will have
        // to use a specific method to create the notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                    notificationChannelId,
                    "Location Service notifications channel",
                    NotificationManager.IMPORTANCE_HIGH
            ).let {
                it.description = "Location Service channel"
                it.enableLights(true)
                it.lightColor = Color.RED
                it.enableVibration(true)
                it.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                it
            }
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent: PendingIntent =
                Intent(this, SalesMainActivity::class.java).let { notificationIntent ->
                    PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
                }

        val builder: Notification.Builder =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
                        this, notificationChannelId
                ) else Notification.Builder(this)

        return builder.setContentTitle("Rupyz Live Location")
                .setContentText("Location sharing in progress")
                .setContentIntent(pendingIntent).setCategory(Notification.CATEGORY_SERVICE)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setSmallIcon(R.mipmap.ic_live_location_notification)
                .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
                .build()
    }
}
