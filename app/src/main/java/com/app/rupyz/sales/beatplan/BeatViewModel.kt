package com.app.rupyz.sales.beatplan

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.rupyz.databse.DatabaseLogManager
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharePrefConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.ActiveBeatRoutePlanResponseModel
import com.app.rupyz.model_kt.AddBeatModel
import com.app.rupyz.model_kt.BeatDetailsResponseModel
import com.app.rupyz.model_kt.BeatListResponseModel
import com.app.rupyz.model_kt.BeatRouteCustomerInfoModel
import com.app.rupyz.model_kt.BeatRouteDailyPlanResponseModel
import com.app.rupyz.model_kt.BeatRoutePlanListResponseModel
import com.app.rupyz.model_kt.BeatRoutePlanResponseModel
import com.app.rupyz.model_kt.CreateBeatRoutePlanModel
import com.app.rupyz.model_kt.CustomerFollowUpListResponseModel
import com.app.rupyz.model_kt.CustomerTypeDataItem
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.model_kt.OrgBeatListResponseModel
import com.app.rupyz.model_kt.UserInfoData
import com.app.rupyz.model_kt.order.customer.CustomerListForBeatModel
import com.app.rupyz.model_kt.order.sales.StaffListWithBeatMappingModel
import com.app.rupyz.model_kt.order.sales.StaffListWithCustomerMappingModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.launch

class BeatViewModel : ViewModel() {
    var customerListLiveData = MutableLiveData<BeatRouteCustomerInfoModel>()
    var leadListLiveData = MutableLiveData<BeatRouteCustomerInfoModel>()
    var currentlyActiveBeatLiveData = MutableLiveData<ActiveBeatRoutePlanResponseModel>()
    var beatPlanListLiveData = MutableLiveData<BeatRoutePlanListResponseModel>()
    var dailyBeatPlanListLiveData = MutableLiveData<BeatRouteDailyPlanResponseModel>()
    var createBeatPlanLiveData = MutableLiveData<BeatRoutePlanResponseModel>()
    var orgBeatListLiveData = MutableLiveData<OrgBeatListResponseModel>()
    var customerListForBeatLiveData = MutableLiveData<CustomerListForBeatModel>()
    var deleteBeatPlanLiveData = MutableLiveData<BeatRoutePlanResponseModel>()
    var beatStatusChangeLiveData = MutableLiveData<BeatRoutePlanResponseModel>()
    var beatPlanHistoryLiveData = MutableLiveData<CustomerFollowUpListResponseModel>()

    var beatListLiveData = MutableLiveData<BeatListResponseModel>()
    var beatDetailsLiveData = MutableLiveData<BeatDetailsResponseModel>()
    var addBeatLiveData = MutableLiveData<GenericResponseModel>()
    var deleteBeatLiveData = MutableLiveData<GenericResponseModel>()

    var staffListWithCustomerBeatLiveData =
            MutableLiveData<StaffListWithCustomerMappingModel>()

    var staffListWithCustomerBeatLiveDataWithInfo =
            MutableLiveData<StaffListWithBeatMappingModel>()

    fun getCustomerList(beatId: Int, customerLevel: String, date: String?, page: Int) {
        viewModelScope.launch {
            BeatRepository().getCustomerList(
                    customerListLiveData,
                    beatId,
                    customerLevel,
                    date,
                    page
            )
        }
    }

    fun getLeadListForBeat(beatId: Int, name: String, date: String?, page: Int) {
        viewModelScope.launch {
            BeatRepository().getLeadList(leadListLiveData, beatId, name, date, page)
        }
    }

    fun getCurrentlyActiveBeatPlan(userId: Int?, date: String?) {
        viewModelScope.launch {
            BeatRepository().getCurrentlyActiveBeatPlan(currentlyActiveBeatLiveData, userId, date)
        }
    }

    fun getStaffBeatPlanInfoList(beatPlanId: Int, date: String?) {
        viewModelScope.launch {
            BeatRepository().getStaffBeatPlanInfoList(currentlyActiveBeatLiveData, beatPlanId, date)
        }
    }

    fun getBeatPlanInfoForEdit(beatId: Int) {
        viewModelScope.launch {
            BeatRepository().getBeatPlanInfoForEdit(dailyBeatPlanListLiveData, beatId)
        }
    }

    fun getBeatPlanList(date: String?, status: String, userId: Int?, currentPage: Int) {
        viewModelScope.launch {
            BeatRepository().getBeatPlanList(
                    beatPlanListLiveData,
                    date,
                    status,
                    userId,
                    currentPage
            )
        }
    }

    fun getDailyBeatPlanList(beatPlanId: Int, date: String?) {
        viewModelScope.launch {
            BeatRepository().getDailyBeatPlanList(
                    dailyBeatPlanListLiveData,
                    beatPlanId,
                    date
            )
        }
    }

    fun getPendingBeatPlanList(currentPage: Int, status: String) {
        viewModelScope.launch {
            BeatRepository().getPendingBeatPlanList(beatPlanListLiveData, status, currentPage)
        }
    }

    fun getBeatPlanHistory(module_id: Int?, currentPage: Int) {
        viewModelScope.launch {
            BeatRepository().getBeatPlanHistory(beatPlanHistoryLiveData, module_id, currentPage)
        }
    }

    fun createBeatPlan(model: CreateBeatRoutePlanModel, beatId: Int) {
        viewModelScope.launch {
            BeatRepository().createBeatPlan(createBeatPlanLiveData, model, beatId)
        }
    }

    fun createBeat(model: AddBeatModel) {
        viewModelScope.launch {
            BeatRepository().createBeat(addBeatLiveData, null, model)
        }
    }

    fun updateBeat(model: AddBeatModel, beatId: Int?) {
        viewModelScope.launch {
            BeatRepository().createBeat(addBeatLiveData, beatId, model)
        }
    }

    fun searchBeat(value: String, page: Int, hasInternet: Boolean) {
        var staffId = 0
        if (hasInternet) {
            if (SharedPref.getInstance().getInt(AppConstant.STAFF_ID) != 0) {
                staffId = SharedPref.getInstance().getInt(AppConstant.STAFF_ID)
            } else if (SharedPref.getInstance().getString(SharePrefConstant.USER_INFO) != null) {
                val userInfoModel = Gson().fromJson(
                        SharedPref.getInstance().getString(SharePrefConstant.USER_INFO),
                        UserInfoData::class.java
                )
                staffId = userInfoModel.staffId ?: 0
            }
            viewModelScope.launch {
                BeatRepository().getOrgBeatList(orgBeatListLiveData, value, staffId, page, null, null)
            }
        } else {
            DatabaseLogManager.getInstance().getOffLineBeatList(orgBeatListLiveData, value)
        }
    }

    fun getCustomerBeatMapping(customerId: Int, value: String, headers: String?, hasInternet: Boolean) {
        if (hasInternet) {
            viewModelScope.launch {
                BeatRepository().getCustomerBeatMapping(orgBeatListLiveData, customerId, headers)
            }
        } else {
            DatabaseLogManager.getInstance().getOffLineBeatList(orgBeatListLiveData, value)
        }
    }

    fun getListOfCustomerForBeat(
            beatId: Int?,
            name: String,
            date: String?,
            beatRoutPlanId: Int,
            status: String,
            forBeatPlan: Boolean,
            customerLevel: String,
            customerParentID: Int?,
            filterCustomerType: ArrayList<CustomerTypeDataItem>,
            sortByOrder: String,
            currentPage: Int
    ) {
        viewModelScope.launch {
            BeatRepository().getListOfCustomerMappingForBeat(
                    customerListForBeatLiveData,
                    beatId,
                    name,
                    date,
                    beatRoutPlanId,
                    status,
                    forBeatPlan,
                    customerLevel,
                    customerParentID,
                    filterCustomerType,
                    sortByOrder,
                    currentPage
            )
        }
    }

    fun deleteBeatPlan(isForced: Boolean, beatId: Int) {
        viewModelScope.launch {
            BeatRepository().deleteBeatPlan(
                    deleteBeatPlanLiveData, beatId, isForced
            )
        }
    }

    fun beatPlanApprovedOrRejected(beatPlanId: Int, status: String, reason: String) {
        viewModelScope.launch {
            BeatRepository().beatPlanApprovedOrRejected(
                    beatStatusChangeLiveData, beatPlanId, status, reason
            )
        }
    }

    fun getBeatList(
            name: String, filterAssignedStaff: Int, customerLevel: String,
            customerParentID: Int?, sortByOrder: String, page: Int
    ) {
        viewModelScope.launch {
            BeatRepository().getBeatList(
                    beatListLiveData, name, filterAssignedStaff, customerLevel,
                    customerParentID, sortByOrder, page
            )
        }
    }

    fun getBeatDetails(id: Int) {
        viewModelScope.launch {
            BeatRepository().getBeatDetails(
                    beatDetailsLiveData, id
            )
        }
    }

    fun deleteBeat(id: Int, jsonObject: JsonObject) {
        viewModelScope.launch {
            BeatRepository().deleteBeat(
                    deleteBeatLiveData, id, jsonObject
            )
        }
    }

    fun getStaffListWithBeatMapping(
            beatId: Int,
            name: String,
            getSelectedOnly: Boolean?,
            page: Int
    ) {
        viewModelScope.launch {
            BeatRepository().getStaffListWithBeatMapping(
                    staffListWithCustomerBeatLiveData,
                    beatId,
                    name,
                    getSelectedOnly,
                    page
            )
        }
    }

    fun getStaffListWithBeatMappingWithData(
            beatId: Int,
            name: String,
            getSelectedOnly: Boolean?,
            page: Int
    ) {
        viewModelScope.launch {
            BeatRepository().getStaffListWithBeatMappingWithData(
                    staffListWithCustomerBeatLiveDataWithInfo,
                    beatId,
                    name,
                    getSelectedOnly,
                    page
            )
        }
    }
}