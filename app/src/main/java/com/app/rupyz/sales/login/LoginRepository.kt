package com.app.rupyz.sales.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.rupyz.generic.base.FailureResponse
import com.app.rupyz.generic.base.NetworkCallback
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.model_kt.LoginModel
import com.app.rupyz.model_kt.UserLoginResponseModel
import com.app.rupyz.retrofit.RetrofitClient
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call

class LoginRepository {

    fun initiateLogin(liveData: MutableLiveData<UserLoginResponseModel>, loginModel: LoginModel) {
        val uploadCred: Call<UserLoginResponseModel> =
                RetrofitClient.apiInterface.loginUser(loginModel)

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

    fun verifyOtp(liveData: MutableLiveData<UserLoginResponseModel>, loginModel: LoginModel) {
        val uploadCred: Call<UserLoginResponseModel> =
                RetrofitClient.apiInterface.otpVerify(loginModel)

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

    fun savePermissions(permissionList: List<String>) {
        PermissionModel.INSTANCE.clearPermissionModel()
        PermissionModel.INSTANCE.setPermissions(permissionList.toSet())
    }

    fun saveFcmToken(liveData: MutableLiveData<GenericResponseModel>, json: JsonObject) {
        val uploadCred: Call<GenericResponseModel> =
                RetrofitClient.apiInterface.saveFcmToken(json)

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