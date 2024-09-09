package com.app.rupyz.sales.staff

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.rupyz.databse.DatabaseLogManager
import com.app.rupyz.model_kt.AssignRolesResponseModel
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.model_kt.StaffCurrentlyActiveTargetResponseModel
import com.app.rupyz.model_kt.StaffUpcomingAndClosedTargetResponseModel
import com.app.rupyz.model_kt.order.customer.CustomerListWithStaffMappingModel
import com.app.rupyz.model_kt.order.sales.StaffAddResponseModel
import com.app.rupyz.model_kt.order.sales.StaffData
import com.app.rupyz.model_kt.order.sales.StaffInfoModel
import kotlinx.coroutines.launch

class StaffViewModel : ViewModel() {

    private var staffListLiveData = MutableLiveData<StaffInfoModel>()
    private var staffAddLiveData = MutableLiveData<StaffAddResponseModel>()
    private var staffByIdLiveData = MutableLiveData<StaffAddResponseModel>()
    private var updateStaffByIdLiveData = MutableLiveData<StaffAddResponseModel>()
    var deleteStaffByIdLiveData = MutableLiveData<GenericResponseModel>()
    var customerListLiveData = MutableLiveData<CustomerListWithStaffMappingModel>()
    var assignedRoleListLiveData = MutableLiveData<AssignRolesResponseModel>()

    var staffCurrentlyActiveTargetsLiveData =
        MutableLiveData<StaffCurrentlyActiveTargetResponseModel>()
    var staffUpcomingClosedTargetsLiveData =
        MutableLiveData<StaffUpcomingAndClosedTargetResponseModel>()

    //    # Get staff by user id
    fun getStaffByIdData(): MutableLiveData<StaffAddResponseModel> {
        return staffByIdLiveData
    }

    fun getStaffById(customerId: Int) {
        StaffRepository().getStaffById(staffByIdLiveData, customerId)
    }

    //    # Update new customer
    fun updateStaff(jsonData: StaffData, staffId: Int) {
        StaffRepository().updateStaff(updateStaffByIdLiveData, jsonData, staffId)
    }

    fun updateStaffLiveData(): MutableLiveData<StaffAddResponseModel> {
        return updateStaffByIdLiveData
    }


    fun getStaffListData(): MutableLiveData<StaffInfoModel> {
        return staffListLiveData
    }

    fun getStaffList(role: String?, name: String, page: Int, hasInternetConnection: Boolean,dd: Boolean = false) {
        viewModelScope.launch {
            if (hasInternetConnection) {
                StaffRepository().getStaffList(
                    staffListLiveData,
                    role,
                    name,
                    page,
                    null,
                    null,
                    dd
                )
            } else {
                DatabaseLogManager.getInstance().getOfflineStaffList(staffListLiveData, role, name, page)
            }
        }
    }

    fun getStaffListForAssignManager(getAssignableManagers: Int, name: String, page: Int) {
        StaffRepository().getStaffListForAssignManager(
            staffListLiveData,
            getAssignableManagers,
            name,
            page
        )
    }

    fun saveStaff(jsonData: StaffData) {
        StaffRepository().addStaff(staffAddLiveData, jsonData)
    }

    fun deleteStaff(id: Int) {
        StaffRepository().deleteStaff(deleteStaffByIdLiveData, id)
    }


    fun addStaffLiveData(): MutableLiveData<StaffAddResponseModel> {
        return staffAddLiveData
    }

    fun getCustomerListWithStaffMapping(staffId: Int, name: String, page: Int) {
        StaffRepository().getCustomerListWithStaffMapping(customerListLiveData, staffId, name, page)
    }

    fun getRoleList(page: Int, hasInternetConnection: Boolean) {
        viewModelScope.launch {
            if (hasInternetConnection) {
                StaffRepository().getRoleList(
                    assignedRoleListLiveData,
                    page,
                    null,
                    null
                )
            } else {
                DatabaseLogManager.getInstance().getStaffRoles(assignedRoleListLiveData, page)
            }
        }
    }

    fun getCurrentlyActiveTargets(staffId: Int?) {
        StaffRepository().getCurrentlyActiveTargets(staffCurrentlyActiveTargetsLiveData, staffId)
    }

    fun getUpcomingAndClosedTargets(upcoming: Boolean, closed: Boolean, staffId: Int?) {
        StaffRepository().getUpcomingAndClosedTargets(
            staffUpcomingClosedTargetsLiveData,
            upcoming,
            closed,
            staffId
        )
    }

    fun dumpStaffList(page: Int, pageSize: Int, offlineDataLastSyncedTime: String?) {
        viewModelScope.launch {
            StaffRepository().getStaffList(
                staffListLiveData,
                "",
                "",
                page,
                pageSize,
                offlineDataLastSyncedTime,false
            )
        }
    }

    fun dumpStaffRoleList(page: Int, pageSize: Int, offlineDataLastSyncedTime: String?) {
        viewModelScope.launch {
            StaffRepository().getRoleList(
                assignedRoleListLiveData,
                page,
                pageSize,
                offlineDataLastSyncedTime
            )
        }
    }


}
