package com.app.rupyz.model_kt

import com.app.rupyz.sales.customer.CustomerFilterType

data class CustomerFilterData(val name: String, var isSelected: Boolean = false, val type: CustomerFilterType )
