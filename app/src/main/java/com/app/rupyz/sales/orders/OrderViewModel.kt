package com.app.rupyz.sales.orders

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.rupyz.databse.DatabaseLogManager
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.model.profile.product.ProductInfoModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.splitFullName
import com.app.rupyz.model_kt.DispatchedOrderDetailsModel
import com.app.rupyz.model_kt.DispatchedOrderListModel
import com.app.rupyz.model_kt.DispatchedOrderModel
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.model_kt.order.order_history.CreatedBy
import com.app.rupyz.model_kt.order.order_history.OrderData
import com.app.rupyz.model_kt.order.order_history.OrderDetailsInfoModel
import kotlinx.coroutines.launch
import java.util.Calendar

class OrderViewModel : ViewModel() {

    var productListLiveData = MutableLiveData<ProductInfoModel>()
    var orderLiveData = MutableLiveData<OrderDetailsInfoModel>()
    var orderDispatchLiveData = MutableLiveData<GenericResponseModel>()
    var orderDispatchDetailsLiveData = MutableLiveData<DispatchedOrderDetailsModel>()
    var orderDispatchListLiveData = MutableLiveData<DispatchedOrderListModel>()

    fun getSearchResultProductList(
            customerId: Int,
            name: String,
            category: String,
            brandList: ArrayList<String>,
            page: Int,
            hasInternetConnection: Boolean
    ) {
        viewModelScope.launch {
            if (hasInternetConnection) {
                OrderRepository().getSearchResultProductList(
                        productListLiveData,
                        customerId,
                        name,
                        category,
                        brandList,
                        page
                )
            } else {
                OrderRepository().getOfflineProductList(
                        productListLiveData,
                        customerId,
                        name,
                        category,
                        brandList,
                        page
                )
            }
        }
    }

    fun getOrderByIdLiveData(): MutableLiveData<OrderDetailsInfoModel> {
        return orderLiveData
    }

    fun getOrderDataById(orderId: Int, hasInternetConnection: Boolean) {
        viewModelScope.launch {
            if (hasInternetConnection) {
                OrderRepository().getOrderDataById(orderLiveData, orderId)
            } else {
                OrderRepository().getOfflineOrderDetails(orderLiveData, orderId)
            }
        }
    }

    fun updateOrder(orderId: Int, model: OrderData?, hasInternet: Boolean) {
        if (hasInternet) {
            OrderRepository().updateOrder(orderLiveData, model, orderId)
        } else {
            model?.id = orderId
            model?.isSyncedToServer = false
            model?.source = AppConstant.ANDROID_OFFLINE_TAG
            model?.deliveryStatus = ""
            val createdBy = CreatedBy()
            val namePair = SharedPref.getInstance().getString(AppConstant.USER_NAME).splitFullName()
            createdBy.firstName = namePair.first
            createdBy.lastName = namePair.second
            model?.createdBy = createdBy
            model?.createdAt = DateFormatHelper.convertDateToIsoUTCFormat(Calendar.getInstance().time)
            DatabaseLogManager.getInstance().updateOfflineOrder(orderLiveData, model)
        }
    }

    fun createOrderDispatched(model: DispatchedOrderModel?, orderId: Int) {
        OrderRepository().createOrderDispatched(orderDispatchLiveData, model, orderId)
    }

    fun getOrderDispatchedDetails(orderId: Int, dispatchId: Int, hasInternetConnection: Boolean) {
        viewModelScope.launch {
            if (hasInternetConnection) {
                OrderRepository().getOrderDispatchedDetails(
                        orderDispatchDetailsLiveData,
                        orderId,
                        dispatchId
                )
            } else {
                OrderRepository().getOfflineOrderDispatchedDetails(
                        orderDispatchDetailsLiveData,
                        orderId,
                        dispatchId
                )
            }
        }
    }

    fun updateOrderDispatched(model: DispatchedOrderModel?, orderId: Int, dispatchId: Int) {
        OrderRepository().updateOrderDispatched(orderDispatchLiveData, model, orderId, dispatchId)
    }
}