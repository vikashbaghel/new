package com.app.rupyz.sales.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.rupyz.databse.DatabaseLogManager
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.OrganizationWiseSalesResponseModel
import com.app.rupyz.model_kt.order.dashboard.DashboardIndoModel
import com.app.rupyz.model_kt.order.order_history.OrderData
import com.app.rupyz.model_kt.order.order_history.OrderInfoModel
import com.app.rupyz.model_kt.order.order_history.OrderUpdateResponseModel
import com.google.gson.JsonObject
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private var dashboardLiveData = MutableLiveData<DashboardIndoModel>()
    var updateOrderStatusLiveData = MutableLiveData<OrderUpdateResponseModel>()
    private var orderLiveData = MutableLiveData<OrderInfoModel>()
    var organizationWiseSalesLiveData = MutableLiveData<OrganizationWiseSalesResponseModel>()

    private val repository: DashboardRepository = DashboardRepository()

    fun getDashboardLiveData(): MutableLiveData<DashboardIndoModel> {
        return dashboardLiveData
    }

    fun getDashboardData(hasInternetConnection: Boolean) {
        viewModelScope.launch {
            if (hasInternetConnection) {
                repository.getDashboardData(dashboardLiveData)
            } else {
                repository.getDashboardOfflineData(dashboardLiveData)
            }
        }
    }

    fun getOrderLiveData(): MutableLiveData<OrderInfoModel> {
        return orderLiveData
    }

    fun getOrderData(
        status: String,
        fullFilledById: Int?,
        customerLevel: String,
        page: Int,
        hasInternetConnection: Boolean
    ) {
        val filterStatus = if (status == AppConstant.DISPATCHED_ORDER){
            AppConstant.SHIPPED_ORDER
        } else {
            AppConstant.getOrderStatusForApiFilter(status) ?: ""
        }

        if (hasInternetConnection) {
            repository.getOrderData(
                orderLiveData,
                filterStatus,
                fullFilledById,
                customerLevel,
                page,
                null,
                null
            )
        } else {
            repository.getOfflineOrderData(
                orderLiveData,
                filterStatus,
                fullFilledById,
                customerLevel,
                page
            )
        }
    }
    fun deleteOrder(model: JsonObject, orderId: Int, hasInternet: Boolean) {
        viewModelScope.launch {
            if (hasInternet) {
                repository.deleteOrder(updateOrderStatusLiveData, orderId, model)
            } else {
                DatabaseLogManager.getInstance().deleteOfflineOrderData(updateOrderStatusLiveData, orderId)
            }
        }
    }

    fun getSearchResultForOrderData(status: String, customer: String, page: Int) {
        repository.getSearchResultForOrderData(orderLiveData, status, customer, page)
    }

    fun updateOrderStatus(model: OrderData, id: Int) {
        val jsonData = JsonObject()
        jsonData.addProperty("delivery_status", model.deliveryStatus)

        if (model.deliveryStatus.equals(AppConstant.ORDER_REJECTED)) {
            jsonData.addProperty("is_rejected", true)
            jsonData.addProperty("reject_reason", model.rejectReason)
        } else if (model.deliveryStatus.equals(AppConstant.ORDER_CLOSE)) {
            jsonData.addProperty("is_closed", true)
        }

        repository.updateOrderStatus(updateOrderStatusLiveData, jsonData, id)
    }

    fun getOrganizationWiseSalesList(
        intervalType: String,
        start: String,
        end: String,
        currentPage: Int
    ) {
        repository.getOrganizationWiseSalesList(
            organizationWiseSalesLiveData,
            intervalType,
            start,
            end,
            currentPage
        )
    }

}