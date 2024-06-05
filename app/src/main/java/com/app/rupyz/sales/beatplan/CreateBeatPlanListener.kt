package com.app.rupyz.sales.beatplan

import com.app.rupyz.model_kt.BeatRouteDayListModel
import com.app.rupyz.model_kt.CreateBeatRoutePlanModel

interface CreateBeatPlanListener {
    fun onCreateBeatPlanHead(model: CreateBeatRoutePlanModel)
    fun onCancelCreateBeatPlanHead()
    fun onSelectCustomer(model: BeatRouteDayListModel, targetCustomerIds: ArrayList<Int>?)
    fun successfullyCreatedBeatPlan()
}