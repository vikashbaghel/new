package com.app.rupyz.ui.network.views

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.rupyz.model_kt.MyNetworkResponseModel
import com.app.rupyz.model_kt.NetWorkConnectModel
import com.app.rupyz.model_kt.NetworkConnectResponseModel
import com.app.rupyz.model_kt.NetworkOrgModel

class NetworkViewModel: ViewModel() {

    var suggestedLiveData = MutableLiveData<NetworkOrgModel>()
    var suggestedSearchLiveData = MutableLiveData<NetworkOrgModel>()
    var followLiveData = MutableLiveData<NetworkConnectResponseModel>()
    var connectionInfoLiveData = MutableLiveData<MyNetworkResponseModel>()

    fun getSuggestionList(currentPage: Int) {
        NetworkRepository().getSuggestionList(suggestedLiveData, currentPage)
    }

    fun getSuggestionListSearch(key: String){
        NetworkRepository().getSuggestedSearchData(key, suggestedSearchLiveData)
    }


    fun onConnect(model: NetWorkConnectModel){
        NetworkRepository().onConnect(model, followLiveData)
    }

    fun onConnectionInfo(){
        NetworkRepository().onConnectionInfo(connectionInfoLiveData)
    }


}