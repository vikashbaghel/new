package com.app.rupyz.ui.discovery

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.rupyz.generic.base.FailureResponse
import com.app.rupyz.generic.base.NetworkCallback
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.DiscoveryResponseModel
import com.app.rupyz.model_kt.RecentSearchResponseModel
import com.app.rupyz.retrofit.RetrofitClient.apiInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call

class DiscoveryRepository {

    fun getDiscoveryList(
        liveData: MutableLiveData<DiscoveryResponseModel>,
        type: String, name: String, state: String, badge: Int, isHardSearch: Boolean,
    ) {
        val uploadCred: Call<DiscoveryResponseModel> = apiInterface.getDiscoverySearch(type,
            name, state, badge, isHardSearch)

        uploadCred.enqueue(object : NetworkCallback<DiscoveryResponseModel?>() {
            override fun onSuccess(t: DiscoveryResponseModel?) {
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

    fun getDiscoveryListWithPagination(
        liveData: MutableLiveData<DiscoveryResponseModel>,
        type: String, name: String, state: String, badge: Int, page: Int
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<DiscoveryResponseModel> = apiInterface.getDiscoverySearchWithPagination(type,
            name, state, badge, page)

        uploadCred.enqueue(object : NetworkCallback<DiscoveryResponseModel?>() {
            override fun onSuccess(t: DiscoveryResponseModel?) {
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

    fun getDiscoverySearchHistory(
        liveData: MutableLiveData<RecentSearchResponseModel>, page_no: Int, type: String) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<RecentSearchResponseModel> = apiInterface.getDiscoverySearchHistory(
            page_no, type
        )

        uploadCred.enqueue(object : NetworkCallback<RecentSearchResponseModel?>() {
            override fun onSuccess(t: RecentSearchResponseModel?) {
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