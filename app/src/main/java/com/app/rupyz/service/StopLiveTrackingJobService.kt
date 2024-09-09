package com.app.rupyz.service

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.os.Build
import com.app.rupyz.generic.helper.Actions


@SuppressLint("SpecifyJobSchedulerIdRange")
class StopLiveTrackingJobService : JobService() {
    private fun actionOnService(context: Context) {
        if (getServiceState(context) == ServiceState.STOPPED) return

        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.S)) {
            Intent(context, EndlessService::class.java).also {
                it.action = Actions.STOP.name
                context.startForegroundService(it)
                return
            }
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