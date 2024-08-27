package com.app.rupyz.ui.network.views

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.rupyz.generic.base.FailureResponse
import com.app.rupyz.generic.base.NetworkCallback
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.MyNetworkResponseModel
import com.app.rupyz.model_kt.NetWorkConnectModel
import com.app.rupyz.model_kt.NetworkConnectResponseModel
import com.app.rupyz.model_kt.NetworkOrgModel
import com.app.rupyz.retrofit.RetrofitClient.apiInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call


class NetworkRepository {

    fun getSuggestedSearchData(searchKey: String, liveData: MutableLiveData<NetworkOrgModel>) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<NetworkOrgModel> = apiInterface.getSuggestionListSearch(id, searchKey)

        uploadCred.enqueue(object : NetworkCallback<NetworkOrgModel?>() {

            override fun onSuccess(t: NetworkOrgModel?) {
                CoroutineScope(IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                Log.e("DEBUG1", "ERROR = ${failureResponse?.errorMessage}")
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG2", "ERROR = ${t?.message}")
            }
        })
    }

    fun getSuggestionList(liveData: MutableLiveData<NetworkOrgModel>, currentPage: Int) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        Log.e("ORGID", "ERROR = $id")
        val uploadCred: Call<NetworkOrgModel> = apiInterface.getSuggestionList(id, currentPage)

        uploadCred.enqueue(object : NetworkCallback<NetworkOrgModel?>() {
            override fun onSuccess(t: NetworkOrgModel?) {
                CoroutineScope(IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                Log.e("DEBUG", "ERROR = ${failureResponse?.errorMessage}")
                Log.e("DEBUG1", "ERROR = ${failureResponse?.errorCode}")
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }


    fun onConnect(
        model: NetWorkConnectModel,
        liveData: MutableLiveData<NetworkConnectResponseModel>
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<NetworkConnectResponseModel> = apiInterface.followOrg(model, id)

        uploadCred.enqueue(object : NetworkCallback<NetworkConnectResponseModel?>() {
            override fun onSuccess(t: NetworkConnectResponseModel?) {
                CoroutineScope(IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                Log.e("DEBUG", "ERROR = ${failureResponse?.errorMessage}")
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun onConnectionInfo(liveData: MutableLiveData<MyNetworkResponseModel>) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<MyNetworkResponseModel> = apiInterface.connectionInfo(id)

        uploadCred.enqueue(object : NetworkCallback<MyNetworkResponseModel?>() {
            override fun onSuccess(t: MyNetworkResponseModel?) {
                CoroutineScope(IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                Log.e("DEBUG", "ERROR = ${failureResponse?.errorMessage}")
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }
}