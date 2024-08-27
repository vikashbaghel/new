package com.app.rupyz.sales.beatplan

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.rupyz.model_kt.BeatCustomerResponseModel

open class SelectCustomerForBeatViewModel : ViewModel() {
    var selectCustomerListLiveData = MutableLiveData<BeatCustomerResponseModel>()

    fun setCustomerList(model: BeatCustomerResponseModel) {
        selectCustomerListLiveData.value = model
    }
}
