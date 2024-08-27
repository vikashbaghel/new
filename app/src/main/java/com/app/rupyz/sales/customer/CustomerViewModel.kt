package com.app.rupyz.sales.customer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.rupyz.databse.DatabaseLogManager
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.createID
import com.app.rupyz.model_kt.AddNewCustomerFormDataModel
import com.app.rupyz.model_kt.CheckInRequest
import com.app.rupyz.model_kt.CheckInResponse
import com.app.rupyz.model_kt.CustomFormCreationModel
import com.app.rupyz.model_kt.CustomerTypeDataItem
import com.app.rupyz.model_kt.CustomerTypeResponseModel
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.model_kt.NewUpdateCustomerInfoModel
import com.app.rupyz.model_kt.OrderTakenByAdminModel
import com.app.rupyz.model_kt.checkIn.CheckInStatus
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
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import java.util.Calendar

class CustomerViewModel : ViewModel() {

    private var customerListLiveData = MutableLiveData<CustomerInfoModel>()
    var distributorListLiveData = MutableLiveData<CustomerInfoModel>()
    private var orderTakenByAdminListLiveData = MutableLiveData<OrderTakenByAdminModel>()
    var parentCustomerListLiveData = MutableLiveData<CustomerInfoModel>()
    var offlineCustomerListWithErrorLiveData = MutableLiveData<Pair<Boolean, CustomerInfoModel>>()
    private var customerByIdLiveData = MutableLiveData<UpdateCustomerInfoModel>()
    private var newCustomerByIdLiveData = MutableLiveData<NewUpdateCustomerInfoModel>()
    private var checkInLiveData = MutableLiveData<CheckInResponse>()
    private var newCustomerUpdateLiveData = MutableLiveData<NewUpdateCustomerInfoModel>()
    private var customerAddLiveData = MutableLiveData<CustomerAddResponseModel>()
    private var customerUpdateLiveData = MutableLiveData<CustomerAddResponseModel>()
    var customerDetailsLiveData = MutableLiveData<CustomerPanOrGstInfoModel>()
    var customerDeleteLiveData = MutableLiveData<CustomerDeleteResponseModel>()
    var checkInStatusLiveData = MutableLiveData<CheckInStatus>()

    var addNewSegmentLiveData = MutableLiveData<GenericResponseModel>()
    var addNewCategoryLiveData = MutableLiveData<GenericResponseModel>()
    var customerTypeLiveData = MutableLiveData<CustomerTypeResponseModel>()
    var addNewCustomerFormData = MutableLiveData<CustomFormCreationModel>()

    var segmentLiveData = MutableLiveData<SegmentListResponseModel>()

    var staffListWithCustomerMappingLiveDataWith =
        MutableLiveData<StaffListWithCustomerMappingModel>()


    //    # Get the list of all customer
    fun getCustomerListData(): MutableLiveData<CustomerInfoModel> {
        return customerListLiveData
    }

    fun getCheckIn(): MutableLiveData<CheckInResponse> {
        return checkInLiveData
    }


    fun getSegmentList(page: Int) {
        CustomerRepository().getSegmentList(segmentLiveData, page)
    }

    //    # Get customer by user id
    fun getCustomerByIdData(): MutableLiveData<UpdateCustomerInfoModel> {
        return customerByIdLiveData
    }

    fun getCustomerByIdDataNew(): MutableLiveData<NewUpdateCustomerInfoModel> {
        return newCustomerByIdLiveData
    }

    fun getCustomerByIdNew(customerId: Int, hasInternetConnection: Boolean) {
        viewModelScope.launch {
            if (hasInternetConnection) {
                CustomerRepository().getCustomerByIdNew(newCustomerByIdLiveData, customerId)
            }
        }
    }

    fun getCheckInData(checkInRequest: CheckInRequest, hasInternetConnection: Boolean) {
        viewModelScope.launch {
            if (hasInternetConnection) {
                CustomerRepository().getCheckIn(checkInLiveData, checkInRequest)
            }
        }
    }


    fun getCustomerById(customerId: Int, hasInternetConnection: Boolean) {
        viewModelScope.launch {
            if (hasInternetConnection) {
                CustomerRepository().getCustomerById(customerByIdLiveData, customerId)
            } else {
                CustomerRepository().getOfflineCustomerDetailsById(customerByIdLiveData, customerId)
            }
        }
    }

    fun getAddNewCustomerFormData(pageNumber: Int, isOnline: Boolean) {
        viewModelScope.launch {
            CustomerRepository().getAddNewCustomerFormData(
                addNewCustomerFormData,
                pageNumber,
                isOnline
            )
        }
    }


    //    # Create new customer
    fun saveCustomer(customer: CustomerData, hasInternet: Boolean) {
        viewModelScope.launch {
            if (hasInternet) {
                CustomerRepository().addCustomer(customerAddLiveData, customer)
            } else {
                customer.id = createID()
                customer.createdAt =
                    DateFormatHelper.convertDateToIsoUTCFormat(Calendar.getInstance().time)
                customer.source = AppConstant.ANDROID_OFFLINE_TAG
                customer.isSyncedToServer = false

                if (customer.selectCategory != null && customer.selectCategory?.addSet.isNullOrEmpty()
                        .not()
                ) {
                    customer.productCategory = customer.selectCategory?.addSet
                }

                DatabaseLogManager.getInstance().saveOfflineCustomer(customerAddLiveData, customer)
            }
        }
    }

    fun saveCustomerNew(customer: AddNewCustomerFormDataModel, hasInternet: Boolean) {
        viewModelScope.launch {
            if (hasInternet) {
                CustomerRepository().addCustomerNew(customerAddLiveData, customer)
            }
        }
    }

    //    # Update new customer
    fun updateCustomerNew(
        customerData: AddNewCustomerFormDataModel,
        customerId: Int,
        hasInternet: Boolean
    ) {
        viewModelScope.launch {
            if (hasInternet) {
                CustomerRepository().updateCustomerNew(
                    newCustomerUpdateLiveData,
                    customerData,
                    customerId
                )
            }
        }
    }

    fun updateCustomerNewLiveData(): MutableLiveData<NewUpdateCustomerInfoModel> {
        return newCustomerUpdateLiveData
    }


    fun addCustomerLiveData(): MutableLiveData<CustomerAddResponseModel> {
        return customerAddLiveData
    }


    //    # Update new customer
    fun updateCustomer(customerData: CustomerData, customerId: Int, hasInternet: Boolean) {
        viewModelScope.launch {
            if (hasInternet) {
                CustomerRepository().updateCustomer(
                    customerUpdateLiveData,
                    customerData,
                    customerId
                )
            } else {
                DatabaseLogManager.getInstance()
                    .updateOfflineCustomer(customerUpdateLiveData, customerData, customerId)
            }
        }
    }

    fun inactiveCustomer(customerId: Int, option: CustomerDeleteOptionModel, hasInternet: Boolean) {
        if (hasInternet) {
            CustomerRepository().inactiveCustomer(customerDeleteLiveData, customerId, option)
        } else {
            DatabaseLogManager.getInstance()
                .deleteOfflineCustomer(customerDeleteLiveData, customerId)
        }
    }


    fun updateCustomerLiveData(): MutableLiveData<CustomerAddResponseModel> {
        return customerUpdateLiveData
    }

    fun orderAdminLiveData(): MutableLiveData<OrderTakenByAdminModel> {
        return orderTakenByAdminListLiveData
    }


    //    # Fetch customer details form the pan number
    fun getCustomerDetailsByPan(jsonData: PanDataInfoModel) {
        CustomerRepository().getCustomerDetailsByPan(customerDetailsLiveData, jsonData)
    }

    //    # Fetch customer details form the GST number
    fun getCustomerDetailsByGst(gstNumber: String) {
        CustomerRepository().getCustomerDetailsByGst(customerDetailsLiveData, gstNumber)
    }

    fun addNewCategory(jsonData: JsonObject) {
        CustomerRepository().addNewCategory(addNewCategoryLiveData, jsonData)
    }

    fun getStaffListWithCustomerMapping(
        customerId: Int,
        name: String,
        headers: String?,
        hasInternet: Boolean
    ) {
        viewModelScope.launch {
            if (hasInternet) {
                CustomerRepository().getStaffListWithCustomerMapping(
                    staffListWithCustomerMappingLiveDataWith,
                    customerId,
                    name,
                    headers
                )
            } else {
                DatabaseLogManager.getInstance().getOfflineStaffListForAddCustomer(
                    staffListWithCustomerMappingLiveDataWith,
                    name,
                    1
                )
            }
        }
    }

    fun getCustomerTypeList(name: String, currentPage: Int, hasInternetConnection: Boolean) {
        viewModelScope.launch {
            if (hasInternetConnection) {
                CustomerRepository().getCustomerTypeList(
                    customerTypeLiveData,
                    name,
                    currentPage,
                    null,
                    null
                )
            } else {
                CustomerRepository().getOfflineCustomerTypeList(
                    customerTypeLiveData,
                    name,
                    currentPage
                )
            }
        }
    }

    fun getCustomerList(
        customerParentId: Int?,
        name: String,
        filterCustomerLevel: String,
        filterCustomerType: ArrayList<CustomerTypeDataItem>,
        sortByOrder: String,
        currentPage: Int,
        hasInternetConnection: Boolean
    ) {
        viewModelScope.launch {
            if (hasInternetConnection) {
                CustomerRepository().getCustomerList(
                    customerListLiveData,
                    customerParentId,
                    name,
                    filterCustomerLevel,
                    filterCustomerType,
                    sortByOrder,
                    currentPage,
                    null,
                    null
                )
            } else {
                CustomerRepository().getOfflineCustomerList(
                    customerListLiveData,
                    customerParentId,
                    name,
                    filterCustomerLevel,
                    filterCustomerType,
                    sortByOrder,
                    currentPage
                )
            }
        }
    }

    fun getOrderTakenByAdminList(
        currentPage: Int,
        name: String,
        dd: Boolean,
        hasInternetConnection: Boolean
    ) {
        viewModelScope.launch {
            if (hasInternetConnection) {
                CustomerRepository().getOrderTakenByAdmin(
                    orderTakenByAdminListLiveData,
                    currentPage,
                    name,
                    dd
                )
            }
        }
    }

    fun getCustomerListFiltered(
        customerParentId: Int?,
        name: String,
        filterCustomerLevel: String?,
        filterCustomerType: ArrayList<CustomerTypeDataItem>,
        sortByOrder: String,
        currentPage: Int,
        customerFilterParams: HashMap<String, String>,
        hasInternetConnection: Boolean
    ) {
        viewModelScope.launch {
            if (hasInternetConnection) {
                CustomerRepository().getCustomerList(
                    customerListLiveData,
                    customerParentId,
                    name,
                    filterCustomerLevel,
                    filterCustomerType,
                    sortByOrder,
                    currentPage,
                    null,
                    null,
                    customerFilterParams
                )
            } else {
                CustomerRepository().getOfflineCustomerList(
                    customerListLiveData,
                    customerParentId,
                    name,
                    filterCustomerLevel,
                    filterCustomerType,
                    sortByOrder,
                    currentPage
                )
            }
        }
    }

    fun getCustomerListMapped(
        customerId: Int?,
        name: String,
        filterCustomerLevel: String,
        filterCustomerType: ArrayList<CustomerTypeDataItem>,
        ignoreMapping: Boolean,
        sortByOrder: String,
        header: String,
        hasInternetConnection: Boolean
    ) {
        viewModelScope.launch {
            if (hasInternetConnection) {
                CustomerRepository().getCustomerListMapped(
                    liveData = customerListLiveData,
                    customerId = customerId,
                    name = name,
                    customerLevel = filterCustomerLevel,
                    filterCustomerType = filterCustomerType,
                    ignoreMapping = ignoreMapping,
                    sortByOrder = sortByOrder,
                    pageSize = null,
                    headers = header,
                    offlineDataLastSyncedTime = null
                )
            }
            /*else {
                CustomerRepository().getOfflineCustomerList(customerListLiveData, customerParentId, name, filterCustomerLevel, filterCustomerType, sortByOrder, currentPage)
            }*/
        }
    }


    fun getDistributorListMapped(
        customerId: Int?,
        name: String,
        filterCustomerLevel: String,
        filterCustomerType: ArrayList<CustomerTypeDataItem>,
        sortByOrder: String,
        header: String,
        hasInternetConnection: Boolean
    ) {
        viewModelScope.launch {
            if (hasInternetConnection) {
                CustomerRepository().getCustomerListMapped(
                    liveData = distributorListLiveData,
                    customerId = customerId,
                    name = name,
                    customerLevel = filterCustomerLevel,
                    filterCustomerType = filterCustomerType,
                    sortByOrder = sortByOrder,
                    pageSize = null,
                    headers = header,
                    offlineDataLastSyncedTime = null,
                    ignoreMapping = true
                )
            }
            /*else {
                CustomerRepository().getOfflineCustomerList(customerListLiveData, customerParentId, name, filterCustomerLevel, filterCustomerType, sortByOrder, currentPage)
            }*/
        }
    }


    fun getParentCustomerList(
        name: String,
        filterCustomerLevel: String,
        filterCustomerType: ArrayList<CustomerTypeDataItem>,
        sortByOrder: String,
        currentPage: Int
    ) {
        viewModelScope.launch {
            CustomerRepository().getCustomerList(
                parentCustomerListLiveData,
                null,
                name,
                filterCustomerLevel,
                filterCustomerType,
                sortByOrder,
                currentPage,
                null,
                null
            )
        }
    }

    fun checkOfflineCustomerWithErrorList() {
        viewModelScope.launch {
            DatabaseLogManager.getInstance()
                .checkOfflineCustomerWithErrorAvailable(offlineCustomerListWithErrorLiveData)
        }
    }

    fun getUserCheckInStatus(hasInternetConnection: Boolean) {
        viewModelScope.launch {
            CustomerRepository().getUserCheckInStatus(checkInStatusLiveData, hasInternetConnection)
        }
    }

}