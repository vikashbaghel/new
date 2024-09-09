package com.app.rupyz.sales.analytics

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.rupyz.generic.base.FailureResponse
import com.app.rupyz.generic.base.NetworkCallback
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.*
import com.app.rupyz.retrofit.RetrofitClient
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call

class AnalyticsRepository {

    fun getCustomerWiseSalesList(
        liveData: MutableLiveData<CustomerWiseSalesResponseModel>,
        interval_type: String,
        start: String,
        end: String,
        currentPage: Int
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<CustomerWiseSalesResponseModel> =
            RetrofitClient.apiInterface.getCustomerWiseSalesList(
                id,
                interval_type,
                start,
                end,
                currentPage
            )

        uploadCred.enqueue(object : NetworkCallback<CustomerWiseSalesResponseModel?>() {

            override fun onSuccess(t: CustomerWiseSalesResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {

                val staffAddResponseModel = CustomerWiseSalesResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    staffAddResponseModel.error = true
                    staffAddResponseModel.message = jsonObj?.get("message")?.asString

                } catch (Ex: Exception) {
                    Ex.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(staffAddResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "onError = ${t?.message}")
            }
        })
    }


    fun getStaffWiseSalesList(
        liveData: MutableLiveData<StaffWiseSalesResponseModel>,
        interval_type: String,
        start: String,
        end: String,
        currentPage: Int
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<StaffWiseSalesResponseModel> =
            RetrofitClient.apiInterface.getStaffWiseSalesList(
                id,
                interval_type,
                start,
                end,
                currentPage
            )

        uploadCred.enqueue(object : NetworkCallback<StaffWiseSalesResponseModel?>() {

            override fun onSuccess(t: StaffWiseSalesResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {

                val staffAddResponseModel = StaffWiseSalesResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    staffAddResponseModel.error = true
                    staffAddResponseModel.message = jsonObj?.get("message")?.asString

                } catch (Ex: Exception) {
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(staffAddResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "onError = ${t?.message}")
            }
        })
    }

    fun getOrganizationWiseSalesList(
        liveData: MutableLiveData<OrganizationWiseSalesResponseModel>,
        interval_type: String,
        start: String,
        end: String,
        currentPage: Int
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<OrganizationWiseSalesResponseModel> =
            RetrofitClient.apiInterface.getOrganizationWiseSalesList(
                id,
                interval_type,
                start,
                end,
                currentPage
            )

        uploadCred.enqueue(object : NetworkCallback<OrganizationWiseSalesResponseModel?>() {

            override fun onSuccess(t: OrganizationWiseSalesResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {

                val staffAddResponseModel = OrganizationWiseSalesResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    staffAddResponseModel.error = true
                    staffAddResponseModel.message = jsonObj?.get("message")?.asString

                } catch (Ex: Exception) {
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(staffAddResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "onError = ${t?.message}")
            }
        })
    }

    fun getTopProductList(
        liveData: MutableLiveData<TopProductResponseModel>,
        interval_type: String,
        start: String,
        end: String,
        currentPage: Int
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<TopProductResponseModel> =
            RetrofitClient.apiInterface.getTopProductList(
                id,
                interval_type,
                start,
                end,
                currentPage
            )

        uploadCred.enqueue(object : NetworkCallback<TopProductResponseModel?>() {

            override fun onSuccess(t: TopProductResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {

                val staffAddResponseModel = TopProductResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    staffAddResponseModel.error = true
                    staffAddResponseModel.message = jsonObj?.get("message")?.asString

                } catch (Ex: Exception) {
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(staffAddResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "onError = ${t?.message}")
            }
        })
    }

    fun getTopCategoryList(
        liveData: MutableLiveData<TopCategoryResponseModel>,
        interval_type: String,
        start: String,
        end: String,
        currentPage: Int
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<TopCategoryResponseModel> =
            RetrofitClient.apiInterface.getTopCategoryList(
                id,
                interval_type,
                start,
                end,
                currentPage,
                true
            )

        uploadCred.enqueue(object : NetworkCallback<TopCategoryResponseModel?>() {

            override fun onSuccess(t: TopCategoryResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {

                val staffAddResponseModel = TopCategoryResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    staffAddResponseModel.error = true
                    staffAddResponseModel.message = jsonObj?.get("message")?.asString

                } catch (Ex: Exception) {
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(staffAddResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "onError = ${t?.message}")
            }
        })
    }
}