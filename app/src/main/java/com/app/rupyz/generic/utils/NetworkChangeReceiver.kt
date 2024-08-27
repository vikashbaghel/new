package com.app.rupyz.generic.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

class NetworkChangeReceiver(private val listener: NetworkChangeListener) : BroadcastReceiver() {

    private var previousNetworkState: Boolean = true
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            val connectivityManager =
                    context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val networkInfo = connectivityManager?.activeNetworkInfo
            val isConnected = networkInfo != null && networkInfo.isConnected

            if (isConnected != previousNetworkState) {
                previousNetworkState = isConnected
                listener.onNetworkStatusChanged(isConnected)
            }
        }
    }

    interface NetworkChangeListener {
        fun onNetworkStatusChanged(networkAvailable: Boolean)
    }
}