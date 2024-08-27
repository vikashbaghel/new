package com.app.rupyz.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.app.rupyz.generic.helper.Actions
import com.app.rupyz.generic.helper.log

class StartReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED && getServiceState(context) == ServiceState.STARTED) {
            Intent(context, EndlessService::class.java).also {
                it.action = Actions.START.name
                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) &&  (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S)  )  {
                    context.startForegroundService(it)
                    log("Starting the service in >=26 Mode from a BroadcastReceiver")
                    return
                }else if  (Build.VERSION.SDK_INT > Build.VERSION_CODES.S){
                    return
                }
                log("Starting the service in < 26 Mode from a BroadcastReceiver")
                context.startService(it)
            }
        }
    }
}
