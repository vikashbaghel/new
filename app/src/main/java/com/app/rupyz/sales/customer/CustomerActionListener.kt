package com.app.rupyz.sales.customer

import com.app.rupyz.model_kt.order.customer.CustomerData

interface CustomerActionListener {
    fun onCall(model: CustomerData, position: Int)
    fun onWCall(model: CustomerData, position: Int)
    fun onNewOrder(model: CustomerData, position: Int)
    fun onRecordPayment(model: CustomerData, position: Int)
    fun onEdit(model: CustomerData, position: Int){}
    fun onInActiveCustomer(model: CustomerData, position: Int){}
    fun onGetCustomerInfo(model: CustomerData)
    fun recordCustomerActivity(model: CustomerData)
    fun viewCustomerPhoto(model: CustomerData)
    fun getCustomerParentDetails(model: CustomerData, position: Int){}
    fun viewCustomerLocation(model: CustomerData)
}