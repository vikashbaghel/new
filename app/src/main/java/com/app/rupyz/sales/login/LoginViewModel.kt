package com.app.rupyz.sales.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.model_kt.LoginModel
import com.app.rupyz.model_kt.UserLoginResponseModel
import com.google.gson.JsonObject

class LoginViewModel: ViewModel() {

    var initiateLoginLiveData = MutableLiveData<UserLoginResponseModel>()
    var loggedInLiveData = MutableLiveData<UserLoginResponseModel>()
    var fcmLiveData = MutableLiveData<GenericResponseModel>()

    fun savePermissions(permissionList: List<String>){
        LoginRepository().savePermissions(permissionList)
    }

    fun initiateLogin(loginModel: LoginModel){
        LoginRepository().initiateLogin(initiateLoginLiveData, loginModel)
    }

    fun verifyOtp(loginModel: LoginModel){
        LoginRepository().verifyOtp(loggedInLiveData, loginModel)
    }

    fun saveFcm(json: JsonObject){
        LoginRepository().saveFcmToken(fcmLiveData, json)
    }
}