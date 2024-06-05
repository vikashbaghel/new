package com.app.rupyz.ui.more

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.rupyz.model_kt.*
import kotlinx.coroutines.launch

class MoreViewModel : ViewModel() {
    var preferenceLiveData = MutableLiveData<PreferencesResponseModel>()
    var updatePreferenceLiveData = MutableLiveData<PreferencesResponseModel>()
    var userPreferenceLiveData = MutableLiveData<UserPreferencesResponseModel>()
    var pricingGroupLiveData = MutableLiveData<PricingGroupResponseModel>()
    var postalCodeResponseLiveData = MutableLiveData<PostalOfficeResponseModel>()

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
}