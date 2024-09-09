package com.app.rupyz.generic.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.model_kt.LogOutModel
import com.app.rupyz.ui.more.MoreRepository

class BaseViewModel : ViewModel() {
    val logoutLiveData = MutableLiveData<GenericResponseModel>()

    fun logout(model: LogOutModel) {
        MoreRepository().logout(model, logoutLiveData)
    }
}