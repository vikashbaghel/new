package com.app.rupyz.ui.connections

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.rupyz.model_kt.*
import com.app.rupyz.ui.network.views.NetworkRepository

class ConnectionViewModel: ViewModel() {

    private var connectionListLiveData = MutableLiveData<ConnectionModel>()
    private var connectionActionLiveData = MutableLiveData<NetworkConnectResponseModel>()


    fun getRequestedConnection(string: String){
        ConnectionRepository().getConnectionList(connectionListLiveData, string)
    }

    fun getConnectionListData(): MutableLiveData<ConnectionModel> {
        return connectionListLiveData
    }

    fun getConnectionList(status: String){
        ConnectionRepository().getConnectionList(connectionListLiveData, status)
    }

    fun connectionAction(model: NetWorkConnectModel){
        NetworkRepository().onConnect(model, connectionActionLiveData)
    }

    fun getConnectionActionData(): MutableLiveData<NetworkConnectResponseModel>{
        return connectionActionLiveData
    }

}