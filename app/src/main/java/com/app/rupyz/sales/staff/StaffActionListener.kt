package com.app.rupyz.sales.staff

import com.app.rupyz.model_kt.order.sales.StaffData

interface StaffActionListener {
    fun onCall(model: StaffData, position: Int)
    fun onWCall(model: StaffData, position: Int)
    fun onEdit(model: StaffData, position: Int)
    fun onDelete(model: StaffData, position: Int)
    fun onGetInfo(model: StaffData, position: Int)
}