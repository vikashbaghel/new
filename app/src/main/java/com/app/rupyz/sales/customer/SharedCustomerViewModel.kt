package com.app.rupyz.sales.customer

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.rupyz.model_kt.CustomFormCreationData
import kotlinx.coroutines.launch

class SharedCustomerViewModel : ViewModel() {

    private var pageSyncCount = MutableLiveData<Int>()
    val backupData : HashMap<Int,Pair<CustomFormCreationData,HashMap<String, View>>> = hashMapOf()
    fun setCustomFormViewData(data : HashMap<Int,Pair<CustomFormCreationData,HashMap<String, View>>>){
        backupData.putAll(data)
        viewModelScope.launch {
            pageSyncCount.postValue(backupData.size)
        }
    }
    fun getPageSyncCount() = pageSyncCount

}