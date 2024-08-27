package com.app.rupyz.sales.address

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.rupyz.databse.DatabaseLogManager
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.createID
import com.app.rupyz.model_kt.CustomerAddressApiResponseModel
import com.app.rupyz.model_kt.CustomerAddressDataItem
import com.app.rupyz.model_kt.CustomerAddressListResponseModel
import kotlinx.coroutines.launch
import java.util.Calendar


class AddressViewModel : ViewModel() {

    var addressLiveData = MutableLiveData<CustomerAddressListResponseModel>()
    var addAddressLiveData = MutableLiveData<CustomerAddressApiResponseModel>()


    fun getAddressList(customerId: Int, hasInternetConnection: Boolean) {
        viewModelScope.launch {
            if (hasInternetConnection) {
                AddressRepository().getAddressList(
                        addressLiveData,
                        customerId,
                        null,
                        null,
                        null
                )
            } else {
                AddressRepository().getOfflineAddressListForCustomer(addressLiveData, customerId)
            }
        }
    }

    fun saveAddress(customerId: Int, address: CustomerAddressDataItem, hasInternet: Boolean) {
        viewModelScope.launch {
            if (hasInternet) {
                AddressRepository().addAddress(addAddressLiveData, customerId, address)
            } else {
                address.customer = customerId
                address.id = createID()
                address.createdAt = DateFormatHelper.convertDateToIsoUTCFormat(Calendar.getInstance().time)
                address.source = AppConstant.ANDROID_OFFLINE_TAG
                address.createdBy = SharedPref.getInstance().getInt(AppConstant.STAFF_ID)
                address.isSyncedToServer = false
                DatabaseLogManager.getInstance().addAddressForOfflineCustomer(addAddressLiveData, address)
            }
        }
    }
}