package com.app.rupyz.sales.organization

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.AddNewAdminModel
import com.app.rupyz.model_kt.AddOrganizationModel
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.model_kt.UserLoginResponseModel
import com.app.rupyz.model_kt.order.sales.StaffAddResponseModel
import com.app.rupyz.model_kt.order.sales.StaffData

class OrganizationViewModel : ViewModel() {

    var addOrgLiveData = MutableLiveData<GenericResponseModel>()
    var verifyOrgLiveData = MutableLiveData<GenericResponseModel>()
    var addNewAdminLiveData = MutableLiveData<GenericResponseModel>()
    var verifyNewAdminLiveData = MutableLiveData<GenericResponseModel>()
    var profileLiveData = MutableLiveData<UserLoginResponseModel>()
    var updateStaffByIdLiveData = MutableLiveData<StaffAddResponseModel>()

    fun addOrganization(addModel: AddOrganizationModel) {
        OrganizationRepository().addOrganization(addOrgLiveData, addModel)
    }

    fun verifyOrganization(addModel: AddOrganizationModel) {
        OrganizationRepository().verifyOrganization(verifyOrgLiveData, addModel)
    }

    fun addNewAdmin(addModel: AddNewAdminModel) {
        OrganizationRepository().addNewAdmin(addNewAdminLiveData, addModel)
    }

    fun verifyNewAdmin(addModel: AddNewAdminModel) {
        OrganizationRepository().verifyNewAdmin(verifyNewAdminLiveData, addModel)
    }

    fun getProfileInfo() {
        if (SharedPref.getInstance().getString(AppConstant.APP_ACCESS_TYPE)
                .equals(AppConstant.ACCESS_TYPE_MASTER)
        ) {
            OrganizationRepository().getProfileInfo(profileLiveData, AppConstant.ACCESS_TYPE_MASTER)
        } else {
            OrganizationRepository().getProfileInfo(profileLiveData, AppConstant.ACCESS_TYPE_STAFF)
        }
    }

    fun updateStaffProfile(model: StaffData) {
        OrganizationRepository().updateStaffProfile(updateStaffByIdLiveData, model)
    }


}