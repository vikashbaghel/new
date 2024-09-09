package com.app.rupyz.ui.organization.profile.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.rupyz.generic.model.profile.profileInfo.OrgProfileDetail
import com.app.rupyz.generic.model.profile.profileInfo.OrgProfileInfoModel
import com.app.rupyz.model_kt.GenericResponseModel

class OrgViewModel: ViewModel() {

    private var liveData = MutableLiveData<OrgProfileInfoModel>()
    var updateUserLiveData = MutableLiveData<OrgProfileInfoModel>()
    var getUserInfoFromGstLiveData = MutableLiveData<OrgProfileInfoModel>()

    fun getInfo(){
        OrgRepository().getInfo(liveData)
    }

    fun getLiveData(): MutableLiveData<OrgProfileInfoModel> {
        return liveData
    }

    fun getInfoUsingSlug(slug: String){
        OrgRepository().getInfoUsingSlug(slug, liveData)
    }
    fun getInfoUsingGstNumber(gst: String){
        OrgRepository().getProfileInfoUsingGstNumber(gst, getUserInfoFromGstLiveData)
    }

    fun updateInfo(slug: OrgProfileDetail){
        OrgRepository().updateInfo(slug, liveData)
    }
    fun updateBasicInfo(slug: OrgProfileDetail){
        OrgRepository().updateProfileBasicInfo(slug, updateUserLiveData)
    }
}