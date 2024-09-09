package com.app.rupyz.ui.organization.profile.activity.addphotos

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.rupyz.generic.base.FailureResponse
import com.app.rupyz.generic.base.NetworkCallback
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.AddPhotoModel
import com.app.rupyz.model_kt.AddPhotoResponseModel
import com.app.rupyz.retrofit.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call

class AddPhotoRepository {

    fun addPhoto(
        liveData: MutableLiveData<AddPhotoResponseModel>,
        photo: AddPhotoModel,
    ) {

        val uploadCred: Call<AddPhotoResponseModel> =
            RetrofitClient.apiInterface.addPhoto(photo, SharedPref.getInstance().getInt(ORG_ID).toString())

        uploadCred.enqueue(object : NetworkCallback<AddPhotoResponseModel?>() {
            override fun onSuccess(t: AddPhotoResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
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