package com.app.rupyz.sales.gallery

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.rupyz.generic.base.FailureResponse
import com.app.rupyz.generic.base.NetworkCallback
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharePrefConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.gallery.GalleryResponseData
import com.app.rupyz.retrofit.RetrofitClient
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call

class PictureGalleryRepository {
    // var userId = SharedPref.getInstance().getString(AppConstant.USER_ID).toInt()

    fun getPictureData(
        liveData: MutableLiveData<GalleryResponseData>,
        userId: ArrayList<Int>,
        customerId: ArrayList<Int>,
        moduleType: ArrayList<String?>,
        moduleId: Int,
        subModuleType: ArrayList<String?>,
        byDateRange: String,
        startDate: String,
        endDate: String,
        state: ArrayList<String>,
        sortByOrder: String,
        pageNo:Int
    ) {
        val id = SharedPref.getInstance().getInt(SharePrefConstant.ORG_ID)
        val sortBy = AppConstant.CREATED_AT
        val stringStateBuilder = StringBuilder()
        val stringUserIdBuilder = StringBuilder()
        val stringCustomerIdBuilder = StringBuilder()
        val moduleTypeBuilder = StringBuilder()
        val subModuleBuilder = StringBuilder()
        state.forEachIndexed { index, s ->
            stringStateBuilder.append(s)
            if (index < state.size-1) {
                stringStateBuilder.append(",")
            }
        }
        userId.forEachIndexed { index, s ->
            stringUserIdBuilder.append(s)
            if (index < userId.size-1) {
                stringUserIdBuilder.append(",")
            }
        }
        customerId.forEachIndexed { index, s ->
            stringCustomerIdBuilder.append(s)
            if (index < customerId.size-1) {
                stringCustomerIdBuilder.append(",")
            }
        }
        moduleType.forEachIndexed { index, s ->
            moduleTypeBuilder.append(s)
            if (index < moduleType.size-1) {
                moduleTypeBuilder.append(",")
            }
        }
        subModuleType.forEachIndexed { index, s ->
            subModuleBuilder.append(s)
            if (index < subModuleType.size-1) {
                subModuleBuilder.append(",")
            }
        }

        val uploadPic: Call<GalleryResponseData> =
            RetrofitClient.apiInterface.getPictureGalleryList(
                id,
                stringUserIdBuilder,
                stringCustomerIdBuilder,
                moduleTypeBuilder,
                moduleId,
                subModuleBuilder,
                byDateRange,
                startDate,
                endDate,
                stringStateBuilder,
                sortBy,
                sortByOrder,
                pageNo
            )

        uploadPic.enqueue(object : NetworkCallback<GalleryResponseData?>() {
            override fun onSuccess(t: GalleryResponseData?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                    Log.e("tagData","${t!!.data!!.size}")
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val model = GalleryResponseData()
                try {
                    val jsonObj = JsonParser.parseString(failureResponse?.errorBody) as JsonObject?
                    model.error = true
                    model.message = jsonObj?.get("message")?.asString

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(model)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }
}