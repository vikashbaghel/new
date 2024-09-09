package com.app.rupyz.sales.beatplan

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.rupyz.generic.base.FailureResponse
import com.app.rupyz.generic.base.NetworkCallback
import com.app.rupyz.generic.helper.divideHeadersIntoQueryParams
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharePrefConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.ActiveBeatRoutePlanResponseModel
import com.app.rupyz.model_kt.AddBeatModel
import com.app.rupyz.model_kt.BeatDetailsResponseModel
import com.app.rupyz.model_kt.BeatListResponseModel
import com.app.rupyz.model_kt.BeatPlanModel
import com.app.rupyz.model_kt.BeatRouteCustomerInfoModel
import com.app.rupyz.model_kt.BeatRouteDailyPlanResponseModel
import com.app.rupyz.model_kt.BeatRoutePlanListResponseModel
import com.app.rupyz.model_kt.BeatRoutePlanResponseModel
import com.app.rupyz.model_kt.CreateBeatRoutePlanModel
import com.app.rupyz.model_kt.CustomerFollowUpListResponseModel
import com.app.rupyz.model_kt.CustomerTypeDataItem
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.model_kt.OrgBeatListResponseModel
import com.app.rupyz.model_kt.order.customer.CustomerListForBeatModel
import com.app.rupyz.model_kt.order.sales.StaffListWithBeatMappingModel
import com.app.rupyz.model_kt.order.sales.StaffListWithCustomerMappingModel
import com.app.rupyz.retrofit.RetrofitClient
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call

class BeatRepository {

    fun getCustomerList(
        liveData: MutableLiveData<BeatRouteCustomerInfoModel>,
        beatId: Int,
        name: String,
        customerLevel: String,
        date: String?,
        page: Int
    ) {
        val id = SharedPref.getInstance().getInt(SharePrefConstant.ORG_ID)
        val uploadCred: Call<BeatRouteCustomerInfoModel> =
            RetrofitClient.apiInterface.getCustomerListForBeat(
                id,
                beatId,
                name,
                customerLevel,
                date,
                page
            )

        uploadCred.enqueue(object : NetworkCallback<BeatRouteCustomerInfoModel?>() {
            override fun onSuccess(t: BeatRouteCustomerInfoModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val generic = BeatRouteCustomerInfoModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    generic.error = true
                    generic.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(generic)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun getLeadList(
        liveData: MutableLiveData<BeatRouteCustomerInfoModel>,
        beatId: Int,
        name: String,
        type: String,
        date: String?,
        page: Int
    ) {
        val id = SharedPref.getInstance().getInt(SharePrefConstant.ORG_ID)
        val uploadCred: Call<BeatRouteCustomerInfoModel> =
            RetrofitClient.apiInterface.getCustomerListForBeat(
                id,
                beatId,
                name,
                type,
                date,
                page
            )

        uploadCred.enqueue(object : NetworkCallback<BeatRouteCustomerInfoModel?>() {
            override fun onSuccess(t: BeatRouteCustomerInfoModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val generic = BeatRouteCustomerInfoModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    generic.error = true
                    generic.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(generic)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun getCurrentlyActiveBeatPlan(
        liveData: MutableLiveData<ActiveBeatRoutePlanResponseModel>,
        userID: Int?,
        date: String?
    ) {
        val id = SharedPref.getInstance().getInt(SharePrefConstant.ORG_ID)
        val uploadCred: Call<ActiveBeatRoutePlanResponseModel> =
            RetrofitClient.apiInterface.getActiveBeatPlanList(id, date, userID, true)

        uploadCred.enqueue(object : NetworkCallback<ActiveBeatRoutePlanResponseModel?>() {
            override fun onSuccess(t: ActiveBeatRoutePlanResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val generic = ActiveBeatRoutePlanResponseModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    generic.error = true
                    generic.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(generic)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun getStaffBeatPlanInfoList(
        liveData: MutableLiveData<ActiveBeatRoutePlanResponseModel>,
        beatPlanId: Int,
        date: String?
    ) {
        val id = SharedPref.getInstance().getInt(SharePrefConstant.ORG_ID)
        val uploadCred: Call<ActiveBeatRoutePlanResponseModel> =
            RetrofitClient.apiInterface.getStaffBeatPlanInfoList(id, beatPlanId, date)

        uploadCred.enqueue(object : NetworkCallback<ActiveBeatRoutePlanResponseModel?>() {
            override fun onSuccess(t: ActiveBeatRoutePlanResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val generic = ActiveBeatRoutePlanResponseModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    generic.error = true
                    generic.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(generic)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }


    fun getBeatPlanInfoForEdit(
        liveData: MutableLiveData<BeatRouteDailyPlanResponseModel>,
        beat_route_plan_id: Int,
    ) {
        val id = SharedPref.getInstance().getInt(SharePrefConstant.ORG_ID)
        val uploadCred: Call<BeatRouteDailyPlanResponseModel> =
            RetrofitClient.apiInterface.getDailyBeatPlanList(id, beat_route_plan_id, null)

        uploadCred.enqueue(object : NetworkCallback<BeatRouteDailyPlanResponseModel?>() {
            override fun onSuccess(t: BeatRouteDailyPlanResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val generic = BeatRouteDailyPlanResponseModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    generic.error = true
                    generic.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(generic)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun getBeatPlanList(
        liveData: MutableLiveData<BeatRoutePlanListResponseModel>,
        date: String?,
        status: String,
        userID: Int?,
        currentPage: Int
    ) {
        val id = SharedPref.getInstance().getInt(SharePrefConstant.ORG_ID)
        val uploadCred: Call<BeatRoutePlanListResponseModel> =
            RetrofitClient.apiInterface.getBeatPlanList(id, date, status, userID, currentPage)

        uploadCred.enqueue(object : NetworkCallback<BeatRoutePlanListResponseModel?>() {
            override fun onSuccess(t: BeatRoutePlanListResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val generic = BeatRoutePlanListResponseModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    generic.error = true
                    generic.errorCode = failureResponse?.errorCode
                    generic.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(generic)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun getDailyBeatPlanList(
        liveData: MutableLiveData<BeatRouteDailyPlanResponseModel>,
        beatPlanId: Int,
        date: String?
    ) {
        val id = SharedPref.getInstance().getInt(SharePrefConstant.ORG_ID)
        val uploadCred: Call<BeatRouteDailyPlanResponseModel> =
            RetrofitClient.apiInterface.getDailyBeatPlanList(id, beatPlanId, date)

        uploadCred.enqueue(object : NetworkCallback<BeatRouteDailyPlanResponseModel?>() {
            override fun onSuccess(t: BeatRouteDailyPlanResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val generic = BeatRouteDailyPlanResponseModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    generic.error = true
                    generic.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(generic)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun getPendingBeatPlanList(
        liveData: MutableLiveData<BeatRoutePlanListResponseModel>,
        status: String,
        currentPage: Int
    ) {
        val id = SharedPref.getInstance().getInt(SharePrefConstant.ORG_ID)
        val uploadCred: Call<BeatRoutePlanListResponseModel> =
            RetrofitClient.apiInterface.getPendingBeatPlanList(id, status, currentPage)

        uploadCred.enqueue(object : NetworkCallback<BeatRoutePlanListResponseModel?>() {
            override fun onSuccess(t: BeatRoutePlanListResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val generic = BeatRoutePlanListResponseModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    generic.error = true
                    generic.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(generic)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun createBeatPlan(
        liveData: MutableLiveData<BeatRoutePlanResponseModel>,
        model: CreateBeatRoutePlanModel,
        beatId: Int
    ) {
        val id = SharedPref.getInstance().getInt(SharePrefConstant.ORG_ID)

        val updatedBeatPlanId: Int?

        val uploadCred: Call<BeatRoutePlanResponseModel>

        if (beatId != 0) {
            updatedBeatPlanId = beatId
            uploadCred =
                RetrofitClient.apiInterface.updateBeatRoutePlan(id, updatedBeatPlanId, model)
        } else {
            uploadCred =
                RetrofitClient.apiInterface.createBeatRoutePlan(id, model)
        }

        uploadCred.enqueue(object : NetworkCallback<BeatRoutePlanResponseModel?>() {
            override fun onSuccess(t: BeatRoutePlanResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val generic = BeatRoutePlanResponseModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    generic.error = true
                    generic.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(generic)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun createBeat(
        liveData: MutableLiveData<GenericResponseModel>,
        beatId: Int?,
        model: AddBeatModel
    ) {
        val id = SharedPref.getInstance().getInt(SharePrefConstant.ORG_ID)

        val uploadCred: Call<GenericResponseModel> = if (beatId != null) {
            RetrofitClient.apiInterface.updateBeat(id, beatId, model)
        } else {
            RetrofitClient.apiInterface.createBeat(id, model)
        }

        uploadCred.enqueue(object : NetworkCallback<GenericResponseModel?>() {
            override fun onSuccess(t: GenericResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val generic = GenericResponseModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    generic.error = true
                    generic.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(generic)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun getOrgBeatList(
        liveData: MutableLiveData<OrgBeatListResponseModel>,
        name: String,
        staffId: Int,
        page: Int,
        pageSize: Int?,
        offlineDataLastSyncedTime: String?
    ) {
        val id = SharedPref.getInstance().getInt(SharePrefConstant.ORG_ID)
        val uploadCred: Call<OrgBeatListResponseModel> = if (pageSize == null) {
            RetrofitClient.apiInterface.getOrgBeatList(
                id,
                staffId,
                name,
                true,
                page,
                null,
                false,
                null
            )
        } else {
            RetrofitClient.apiInterface.getOrgBeatList(
                id,
                staffId,
                name,
                true,
                page,
                pageSize,
                true,
                offlineDataLastSyncedTime
            )
        }

        uploadCred.enqueue(object : NetworkCallback<OrgBeatListResponseModel?>() {
            override fun onSuccess(t: OrgBeatListResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val generic = OrgBeatListResponseModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    generic.error = true
                    generic.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(generic)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun getCustomerBeatMapping(
        liveData: MutableLiveData<OrgBeatListResponseModel>,
        customerId: Int,
        headers: String?
    ) {
        val id = SharedPref.getInstance().getInt(SharePrefConstant.ORG_ID)
        val uploadCred: Call<OrgBeatListResponseModel> =
            RetrofitClient.apiInterface.getCustomerBeatMapping(
                id,
                customerId, headers.divideHeadersIntoQueryParams().first,
                headers.divideHeadersIntoQueryParams().second
            )


        uploadCred.enqueue(object : NetworkCallback<OrgBeatListResponseModel?>() {
            override fun onSuccess(t: OrgBeatListResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val generic = OrgBeatListResponseModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    generic.error = true
                    generic.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(generic)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun getListOfCustomerMappingForBeat(
        liveData: MutableLiveData<CustomerListForBeatModel>,
        beatId: Int?,
        name: String,
        date: String?,
        beatRoutePlanId: Int,
        status: String,
        forBeatPlan: Boolean,
        customerLevel: String,
        customerParentID: Int?,
        filterCustomerType: ArrayList<CustomerTypeDataItem>,
        sortByOrder: String,
        page: Int
    ) {
        val id = SharedPref.getInstance().getInt(SharePrefConstant.ORG_ID)

        val sortBy = if (sortByOrder.isNotEmpty()) {
            AppConstant.NAME
        } else {
            AppConstant.CREATED_AT
        }

        val stringBuilder = StringBuilder()

        filterCustomerType.forEachIndexed { index, s ->
            stringBuilder.append(s.name)

            if (index < filterCustomerType.size) {
                stringBuilder.append(",")
            }
        }

        val uploadCred: Call<CustomerListForBeatModel> =
            RetrofitClient.apiInterface.getCustomerMappingListForBeat(
                id,
                beatId,
                name,
                date,
                beatRoutePlanId,
                forBeatPlan,
                true,
                status,
                customerLevel,
                customerParentID,
                stringBuilder,
                sortBy,
                sortByOrder,
                page
            )

        uploadCred.enqueue(object : NetworkCallback<CustomerListForBeatModel?>() {
            override fun onSuccess(t: CustomerListForBeatModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val generic = CustomerListForBeatModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    generic.error = true
                    generic.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(generic)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun deleteBeatPlan(
        liveData: MutableLiveData<BeatRoutePlanResponseModel>,
        beatId: Int,
        forced: Boolean
    ) {
        val id = SharedPref.getInstance().getInt(SharePrefConstant.ORG_ID)

        val model = JsonObject()
        if (forced) {
            model.addProperty("is_forced", true)
        }

        val uploadCred: Call<BeatRoutePlanResponseModel> =
            RetrofitClient.apiInterface.deleteBeatPlan(
                id,
                beatId,
                model
            )

        uploadCred.enqueue(object : NetworkCallback<BeatRoutePlanResponseModel?>() {
            override fun onSuccess(t: BeatRoutePlanResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val generic = BeatRoutePlanResponseModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    generic.error = true
                    generic.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(generic)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun beatPlanApprovedOrRejected(
        liveData: MutableLiveData<BeatRoutePlanResponseModel>,
        beatPlanId: Int,
        status: String,
        reason: String
    ) {
        val id = SharedPref.getInstance().getInt(SharePrefConstant.ORG_ID)

        val model = BeatPlanModel()
        if (status == AppConstant.REJECTED) {
            model.status = "REJECTED"
            model.rejectReason = reason
        } else {
            model.status = "APPROVED"
            model.comments = reason
        }

        val uploadCred: Call<BeatRoutePlanResponseModel> =
            RetrofitClient.apiInterface.beatPlanApprovedOrRejected(
                id,
                beatPlanId,
                model
            )

        uploadCred.enqueue(object : NetworkCallback<BeatRoutePlanResponseModel?>() {
            override fun onSuccess(t: BeatRoutePlanResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val generic = BeatRoutePlanResponseModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    generic.error = true
                    generic.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(generic)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })

    }

    fun getBeatPlanHistory(
        liveData: MutableLiveData<CustomerFollowUpListResponseModel>,
        moduleId: Int?,
        currentPage: Int
    ) {
        val id = SharedPref.getInstance().getInt(SharePrefConstant.ORG_ID)

        val uploadCred: Call<CustomerFollowUpListResponseModel> =
            RetrofitClient.apiInterface.getBeatPlanHistory(
                id,
                moduleId,
                AppConstant.BEAT_PLAN,
                currentPage
            )

        uploadCred.enqueue(object : NetworkCallback<CustomerFollowUpListResponseModel?>() {
            override fun onSuccess(t: CustomerFollowUpListResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val generic = CustomerFollowUpListResponseModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    generic.error = true
                    generic.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(generic)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun getBeatList(
        liveData: MutableLiveData<BeatListResponseModel>,
        name: String?,
        filterAssignedStaff: Int,
        customerLevel: String,
        customerParentID: Int?,
        sortByOrder: String,
        currentPage: Int
    ) {
        val id = SharedPref.getInstance().getInt(SharePrefConstant.ORG_ID)

        var sortingOrder = AppConstant.SORTING_LEVEL_DESCENDING
        var sortBy = ""
        if (sortByOrder.isNotEmpty()) {
            sortBy = AppConstant.NAME
            sortingOrder = sortByOrder
        } else {
            sortBy = AppConstant.CREATED_AT
        }

        val uploadCred: Call<BeatListResponseModel> =
            RetrofitClient.apiInterface.getBeatList(
                id,
                name,
                filterAssignedStaff,
                customerLevel,
                customerParentID,
                sortBy,
                sortingOrder,
                currentPage
            )

        uploadCred.enqueue(object : NetworkCallback<BeatListResponseModel?>() {
            override fun onSuccess(t: BeatListResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val generic = BeatListResponseModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    generic.error = true
                    generic.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(generic)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun getBeatList(
        liveData: MutableLiveData<BeatListResponseModel>,
        name: String?,
        currentPage: Int,
        dd: Boolean
    ) {
        val id = SharedPref.getInstance().getInt(SharePrefConstant.ORG_ID)

        var sortingOrder = AppConstant.SORTING_LEVEL_ASCENDING
        var sortBy = ""
        sortBy = AppConstant.NAME


        val uploadCred: Call<BeatListResponseModel> =
            RetrofitClient.apiInterface.getBeatList(
                id,
                name,
                sortBy,
                sortingOrder,
                currentPage, dd
            )
        uploadCred.enqueue(object : NetworkCallback<BeatListResponseModel?>() {
            override fun onSuccess(t: BeatListResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val generic = BeatListResponseModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    generic.error = true
                    generic.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(generic)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun getBeatDetails(
        liveData: MutableLiveData<BeatDetailsResponseModel>,
        beatId: Int
    ) {
        val id = SharedPref.getInstance().getInt(SharePrefConstant.ORG_ID)

        val uploadCred: Call<BeatDetailsResponseModel> =
            RetrofitClient.apiInterface.getBeatDetails(
                id,
                beatId
            )

        uploadCred.enqueue(object : NetworkCallback<BeatDetailsResponseModel?>() {
            override fun onSuccess(t: BeatDetailsResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val generic = BeatDetailsResponseModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    generic.error = true
                    generic.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(generic)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun deleteBeat(
        liveData: MutableLiveData<GenericResponseModel>,
        beatId: Int,
        jsonObject: JsonObject
    ) {
        val id = SharedPref.getInstance().getInt(SharePrefConstant.ORG_ID)

        val uploadCred: Call<GenericResponseModel> =
            RetrofitClient.apiInterface.deleteBeat(
                id,
                beatId,
                jsonObject
            )

        uploadCred.enqueue(object : NetworkCallback<GenericResponseModel?>() {
            override fun onSuccess(t: GenericResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val generic = GenericResponseModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    generic.error = true
                    generic.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(generic)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }


    fun getStaffListWithBeatMapping(
        liveData: MutableLiveData<StaffListWithCustomerMappingModel>,
        beatId: Int,
        name: String,
        getSelectedOnly: Boolean?,
        page: Int
    ) {
        val id = SharedPref.getInstance().getInt(SharePrefConstant.ORG_ID)
        val uploadCred: Call<StaffListWithCustomerMappingModel> =
            RetrofitClient.apiInterface.getStaffListWithBeatMapping(
                id,
                beatId,
                name,
                getSelectedOnly,
                page
            )

        uploadCred.enqueue(object : NetworkCallback<StaffListWithCustomerMappingModel?>() {
            override fun onSuccess(t: StaffListWithCustomerMappingModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val staffAddResponseModel = StaffListWithCustomerMappingModel()
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

    fun getStaffListWithBeatMappingWithData(
        liveData: MutableLiveData<StaffListWithBeatMappingModel>,
        beatId: Int,
        name: String,
        getSelectedOnly: Boolean?,
        page: Int
    ) {
        val id = SharedPref.getInstance().getInt(SharePrefConstant.ORG_ID)
        val uploadCred: Call<StaffListWithBeatMappingModel> =
            RetrofitClient.apiInterface.getStaffListWithBeatMappingWithData(
                id,
                beatId,
                name,
                getSelectedOnly,
                page
            )

        uploadCred.enqueue(object : NetworkCallback<StaffListWithBeatMappingModel?>() {
            override fun onSuccess(t: StaffListWithBeatMappingModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val staffAddResponseModel = StaffListWithBeatMappingModel()
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