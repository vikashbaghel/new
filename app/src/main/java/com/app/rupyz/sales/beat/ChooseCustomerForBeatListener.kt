package com.app.rupyz.sales.beat

import com.app.rupyz.model_kt.AddBeatModel
import com.app.rupyz.model_kt.BeatCustomerResponseModel
import com.app.rupyz.model_kt.order.customer.CustomerData

interface ChooseCustomerForBeatListener {
    fun onChooseCustomerList(model: AddBeatModel)
    fun onCancelChooseCustomer()
    fun onCancelSearchCustomer()
    fun onSaveSearchCustomerListData()

}