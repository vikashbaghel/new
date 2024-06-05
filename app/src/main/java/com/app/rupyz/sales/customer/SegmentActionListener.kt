package com.app.rupyz.sales.customer

import com.app.rupyz.model_kt.order.customer.SegmentDataItem

interface SegmentActionListener {
    fun onEdit(model: SegmentDataItem, position: Int)
}