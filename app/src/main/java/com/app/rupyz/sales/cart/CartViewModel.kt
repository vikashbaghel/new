package com.app.rupyz.sales.cart

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.rupyz.databse.DatabaseLogManager
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.createID
import com.app.rupyz.generic.utils.splitFullName
import com.app.rupyz.model_kt.order.order_history.CreateOrderResponseModel
import com.app.rupyz.model_kt.order.order_history.CreatedBy
import com.app.rupyz.model_kt.order.order_history.OrderData
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CartViewModel : ViewModel() {

    var confirmOrderLiveData = MutableLiveData<CreateOrderResponseModel>()

    fun confirmOrder(orderData: OrderData, hasInternet: Boolean) {
        viewModelScope.launch {
            if (hasInternet) {
                CartRepository().confirmOrder(confirmOrderLiveData, orderData)
            } else {
                orderData.id = createID()
                orderData.orderId = "${
                    DateFormatHelper.convertDateToCustomDateFormat(
                            Calendar.getInstance().time,
                            SimpleDateFormat("yyyyMMdd", Locale.US))
                }-${createID()}"
                
                orderData.isSyncedToServer = false
                orderData.source = AppConstant.ANDROID_OFFLINE_TAG
                orderData.deliveryStatus = ""

                val createdBy = CreatedBy()
                val namePair = SharedPref.getInstance().getString(AppConstant.USER_NAME).splitFullName()
                createdBy.firstName = namePair.first
                createdBy.lastName = namePair.second
                orderData.createdBy = createdBy

                orderData.createdAt = DateFormatHelper.convertDateToIsoUTCFormat(Calendar.getInstance().time)
                DatabaseLogManager.getInstance()
                        .addOrderToDatabase(confirmOrderLiveData, orderData)
            }
        }
    }

}