package com.app.rupyz.sales.lead

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.rupyz.databse.DatabaseLogManager
import com.app.rupyz.generic.base.FailureResponse
import com.app.rupyz.generic.base.NetworkCallback
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.*
import com.app.rupyz.retrofit.OfflineRetrofitClient
import com.app.rupyz.retrofit.RetrofitClient
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call

class LeadRepository {

    fun getAllCategoryList(
        liveData: MutableLiveData<LeadCategoryListResponseModel>,
        category: String,
        page: Int?,
        pageSize: Int?,
        offlineDataLastSyncedTime: String?
    ) {
        val uploadCred: Call<LeadCategoryListResponseModel> = if (pageSize == null) {
            RetrofitClient.apiInterface.getAllLeadCategoryList(
                SharedPref.getInstance().getInt(ORG_ID), category, page, null, null, null
            )
        } else {
            OfflineRetrofitClient.offlineApiInterface.getAllLeadCategoryList(
                SharedPref.getInstance().getInt(ORG_ID), "", page, pageSize, true, offlineDataLastSyncedTime
            )
        }

        uploadCred.enqueue(object : NetworkCallback<LeadCategoryListResponseModel?>() {
            override fun onSuccess(t: LeadCategoryListResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = LeadCategoryListResponseModel()
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

    fun getOfflineLeadCategoryList(
        liveData: MutableLiveData<LeadCategoryListResponseModel>,
    ) {
        DatabaseLogManager.getInstance().getLeadCategoryList(liveData)
    }

    fun getOfflineLeadList(
        liveData: MutableLiveData<LeadListResponseModel>,
        name: String,
        category: String,
        currentPage: Int
    ) {
        DatabaseLogManager.getInstance().getOfflineLeadList(liveData, name, category, currentPage)
    }

    fun createLeadCategory(
        liveData: MutableLiveData<LeadCategoryResponseModel>, jsonObject: JsonObject
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<LeadCategoryResponseModel> =
            RetrofitClient.apiInterface.creteLeadCategory(id, jsonObject)

        uploadCred.enqueue(object : NetworkCallback<LeadCategoryResponseModel?>() {
            override fun onSuccess(t: LeadCategoryResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = LeadCategoryResponseModel()
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

    fun createNewLead(
        liveData: MutableLiveData<AddLeadResponseModel>, jsonObject: LeadLisDataItem
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<AddLeadResponseModel> =
            RetrofitClient.apiInterface.creteNewLead(id, jsonObject)

        uploadCred.enqueue(object : NetworkCallback<AddLeadResponseModel?>() {
            override fun onSuccess(t: AddLeadResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = AddLeadResponseModel()
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

    fun updateLead(
        liveData: MutableLiveData<AddLeadResponseModel>, jsonObject: LeadLisDataItem, leadId: Int
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<AddLeadResponseModel> =
            RetrofitClient.apiInterface.updateLead(id, leadId, jsonObject)

        uploadCred.enqueue(object : NetworkCallback<AddLeadResponseModel?>() {
            override fun onSuccess(t: AddLeadResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = AddLeadResponseModel()
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


    fun getLeadInfo(
        liveData: MutableLiveData<AddLeadResponseModel>, leadId: Int
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<AddLeadResponseModel> =
            RetrofitClient.apiInterface.getLeadInfo(id, leadId)

        uploadCred.enqueue(object : NetworkCallback<AddLeadResponseModel?>() {
            override fun onSuccess(t: AddLeadResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = AddLeadResponseModel()
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

    fun deleteLead(
        liveData: MutableLiveData<AddLeadResponseModel>, leadId: Int
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<AddLeadResponseModel> =
            RetrofitClient.apiInterface.delteLead(id, leadId)

        uploadCred.enqueue(object : NetworkCallback<AddLeadResponseModel?>() {
            override fun onSuccess(t: AddLeadResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = AddLeadResponseModel()
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

    fun checkLeadMobileExist(
        liveData: MutableLiveData<GenericResponseModel>, mobile: String
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)

        val uploadCred: Call<GenericResponseModel> =
            RetrofitClient.apiInterface.checkLeadMobileNumberExist(id, mobile)

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


    fun getLeadList(
        liveData: MutableLiveData<LeadListResponseModel>,
        name: String,
        category: String,
        currentPage: Int,
        pageSize: Int?,
        offlineDataLastSyncedTime: String?
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<LeadListResponseModel> = if (pageSize == null) {
            RetrofitClient.apiInterface.getAllLeadList(
                id, name, category,
                currentPage, null, null, null
            )
        } else {
            OfflineRetrofitClient.offlineApiInterface.getAllLeadList(
                id,
                name,
                category,
                currentPage,
                pageSize,
                true, offlineDataLastSyncedTime
            )
        }

        uploadCred.enqueue(object : NetworkCallback<LeadListResponseModel?>() {
            override fun onSuccess(t: LeadListResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = LeadListResponseModel()
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

    fun getGstInfo(
        liveData: MutableLiveData<GstInfoResponseModel>, gst: String
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<GstInfoResponseModel> =
            RetrofitClient.apiInterface.getGstInfo(id, gst)

        uploadCred.enqueue(object : NetworkCallback<GstInfoResponseModel?>() {
            override fun onSuccess(t: GstInfoResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = GstInfoResponseModel()
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

    fun approveLead(
        liveData: MutableLiveData<AddLeadResponseModel>,
        leadId: Int,
        status: String,
        comments: String
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val jsonObject = JsonObject()
        jsonObject.addProperty("status", status)
        if (comments.isNotEmpty().not()) {
            jsonObject.addProperty("comments", comments)
        }

        val uploadCred: Call<AddLeadResponseModel> =
            RetrofitClient.apiInterface.approveLead(id, leadId, jsonObject)

        uploadCred.enqueue(object : NetworkCallback<AddLeadResponseModel?>() {
            override fun onSuccess(t: AddLeadResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = AddLeadResponseModel()
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