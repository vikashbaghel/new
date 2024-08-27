package com.app.rupyz.sales.staff

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.rupyz.databse.DatabaseLogManager
import com.app.rupyz.generic.base.FailureResponse
import com.app.rupyz.generic.base.NetworkCallback
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.order.order_history.OrderInfoModel
import com.app.rupyz.retrofit.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call

class SalesAndOrderRepository {

    fun getRecentPaymentList(liveData: MutableLiveData<OrderInfoModel>, id: Int, type: String) {
        val orgId = SharedPref.getInstance().getInt(ORG_ID)
        val recentPayment: Call<OrderInfoModel>

        if (type == AppConstant.CUSTOMER) {
            recentPayment = RetrofitClient.apiInterface.getRecentPaymentListForCustomer(orgId, id)
        } else {
            recentPayment = RetrofitClient.apiInterface.getRecentPaymentListForStaff(orgId, id)
        }

        recentPayment.enqueue(object : NetworkCallback<OrderInfoModel?>() {
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

    fun getRecentOrderListById(
        liveData: MutableLiveData<OrderInfoModel>,
        customerId: Int,
        currentPage: Int
    ) {

        val recentOrderList: Call<OrderInfoModel> =
            RetrofitClient.apiInterface.getRecentOrderListForCustomer(
                SharedPref.getInstance().getInt(ORG_ID), customerId, currentPage
            )

        recentOrderList.enqueue(object : NetworkCallback<OrderInfoModel?>() {
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

    fun getOfflineCustomerOrderListById(
        liveData: MutableLiveData<OrderInfoModel>,
        customerId: Int,
        currentPage: Int
    ) {

        DatabaseLogManager.getInstance().getOfflineCustomerOrderListById(liveData, customerId, currentPage)
    }

}