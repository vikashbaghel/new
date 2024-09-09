package com.app.rupyz.sales.address

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.rupyz.databse.DatabaseLogManager
import com.app.rupyz.generic.base.FailureResponse
import com.app.rupyz.generic.base.NetworkCallback
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.CustomerAddressApiResponseModel
import com.app.rupyz.model_kt.CustomerAddressDataItem
import com.app.rupyz.model_kt.CustomerAddressListResponseModel
import com.app.rupyz.retrofit.OfflineRetrofitClient
import com.app.rupyz.retrofit.RetrofitClient
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call

class AddressRepository {

    fun getAddressList(
        liveData: MutableLiveData<CustomerAddressListResponseModel>,
        customerId: Int,
        page: Int?,
        pageSize: Int?,
        offlineDataLastSyncedTime: String?
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val addressList: Call<CustomerAddressListResponseModel> = if (pageSize == null) {
            RetrofitClient.apiInterface.getCustomerAddressList(
                id,
                customerId,
                null,
                null,
                null,
                null
            )
        } else {
            OfflineRetrofitClient.offlineApiInterface.getCustomerAddressList(
                id,
                customerId,
                page,
                pageSize,
                true,
                offlineDataLastSyncedTime
            )
        }

        addressList.enqueue(object : NetworkCallback<CustomerAddressListResponseModel?>() {
            override fun onSuccess(t: CustomerAddressListResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = CustomerAddressListResponseModel()
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

    fun getOfflineAddressListForCustomer(
        liveData: MutableLiveData<CustomerAddressListResponseModel>,
        customerId: Int
    ) {
        DatabaseLogManager.getInstance().getAddressListForCustomer(liveData, customerId)
    }

    fun addAddress(
            liveData: MutableLiveData<CustomerAddressApiResponseModel>,
            customerId: Int, address: CustomerAddressDataItem
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val addAddress: Call<CustomerAddressApiResponseModel> =
            RetrofitClient.apiInterface.addCustomerAddress(id, customerId, address)

        addAddress.enqueue(object : NetworkCallback<CustomerAddressApiResponseModel?>() {

            override fun onSuccess(t: CustomerAddressApiResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = CustomerAddressApiResponseModel()
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