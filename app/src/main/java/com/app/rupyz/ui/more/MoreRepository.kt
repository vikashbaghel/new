package com.app.rupyz.ui.more

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.rupyz.generic.base.FailureResponse
import com.app.rupyz.generic.base.NetworkCallback
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.DeviceActivityLogsPostModel
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.model_kt.LogOutModel
import com.app.rupyz.model_kt.PostalOfficeResponseModel
import com.app.rupyz.model_kt.PreferenceData
import com.app.rupyz.model_kt.PreferencesResponseModel
import com.app.rupyz.model_kt.PricingGroupResponseModel
import com.app.rupyz.model_kt.UserPreferenceData
import com.app.rupyz.model_kt.UserPreferencesResponseModel
import com.app.rupyz.model_kt.VerifyWhatsAppNumberResponseModel
import com.app.rupyz.retrofit.CustomerRetrofitClient
import com.app.rupyz.retrofit.RetrofitClient
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import java.util.Calendar

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

    fun getPreferencesInfo(liveData: MutableLiveData<PreferencesResponseModel>) {
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
                    cartListResponseModel.errorCode = failureResponse?.errorCode
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
        val id = SharedPref.getInstance().getInt(ORG_ID)
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

    fun validateWhatsAppNumber(
        liveData: MutableLiveData<VerifyWhatsAppNumberResponseModel>,
        mobileNumber: String,
        module: String
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)

        val validateMobile: Call<VerifyWhatsAppNumberResponseModel> =
            CustomerRetrofitClient.apiInterface.verifyWhatsAppNumberLiveData(
                org_id = id,
                module = module,
                mobile = mobileNumber
            )

        validateMobile.enqueue(object : NetworkCallback<VerifyWhatsAppNumberResponseModel>() {
            override fun onSuccess(t: VerifyWhatsAppNumberResponseModel) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val model = VerifyWhatsAppNumberResponseModel()
                try {
                    val jsonObj = JsonParser.parseString(failureResponse?.errorBody) as JsonObject?
                    val errorModel = VerifyWhatsAppNumberResponseModel()
                    errorModel.error = true
                    errorModel.message = jsonObj?.get("message")?.asString
                } catch (e: Exception) {
                    val errorModel = PostalOfficeResponseModel()
                    errorModel.status = "Failed"
                    errorModel.message = "Connection Timeout"
                    Log.e("DEBUG", "${e.printStackTrace()}")
                    e.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(model)
                }
            }

            override fun onError(t: Throwable?) {
                val model = VerifyWhatsAppNumberResponseModel()
                try {
                    val errorModel = VerifyWhatsAppNumberResponseModel()
                    errorModel.error = true
                    errorModel.message = "Connection Timeout"
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(model)
                }
            }
        })
    }

    fun logout(model: LogOutModel, liveData: MutableLiveData<GenericResponseModel>) {
        val uploadCred: Call<GenericResponseModel> =
            CustomerRetrofitClient.apiInterface.logout(model)

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

    fun sendDeviceLogs(
        model: DeviceActivityLogsPostModel,
        liveData: MutableLiveData<GenericResponseModel>
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val userId = SharedPref.getInstance().getInt(AppConstant.STAFF_ID)

        val date = DateFormatHelper.convertDateTo_YYYY_MM_DD_Format(Calendar.getInstance().time)

        val uploadCred: Call<GenericResponseModel> =
            CustomerRetrofitClient.apiInterface.sendDeviceLogs(id, userId, date, model)

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