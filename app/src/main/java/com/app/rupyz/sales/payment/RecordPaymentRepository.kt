package com.app.rupyz.sales.payment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.rupyz.databse.DatabaseLogManager
import com.app.rupyz.generic.base.FailureResponse
import com.app.rupyz.generic.base.NetworkCallback
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.model_kt.order.payment.PaymentRecordResponseModel
import com.app.rupyz.model_kt.order.payment.RecordPaymentData
import com.app.rupyz.model_kt.order.payment.RecordPaymentDetailModel
import com.app.rupyz.model_kt.order.payment.RecordPaymentInfoModel
import com.app.rupyz.retrofit.OfflineRetrofitClient
import com.app.rupyz.retrofit.RetrofitClient
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call

class RecordPaymentRepository {
    fun getRecordPaymentListById(
        liveData: MutableLiveData<RecordPaymentInfoModel>,
        id: Int,
        page_no: Int
    ) {
        val uploadCred: Call<RecordPaymentInfoModel> =
            RetrofitClient.apiInterface.getRecordPaymentListById(
                SharedPref.getInstance().getInt(ORG_ID), id, page_no
            )

        uploadCred.enqueue(object : NetworkCallback<RecordPaymentInfoModel?>() {
            override fun onSuccess(t: RecordPaymentInfoModel?) {
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

    fun getRecordPaymentList(
        liveData: MutableLiveData<RecordPaymentInfoModel>,
        status: String,
        pageNo: Int,
        pageSize: Int?,
        offlineDataLastSyncedTime: String?
    ) {
        val uploadCred: Call<RecordPaymentInfoModel> = if (pageSize == null) {
            RetrofitClient.apiInterface.getRecordPaymentList(
                SharedPref.getInstance().getInt(ORG_ID), status, pageNo, null, null, null
            )
        } else {
            OfflineRetrofitClient.offlineApiInterface.getRecordPaymentList(
                SharedPref.getInstance().getInt(ORG_ID),
                status,
                pageNo,
                pageSize,
                true,
                offlineDataLastSyncedTime
            )
        }

        uploadCred.enqueue(object : NetworkCallback<RecordPaymentInfoModel?>() {
            override fun onSuccess(t: RecordPaymentInfoModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val responseModel = RecordPaymentInfoModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    responseModel.error = true
                    responseModel.errorCode = failureResponse?.errorCode
                    responseModel.message = jsonObj?.get("message")?.asString

                } catch (Ex: Exception) {
                    Ex.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(responseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun getOfflinePaymentList(
        liveData: MutableLiveData<RecordPaymentInfoModel>,
        status: String,
        pageNo: Int
    ) {
        DatabaseLogManager.getInstance().getOfflinePaymentList(liveData, status, pageNo)
    }

    fun addRecordPayment(
        liveData: MutableLiveData<PaymentRecordResponseModel>,
        jsonData: RecordPaymentData
    ) {
        val uploadCred: Call<PaymentRecordResponseModel> =
            RetrofitClient.apiInterface.recordPayment(
                jsonData,
                SharedPref.getInstance().getInt(ORG_ID)
            )

        uploadCred.enqueue(object : NetworkCallback<PaymentRecordResponseModel?>() {

            override fun onSuccess(t: PaymentRecordResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val responseModel = PaymentRecordResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    responseModel.error = true
                    responseModel.errorCode = failureResponse?.errorCode
                    responseModel.message = jsonObj?.get("message")?.asString

                } catch (Ex: Exception) {
                    Ex.printStackTrace()
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

    fun updateRecordPayment(
        liveData: MutableLiveData<PaymentRecordResponseModel>, jsonData: RecordPaymentData,
        id: Int
    ) {
        val uploadCred: Call<PaymentRecordResponseModel> =
            RetrofitClient.apiInterface.updateRecordPayment(
                jsonData,
                SharedPref.getInstance().getInt(ORG_ID),
                id
            )

        uploadCred.enqueue(object : NetworkCallback<PaymentRecordResponseModel?>() {

            override fun onSuccess(t: PaymentRecordResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val responseModel = PaymentRecordResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    responseModel.error = true
                    responseModel.errorCode = failureResponse?.errorCode
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

    fun getRecordPaymentDetails(
        liveData: MutableLiveData<RecordPaymentDetailModel>,
        payment_id: Int
    ) {
        val uploadCred: Call<RecordPaymentDetailModel> =
            RetrofitClient.apiInterface.getRecordPaymentDetails(
                SharedPref.getInstance().getInt(ORG_ID), payment_id
            )

        uploadCred.enqueue(object : NetworkCallback<RecordPaymentDetailModel?>() {

            override fun onSuccess(t: RecordPaymentDetailModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val responseModel = RecordPaymentDetailModel()
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

    fun deletePayment(
        liveData: MutableLiveData<GenericResponseModel>,
        jsonObject: JsonObject,
        payment_id: Int
    ) {
        val uploadCred: Call<GenericResponseModel> =
            RetrofitClient.apiInterface.deletePayment(
                SharedPref.getInstance().getInt(ORG_ID),
                jsonObject,
                payment_id
            )

        uploadCred.enqueue(object : NetworkCallback<GenericResponseModel?>() {

            override fun onSuccess(t: GenericResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val responseModel = GenericResponseModel()
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
}