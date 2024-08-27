package com.app.rupyz.dialog.checkIn

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.rupyz.model_kt.CheckInResponse
import com.app.rupyz.model_kt.CheckoutRequest
import com.app.rupyz.sales.customer.CustomerRepository
import kotlinx.coroutines.launch

class CheckOutViewModel : ViewModel() {
  
    private var checkOutLiveData = MutableLiveData<CheckInResponse>()
  
    fun getCheckOut(): MutableLiveData<CheckInResponse> {
        return checkOutLiveData
    }

    fun getCheckOutData(checkoutRequest: CheckoutRequest, hasInternetConnection: Boolean) {
        if (hasInternetConnection) {
            CustomerRepository().getCheckOut(checkOutLiveData, checkoutRequest)
        }
    }
    
    fun clear() = checkOutLiveData.postValue(CheckInResponse(errorCode = 0))
    
    
    
}