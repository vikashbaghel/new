package com.app.rupyz.ui.organization.profile.activity

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.rupyz.generic.base.FailureResponse
import com.app.rupyz.generic.base.NetworkCallback
import com.app.rupyz.generic.model.profile.profileInfo.OrgProfileDetail
import com.app.rupyz.generic.model.profile.profileInfo.OrgProfileInfoModel
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID
import com.app.rupyz.generic.utils.SharePrefConstant.TOKEN
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utility
import com.app.rupyz.retrofit.RetrofitClient.apiInterface
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call

class OrgRepository {

    fun getInfo(liveData: MutableLiveData<OrgProfileInfoModel>) {
        val uploadCred: Call<OrgProfileInfoModel> = apiInterface.getProfileInfo(SharedPref.getInstance().getInt(ORG_ID))

        uploadCred.enqueue(object : NetworkCallback<OrgProfileInfoModel?>() {
            override fun onSuccess(t: OrgProfileInfoModel?) {
                CoroutineScope(IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = OrgProfileInfoModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    genericResponseModel.error = true
                    genericResponseModel.message = jsonObj?.get("message")?.asString

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(IO).launch {
                    liveData.postValue(genericResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun getInfoUsingSlug(slug: String, liveData: MutableLiveData<OrgProfileInfoModel>) {
        val uploadCred: Call<OrgProfileInfoModel> = apiInterface.getProfileInfoUsingSlug(slug, SharedPref.getInstance().getInt(ORG_ID))

        uploadCred.enqueue(object : NetworkCallback<OrgProfileInfoModel?>() {
            override fun onSuccess(t: OrgProfileInfoModel?) {
                CoroutineScope(IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = OrgProfileInfoModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    genericResponseModel.error = true
                    genericResponseModel.message = jsonObj?.get("message")?.asString

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(IO).launch {
                    liveData.postValue(genericResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun updateInfo(data: OrgProfileDetail, liveData: MutableLiveData<OrgProfileInfoModel>) {
        val uploadCred: Call<OrgProfileInfoModel> = apiInterface.updateProfileInfo(
            SharedPref.getInstance().getInt(ORG_ID), "Bearer " + SharedPref.getInstance().getString(TOKEN), data)

        uploadCred.enqueue(object : NetworkCallback<OrgProfileInfoModel?>() {
            override fun onSuccess(t: OrgProfileInfoModel?) {
                CoroutineScope(IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = OrgProfileInfoModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    genericResponseModel.error = true
                    genericResponseModel.message = jsonObj?.get("message")?.asString

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(IO).launch {
                    liveData.postValue(genericResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }
    fun updateProfileBasicInfo(data: OrgProfileDetail, liveData: MutableLiveData<OrgProfileInfoModel>) {
        val uploadCred: Call<OrgProfileInfoModel> = apiInterface.updateProfileBasicInfo(
            SharedPref.getInstance().getInt(ORG_ID), data)

        uploadCred.enqueue(object : NetworkCallback<OrgProfileInfoModel?>() {
            override fun onSuccess(t: OrgProfileInfoModel?) {
                CoroutineScope(IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = OrgProfileInfoModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    genericResponseModel.error = true
                    genericResponseModel.message = jsonObj?.get("message")?.asString

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(IO).launch {
                    liveData.postValue(genericResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun getProfileInfoUsingGstNumber(gst_number: String, liveData: MutableLiveData<OrgProfileInfoModel>) {
        val uploadCred: Call<OrgProfileInfoModel> = apiInterface.getProfileInfoUsingGstNumber(
            SharedPref.getInstance().getInt(ORG_ID), gst_number)

        uploadCred.enqueue(object : NetworkCallback<OrgProfileInfoModel?>() {
            override fun onSuccess(t: OrgProfileInfoModel?) {
                CoroutineScope(IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val genericResponseModel = OrgProfileInfoModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    genericResponseModel.error = true
                    genericResponseModel.message = jsonObj?.get("message")?.asString

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(IO).launch {
                    liveData.postValue(genericResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }
}