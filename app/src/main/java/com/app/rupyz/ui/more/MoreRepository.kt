package com.app.rupyz.ui.more

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.rupyz.generic.base.FailureResponse
import com.app.rupyz.generic.base.NetworkCallback
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.*
import com.app.rupyz.retrofit.CustomerRetrofitClient
import com.app.rupyz.retrofit.RetrofitClient
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call

class MoreRepository {

    fun updatePreferences(
        liveData: MutableLiveData<PreferencesResponseModel>,
        updatePreferencesModel: PreferenceData
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<PreferencesResponseModel> =
            RetrofitClient.apiInterface.updatePreferences(id, updatePreferencesModel)

        uploadCred.enqueue(object : NetworkCallback<PreferencesResponseModel?>() {
            override fun onSuccess(t: PreferencesResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val cartListResponseModel = PreferencesResponseModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    cartListResponseModel.error = true
                    cartListResponseModel.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(cartListResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }


    fun getPreferencesInfo(
        liveData: MutableLiveData<PreferencesResponseModel>
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<PreferencesResponseModel> =
            RetrofitClient.apiInterface.getPreferencesInfo(id)

        uploadCred.enqueue(object : NetworkCallback<PreferencesResponseModel?>() {
            override fun onSuccess(t: PreferencesResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val cartListResponseModel = PreferencesResponseModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    cartListResponseModel.error = true
                    cartListResponseModel.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(cartListResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun getUserPreferencesInfo(liveData: MutableLiveData<UserPreferencesResponseModel>) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<UserPreferencesResponseModel> =
            RetrofitClient.apiInterface.getUserPreferencesInfo(id)

        uploadCred.enqueue(object : NetworkCallback<UserPreferencesResponseModel?>() {
            override fun onSuccess(t: UserPreferencesResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val model = UserPreferencesResponseModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    model.error = true
                    model.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(model)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun setUserPreferencesInfo(
        liveData: MutableLiveData<UserPreferencesResponseModel>,
        jsonObject: UserPreferenceData
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<UserPreferencesResponseModel> =
            RetrofitClient.apiInterface.setUserPreferencesInfo(id, jsonObject)

        uploadCred.enqueue(object : NetworkCallback<UserPreferencesResponseModel?>() {
            override fun onSuccess(t: UserPreferencesResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val model = UserPreferencesResponseModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    model.error = true
                    model.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(model)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun getPricingGroup(liveData: MutableLiveData<PricingGroupResponseModel>) {
        val id =SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<PricingGroupResponseModel> =
            RetrofitClient.apiInterface.getPricingGroupList(id, true)

        uploadCred.enqueue(object : NetworkCallback<PricingGroupResponseModel?>() {
            override fun onSuccess(t: PricingGroupResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val model = PricingGroupResponseModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    model.error = true
                    model.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(model)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun getPostalResponse(liveData: MutableLiveData<PostalOfficeResponseModel>, pinCode: String) {
        val uploadCred: Call<ArrayList<PostalOfficeResponseModel>> =
            CustomerRetrofitClient.apiInterface.getPostalResponse(pinCode)

        uploadCred.enqueue(object : NetworkCallback<ArrayList<PostalOfficeResponseModel>>() {
            override fun onSuccess(t: ArrayList<PostalOfficeResponseModel>) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t[0])
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val model = ArrayList<PostalOfficeResponseModel>()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    val errorModel = PostalOfficeResponseModel()
                    errorModel.status = "Error"
                    errorModel.message = jsonObj?.get("message")?.asString

                    model.add(errorModel)
                } catch (e: Exception) {
                    val errorModel = PostalOfficeResponseModel()
                    errorModel.status = "Failed"
                    errorModel.message = "Connection Timeout"

                    model.add(errorModel)
                    Log.e("DEBUG", "${e.printStackTrace()}")
                    e.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    if (model.isEmpty().not()) {
                        liveData.postValue(model[0])
                    }
                }
            }

            override fun onError(t: Throwable?) {
                val model = ArrayList<PostalOfficeResponseModel>()
                try {

                    val errorModel = PostalOfficeResponseModel()
                    errorModel.status = "Failed"
                    errorModel.message = "Connection Timeout"

                    model.add(errorModel)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    if (model.isEmpty().not()) {
                        liveData.postValue(model[0])
                    }
                }
            }
        })
    }

    fun logout(liveData: MutableLiveData<GenericResponseModel>) {
        val uploadCred: Call<GenericResponseModel> =
            CustomerRetrofitClient.apiInterface.logout()

        uploadCred.enqueue(object : NetworkCallback<GenericResponseModel>() {
            override fun onSuccess(t: GenericResponseModel) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
               liveData.postValue(GenericResponseModel())
            }

            override fun onError(t: Throwable?) {
                liveData.postValue(GenericResponseModel())
            }
        })
    }
}