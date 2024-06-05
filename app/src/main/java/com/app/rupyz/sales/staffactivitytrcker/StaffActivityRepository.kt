package com.app.rupyz.sales.staffactivitytrcker

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.rupyz.generic.base.FailureResponse
import com.app.rupyz.generic.base.NetworkCallback
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.AddCheckInOutModel
import com.app.rupyz.model_kt.CheckInOutResponseModel
import com.app.rupyz.model_kt.CustomFormCreationModel
import com.app.rupyz.model_kt.CustomerFeedbackListResponseModel
import com.app.rupyz.model_kt.CustomerFollowUpDataItem
import com.app.rupyz.model_kt.CustomerFollowUpListResponseModel
import com.app.rupyz.model_kt.CustomerFollowUpResponseModel
import com.app.rupyz.model_kt.DailySalesReportResponseModel
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.model_kt.LiveLocationResponseModel
import com.app.rupyz.model_kt.StaffTrackingDetailsResponseModel
import com.app.rupyz.model_kt.TeamTrackingDetailsResponseModel
import com.app.rupyz.model_kt.UploadOfflineAttendanceModel
import com.app.rupyz.retrofit.RetrofitClient
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call

class StaffActivityRepository {

    fun addFeedbackFollowUp(
            liveData: MutableLiveData<CustomerFollowUpResponseModel>, model: CustomerFollowUpDataItem
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<CustomerFollowUpResponseModel> =
                RetrofitClient.apiInterface.addFeedbackFollowUp(id, model)

        uploadCred.enqueue(object : NetworkCallback<CustomerFollowUpResponseModel?>() {
            override fun onSuccess(t: CustomerFollowUpResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = CustomerFollowUpResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    genericResponseModel.error = true
                    genericResponseModel.errorCode = failureResponse?.errorCode
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

    fun addAttendance(
            liveData: MutableLiveData<GenericResponseModel>, model: AddCheckInOutModel?
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<GenericResponseModel> =
                RetrofitClient.apiInterface.addAttendance(id, model)

        uploadCred.enqueue(object : NetworkCallback<GenericResponseModel?>() {
            override fun onSuccess(t: GenericResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = GenericResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    genericResponseModel.error = true
                    genericResponseModel.errorCode = failureResponse?.errorCode
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

    fun getAttendance(
            liveData: MutableLiveData<CheckInOutResponseModel>) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<CheckInOutResponseModel> =
                RetrofitClient.apiInterface.getAttendance(id)

        uploadCred.enqueue(object : NetworkCallback<CheckInOutResponseModel?>() {
            override fun onSuccess(t: CheckInOutResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = CheckInOutResponseModel()
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


    fun addOfflineAttendance(
            liveData: MutableLiveData<GenericResponseModel>, model: UploadOfflineAttendanceModel?
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<GenericResponseModel> =
                RetrofitClient.apiInterface.addOfflineAttendance(id, model)

        uploadCred.enqueue(object : NetworkCallback<GenericResponseModel?>() {
            override fun onSuccess(t: GenericResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = GenericResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    genericResponseModel.error = true
                    genericResponseModel.errorCode = failureResponse?.errorCode
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

    fun getCustomerFeedback(
            liveData: MutableLiveData<CustomerFollowUpListResponseModel>,
            customerId: Int,
            customerType: String,
            currentPage: Int
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<CustomerFollowUpListResponseModel> = if (customerType == AppConstant.LEAD_FEEDBACK) {
            RetrofitClient.apiInterface.getCustomerFeedbackList(id, null, customerId, true, currentPage)
        } else {
            RetrofitClient.apiInterface.getCustomerFeedbackList(id, customerId, null, true, currentPage)
        }

        uploadCred.enqueue(object : NetworkCallback<CustomerFollowUpListResponseModel?>() {
            override fun onSuccess(t: CustomerFollowUpListResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = CustomerFollowUpListResponseModel()
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

    fun getCustomerFeedbackDetails(
            liveData: MutableLiveData<CustomerFollowUpResponseModel>,
            feedbackId: Int,
            notificationOrgId: Int?
    ) {
        val orgId: Int = if (notificationOrgId != null && notificationOrgId != 0) {
            notificationOrgId
        } else {
            SharedPref.getInstance().getInt(ORG_ID)
        }

        val uploadCred: Call<CustomerFollowUpResponseModel> =
                RetrofitClient.apiInterface.getCustomerFeedbackDetails(orgId, feedbackId)

        uploadCred.enqueue(object : NetworkCallback<CustomerFollowUpResponseModel?>() {
            override fun onSuccess(t: CustomerFollowUpResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = CustomerFollowUpResponseModel()
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

    fun updateCustomerFeedbackDetails(
            liveData: MutableLiveData<CustomerFollowUpResponseModel>,
            feedbackId: Int,
            model: CustomerFollowUpDataItem
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<CustomerFollowUpResponseModel> =
                RetrofitClient.apiInterface.updateCustomerFeedbackDetails(id, feedbackId, model)

        uploadCred.enqueue(object : NetworkCallback<CustomerFollowUpResponseModel?>() {
            override fun onSuccess(t: CustomerFollowUpResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = CustomerFollowUpResponseModel()
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


    fun deleteCustomerFeedbackDetails(
            liveData: MutableLiveData<CustomerFollowUpResponseModel>,
            feedbackId: Int
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<CustomerFollowUpResponseModel> =
                RetrofitClient.apiInterface.deleteCustomerFeedbackDetails(id, feedbackId)

        uploadCred.enqueue(object : NetworkCallback<CustomerFollowUpResponseModel?>() {
            override fun onSuccess(t: CustomerFollowUpResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = CustomerFollowUpResponseModel()
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

    fun getStaffTrackingDetails(
            liveData: MutableLiveData<StaffTrackingDetailsResponseModel>,
            filterDate: String,
            staffId: Int?,
            page: Int
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<StaffTrackingDetailsResponseModel> =
                RetrofitClient.apiInterface.getStaffTrackingDetails(id, filterDate, staffId)

        uploadCred.enqueue(object : NetworkCallback<StaffTrackingDetailsResponseModel?>() {
            override fun onSuccess(t: StaffTrackingDetailsResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = StaffTrackingDetailsResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    genericResponseModel.error = true
                    genericResponseModel.errorCode = failureResponse?.errorCode
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

    fun getTeamTrackingActivity(
            liveData: MutableLiveData<TeamTrackingDetailsResponseModel>,
            filterDate: String,
            page: Int
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<TeamTrackingDetailsResponseModel> =
                RetrofitClient.apiInterface.getTeamTrackingDetails(id, filterDate, page)

        uploadCred.enqueue(object : NetworkCallback<TeamTrackingDetailsResponseModel?>() {
            override fun onSuccess(t: TeamTrackingDetailsResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = TeamTrackingDetailsResponseModel()
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

    fun getFollowUpList(liveData: MutableLiveData<CustomerFeedbackListResponseModel>) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<CustomerFeedbackListResponseModel> =
                RetrofitClient.apiInterface.getFollowUpList(id)

        uploadCred.enqueue(object : NetworkCallback<CustomerFeedbackListResponseModel?>() {
            override fun onSuccess(t: CustomerFeedbackListResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = CustomerFeedbackListResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    genericResponseModel.error = true
                    genericResponseModel.errorCode = failureResponse?.errorCode
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

    fun getDailySalesReport(
            liveData: MutableLiveData<DailySalesReportResponseModel>,
            userId: Int?,
            date: String
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<DailySalesReportResponseModel> =
                RetrofitClient.apiInterface.getDailySalesReport(id, userId, date)

        uploadCred.enqueue(object : NetworkCallback<DailySalesReportResponseModel?>() {
            override fun onSuccess(t: DailySalesReportResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = DailySalesReportResponseModel()
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

    fun getLiveLocationData(
            liveData: MutableLiveData<LiveLocationResponseModel>,
            staffId: Int,
            date: String
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        var userId = 0
        userId = if (staffId != 0) {
            staffId
        } else {
            SharedPref.getInstance().getString(AppConstant.USER_ID).toInt()
        }
        val uploadCred: Call<LiveLocationResponseModel> =
                RetrofitClient.apiInterface.getLiveLocationData(id, userId, date)

        uploadCred.enqueue(object : NetworkCallback<LiveLocationResponseModel?>() {
            override fun onSuccess(t: LiveLocationResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = LiveLocationResponseModel()
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

    fun getCustomFormInputList(liveData: MutableLiveData<CustomFormCreationModel>, feedbackId: Int) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<CustomFormCreationModel> =
                RetrofitClient.apiInterface.getCustomFormInputList(id, feedbackId)

        uploadCred.enqueue(object : NetworkCallback<CustomFormCreationModel?>() {
            override fun onSuccess(t: CustomFormCreationModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = CustomFormCreationModel()
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