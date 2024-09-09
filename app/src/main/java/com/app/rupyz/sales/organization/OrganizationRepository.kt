package com.app.rupyz.sales.organization

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.rupyz.generic.base.FailureResponse
import com.app.rupyz.generic.base.NetworkCallback
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.AddNewAdminModel
import com.app.rupyz.model_kt.AddOrganizationModel
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.model_kt.UserLoginResponseModel
import com.app.rupyz.model_kt.order.sales.StaffAddResponseModel
import com.app.rupyz.model_kt.order.sales.StaffData
import com.app.rupyz.retrofit.RetrofitClient
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call

class OrganizationRepository {

    fun addOrganization(
        liveData: MutableLiveData<GenericResponseModel>,
        addModel: AddOrganizationModel
    ) {
        val uploadCred: Call<GenericResponseModel> =
            RetrofitClient.apiInterface.addOrganization(addModel)

        uploadCred.enqueue(object : NetworkCallback<GenericResponseModel?>() {
            override fun onSuccess(t: GenericResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val model = GenericResponseModel()
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

    fun verifyOrganization(
        liveData: MutableLiveData<GenericResponseModel>,
        addModel: AddOrganizationModel
    ) {
        val uploadCred: Call<GenericResponseModel> =
            RetrofitClient.apiInterface.verifyOrganization(addModel)

        uploadCred.enqueue(object : NetworkCallback<GenericResponseModel?>() {
            override fun onSuccess(t: GenericResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val model = GenericResponseModel()
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

    fun getProfileInfo(
        liveData: MutableLiveData<UserLoginResponseModel>,
        accessType: String
    ) {
        val orgId = SharedPref.getInstance().getInt(ORG_ID)

        val uploadCred: Call<UserLoginResponseModel> =
            if (accessType == AppConstant.ACCESS_TYPE_MASTER) {
                RetrofitClient.apiInterface.getProfile(orgId)
            } else {
                RetrofitClient.apiInterface.getStaffProfileDetails()
            }

        uploadCred.enqueue(object : NetworkCallback<UserLoginResponseModel?>() {
            override fun onSuccess(t: UserLoginResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val model = UserLoginResponseModel()
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

    fun addNewAdmin(
        liveData: MutableLiveData<GenericResponseModel>,
        addModel: AddNewAdminModel
    ) {
        val orgId = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<GenericResponseModel> =
            RetrofitClient.apiInterface.addNewAdmin(orgId, addModel)

        uploadCred.enqueue(object : NetworkCallback<GenericResponseModel?>() {
            override fun onSuccess(t: GenericResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val model = GenericResponseModel()
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

    fun updateStaffProfile(liveData: MutableLiveData<StaffAddResponseModel>, model: StaffData) {
        val uploadCred: Call<StaffAddResponseModel> =
            RetrofitClient.apiInterface.updateMyProfileDetails(model)

        uploadCred.enqueue(object : NetworkCallback<StaffAddResponseModel?>() {

            override fun onSuccess(t: StaffAddResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val staffAddResponseModel = StaffAddResponseModel()
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

    fun verifyNewAdmin(
        liveData: MutableLiveData<GenericResponseModel>,
        addModel: AddNewAdminModel
    ) {
        val orgId = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<GenericResponseModel> =
            RetrofitClient.apiInterface.verifyNewAdmin(orgId, addModel)

        uploadCred.enqueue(object : NetworkCallback<GenericResponseModel?>() {
            override fun onSuccess(t: GenericResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val model = GenericResponseModel()
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
}