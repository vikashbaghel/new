package com.app.rupyz.sales.reminder

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.rupyz.generic.base.FailureResponse
import com.app.rupyz.generic.base.NetworkCallback
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.*
import com.app.rupyz.retrofit.RetrofitClient.apiInterface
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call

class ReminderRepository {

    fun getReminderList(
        productLiveData: MutableLiveData<ReminderListResponseModel>,
        category: String,
        particularDate: String?,
        page: Int
    ) {

        val uploadCred: Call<ReminderListResponseModel> =
            apiInterface.getReminderList(
                SharedPref.getInstance().getInt(ORG_ID),
                category,
                particularDate,
                page
            )

        uploadCred.enqueue(object : NetworkCallback<ReminderListResponseModel?>() {
            override fun onSuccess(t: ReminderListResponseModel?) {
                CoroutineScope(IO).launch {
                    productLiveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val errorModel = ReminderListResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    errorModel.error = true
                    errorModel.message = jsonObj?.get("message")?.asString

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(IO).launch {
                    productLiveData.postValue(errorModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun deleteReminder(liveData: MutableLiveData<GenericResponseModel>, id: Int?) {
        val uploadCred: Call<GenericResponseModel> =
                apiInterface.deleteReminder(
                SharedPref.getInstance().getInt(ORG_ID),
                id
            )

        uploadCred.enqueue(object : NetworkCallback<GenericResponseModel?>() {
            override fun onSuccess(t: GenericResponseModel?) {
                CoroutineScope(IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val errorModel = GenericResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    errorModel.error = true
                    errorModel.message = jsonObj?.get("message")?.asString

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(IO).launch {
                    liveData.postValue(errorModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

}