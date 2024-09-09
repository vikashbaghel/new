package com.app.rupyz.sales.lead

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.rupyz.databse.DatabaseLogManager
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.createID
import com.app.rupyz.model_kt.AddLeadResponseModel
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.model_kt.GstInfoResponseModel
import com.app.rupyz.model_kt.LeadCategoryListResponseModel
import com.app.rupyz.model_kt.LeadCategoryResponseModel
import com.app.rupyz.model_kt.LeadLisDataItem
import com.app.rupyz.model_kt.LeadListResponseModel
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import java.util.Calendar

class LeadViewModel : ViewModel() {
    var leadListLiveData = MutableLiveData<LeadListResponseModel>()
    var addLeadLiveData = MutableLiveData<AddLeadResponseModel>()
    var approveRejectLeadLiveData = MutableLiveData<AddLeadResponseModel>()
    var leadDetailLiveData = MutableLiveData<AddLeadResponseModel>()
    var createLeadLiveData = MutableLiveData<LeadCategoryResponseModel>()
    var leadCategoryLiveData = MutableLiveData<LeadCategoryListResponseModel>()
    var gstInfoLiveData = MutableLiveData<GstInfoResponseModel>()
    var mobileCheckLiveData = MutableLiveData<GenericResponseModel>()

    fun getAllCategoryList(category: String, hasInternetConnection: Boolean) {
        viewModelScope.launch {
            if (hasInternetConnection) {
                LeadRepository().getAllCategoryList(
                        leadCategoryLiveData,
                        category,
                        null,
                        null,
                        null
                )
            } else {
                LeadRepository().getOfflineLeadCategoryList(leadCategoryLiveData)
            }
        }
    }

    fun getLeadList(
            name: String,
            category: String,
            currentPage: Int,
            hasInternetConnection: Boolean
    ) {
        viewModelScope.launch {
            if (hasInternetConnection) {
                LeadRepository().getLeadList(
                        leadListLiveData,
                        name,
                        category,
                        currentPage,
                        null,
                        null
                )
            } else {
                LeadRepository().getOfflineLeadList(
                        leadListLiveData,
                        name,
                        category,
                        currentPage
                )
            }
        }
    }

    fun getGstInfo(gst: String) {
        viewModelScope.launch {
            LeadRepository().getGstInfo(gstInfoLiveData, gst)
        }
    }

    fun createNewLeadCategory(jsonObject: JsonObject) {
        viewModelScope.launch {
            LeadRepository().createLeadCategory(createLeadLiveData, jsonObject)
        }
    }

    fun addNewLead(leadModel: LeadLisDataItem, hasInternet: Boolean) {
        viewModelScope.launch {
            if (hasInternet) {
                LeadRepository().createNewLead(addLeadLiveData, leadModel)
            } else {
                leadModel.id = createID()
                leadModel.createdAt = DateFormatHelper.convertDateToIsoUTCFormat(Calendar.getInstance().time)
                leadModel.source = AppConstant.ANDROID_OFFLINE_TAG
                leadModel.createdByName = SharedPref.getInstance().getString(AppConstant.USER_NAME)
                leadModel.isSyncedToServer = false
                DatabaseLogManager.getInstance().createOfflineLead(addLeadLiveData, leadModel)
            }
        }
    }

    fun updateLead(leadData: LeadLisDataItem, leadId: Int, hasInternet: Boolean) {
        viewModelScope.launch {
            if (hasInternet) {
                LeadRepository().updateLead(addLeadLiveData, leadData, leadId)
            } else {
                leadData.id = leadId
                leadData.isSyncedToServer = false
                leadData.source = AppConstant.ANDROID_OFFLINE_TAG
                leadData.createdByName = SharedPref.getInstance().getString(AppConstant.USER_NAME)
                leadData.createdAt = DateFormatHelper.convertDateToIsoUTCFormat(Calendar.getInstance().time)
                DatabaseLogManager.getInstance().updateOfflineLeadDetails(addLeadLiveData, leadData)
            }
        }
    }

    fun updateOfflineLeadToCustomer(leadData: LeadLisDataItem, leadId: Int, hasInternet: Boolean) {
        viewModelScope.launch {
            leadData.id = leadId
            DatabaseLogManager.getInstance().updateOfflineLeadDetails(addLeadLiveData, leadData)
        }
    }

    fun getLeadDetail(leadId: Int, hasInternet: Boolean) {
        viewModelScope.launch {
            if (hasInternet) {
                LeadRepository().getLeadInfo(leadDetailLiveData, leadId)
            } else {
                DatabaseLogManager.getInstance().getOfflineLeadDetails(leadDetailLiveData, leadId)
            }
        }
    }

    fun deleteLead(leadId: Int, hasInternet: Boolean) {
        viewModelScope.launch {
            if (hasInternet) {
                LeadRepository().deleteLead(addLeadLiveData, leadId)
            } else {
                DatabaseLogManager.getInstance().deleteOfflineLead(addLeadLiveData, leadId)
            }
        }
    }

    fun checkExistingLeadMobileNumber(mobile: String) {
        viewModelScope.launch {
            LeadRepository().checkLeadMobileExist(mobileCheckLiveData, mobile)
        }
    }

    fun approveOrRejectLead(leadId: Int, status: String, comments: String) {
        viewModelScope.launch {
            LeadRepository().approveLead(approveRejectLeadLiveData, leadId, status, comments)
        }
    }
}