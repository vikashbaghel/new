package com.app.rupyz.service

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService
import com.app.rupyz.R
import com.app.rupyz.databse.DatabaseLogManager
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.helper.fromJson
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.AddCheckInOutModel
import com.app.rupyz.model_kt.CustomerFollowUpDataItem
import com.app.rupyz.model_kt.SaveAttendanceModel
import com.google.gson.Gson
import java.util.Calendar

@SuppressLint("SpecifyJobSchedulerIdRange")
class MarkEndDayJobService : JobService() {
    private fun actionOnService() {
        val attendance: SaveAttendanceModel? = Gson().fromJson(
                SharedPref.getInstance().getString(AppConstant.SAVE_ATTENDANCE_PREF))
        if (attendance != null && attendance.checkOut == null) {
            val model = AddCheckInOutModel()
            model.action = AppConstant.ATTENDANCE_CHECK_OUT
            model.attendanceType = AppConstant.ACTIVITY_TYPE_FULL_DAY
            model.createdAt = DateFormatHelper.convertDateToIsoUTCFormat(Calendar.getInstance().time)
            model.createdByName = SharedPref.getInstance().getString(AppConstant.USER_NAME)
            DatabaseLogManager.getInstance().addOfflineAttendance(null, model)

            SharedPref.getInstance().putModelClass(AppConstant.SAVE_ATTENDANCE_PREF,
                    SaveAttendanceModel(
                            date = DateFormatHelper.convertDateToIsoFormat(Calendar.getInstance().time),
                            checkIn = null,
                            checkOut = true
                    )
            )
        }
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        // This method will be called when the job is scheduled to run
        // Implement your desired function here
        actionOnService()
        // Call your function here

        // Remember to call jobFinished when the job is done
        jobFinished(params, false)
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        // This method is called if the job is interrupted
        return false // Return true if you want to reschedule the job
    }
}