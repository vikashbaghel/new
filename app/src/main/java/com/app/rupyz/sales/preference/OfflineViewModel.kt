package com.app.rupyz.sales.preference

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.rupyz.databse.DatabaseLogManager
import com.app.rupyz.generic.model.profile.product.ProductInfoModel
import com.app.rupyz.model_kt.AllCategoryInfoModel
import com.app.rupyz.model_kt.AssignRolesResponseModel
import com.app.rupyz.model_kt.BranListResponseModel
import com.app.rupyz.model_kt.CheckInOutListResponseModel
import com.app.rupyz.model_kt.CustomerAddressListResponseModel
import com.app.rupyz.model_kt.CustomerFollowUpListResponseModel
import com.app.rupyz.model_kt.CustomerTypeResponseModel
import com.app.rupyz.model_kt.DispatchedOrderListModel
import com.app.rupyz.model_kt.ExpenseResponseModel
import com.app.rupyz.model_kt.ExpenseTrackerResponseModel
import com.app.rupyz.model_kt.LeadCategoryListResponseModel
import com.app.rupyz.model_kt.LeadListResponseModel
import com.app.rupyz.model_kt.OrgBeatListResponseModel
import com.app.rupyz.model_kt.order.customer.CustomerInfoModel
import com.app.rupyz.model_kt.order.order_history.OrderInfoModel
import com.app.rupyz.model_kt.order.payment.RecordPaymentInfoModel
import com.app.rupyz.model_kt.order.sales.StaffInfoModel
import com.app.rupyz.sales.address.AddressRepository
import com.app.rupyz.sales.beatplan.BeatRepository
import com.app.rupyz.sales.customer.CustomerRepository
import com.app.rupyz.sales.expense.ExpenseRepository
import com.app.rupyz.sales.home.DashboardRepository
import com.app.rupyz.sales.lead.LeadRepository
import com.app.rupyz.sales.orders.OrderRepository
import com.app.rupyz.sales.payment.RecordPaymentRepository
import com.app.rupyz.sales.product.ProductRepository
import com.app.rupyz.sales.staff.StaffRepository
import kotlinx.coroutines.launch

class OfflineViewModel : ViewModel() {
    var orderDispatchListLiveData = MutableLiveData<DispatchedOrderListModel>()
    var beatListLiveData = MutableLiveData<OrgBeatListResponseModel>()
    var leadCategoryLiveData = MutableLiveData<LeadCategoryListResponseModel>()
    var assignedRoleListLiveData = MutableLiveData<AssignRolesResponseModel>()
    var addressLiveData = MutableLiveData<CustomerAddressListResponseModel>()
    var expenseTrackerLiveData = MutableLiveData<ExpenseTrackerResponseModel>()
    var expenseLiveData = MutableLiveData<ExpenseResponseModel>()
    var leadListLiveData = MutableLiveData<LeadListResponseModel>()
    var recordPaymentListLiveData = MutableLiveData<RecordPaymentInfoModel>()
    var staffListLiveData = MutableLiveData<StaffInfoModel>()
    var orderLiveData = MutableLiveData<OrderInfoModel>()
    var customerTypeLiveData = MutableLiveData<CustomerTypeResponseModel>()
    var customerListLiveData = MutableLiveData<CustomerInfoModel>()
    var brandListLiveData = MutableLiveData<BranListResponseModel>()
    var productCategoryLiveData = MutableLiveData<AllCategoryInfoModel>()
    var productLiveData = MutableLiveData<ProductInfoModel>()


    var offlineAddressLiveData = MutableLiveData<CustomerAddressListResponseModel>()
    var offlineOrderLiveData = MutableLiveData<OrderInfoModel>()
    var offlineCustomerLiveData = MutableLiveData<CustomerInfoModel>()
    var offlineLeadLiveData = MutableLiveData<LeadListResponseModel>()
    var offlinePaymentLiveData = MutableLiveData<RecordPaymentInfoModel>()
    var offlineCustomerActivityLiveData = MutableLiveData<CustomerFollowUpListResponseModel>()
    var offlineExpenseHeadLiveData = MutableLiveData<ExpenseTrackerResponseModel>()
    var offlineExpenseListLiveData = MutableLiveData<ExpenseResponseModel>()

    val offlineDataAvailableLiveData = MutableLiveData<Pair<Boolean, Int>>()
    val attendanceLiveData = MutableLiveData<CheckInOutListResponseModel>()

    fun dumpOrderDispatchList(dispatchOrderPageCount: Int, pageSize: Int, offlineDataLastSyncedTime: String?) {
        viewModelScope.launch {
            OrderRepository().dumpOrderDispatchList(orderDispatchListLiveData, dispatchOrderPageCount, pageSize, offlineDataLastSyncedTime)
        }
    }

    fun dumpBeatList(pageCount: Int, pageSize: Int, offlineDataLastSyncedTime: String?) {
        viewModelScope.launch {
            BeatRepository().getOrgBeatList(beatListLiveData, "", 0, pageCount, pageSize, offlineDataLastSyncedTime)
        }
    }

    fun dumpLeadCategory(page: Int, pageSize: Int, offlineDataLastSyncedTime: String?) {
        viewModelScope.launch {
            LeadRepository().getAllCategoryList(leadCategoryLiveData, "", page, pageSize, offlineDataLastSyncedTime)
        }
    }

    fun dumpStaffRoleList(page: Int, pageSize: Int, offlineDataLastSyncedTime: String?) {
        viewModelScope.launch {
            StaffRepository().getRoleList(assignedRoleListLiveData, page, pageSize, offlineDataLastSyncedTime)
        }
    }

    fun dumpAddressList(page: Int, pageSize: Int, offlineDataLastSyncedTime: String?) {
        viewModelScope.launch {
            AddressRepository().getAddressList(addressLiveData, 0, page, pageSize, offlineDataLastSyncedTime)
        }
    }

    fun dumpExpenseHeadList(page: Int, pageSize: Int, offlineDataLastSyncedTime: String?) {
        viewModelScope.launch {
            ExpenseRepository().getTotalExpenseList(expenseTrackerLiveData, "", page, pageSize, offlineDataLastSyncedTime)
        }
    }

    fun dumpTotalExpenseList(page: Int, pageSize: Int, offlineDataLastSyncedTime: String?) {
        viewModelScope.launch {
            ExpenseRepository().getExpenseList(expenseLiveData, null, page, pageSize, offlineDataLastSyncedTime)
        }
    }

    fun dumpLeadList(currentPage: Int, pageSize: Int, offlineDataLastSyncedTime: String?) {
        viewModelScope.launch {
            LeadRepository().getLeadList(leadListLiveData, "", "", currentPage, pageSize, offlineDataLastSyncedTime)
        }
    }

    fun dumpPaymentList(pageNo: Int, pageSize: Int, offlineDataLastSyncedTime: String?) {
        viewModelScope.launch {
            RecordPaymentRepository().getRecordPaymentList(recordPaymentListLiveData, "", pageNo, pageSize, offlineDataLastSyncedTime)
        }
    }

    fun dumpStaffList(page: Int, pageSize: Int, offlineDataLastSyncedTime: String?) {
        viewModelScope.launch {
            StaffRepository().getStaffList(staffListLiveData, "", "", page, pageSize, offlineDataLastSyncedTime,false)
        }
    }

    fun dumpOfflineOrderData(page: Int, pageSize: Int, offlineDataLastSyncedTime: String?) {
       // DashboardRepository().getOrderData(orderLiveData, "", null, "", page, pageSize, offlineDataLastSyncedTime)
    }

    fun dumpOfflineCustomerTypeList(currentPage: Int, pageSize: Int, offlineDataLastSyncedTime: String?) {
        viewModelScope.launch {
            CustomerRepository().getCustomerTypeList(customerTypeLiveData, "", currentPage, pageSize, offlineDataLastSyncedTime)
        }
    }

    fun dumpOfflineCustomerList(currentPage: Int, pageSize: Int, offlineDataLastSyncedTime: String?) {
        viewModelScope.launch {
            CustomerRepository().getCustomerList(customerListLiveData, null, "", "", ArrayList(), "", currentPage, pageSize, offlineDataLastSyncedTime)
        }
    }

    fun dumpOfflineBrandList(page: Int, pageSize: Int, offlineDataLastSyncedTime: String?) {
        viewModelScope.launch {
            ProductRepository().getBrandList(brandListLiveData, "", page, pageSize, offlineDataLastSyncedTime)
        }
    }

    fun dumpOfflineCategoryList(page: Int, pageSize: Int, offlineDataLastSyncedTime: String?) {
        viewModelScope.launch {
            ProductRepository().getAllCategoryList(productCategoryLiveData, null, "", page, pageSize, offlineDataLastSyncedTime)
        }
    }

    fun dumpOfflineProductList(page: Int, pageSize: Int, offlineDataLastSyncedTime: String?) {
        viewModelScope.launch {
            ProductRepository().getProductList(productLiveData, ArrayList(), "", "", page, pageSize, offlineDataLastSyncedTime)
        }
    }

    fun getOrderNotSyncedData() {
        viewModelScope.launch {
            DatabaseLogManager.getInstance().getOrderNotSyncedData(offlineOrderLiveData)
        }
    }

    fun getCustomerNotSyncedData() {
        viewModelScope.launch {
            DatabaseLogManager.getInstance().getCustomerNotSyncedData(offlineCustomerLiveData)
        }
    }

    fun getCustomerAddressNotSyncedData() {
        viewModelScope.launch {
            DatabaseLogManager.getInstance().getCustomerAddressNotSyncedData(offlineAddressLiveData)
        }
    }

    fun getLeadNotSyncedData() {
        viewModelScope.launch {
            DatabaseLogManager.getInstance().getLeadNotSyncedData(offlineLeadLiveData)
        }
    }

    fun getPaymentNotSyncedData() {
        viewModelScope.launch {
            DatabaseLogManager.getInstance().getPaymentNotSyncedData(offlinePaymentLiveData)
        }
    }

    fun getCustomerActivityListData() {
        viewModelScope.launch {
            DatabaseLogManager.getInstance().getCustomerActivityList(offlineCustomerActivityLiveData)
        }
    }


    fun getExpenseHeadListData() {
        viewModelScope.launch {
            DatabaseLogManager.getInstance().getExpenseHeadList(offlineExpenseHeadLiveData)
        }
    }


    fun getExpenseListData() {
        viewModelScope.launch {
            DatabaseLogManager.getInstance().getExpenseList(offlineExpenseListLiveData)
        }
    }

    fun isOfflineDataAvailable() {
        DatabaseLogManager.getInstance().checkOfflineDataAvailable(offlineDataAvailableLiveData)
    }

    fun isAttendanceDataAvailable() {
        DatabaseLogManager.getInstance().checkOfflineAttendanceAvailable(attendanceLiveData)
    }
}