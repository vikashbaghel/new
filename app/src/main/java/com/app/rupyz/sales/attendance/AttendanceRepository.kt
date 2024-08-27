package com.app.rupyz.sales.attendance

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.rupyz.generic.base.FailureResponse
import com.app.rupyz.generic.base.NetworkCallback
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.AttendanceDataItem
import com.app.rupyz.model_kt.AttendanceResponseModel
import com.app.rupyz.model_kt.CustomerFollowUpResponseModel
import com.app.rupyz.model_kt.CustomerFollowUpSingleResponseModel
import com.app.rupyz.model_kt.UpdateAttendanceResponseModel
import com.app.rupyz.retrofit.RetrofitClient
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call

class AttendanceRepository {

    fun getAttendanceList(
        liveData: MutableLiveData<AttendanceResponseModel>, month: String, year: String
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<AttendanceResponseModel> =
            RetrofitClient.apiInterface.getAttendanceList(id, month, year)

        uploadCred.enqueue(object : NetworkCallback<AttendanceResponseModel?>() {
            override fun onSuccess(t: AttendanceResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = AttendanceResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    genericResponseModel.error = true
                    genericResponseModel.message = jsonObj?.get("message")?.asString

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(genericResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun updateAttendance(
        liveData: MutableLiveData<UpdateAttendanceResponseModel>,
        model: AttendanceDataItem
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<UpdateAttendanceResponseModel> =
            RetrofitClient.apiInterface.updateAttendance(id, model)

        uploadCred.enqueue(object : NetworkCallback<UpdateAttendanceResponseModel?>() {
            override fun onSuccess(t: UpdateAttendanceResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = UpdateAttendanceResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    genericResponseModel.error = true
                    genericResponseModel.message = jsonObj?.get("message")?.asString

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(genericResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun deleteAttendance(
        liveData: MutableLiveData<UpdateAttendanceResponseModel>,
        attendanceId: Int
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<UpdateAttendanceResponseModel> =
            RetrofitClient.apiInterface.deleteAttendance(id, attendanceId)

        uploadCred.enqueue(object : NetworkCallback<UpdateAttendanceResponseModel?>() {
            override fun onSuccess(t: UpdateAttendanceResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = UpdateAttendanceResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    genericResponseModel.error = true
                    genericResponseModel.message = jsonObj?.get("message")?.asString

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(genericResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }
    
    fun getAttendanceDetails(liveData: MutableLiveData<CustomerFollowUpSingleResponseModel> , attendanceId : Int) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<CustomerFollowUpSingleResponseModel> = RetrofitClient.apiInterface.getAttendanceDetails(id, attendanceId)
        
        uploadCred.enqueue(object : NetworkCallback<CustomerFollowUpSingleResponseModel?>() {
            override fun onSuccess(t: CustomerFollowUpSingleResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }
            
            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = CustomerFollowUpSingleResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?
                    
                    genericResponseModel.error = true
                    genericResponseModel.message = jsonObj?.get("message")?.asString
                    
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(genericResponseModel)
                }
            }
            
            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

}