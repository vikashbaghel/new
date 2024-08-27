package com.app.rupyz.sales.customer

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.rupyz.generic.base.FailureResponse
import com.app.rupyz.generic.base.NetworkCallback
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.CustomerInsightsResponse
import com.app.rupyz.retrofit.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call

class CustomerInsightsRepository {


    fun getCustomerInsights(
        liveData: MutableLiveData<CustomerInsightsResponse>,
        customerId: Int,

        ) {

        val recentOrderList: Call<CustomerInsightsResponse> =
            RetrofitClient.apiInterface.getCustomerInsights(
                SharedPref.getInstance().getInt(ORG_ID), customerId
            )

        recentOrderList.enqueue(object : NetworkCallback<CustomerInsightsResponse?>() {
            override fun onSuccess(t: CustomerInsightsResponse?) {
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


}