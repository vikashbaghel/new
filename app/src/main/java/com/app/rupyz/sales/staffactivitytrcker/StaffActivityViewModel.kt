package com.app.rupyz.sales.staffactivitytrcker

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.rupyz.databse.DatabaseLogManager
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.AddCheckInOutModel
import com.app.rupyz.model_kt.CheckInOutResponseModel
import com.app.rupyz.model_kt.CustomFormCreationModel
import com.app.rupyz.model_kt.CustomerFeedbackListResponseModel
import com.app.rupyz.model_kt.CustomerFollowUpDataItem
import com.app.rupyz.model_kt.CustomerFollowUpListResponseModel
import com.app.rupyz.model_kt.CustomerFollowUpResponseModel
import com.app.rupyz.model_kt.DailySalesReportResponseModel
import com.app.rupyz.model_kt.DeviceActivityLogsResponseModel
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.model_kt.LiveLocationResponseModel
import com.app.rupyz.model_kt.StaffActiveResponseModel
import com.app.rupyz.model_kt.StaffActivitySortingModel
import com.app.rupyz.model_kt.StaffTcPcInfoModel
import com.app.rupyz.model_kt.StaffTrackingDetailsResponseModel
import com.app.rupyz.model_kt.TeamAggregatedInfoResponseModel
import com.app.rupyz.model_kt.TeamTrackingDetailsResponseModel
import kotlinx.coroutines.launch
import java.util.Calendar

class StaffActivityViewModel : ViewModel() {
    var addFeedbackFollowUpLiveData = MutableLiveData<CustomerFollowUpResponseModel>()
    var addAttendanceLiveData = MutableLiveData<GenericResponseModel>()
    var getAttendanceLiveData = MutableLiveData<CheckInOutResponseModel>()
    var getFeedbackDetailLiveData = MutableLiveData<CustomerFollowUpResponseModel>()
    var getCustomerFeedbackListLiveData = MutableLiveData<CustomerFollowUpListResponseModel>()
    var staffTrackingDetailsLiveData = MutableLiveData<StaffTrackingDetailsResponseModel>()
    var teamTrackingDetailsLiveData = MutableLiveData<TeamTrackingDetailsResponseModel>()
    var getFollowUpListLiveData = MutableLiveData<CustomerFeedbackListResponseModel>()
    var dailySalesReportLiveData = MutableLiveData<DailySalesReportResponseModel>()
    var liveLocationData = MutableLiveData<LiveLocationResponseModel>()
    var getCustomFormFieldLiveData = MutableLiveData<CustomFormCreationModel>()
    var getTeamAggregatedLiveData = MutableLiveData<TeamAggregatedInfoResponseModel>()
    var activeInActiveLiveData = MutableLiveData<StaffActiveResponseModel>()
    var staffTcPcInfoLiveData = MutableLiveData<StaffTcPcInfoModel>()
    var deviceActivityLogsLiveData = MutableLiveData<DeviceActivityLogsResponseModel>()

    fun addFeedbackFollowUp(model: CustomerFollowUpDataItem, hasInternet: Boolean) {
        viewModelScope.launch {
            if (hasInternet) {
                StaffActivityRepository().addFeedbackFollowUp(
                    addFeedbackFollowUpLiveData,
                    model
                )
            } else {
                model.createdAt =
                    DateFormatHelper.convertDateToIsoUTCFormat(Calendar.getInstance().time)
                model.createdByName = SharedPref.getInstance().getString(AppConstant.USER_NAME)
                DatabaseLogManager.getInstance()
                    .addOfflineActivity(addFeedbackFollowUpLiveData, model)
            }
        }
    }

    fun addAttendance(model: AddCheckInOutModel, hasInternet: Boolean) {
        viewModelScope.launch {
            if (hasInternet) {
                StaffActivityRepository().addAttendance(
                    addAttendanceLiveData,
                    model
                )
            } else {
                model.createdAt =
                    DateFormatHelper.convertDateToIsoUTCFormat(Calendar.getInstance().time)
                model.createdByName = SharedPref.getInstance().getString(AppConstant.USER_NAME)
                DatabaseLogManager.getInstance().addOfflineAttendance(addAttendanceLiveData, model)
            }
        }
    }

    fun getAttendance(hasInternet: Boolean) {
        viewModelScope.launch {
            if (hasInternet) {
                StaffActivityRepository().getAttendance(getAttendanceLiveData)
            }
        }
    }

    fun getCustomerFeedback(
        customerId: Int,
        currentPage: Int,
        customerType: String,
        hasInternet: Boolean
    ) {
        if (hasInternet) {
            StaffActivityRepository().getCustomerFeedback(
                getCustomerFeedbackListLiveData,
                customerId, customerType, currentPage
            )
        } else {
            DatabaseLogManager.getInstance()
                .getOfflineCustomerFeedbackList(getCustomerFeedbackListLiveData, customerId)
        }
    }

    fun getCustomerFeedbackDetails(notificationOrgId: Int?, feedbackId: Int) {
        StaffActivityRepository().getCustomerFeedbackDetails(
            getFeedbackDetailLiveData, feedbackId, notificationOrgId
        )
    }

    fun deleteFeedback(feedbackId: Int) {
        StaffActivityRepository().deleteCustomerFeedbackDetails(
            addFeedbackFollowUpLiveData, feedbackId
        )
    }

    fun updateCustomerFeedback(feedbackId: Int, model: CustomerFollowUpDataItem) {
        StaffActivityRepository().updateCustomerFeedbackDetails(
            addFeedbackFollowUpLiveData, feedbackId, model
        )
    }

    fun getStaffTrackingDetails(dateRange: String, staffId: Int?, page: Int) {
        StaffActivityRepository().getStaffTrackingDetails(
            staffTrackingDetailsLiveData, dateRange, staffId, page
        )
    }

    fun getTeamTrackingActivity(
        dateRange: String,
        startDateRange: String,
        endDateRange: String,
        page: Int,
        name: String,
        filteredRoleList: ArrayList<String?>,
        filterReportingManager: Int?,
        staffActivitySortingModel: StaffActivitySortingModel?
    ) {
        StaffActivityRepository().getTeamTrackingActivity(
            teamTrackingDetailsLiveData,
            dateRange,
            startDateRange,
            endDateRange,
            name,
            filteredRoleList,
            filterReportingManager,
            staffActivitySortingModel,
            page
        )
    }

    fun getFollowUpList() {
        StaffActivityRepository().getFollowUpList(
            getFollowUpListLiveData
        )
    }

    fun getOfflineFollowUpList() {
        DatabaseLogManager.getInstance().getFollowUpList(
            getFollowUpListLiveData
        )
    }

    fun getDailySalesReport(userId: Int?, date: String) {
        StaffActivityRepository().getDailySalesReport(
            dailySalesReportLiveData, userId, date
        )
    }

    fun getLiveLocationData(filterDate: String, staffId: Int) {
        viewModelScope.launch {
            StaffActivityRepository().getLiveLocationData(
                liveLocationData, staffId, filterDate
            )
        }
    }

    fun getCustomForInputList(feedbackId: Int) {
        viewModelScope.launch {
            StaffActivityRepository().getCustomFormInputList(getCustomFormFieldLiveData, feedbackId)
        }
    }

    fun getTeamAggregatedInfo(filterDate: String) {
        viewModelScope.launch {
            StaffActivityRepository().getTeamAggregatedInfo(filterDate, getTeamAggregatedLiveData)
        }
    }

    fun getActiveInActiveStaffList(status: String, date: String, currentPage: Int) {
        viewModelScope.launch {
            StaffActivityRepository().getActiveInActiveStaffInfo(
                status,
                date,
                currentPage,
                activeInActiveLiveData
            )
        }
    }

    fun getStaffTcPcList(staffId: Int, date: String, isPc: Boolean, page: Int) {
        viewModelScope.launch {
            StaffActivityRepository().getStaffTcPcInfo(
                staffId,
                date,
                isPc,
                page,
                staffTcPcInfoLiveData
            )
        }
    }

    fun getDeviceLogs(staffId: Int, date: String, currentPage: Int) {
        viewModelScope.launch {
            StaffActivityRepository().getDeviceLogs(
                staffId,
                date,
                currentPage,
                deviceActivityLogsLiveData
            )
        }
    }
}