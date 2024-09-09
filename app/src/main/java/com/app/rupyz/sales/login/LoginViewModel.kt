package com.app.rupyz.sales.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.rupyz.BuildConfig
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.model_kt.LoginModel
import com.app.rupyz.model_kt.UserInfoData
import com.app.rupyz.model_kt.UserLoginResponseModel
import com.google.gson.JsonObject

class LoginViewModel : ViewModel() {

    var initiateLoginLiveData = MutableLiveData<UserLoginResponseModel>()
    var loggedInLiveData = MutableLiveData<UserLoginResponseModel>()
    var fcmLiveData = MutableLiveData<GenericResponseModel>()

    fun savePermissions(permissionList: List<String>) {
        LoginRepository().savePermissions(permissionList)
    }

    fun initiateLogin(loginModel: LoginModel) {

        val loginData = SharedPref.getInstance().getString(AppConstant.INITIATE_LOGIN)

        if (loginData.isNullOrEmpty().not()) {
            val parts = loginData.split(":")
            // Convert back to a Triple (assuming the same types)
            val restoredTriple = Triple(parts[0], parts[1].toLong(), parts[2])

            if (restoredTriple.first == loginModel.username && DateFormatHelper.isTimeGreaterThen30Second(
                    restoredTriple.second
                ).not()
            ) {
                val model = UserLoginResponseModel()
                model.error = false
                val userData = UserInfoData()
                userData.otpRef = restoredTriple.third
                userData.updatedAt = restoredTriple.second
                model.data = userData
                initiateLoginLiveData.postValue(model)
            } else {
                LoginRepository().initiateLogin(initiateLoginLiveData, loginModel)
            }
        } else {
            LoginRepository().initiateLogin(initiateLoginLiveData, loginModel)
        }
    }

    fun verifyOtp(loginModel: LoginModel) {
        LoginRepository().verifyOtp(loggedInLiveData, loginModel)
    }

    fun saveFcm(json: JsonObject) {
        LoginRepository().saveFcmToken(fcmLiveData, json)
    }
}
