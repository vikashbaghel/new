package com.app.rupyz.sales.analytics

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.rupyz.model_kt.*
import com.app.rupyz.model_kt.order.customer.*

class AnalyticsViewModel : ViewModel() {
    var customerWiseSalesLiveData = MutableLiveData<CustomerWiseSalesResponseModel>()
    var staffWiseSalesLiveData = MutableLiveData<StaffWiseSalesResponseModel>()
    var organizationWiseSalesLiveData = MutableLiveData<OrganizationWiseSalesResponseModel>()
    var topProductLiveData = MutableLiveData<TopProductResponseModel>()
    var topCategoryLiveData = MutableLiveData<TopCategoryResponseModel>()

    fun getCustomerWiseSalesList(intervalType: String, start: String, end: String, currentPage: Int) {
        AnalyticsRepository().getCustomerWiseSalesList(
            customerWiseSalesLiveData,
            intervalType,
            start,
            end,
            currentPage
        )
    }

    fun getStaffWiseSalesList(intervalType: String, start: String, end: String, currentPage: Int) {
        AnalyticsRepository().getStaffWiseSalesList(
            staffWiseSalesLiveData,
            intervalType,
            start,
            end,
            currentPage
        )
    }

    fun getOrganizationWiseSalesList(intervalType: String, start: String, end: String, currentPage: Int) {
        AnalyticsRepository().getOrganizationWiseSalesList(
            organizationWiseSalesLiveData,
            intervalType,
            start,
            end,
            currentPage
        )
    }

    fun getTopProductList(intervalType: String, start: String, end: String, currentPage: Int) {
        AnalyticsRepository().getTopProductList(
            topProductLiveData,
            intervalType,
            start,
            end,
            currentPage
        )
    }

    fun getTopCategoryList(intervalType: String, start: String, end: String, currentPage: Int) {
        AnalyticsRepository().getTopCategoryList(
            topCategoryLiveData,
            intervalType,
            start,
            end,
            currentPage
        )
    }
}