package com.app.rupyz.sales.attendance

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.rupyz.model_kt.AttendanceDataItem
import com.app.rupyz.model_kt.AttendanceResponseModel
import com.app.rupyz.model_kt.CustomerFollowUpResponseModel
import com.app.rupyz.model_kt.CustomerFollowUpSingleResponseModel
import com.app.rupyz.model_kt.UpdateAttendanceResponseModel

class AttendanceViewModel : ViewModel() {
    var attendanceListLiveData = MutableLiveData<AttendanceResponseModel>()
    var attendanceDetailsLiveData = MutableLiveData<CustomerFollowUpSingleResponseModel>()
    var updateAttendanceLiveData = MutableLiveData<UpdateAttendanceResponseModel>()
    
    fun getAttendanceDetails(attendanceId: Int) {
        AttendanceRepository().getAttendanceDetails(attendanceDetailsLiveData,attendanceId)
    }
    
    fun getAttendanceList(month: String, year: String) {
        AttendanceRepository().getAttendanceList(
            attendanceListLiveData,
            month, year
        )
    }

    fun updateAttendance(model: AttendanceDataItem) {
        AttendanceRepository().updateAttendance(
            updateAttendanceLiveData,
            model
        )
    }

    fun deleteAttendance(id: Int) {
        AttendanceRepository().deleteAttendance(
            updateAttendanceLiveData,
            id
        )
    }
}