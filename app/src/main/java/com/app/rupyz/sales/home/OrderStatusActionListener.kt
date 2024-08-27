package com.app.rupyz.sales.home

import com.app.rupyz.model_kt.order.order_history.OrderData

interface OrderStatusActionListener {
    fun onStatusChange(model: OrderData, position: Int, status: String)
    fun onGetOrderInfo(model: OrderData, position: Int)
    fun onDeleteOrder(model: OrderData, position: Int)
    fun getStoreFrontInfo()
}