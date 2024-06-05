package com.app.rupyz.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.widget.Toast


class GpsLocationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action!! == LocationManager.PROVIDERS_CHANGED_ACTION) {
            Toast.makeText(
                context, "in android.location.PROVIDERS_CHANGED",
                Toast.LENGTH_SHORT
            ).show()
//            val pushIntent = Intent(context, LocalService::class.java)
//            context!!.startService(pushIntent)
        }
    }
}