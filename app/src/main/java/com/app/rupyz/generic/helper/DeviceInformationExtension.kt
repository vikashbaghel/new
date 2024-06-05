package com.app.rupyz.generic.helper

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import com.app.rupyz.model_kt.DeviceInformationModel

fun Context.getBatteryInformation(): Pair<Int?, Boolean> {
    val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    val batteryStatus: Intent? = registerReceiver(null, intentFilter)

    val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
    val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

    val batteryPercentage = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)

    return Pair(batteryPercentage, isCharging)
}

fun Context.isBatteryOptimizationEnabled(): Boolean {
    val powerManager = getSystemService(Context.POWER_SERVICE) as? PowerManager
    return powerManager?.isPowerSaveMode == true
}

fun Context.getDeviceInformation(): DeviceInformationModel {
    val json = DeviceInformationModel()
    json.model = Build.MODEL
    json.os = "Android"
    json.manufacturer = Build.MANUFACTURER
    json.brand = Build.BRAND
    json.device = Build.DEVICE
    json.product = Build.PRODUCT
    json.ram = getAvailableRAM()
    return json
}

fun Context.getAvailableRAM(): Long {
    val activityManager = this.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
    val memoryInfo = android.app.ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memoryInfo)
    return memoryInfo.availMem
}

fun Context.isGpsEnabled(): Boolean {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}