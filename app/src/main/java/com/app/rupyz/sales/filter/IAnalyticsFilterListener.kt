package com.app.rupyz.sales.filter

import com.app.rupyz.model_kt.DateFilterModel

interface IAnalyticsFilterListener {
    fun onFilterDate(model: DateFilterModel)
}