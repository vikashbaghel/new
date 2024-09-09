package com.app.rupyz.sales.customer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.rupyz.model_kt.CustomerInsightsResponse


class CustomerInsightsModel : ViewModel() {

    private var customerInsightsLiveData = MutableLiveData<CustomerInsightsResponse>()

    fun getCustomerInsights(): MutableLiveData<CustomerInsightsResponse> {
        return customerInsightsLiveData
    }

    fun getCustomerInsightsData(customerID: Int, hasInternetConnection: Boolean) {
        if (hasInternetConnection) {
            CustomerInsightsRepository().getCustomerInsights(
                customerInsightsLiveData,
                customerID,

                )
        }
    }

}