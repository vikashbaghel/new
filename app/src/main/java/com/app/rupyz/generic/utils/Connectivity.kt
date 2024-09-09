package com.app.rupyz.generic.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast

class Connectivity {
    companion object {
        fun hasInternetConnection(context: Context): Boolean {
            return true
//            if (SharedPref.getInstance().getBoolean(AppConstant.ENABLE_ORG_OFFLINE_MODE, false).not()) {
//            }
//            else {
//                val connectivityManager = context.getSystemService(
//                        Context.CONNECTIVITY_SERVICE
//                ) as ConnectivityManager
//
//                val activeNetwork = connectivityManager.activeNetwork ?: return false
//                val capabilities =
//                        connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
//                return when {
//                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
//                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
//                        val info = getNetworkInfo(context)
//                        if (info != null) {
//                            when (info.subtype) {
//                                TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_EDGE -> false // ~ 50-100 kbps
//                                TelephonyManager.NETWORK_TYPE_CDMA -> false // ~ 14-64 kbps
//                                TelephonyManager.NETWORK_TYPE_EVDO_0 -> true // ~ 400-1000 kbps
//                                TelephonyManager.NETWORK_TYPE_EVDO_A -> true // ~ 600-1400 kbps
//                                TelephonyManager.NETWORK_TYPE_GPRS -> false // ~ 100 kbps
//                                TelephonyManager.NETWORK_TYPE_HSDPA -> true // ~ 2-14 Mbps
//                                TelephonyManager.NETWORK_TYPE_HSPA -> true // ~ 700-1700 kbps
//                                TelephonyManager.NETWORK_TYPE_HSUPA -> true // ~ 1-23 Mbps
//                                TelephonyManager.NETWORK_TYPE_UMTS -> true // ~ 400-7000 kbps
//                                TelephonyManager.NETWORK_TYPE_EHRPD -> true // ~ 1-2 Mbps
//                                TelephonyManager.NETWORK_TYPE_EVDO_B -> true // ~ 5 Mbps
//                                TelephonyManager.NETWORK_TYPE_HSPAP -> true // ~ 10-20 Mbps
//                                TelephonyManager.NETWORK_TYPE_IDEN -> false // ~25 kbps
//                                TelephonyManager.NETWORK_TYPE_LTE -> true // ~ 10+ Mbps
//                                TelephonyManager.NETWORK_TYPE_UNKNOWN -> false
//                                else -> false
//                            }
//                        } else {
//                            return true
//                        }
//                    }
//
//                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
//
//                    else -> false
//                }
//            }
        }

        private fun getNetworkInfo(context: Context): NetworkInfo? {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo
        }
    }
}