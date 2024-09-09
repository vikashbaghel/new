package com.app.rupyz.sales.expense

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.rupyz.databse.DatabaseLogManager
import com.app.rupyz.generic.base.FailureResponse
import com.app.rupyz.generic.base.NetworkCallback
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.AddExpenseResponseModel
import com.app.rupyz.model_kt.AddTotalExpenseResponseModel
import com.app.rupyz.model_kt.ApprovalRequestResponseModel
import com.app.rupyz.model_kt.ExpenseDataItem
import com.app.rupyz.model_kt.ExpenseResponseModel
import com.app.rupyz.model_kt.ExpenseTrackerDataItem
import com.app.rupyz.model_kt.ExpenseTrackerResponseModel
import com.app.rupyz.retrofit.OfflineRetrofitClient
import com.app.rupyz.retrofit.RetrofitClient
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call

class ExpenseRepository {

    fun getTotalExpenseList(
            liveData: MutableLiveData<ExpenseTrackerResponseModel>,
            status: String,
            page: Int?,
            pageSize: Int?,
            offlineDataLastSyncedTime: String?
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<ExpenseTrackerResponseModel> = if (pageSize == null) {
            RetrofitClient.apiInterface.getTotalExpenseTrackerList(id, status, page, null, null, null)
        } else {
            RetrofitClient.apiInterface.getTotalExpenseTrackerList(id, status, page, pageSize, true, offlineDataLastSyncedTime)
        }

        uploadCred.enqueue(object : NetworkCallback<ExpenseTrackerResponseModel?>() {
            override fun onSuccess(t: ExpenseTrackerResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = ExpenseTrackerResponseModel()
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

    fun getOfflineTotalExpenseList(
            liveData: MutableLiveData<ExpenseTrackerResponseModel>,
            status: String,
            page: Int
    ) {
        DatabaseLogManager.getInstance().getOfflineTotalExpenseList(liveData, status, page)
    }

    fun getOfflineExpenseList(
            liveData: MutableLiveData<ExpenseResponseModel>,
            rtId: Int
    ) {
        DatabaseLogManager.getInstance().getOfflineExpenseList(liveData, rtId)
    }

    fun getTotalExpenseDetails(
            liveData: MutableLiveData<AddTotalExpenseResponseModel>, rtId: Int
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<AddTotalExpenseResponseModel> =
                RetrofitClient.apiInterface.getTotalExpenseTrackerDetails(id, rtId)

        uploadCred.enqueue(object : NetworkCallback<AddTotalExpenseResponseModel?>() {
            override fun onSuccess(t: AddTotalExpenseResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = AddTotalExpenseResponseModel()
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

    fun getOfflineExpenseHeadDetails(
            liveData: MutableLiveData<AddTotalExpenseResponseModel>, rtId: Int
    ) {
        DatabaseLogManager.getInstance().getOfflineExpenseHeadDetails(liveData, rtId)
    }

    fun getExpenseDetails(
            liveData: MutableLiveData<AddExpenseResponseModel>, rem_id: Int
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<AddExpenseResponseModel> =
                RetrofitClient.apiInterface.getExpenseTrackerDetails(id, rem_id)

        uploadCred.enqueue(object : NetworkCallback<AddExpenseResponseModel?>() {
            override fun onSuccess(t: AddExpenseResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = AddExpenseResponseModel()
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

    fun getExpenseList(
            liveData: MutableLiveData<ExpenseResponseModel>,
            rtId: Int?,
            page: Int?,
            pageSize: Int?,
            offlineDataLastSyncedTime: String?
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<ExpenseResponseModel> =
                if (pageSize == null) {
                    RetrofitClient.apiInterface.getExpenseList(id, rtId, null, null,null, null)
                } else {
                    OfflineRetrofitClient.offlineApiInterface.getExpenseList(id, null, page, pageSize, true, offlineDataLastSyncedTime)
                }

        uploadCred.enqueue(object : NetworkCallback<ExpenseResponseModel?>() {
            override fun onSuccess(t: ExpenseResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = ExpenseResponseModel()
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


    fun getApprovalRequestList(
            liveData: MutableLiveData<ApprovalRequestResponseModel>,
            status: String
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<ApprovalRequestResponseModel> =
                RetrofitClient.apiInterface.getApprovalRequestList(id, status)

        uploadCred.enqueue(object : NetworkCallback<ApprovalRequestResponseModel?>() {
            override fun onSuccess(t: ApprovalRequestResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = ApprovalRequestResponseModel()
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

    fun addTotalExpenseTracker(
            liveData: MutableLiveData<AddTotalExpenseResponseModel>, model: ExpenseTrackerDataItem
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<AddTotalExpenseResponseModel> =
                RetrofitClient.apiInterface.addTotalExpenseTracker(id, model)

        uploadCred.enqueue(object : NetworkCallback<AddTotalExpenseResponseModel?>() {
            override fun onSuccess(t: AddTotalExpenseResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = AddTotalExpenseResponseModel()
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

    fun addExpenseTracker(
            liveData: MutableLiveData<AddExpenseResponseModel>, model: ExpenseDataItem
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<AddExpenseResponseModel> =
                RetrofitClient.apiInterface.addExpense(id, model)

        uploadCred.enqueue(object : NetworkCallback<AddExpenseResponseModel?>() {
            override fun onSuccess(t: AddExpenseResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = AddExpenseResponseModel()
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

    fun deleteExpensesTracker(
            liveData: MutableLiveData<AddTotalExpenseResponseModel>, rt_id: Int
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<AddTotalExpenseResponseModel> =
                RetrofitClient.apiInterface.deleteExpenseTracker(id, rt_id)

        uploadCred.enqueue(object : NetworkCallback<AddTotalExpenseResponseModel?>() {
            override fun onSuccess(t: AddTotalExpenseResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = AddTotalExpenseResponseModel()
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

    fun deleteExpense(
            liveData: MutableLiveData<AddExpenseResponseModel>, rem_id: Int
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<AddExpenseResponseModel> =
                RetrofitClient.apiInterface.deleteExpense(id, rem_id)

        uploadCred.enqueue(object : NetworkCallback<AddExpenseResponseModel?>() {
            override fun onSuccess(t: AddExpenseResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = AddExpenseResponseModel()
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

    fun updateExpensesTrackerStatus(
            liveData: MutableLiveData<AddTotalExpenseResponseModel>,
            rt_id: Int,
            status: String,
            reason: String
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)

        val jsonObject = JsonObject()
        jsonObject.addProperty("status", status)
        if (status == AppConstant.REJECTED && reason.isNotEmpty()) {
            jsonObject.addProperty("comments", reason)
        }

        val uploadCred: Call<AddTotalExpenseResponseModel> =
                RetrofitClient.apiInterface.updateExpenseTrackerStatus(id, rt_id, jsonObject)

        uploadCred.enqueue(object : NetworkCallback<AddTotalExpenseResponseModel?>() {
            override fun onSuccess(t: AddTotalExpenseResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = AddTotalExpenseResponseModel()
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

    fun updateExpensesTracker(
            liveData: MutableLiveData<AddTotalExpenseResponseModel>,
            rt_id: Int,
            model: ExpenseTrackerDataItem
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)

        val uploadCred: Call<AddTotalExpenseResponseModel> =
                RetrofitClient.apiInterface.updateExpenseTracker(id, rt_id, model)

        uploadCred.enqueue(object : NetworkCallback<AddTotalExpenseResponseModel?>() {
            override fun onSuccess(t: AddTotalExpenseResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = AddTotalExpenseResponseModel()
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

    fun updateExpenses(
            liveData: MutableLiveData<AddExpenseResponseModel>, rt_id: Int, model: ExpenseDataItem
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)

        val uploadCred: Call<AddExpenseResponseModel> =
                RetrofitClient.apiInterface.updateExpense(id, rt_id, model)

        uploadCred.enqueue(object : NetworkCallback<AddExpenseResponseModel?>() {
            override fun onSuccess(t: AddExpenseResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = AddExpenseResponseModel()
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