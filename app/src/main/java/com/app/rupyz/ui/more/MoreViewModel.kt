package com.app.rupyz.ui.more

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.DeviceActivityListItem
import com.app.rupyz.model_kt.DeviceActivityLogsPostModel
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.model_kt.PostalOfficeResponseModel
import com.app.rupyz.model_kt.PreferenceData
import com.app.rupyz.model_kt.PreferencesResponseModel
import com.app.rupyz.model_kt.PricingGroupResponseModel
import com.app.rupyz.model_kt.UserPreferenceData
import com.app.rupyz.model_kt.UserPreferencesResponseModel
import com.app.rupyz.model_kt.VerifyWhatsAppNumberResponseModel
import com.google.gson.Gson
import kotlinx.coroutines.launch

class MoreViewModel : ViewModel() {
    var preferenceLiveData = MutableLiveData<PreferencesResponseModel>()
    var updatePreferenceLiveData = MutableLiveData<PreferencesResponseModel>()
    var userPreferenceLiveData = MutableLiveData<UserPreferencesResponseModel>()
    var pricingGroupLiveData = MutableLiveData<PricingGroupResponseModel>()
    var postalCodeResponseLiveData = MutableLiveData<PostalOfficeResponseModel>()
    var verifyWhatsAppNumberLiveData = MutableLiveData<VerifyWhatsAppNumberResponseModel>()
    var deviceLogsliveData = MutableLiveData<GenericResponseModel>()

    fun updatePreferences(updatePreferencesModel: PreferenceData) {
        MoreRepository().updatePreferences(updatePreferenceLiveData, updatePreferencesModel)
    }

    fun getPreferencesInfo() {
        MoreRepository().getPreferencesInfo(preferenceLiveData)
    }


    fun getUserPreferencesInfo() {
        MoreRepository().getUserPreferencesInfo(userPreferenceLiveData)
    }

    fun setUserPreferencesInfo(jsonObject: UserPreferenceData) {
        MoreRepository().setUserPreferencesInfo(userPreferenceLiveData, jsonObject)
    }

    fun getPricingGroupList() {
        viewModelScope.launch {
            MoreRepository().getPricingGroup(pricingGroupLiveData)
        }
    }

    fun getPostalResponse(pinCode: String) {
        viewModelScope.launch {
            MoreRepository().getPostalResponse(postalCodeResponseLiveData, pinCode)
        }
    }

    fun validateWhatsAppNumber(mobileNumber: String, module: String) {
        viewModelScope.launch {
            MoreRepository().validateWhatsAppNumber(
                liveData = verifyWhatsAppNumberLiveData,
                module = module,
                mobileNumber = mobileNumber
            )
        }
    }

    fun sendDeviceLogs(
        model: DeviceActivityListItem,
        hasInternetConnection: Boolean,
        isApiRequired: Boolean
    ) {
        viewModelScope.launch {
            var totalDeviceLogs = Gson().fromJson(
                SharedPref.getInstance().getString(AppConstant.DEVICE_LOGS),
                DeviceActivityLogsPostModel::class.java
            )

            val deviceLogs = ArrayList<DeviceActivityListItem?>()

            if (totalDeviceLogs != null) {
                if (totalDeviceLogs.deviceActivityList.isNullOrEmpty().not()) {
                    deviceLogs.addAll(totalDeviceLogs.deviceActivityList!!)
                    deviceLogs.add(model)
                    totalDeviceLogs.deviceActivityList = deviceLogs
                } else {
                    deviceLogs.add(model)
                    totalDeviceLogs.deviceActivityList = deviceLogs
                }
            } else {
                totalDeviceLogs = DeviceActivityLogsPostModel()
                deviceLogs.add(model)
                totalDeviceLogs.deviceActivityList = deviceLogs
            }

            if (hasInternetConnection && isApiRequired) {
                MoreRepository().sendDeviceLogs(totalDeviceLogs, deviceLogsliveData)
            } else {
                SharedPref.getInstance().putModelClass(AppConstant.DEVICE_LOGS, totalDeviceLogs)
            }
        }
    }

}