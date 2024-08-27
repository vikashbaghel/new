package com.app.rupyz.sales.beatplan

import com.app.rupyz.model_kt.BeatCustomerResponseModel
import com.app.rupyz.model_kt.BeatRouteDayListModel
import com.app.rupyz.model_kt.order.customer.CustomerData

interface ChooseCustomerForBeatPlanListener {
    fun onChooseCustomerList(model: BeatRouteDayListModel)
    fun onCancelChooseCustomer()
    fun onChooseRetailer(
        dayModel: BeatRouteDayListModel?,
        customerData: CustomerData,
        addCustomerIdSet: BeatCustomerResponseModel
    )
    fun onSelectRetailerList()
    fun onCancelSearchCustomer()
    fun onSaveSearchCustomerListData()

}