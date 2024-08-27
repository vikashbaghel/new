package com.app.rupyz.sales.staff

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.rupyz.model_kt.order.order_history.OrderInfoModel
import kotlinx.coroutines.launch

class SalesAnOrderDetailsViewModel : ViewModel() {

    var recentOrderLiveData = MutableLiveData<OrderInfoModel>()
    var recentPaymentLiveData = MutableLiveData<OrderInfoModel>()

    fun getRecentOrderListById(id: Int, currentPage: Int, hasInternetConnection: Boolean) {
        viewModelScope.launch {
            if (hasInternetConnection) {
                SalesAndOrderRepository().getRecentOrderListById(
                    recentOrderLiveData,
                    id,
                    currentPage
                )
            } else {
                SalesAndOrderRepository().getOfflineCustomerOrderListById(
                    recentOrderLiveData,
                    id,
                    currentPage
                )
            }
        }
    }

    fun getRecentPaymentListById(id: Int, type: String) {
        SalesAndOrderRepository().getRecentPaymentList(recentPaymentLiveData, id, type)
    }

}