package com.app.rupyz.service

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.app.rupyz.generic.helper.Actions
import com.app.rupyz.generic.helper.log


@SuppressLint("SpecifyJobSchedulerIdRange")
class StopLiveTrackingJobService : JobService() {
    private fun actionOnService(context: Context) {
        if (getServiceState(context) == ServiceState.STOPPED) return
        Intent(context, EndlessService::class.java).also {
            it.action = Actions.STOP.name
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) &&  (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S)  )  {
                context.startForegroundService(it)
                return
            }else if  (Build.VERSION.SDK_INT > Build.VERSION_CODES.S){
                return
            }
            log("Starting the service in < 26 Mode")
            context.startService(it)
        }
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        // This method will be called when the job is scheduled to run
        // Implement your desired function here
        actionOnService(this.applicationContext)
        // Call your function here
        // Example: MyFunctionClass.doSomething();

        // Remember to call jobFinished when the job is done
        jobFinished(params, false)
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        // This method is called if the job is interrupted
        return false // Return true if you want to reschedule the job
    }
}