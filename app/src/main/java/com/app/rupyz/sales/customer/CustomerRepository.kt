package com.app.rupyz.sales.customer

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.rupyz.databse.DatabaseLogManager
import com.app.rupyz.generic.base.FailureResponse
import com.app.rupyz.generic.base.NetworkCallback
import com.app.rupyz.generic.helper.divideHeadersIntoQueryParams
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.AddNewSegmentModel
import com.app.rupyz.model_kt.CustomerTypeDataItem
import com.app.rupyz.model_kt.CustomerTypeResponseModel
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.model_kt.Headers
import com.app.rupyz.model_kt.order.customer.CustomerAddResponseModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.customer.CustomerDeleteOptionModel
import com.app.rupyz.model_kt.order.customer.CustomerDeleteResponseModel
import com.app.rupyz.model_kt.order.customer.CustomerInfoModel
import com.app.rupyz.model_kt.order.customer.CustomerPanOrGstInfoModel
import com.app.rupyz.model_kt.order.customer.PanDataInfoModel
import com.app.rupyz.model_kt.order.customer.SegmentListResponseModel
import com.app.rupyz.model_kt.order.customer.UpdateCustomerInfoModel
import com.app.rupyz.model_kt.order.sales.StaffListWithCustomerMappingModel
import com.app.rupyz.retrofit.OfflineRetrofitClient
import com.app.rupyz.retrofit.RetrofitClient
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call

class CustomerRepository {

    fun getCustomerList(
            liveData: MutableLiveData<CustomerInfoModel>,
            customerParentID: Int?,
            name: String,
            customerLevel: String,
            filterCustomerType: ArrayList<CustomerTypeDataItem>,
            sortByOrder: String,
            page: Int,
            pageSize: Int?,
            offlineDataLastSyncedTime: String?
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val stringBuilder = StringBuilder()

        var sortingOrder = AppConstant.SORTING_LEVEL_DESCENDING
        var sortBy = ""
        if (sortByOrder.isNotEmpty()) {
            sortBy = AppConstant.NAME
            sortingOrder = sortByOrder
        } else {
            sortBy = AppConstant.CREATED_AT
        }

        filterCustomerType.forEachIndexed { index, s ->
            stringBuilder.append(s.name)
            if (index < filterCustomerType.size) {
                stringBuilder.append(",")
            }
        }

        val uploadCred: Call<CustomerInfoModel> = if (pageSize == null) {
            RetrofitClient.apiInterface.getCustomerList(
                    id,
                    customerParentID,
                    name,
                    customerLevel,
                    stringBuilder,
                    sortBy,
                    sortingOrder,
                    page,
                    null,
                    null, null
            )
        } else {
            OfflineRetrofitClient.offlineApiInterface.getCustomerList(
                    id,
                    customerParentID,
                    name,
                    customerLevel,
                    stringBuilder,
                    sortBy,
                    sortingOrder,
                    page,
                    pageSize, true, offlineDataLastSyncedTime
            )
        }

        uploadCred.enqueue(object : NetworkCallback<CustomerInfoModel?>() {
            override fun onSuccess(t: CustomerInfoModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                    t?.data.let {
                        DatabaseLogManager.getInstance().insertCustomerData(it!!)
                    }
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val model = CustomerInfoModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                            parser.parse(failureResponse?.errorBody) as JsonObject?

                    model.error = true
                    model.errorCode = failureResponse?.errorCode
                    model.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
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

    fun getOfflineCustomerList(
            liveData: MutableLiveData<CustomerInfoModel>,
            customerParentID: Int?,
            name: String,
            customerLevel: String,
            filterCustomerType: ArrayList<CustomerTypeDataItem>,
            sortByOrder: String,
            page: Int
    ) {
        DatabaseLogManager.getInstance().getOfflineCustomerList(
                liveData,
                customerParentID,
                name,
                customerLevel,
                filterCustomerType,
                sortByOrder,
                page
        )
    }

    fun getSegmentList(liveData: MutableLiveData<SegmentListResponseModel>, page: Int) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<SegmentListResponseModel> =
                RetrofitClient.apiInterface.getSegmentList(id, page)

        uploadCred.enqueue(object : NetworkCallback<SegmentListResponseModel?>() {
            override fun onSuccess(t: SegmentListResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val model = SegmentListResponseModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                            parser.parse(failureResponse?.errorBody) as JsonObject?

                    model.error = true
                    model.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
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


    fun getCustomerById(liveData: MutableLiveData<UpdateCustomerInfoModel>, customerId: Int) {
        val uploadCred: Call<UpdateCustomerInfoModel> =
                RetrofitClient.apiInterface.getCustomerById(
                        SharedPref.getInstance().getInt(ORG_ID),
                        customerId
                )

        uploadCred.enqueue(object : NetworkCallback<UpdateCustomerInfoModel?>() {
            override fun onSuccess(t: UpdateCustomerInfoModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val staffAddResponseModel = UpdateCustomerInfoModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                            parser.parse(failureResponse?.errorBody) as JsonObject?

                    staffAddResponseModel.error = true
                    staffAddResponseModel.errorCode = failureResponse?.errorCode
                    staffAddResponseModel.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
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

    fun getOfflineCustomerDetailsById(
            liveData: MutableLiveData<UpdateCustomerInfoModel>,
            customerId: Int
    ) {
        DatabaseLogManager.getInstance().getOfflineCustomerDetailsById(liveData, customerId)
    }

    fun addCustomer(liveData: MutableLiveData<CustomerAddResponseModel>, jsonData: CustomerData) {
        val uploadCred: Call<CustomerAddResponseModel> =
                RetrofitClient.apiInterface.addCustomer(
                        jsonData,
                        SharedPref.getInstance().getInt(ORG_ID)
                )

        uploadCred.enqueue(object : NetworkCallback<CustomerAddResponseModel?>() {

            override fun onSuccess(t: CustomerAddResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val staffAddResponseModel = CustomerAddResponseModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                            parser.parse(failureResponse?.errorBody) as JsonObject?

                    staffAddResponseModel.error = true
                    staffAddResponseModel.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
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

    fun updateCustomer(
            liveData: MutableLiveData<CustomerAddResponseModel>, jsonData: CustomerData,
            customerId: Int
    ) {
        val uploadCred: Call<CustomerAddResponseModel> =
                RetrofitClient.apiInterface.updateCustomer(
                        jsonData,
                        SharedPref.getInstance().getInt(ORG_ID),
                        customerId
                )

        uploadCred.enqueue(object : NetworkCallback<CustomerAddResponseModel?>() {

            override fun onSuccess(t: CustomerAddResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val staffAddResponseModel = CustomerAddResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    staffAddResponseModel.error = true
                    staffAddResponseModel.message = jsonObj?.get("message")?.asString

                } catch (Ex: Exception) {
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

    fun inactiveCustomer(
            liveData: MutableLiveData<CustomerDeleteResponseModel>,
            customerId: Int,
            option: CustomerDeleteOptionModel
    ) {
        val uploadCred: Call<CustomerDeleteResponseModel> =
                RetrofitClient.apiInterface.inactiveCustomer(
                        SharedPref.getInstance().getInt(ORG_ID),
                        customerId,
                        option
                )

        uploadCred.enqueue(object : NetworkCallback<CustomerDeleteResponseModel?>() {

            override fun onSuccess(t: CustomerDeleteResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val staffAddResponseModel = CustomerDeleteResponseModel()
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


    fun getCustomerDetailsByPan(
            liveData: MutableLiveData<CustomerPanOrGstInfoModel>,
            jsonData: PanDataInfoModel,
    ) {
        val uploadCred: Call<CustomerPanOrGstInfoModel> =
                RetrofitClient.apiInterface.createOrganization(jsonData)

        uploadCred.enqueue(object : NetworkCallback<CustomerPanOrGstInfoModel?>() {

            override fun onSuccess(t: CustomerPanOrGstInfoModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val staffAddResponseModel = CustomerPanOrGstInfoModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    staffAddResponseModel.error = true
                    staffAddResponseModel.message = jsonObj?.get("message")?.asString

                } catch (Ex: Exception) {
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

    fun getCustomerDetailsByGst(
            liveData: MutableLiveData<CustomerPanOrGstInfoModel>,
            gstNumber: String
    ) {
        val uploadCred: Call<CustomerPanOrGstInfoModel> =
                RetrofitClient.apiInterface.getCustomerDetailsByGst(
                        SharedPref.getInstance().getInt(ORG_ID), gstNumber
                )

        uploadCred.enqueue(object : NetworkCallback<CustomerPanOrGstInfoModel?>() {

            override fun onSuccess(t: CustomerPanOrGstInfoModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val staffAddResponseModel = CustomerPanOrGstInfoModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    staffAddResponseModel.error = true
                    staffAddResponseModel.message = jsonObj?.get("message")?.asString

                } catch (Ex: Exception) {
                    Ex.printStackTrace()
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


    fun addNewSegment(
            liveData: MutableLiveData<GenericResponseModel>,
            jsonData: AddNewSegmentModel,
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<GenericResponseModel> =
                RetrofitClient.apiInterface.addNewSegment(id, jsonData)

        uploadCred.enqueue(object : NetworkCallback<GenericResponseModel?>() {

            override fun onSuccess(t: GenericResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                Log.e("DEBUG", "ERROR = ${failureResponse?.errorMessage}")

                val staffAddResponseModel = GenericResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    staffAddResponseModel.error = true
                    staffAddResponseModel.message = jsonObj?.get("message")?.asString

                } catch (Ex: Exception) {
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

    fun addNewCategory(
            liveData: MutableLiveData<GenericResponseModel>,
            jsonData: JsonObject,
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<GenericResponseModel> =
                RetrofitClient.apiInterface.addNewCategory(id, jsonData)

        uploadCred.enqueue(object : NetworkCallback<GenericResponseModel?>() {

            override fun onSuccess(t: GenericResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                Log.e("DEBUG", "ERROR = ${failureResponse?.errorMessage}")

                val staffAddResponseModel = GenericResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    staffAddResponseModel.error = true
                    staffAddResponseModel.message = jsonObj?.get("message")?.asString

                } catch (Ex: Exception) {
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

    fun updateSegment(
            liveData: MutableLiveData<GenericResponseModel>,
            jsonData: AddNewSegmentModel,
            segment_id: Int
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<GenericResponseModel> =
                RetrofitClient.apiInterface.updateSegment(id, segment_id, jsonData)

        uploadCred.enqueue(object : NetworkCallback<GenericResponseModel?>() {

            override fun onSuccess(t: GenericResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                Log.e("DEBUG", "ERROR = ${failureResponse?.errorMessage}")

                val staffAddResponseModel = GenericResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    staffAddResponseModel.error = true
                    staffAddResponseModel.message = jsonObj?.get("message")?.asString

                } catch (Ex: Exception) {
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

    fun getStaffListWithCustomerMapping(
            liveData: MutableLiveData<StaffListWithCustomerMappingModel>,
            customerId: Int,
            name: String,
            headers: String?
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<StaffListWithCustomerMappingModel> =
                RetrofitClient.apiInterface.getStaffListUsingCustomerMapping(id, customerId, name,
                        headers.divideHeadersIntoQueryParams().first,
                        headers.divideHeadersIntoQueryParams().second)

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

    fun getCustomerTypeList(
            liveData: MutableLiveData<CustomerTypeResponseModel>,
            name: String,
            currentPage: Int,
            pageSize: Int?,
            offlineDataLastSyncedTime: String?
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<CustomerTypeResponseModel> = if (pageSize == null) {
            RetrofitClient.apiInterface.getCustomerTypeList(
                    id, name, currentPage,
                    null, null, null
            )
        } else {
            OfflineRetrofitClient.offlineApiInterface.getCustomerTypeList(
                    id,
                    name,
                    currentPage,
                    pageSize,
                    true, offlineDataLastSyncedTime
            )
        }

        uploadCred.enqueue(object : NetworkCallback<CustomerTypeResponseModel?>() {
            override fun onSuccess(t: CustomerTypeResponseModel?) {
                CoroutineScope(Dispatchers.IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val model = CustomerTypeResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

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

    fun getOfflineCustomerTypeList(
            liveData: MutableLiveData<CustomerTypeResponseModel>,
            name: String,
            currentPage: Int
    ) {
        DatabaseLogManager.getInstance().getOfflineCustomerTypeList(
                liveData,
                name,
                currentPage
        )
    }
}