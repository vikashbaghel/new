package com.app.rupyz.ui.connections

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.rupyz.generic.base.FailureResponse
import com.app.rupyz.generic.base.NetworkCallback
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.ConnectionModel
import com.app.rupyz.retrofit.RetrofitClient.apiInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call


class ConnectionRepository {

    fun getConnectionList(liveData: MutableLiveData<ConnectionModel>, string: String) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<ConnectionModel> = apiInterface.getConnectionList(id, string)

        uploadCred.enqueue(object : NetworkCallback<ConnectionModel?>() {
            override fun onSuccess(t: ConnectionModel?) {
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