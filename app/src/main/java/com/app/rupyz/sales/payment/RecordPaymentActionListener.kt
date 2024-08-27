package com.app.rupyz.sales.payment

import com.app.rupyz.model_kt.order.payment.RecordPaymentData

interface RecordPaymentActionListener {
    fun onStatusChange(status: String, model: RecordPaymentData, position: Int)
    fun getPaymentInfo(model: RecordPaymentData, position: Int)
    fun onDeletePayment(model: RecordPaymentData, position: Int)
}