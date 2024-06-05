package com.app.rupyz.sales.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.rupyz.databse.DatabaseLogManager
import com.app.rupyz.generic.base.FailureResponse
import com.app.rupyz.generic.base.NetworkCallback
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.OrganizationWiseSalesResponseModel
import com.app.rupyz.model_kt.order.dashboard.DashboardData
import com.app.rupyz.model_kt.order.dashboard.DashboardIndoModel
import com.app.rupyz.model_kt.order.order_history.OrderInfoModel
import com.app.rupyz.model_kt.order.order_history.OrderUpdateResponseModel
import com.app.rupyz.retrofit.OfflineRetrofitClient
import com.app.rupyz.retrofit.RetrofitClient
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call

class DashboardRepository {

    fun getDashboardData(liveData: MutableLiveData<DashboardIndoModel>) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<DashboardIndoModel> =
                RetrofitClient.apiInterface.getDashboardData(id, true)

        uploadCred.enqueue(object : NetworkCallback<DashboardIndoModel?>() {
            override fun onSuccess(t: DashboardIndoModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                    t?.data?.let { data ->
                        insertDashboardData(data)
                    }
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = DashboardIndoModel()
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

    fun insertDashboardData(dashboardData: DashboardData) {
        DatabaseLogManager.getInstance().insertDashBoardData(dashboardData)
    }

    fun getDashboardOfflineData(dashboardLiveData: MutableLiveData<DashboardIndoModel>) {
        DatabaseLogManager.getInstance().getDashBoardData(dashboardLiveData)
    }

    fun getOrderData(
        liveData: MutableLiveData<OrderInfoModel>,
        status: String,
        fullFilledById: Int?,
        customerLevel: String,
        page: Int,
        pageSize: Int?,
        offlineDataLastSyncedTime: String?
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<OrderInfoModel> = if (pageSize == null) {
            RetrofitClient.apiInterface.getOrderList(
                id, status, fullFilledById, customerLevel, page, null, null, null
            )
        } else {
            OfflineRetrofitClient.offlineApiInterface.getOrderList(
                id, status, fullFilledById, customerLevel, page, pageSize, true, offlineDataLastSyncedTime
            )
        }

        uploadCred.enqueue(object : NetworkCallback<OrderInfoModel?>() {
            override fun onSuccess(t: OrderInfoModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = OrderInfoModel()
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


    fun getOfflineOrderData(
        liveData: MutableLiveData<OrderInfoModel>,
        status: String,
        fullFilledById: Int?,
        customerLevel: String,
        page: Int
    ) {
        DatabaseLogManager.getInstance()
            .getOfflineOrderList(liveData, status, fullFilledById, customerLevel, page)
    }

    fun getSearchResultForOrderData(
        liveData: MutableLiveData<OrderInfoModel>,
        status: String, customer: String, page: Int
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<OrderInfoModel> =
            RetrofitClient.apiInterface.getSearchResultForOrderData(id, status, customer, page)

        uploadCred.enqueue(object : NetworkCallback<OrderInfoModel?>() {
            override fun onSuccess(t: OrderInfoModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                Log.e("DEBUG", "ERROR = ${failureResponse?.errorMessage}")
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }


    fun updateOrderStatus(
        liveData: MutableLiveData<OrderUpdateResponseModel>, jsonData: JsonObject,
        id: Int
    ) {
        val uploadCred: Call<OrderUpdateResponseModel> =
            RetrofitClient.apiInterface.updateOrderStatus(
                jsonData,
                SharedPref.getInstance().getInt(ORG_ID),
                id
            )
        uploadCred.enqueue(object : NetworkCallback<OrderUpdateResponseModel?>() {

            override fun onSuccess(t: OrderUpdateResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val responseModel = OrderUpdateResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    responseModel.error = true
                    responseModel.message = jsonObj?.get("message")?.asString
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(responseModel)
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

                } catch (ex: Exception) {
                    ex.printStackTrace()
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

    fun deleteOrder(
        liveData: MutableLiveData<OrderUpdateResponseModel>,
        orderId: Int,
        model: JsonObject,
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<OrderUpdateResponseModel> =
            RetrofitClient.apiInterface.deleteOrder(id, orderId, model)

        uploadCred.enqueue(object : NetworkCallback<OrderUpdateResponseModel?>() {

            override fun onSuccess(t: OrderUpdateResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {

                val staffAddResponseModel = OrderUpdateResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    staffAddResponseModel.error = true
                    staffAddResponseModel.message = jsonObj?.get("message")?.asString

                } catch (ex: Exception) {
                    ex.printStackTrace()
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