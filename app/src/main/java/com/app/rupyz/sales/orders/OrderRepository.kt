package com.app.rupyz.sales.orders

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.rupyz.databse.DatabaseLogManager
import com.app.rupyz.generic.base.FailureResponse
import com.app.rupyz.generic.base.NetworkCallback
import com.app.rupyz.generic.model.profile.product.ProductInfoModel
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.DispatchedOrderDetailsModel
import com.app.rupyz.model_kt.DispatchedOrderListModel
import com.app.rupyz.model_kt.DispatchedOrderModel
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.model_kt.order.order_history.OrderData
import com.app.rupyz.model_kt.order.order_history.OrderDetailsInfoModel
import com.app.rupyz.retrofit.OfflineRetrofitClient
import com.app.rupyz.retrofit.RetrofitClient
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call

class OrderRepository {

    fun getProductList(liveData: MutableLiveData<ProductInfoModel>, customerId: Int, page: Int) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<ProductInfoModel> =
            RetrofitClient.apiInterface.getProductListForCustomer(id, customerId, page)

        uploadCred.enqueue(object : NetworkCallback<ProductInfoModel?>() {
            override fun onSuccess(t: ProductInfoModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = ProductInfoModel()
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

    fun getSearchResultProductList(
        liveData: MutableLiveData<ProductInfoModel>,
        customerId: Int,
        name: String,
        category: String,
        brandList: List<String>,
        page: Int
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val stringBuilder = StringBuilder()

        brandList.forEachIndexed { index, s ->
            stringBuilder.append(s)

            if (index < brandList.size) {
                stringBuilder.append(",")
            }
        }

        val uploadCred: Call<ProductInfoModel> =
            RetrofitClient.apiInterface.getElasticSearchProductList(
                id,
                customerId,
                name,
                category,
                stringBuilder,
                page
            )

        uploadCred.enqueue(object : NetworkCallback<ProductInfoModel?>() {
            override fun onSuccess(t: ProductInfoModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {

                val genericResponseModel = ProductInfoModel()
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

    fun getOfflineProductList(
        liveData: MutableLiveData<ProductInfoModel>,
        customerId: Int,
        name: String,
        category: String,
        brandList: List<String>,
        page: Int
    ) {
        DatabaseLogManager.getInstance().getProductListData(
            liveData,
            name,
            brandList,
            category,
            customerId,
            page
        )
    }


    fun getOrderDataById(liveData: MutableLiveData<OrderDetailsInfoModel>, orderId: Int) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<OrderDetailsInfoModel> =
            RetrofitClient.apiInterface.getOrderById(id, orderId)

        uploadCred.enqueue(object : NetworkCallback<OrderDetailsInfoModel?>() {
            override fun onSuccess(t: OrderDetailsInfoModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = OrderDetailsInfoModel()
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

    fun getOfflineOrderDetails(liveData: MutableLiveData<OrderDetailsInfoModel>, orderId: Int) {
        DatabaseLogManager.getInstance().getOfflineOrderDetails(liveData, orderId)
    }

    fun updateOrder(
        liveData: MutableLiveData<OrderDetailsInfoModel>,
        model: OrderData?, orderId: Int
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<OrderDetailsInfoModel> =
            RetrofitClient.apiInterface.updateOrder(id, orderId, model)

        uploadCred.enqueue(object : NetworkCallback<OrderDetailsInfoModel?>() {
            override fun onSuccess(t: OrderDetailsInfoModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = OrderDetailsInfoModel()
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

    fun createOrderDispatched(
        liveData: MutableLiveData<GenericResponseModel>, model: DispatchedOrderModel?, orderId: Int
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<GenericResponseModel> =
            RetrofitClient.apiInterface.createOrderDispatched(id, orderId, model)

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

    fun updateOrderDispatched(
        liveData: MutableLiveData<GenericResponseModel>, model: DispatchedOrderModel?,
        orderId: Int, dispatchId: Int
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<GenericResponseModel> =
            RetrofitClient.apiInterface.updateOrderDispatched(id, orderId, dispatchId, model)

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

    fun getOrderDispatchedDetails(
        liveData: MutableLiveData<DispatchedOrderDetailsModel>, orderId: Int, dispatch_id: Int
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<DispatchedOrderDetailsModel> =
            RetrofitClient.apiInterface.getOrderDispatchedDetails(id, orderId, dispatch_id)

        uploadCred.enqueue(object : NetworkCallback<DispatchedOrderDetailsModel?>() {
            override fun onSuccess(t: DispatchedOrderDetailsModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = DispatchedOrderDetailsModel()
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

    fun getOfflineOrderDispatchedDetails(
        liveData: MutableLiveData<DispatchedOrderDetailsModel>,
        orderId: Int,
        dispatchId: Int
    ) {
        DatabaseLogManager.getInstance()
            .getOfflineOrderDispatchedDetails(liveData, orderId, dispatchId)
    }

    fun dumpOrderDispatchList(
        liveData: MutableLiveData<DispatchedOrderListModel>,
        dispatchOrderPageCount: Int,
        pageSize: Int,
        offlineDataLastSyncedTime: String?
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<DispatchedOrderListModel> =
            OfflineRetrofitClient.offlineApiInterface.getOrderDispatchedListForDump(
                id,
                0,
                dispatchOrderPageCount,
                pageSize, true,  offlineDataLastSyncedTime
            )

        uploadCred.enqueue(object : NetworkCallback<DispatchedOrderListModel?>() {
            override fun onSuccess(t: DispatchedOrderListModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = DispatchedOrderListModel()
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