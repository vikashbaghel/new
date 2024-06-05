package com.app.rupyz.sales.staff

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.rupyz.generic.base.FailureResponse
import com.app.rupyz.generic.base.NetworkCallback
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.AssignRolesResponseModel
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.model_kt.StaffCurrentlyActiveTargetResponseModel
import com.app.rupyz.model_kt.StaffUpcomingAndClosedTargetResponseModel
import com.app.rupyz.model_kt.order.customer.CustomerListWithStaffMappingModel
import com.app.rupyz.model_kt.order.sales.StaffAddResponseModel
import com.app.rupyz.model_kt.order.sales.StaffData
import com.app.rupyz.model_kt.order.sales.StaffInfoModel
import com.app.rupyz.retrofit.OfflineRetrofitClient
import com.app.rupyz.retrofit.RetrofitClient
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call

class StaffRepository {

    fun getStaffList(
        liveData: MutableLiveData<StaffInfoModel>,
        role: String,
        name: String,
        page: Int,
        pageSize: Int?,
        offlineDataLastSyncedTime: String?
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)

        val uploadCred: Call<StaffInfoModel> = if (pageSize == null) {
            RetrofitClient.apiInterface.getStaffList(id, role, name, page, null, null, null)
        } else {
            OfflineRetrofitClient.offlineApiInterface.getStaffList(
                id,
                role,
                name,
                page,
                pageSize,
                true, offlineDataLastSyncedTime
            )
        }

        uploadCred.enqueue(object : NetworkCallback<StaffInfoModel?>() {
            override fun onSuccess(t: StaffInfoModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val staffAddResponseModel = StaffInfoModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    staffAddResponseModel.error = true
                    staffAddResponseModel.message = jsonObj?.get("message")?.asString

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(staffAddResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }


    fun getStaffListForAssignManager(
        liveData: MutableLiveData<StaffInfoModel>,
        getAssignableManagers: Int,
        name: String,
        page: Int
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<StaffInfoModel> =
            RetrofitClient.apiInterface.getStaffListForAssignManager(
                id,
                getAssignableManagers,
                name,
                page
            )

        uploadCred.enqueue(object : NetworkCallback<StaffInfoModel?>() {
            override fun onSuccess(t: StaffInfoModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val staffAddResponseModel = StaffInfoModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    staffAddResponseModel.error = true
                    staffAddResponseModel.message = jsonObj?.get("message")?.asString

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(staffAddResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun getStaffById(liveData: MutableLiveData<StaffAddResponseModel>, staffId: Int) {
        val uploadCred: Call<StaffAddResponseModel> =
            RetrofitClient.apiInterface.getStaffById(
                SharedPref.getInstance().getInt(ORG_ID),
                staffId
            )

        uploadCred.enqueue(object : NetworkCallback<StaffAddResponseModel?>() {
            override fun onSuccess(t: StaffAddResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val staffAddResponseModel = StaffAddResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    staffAddResponseModel.error = true
                    staffAddResponseModel.message = jsonObj?.get("message")?.asString

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(staffAddResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun updateStaff(
        liveData: MutableLiveData<StaffAddResponseModel>, jsonData: StaffData,
        staffId: Int
    ) {
        val uploadCred: Call<StaffAddResponseModel> =
            RetrofitClient.apiInterface.updateStaff(
                jsonData,
                SharedPref.getInstance().getInt(ORG_ID),
                staffId
            )

        uploadCred.enqueue(object : NetworkCallback<StaffAddResponseModel?>() {

            override fun onSuccess(t: StaffAddResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val staffAddResponseModel = StaffAddResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    staffAddResponseModel.error = true
                    staffAddResponseModel.message = jsonObj?.get("message")?.asString

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(staffAddResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "onError = ${t?.message}")
            }
        })
    }

    fun addStaff(liveData: MutableLiveData<StaffAddResponseModel>, jsonData: StaffData) {
        val uploadCred: Call<StaffAddResponseModel> =
            RetrofitClient.apiInterface.addStaff(jsonData, SharedPref.getInstance().getInt(ORG_ID))

        Log.e("DEBUG", "ERROR = ${uploadCred.request().url}")
        uploadCred.enqueue(object : NetworkCallback<StaffAddResponseModel?>() {

            override fun onSuccess(t: StaffAddResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val staffAddResponseModel = StaffAddResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    staffAddResponseModel.error = true
                    staffAddResponseModel.message = jsonObj?.get("message")?.asString

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(staffAddResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "onError = ${t?.message}")
            }
        })
    }


    fun deleteStaff(liveData: MutableLiveData<GenericResponseModel>, id: Int) {
        val uploadCred: Call<GenericResponseModel> =
            RetrofitClient.apiInterface.deleteStaff(SharedPref.getInstance().getInt(ORG_ID), id)
        uploadCred.enqueue(object : NetworkCallback<GenericResponseModel?>() {

            override fun onSuccess(t: GenericResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val staffAddResponseModel = GenericResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    staffAddResponseModel.error = true
                    staffAddResponseModel.message = jsonObj?.get("message")?.asString

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(staffAddResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "onError = ${t?.message}")
            }
        })
    }

    fun getCustomerListWithStaffMapping(
        liveData: MutableLiveData<CustomerListWithStaffMappingModel>,
        staffId: Int,
        name: String,
        page: Int
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<CustomerListWithStaffMappingModel> =
            RetrofitClient.apiInterface.getCustomerListWithStaffMapping(id, staffId, name, page)

        uploadCred.enqueue(object : NetworkCallback<CustomerListWithStaffMappingModel?>() {
            override fun onSuccess(t: CustomerListWithStaffMappingModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val staffAddResponseModel = CustomerListWithStaffMappingModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    staffAddResponseModel.error = true
                    staffAddResponseModel.message = jsonObj?.get("message")?.asString

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(staffAddResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun getRoleList(
        liveData: MutableLiveData<AssignRolesResponseModel>,
        page: Int,
        pageSize: Int?,
        offlineDataLastSyncedTime: String?
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<AssignRolesResponseModel> = if (pageSize == null) {
            RetrofitClient.apiInterface.getRoleList(id, page, null, null, null)
        } else {
            OfflineRetrofitClient.offlineApiInterface.getRoleList(
                id,
                page,
                pageSize,
                true,
                offlineDataLastSyncedTime
            )
        }

        uploadCred.enqueue(object : NetworkCallback<AssignRolesResponseModel?>() {
            override fun onSuccess(t: AssignRolesResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val staffAddResponseModel = AssignRolesResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    staffAddResponseModel.error = true
                    staffAddResponseModel.message = jsonObj?.get("message")?.asString

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(staffAddResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun getCurrentlyActiveTargets(
        liveData: MutableLiveData<StaffCurrentlyActiveTargetResponseModel>,
        staffId: Int?
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)

        val uploadCred: Call<StaffCurrentlyActiveTargetResponseModel> = if (staffId == 0) {
            RetrofitClient.apiInterface.getCurrentlyActiveTargets(id, true)
        } else {
            RetrofitClient.apiInterface.getCurrentlyActiveTargetsForStaff(id, staffId, true)
        }

        uploadCred.enqueue(object : NetworkCallback<StaffCurrentlyActiveTargetResponseModel?>() {
            override fun onSuccess(t: StaffCurrentlyActiveTargetResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val staffAddResponseModel = StaffCurrentlyActiveTargetResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    staffAddResponseModel.error = true
                    staffAddResponseModel.message = jsonObj?.get("message")?.asString

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(staffAddResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun getUpcomingAndClosedTargets(
        liveData: MutableLiveData<StaffUpcomingAndClosedTargetResponseModel>,
        upcoming: Boolean,
        closed: Boolean,
        staffId: Int?
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)

        val uploadCred: Call<StaffUpcomingAndClosedTargetResponseModel> = if (staffId == 0) {
            RetrofitClient.apiInterface.getUpcomingAndClosedTargets(id, upcoming, closed)
        } else {
            RetrofitClient.apiInterface.getUpcomingAndClosedTargetsForStaff(
                id,
                upcoming,
                closed,
                staffId
            )
        }

        uploadCred.enqueue(object : NetworkCallback<StaffUpcomingAndClosedTargetResponseModel?>() {
            override fun onSuccess(t: StaffUpcomingAndClosedTargetResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val staffAddResponseModel = StaffUpcomingAndClosedTargetResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    staffAddResponseModel.error = true
                    staffAddResponseModel.message = jsonObj?.get("message")?.asString

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(staffAddResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }
}