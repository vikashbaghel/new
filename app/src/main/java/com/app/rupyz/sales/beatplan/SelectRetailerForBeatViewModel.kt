package com.app.rupyz.sales.beatplan

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.rupyz.model_kt.BeatRetailerResponseModel

open class SelectRetailerForBeatViewModel : ViewModel() {
    var selectCustomerListLiveData = MutableLiveData<BeatRetailerResponseModel>()

    fun setCustomerList(model: BeatRetailerResponseModel) {
        selectCustomerListLiveData.value = model
    }
}
