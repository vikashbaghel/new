package com.app.rupyz.generic.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.telephony.TelephonyManager
import java.net.InetSocketAddress
import java.net.Socket

class NetworkMonitor(private val context: Context) {

    private fun getNetworkInfo(context: Context): NetworkInfo? {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo
    }

    fun startListeningForNetworkChanges() {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(
            networkRequest,
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    val networkType = hasInternetConnection(context)
                    networkChangeListener?.onNetworkChanged(networkType)
                }

                override fun onLost(network: Network) {
                    networkChangeListener?.onNetworkChanged(NetworkType.NONE)
                }
            }
        )
    }

    interface NetworkChangeListener {
        fun onNetworkChanged(networkType: NetworkType)
    }

    private var networkChangeListener: NetworkChangeListener? = null

    fun setNetworkChangeListener(listener: NetworkChangeListener) {
        networkChangeListener = listener
    }


    fun stopListeningForNetworkChanges() {
        try {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.unregisterNetworkCallback(
                object : ConnectivityManager.NetworkCallback() {}
            )
        } catch (e: IllegalArgumentException){
            e.printStackTrace()
        }
    }

    fun hasInternetConnection(
        context: Context
    ): NetworkType {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return NetworkType.NONE
        val capabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return NetworkType.NONE

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                if (isInternetAvailable("8.8.8.8", 53)) {
                    NetworkType.WIFI
                } else {
                    NetworkType.NONE
                }
            }

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                val info = getNetworkInfo(context)
                val networkType = when (info?.subtype) {
                    TelephonyManager.NETWORK_TYPE_1xRTT,
                    TelephonyManager.NETWORK_TYPE_EDGE,
                    TelephonyManager.NETWORK_TYPE_CDMA,
                    TelephonyManager.NETWORK_TYPE_GPRS,
                    TelephonyManager.NETWORK_TYPE_IDEN -> NetworkType.NONE

                    TelephonyManager.NETWORK_TYPE_EVDO_0,
                    TelephonyManager.NETWORK_TYPE_EVDO_A,
                    TelephonyManager.NETWORK_TYPE_HSDPA,
                    TelephonyManager.NETWORK_TYPE_HSPA,
                    TelephonyManager.NETWORK_TYPE_HSUPA,
                    TelephonyManager.NETWORK_TYPE_UMTS,
                    TelephonyManager.NETWORK_TYPE_EHRPD,
                    TelephonyManager.NETWORK_TYPE_EVDO_B,
                    TelephonyManager.NETWORK_TYPE_HSPAP,
                    TelephonyManager.NETWORK_TYPE_LTE -> NetworkType.CELLULAR

                    else -> NetworkType.NONE
                }
                networkType
            }

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                NetworkType.ETHERNET
            }

            else -> {
                NetworkType.NONE
            }
        }
    }

    private fun isInternetAvailable(host: String, port: Int, timeoutMs: Int = 1500): Boolean {
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress(host, port), timeoutMs)
            socket.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    enum class NetworkType {
        WIFI,
        CELLULAR,
        ETHERNET,
        NONE
    }
}
