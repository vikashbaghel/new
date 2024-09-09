package com.app.rupyz.sales.preference

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.rupyz.databse.DatabaseLogManager
import com.app.rupyz.model_kt.AddCheckInOutModel
import com.app.rupyz.model_kt.AddExpenseResponseModel
import com.app.rupyz.model_kt.AddLeadResponseModel
import com.app.rupyz.model_kt.AddTotalExpenseResponseModel
import com.app.rupyz.model_kt.CustomerAddressApiResponseModel
import com.app.rupyz.model_kt.CustomerAddressDataItem
import com.app.rupyz.model_kt.CustomerFollowUpDataItem
import com.app.rupyz.model_kt.CustomerFollowUpResponseModel
import com.app.rupyz.model_kt.ExpenseDataItem
import com.app.rupyz.model_kt.ExpenseTrackerDataItem
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.model_kt.LeadLisDataItem
import com.app.rupyz.model_kt.UploadOfflineAttendanceModel
import com.app.rupyz.model_kt.order.customer.CustomerAddResponseModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.order_history.CreateOrderResponseModel
import com.app.rupyz.model_kt.order.order_history.OrderData
import com.app.rupyz.model_kt.order.payment.PaymentRecordResponseModel
import com.app.rupyz.model_kt.order.payment.RecordPaymentData
import com.app.rupyz.sales.address.AddressRepository
import com.app.rupyz.sales.cart.CartRepository
import com.app.rupyz.sales.customer.CustomerRepository
import com.app.rupyz.sales.expense.ExpenseRepository
import com.app.rupyz.sales.lead.LeadRepository
import com.app.rupyz.sales.payment.RecordPaymentRepository
import com.app.rupyz.sales.staffactivitytrcker.StaffActivityRepository
import kotlinx.coroutines.launch

class UploadingViewModel : ViewModel() {
    var customerAddLiveData = MutableLiveData<CustomerAddResponseModel>()
    var confirmOrderLiveData = MutableLiveData<CreateOrderResponseModel>()
    var addLeadLiveData = MutableLiveData<AddLeadResponseModel>()
    var paymentRecordLiveData = MutableLiveData<PaymentRecordResponseModel>()
    var addFeedbackFollowUpLiveData = MutableLiveData<CustomerFollowUpResponseModel>()
    var addBulkAttendanceLiveData = MutableLiveData<GenericResponseModel>()
    var addAttendanceLiveData = MutableLiveData<GenericResponseModel>()
    var addAddressLiveData = MutableLiveData<CustomerAddressApiResponseModel>()
    var addTotalExpenseLiveData = MutableLiveData<AddTotalExpenseResponseModel>()
    var addExpenseListLiveData = MutableLiveData<AddExpenseResponseModel>()

    var updateCustomerLiveData = MutableLiveData<GenericResponseModel>()
    var updateLeadLiveData = MutableLiveData<GenericResponseModel>()
    var updateAddressLiveData = MutableLiveData<GenericResponseModel>()
    var updateExpenseLiveData = MutableLiveData<GenericResponseModel>()

    fun addAttendance(model: AddCheckInOutModel?) {
        viewModelScope.launch {
            StaffActivityRepository().addAttendance(addAttendanceLiveData, model)
        }
    }

    fun addBulkAttendance(model: UploadOfflineAttendanceModel?) {
        viewModelScope.launch {
            StaffActivityRepository().addOfflineAttendance(addBulkAttendanceLiveData, model)
        }
    }

    fun saveCustomer(customer: CustomerData) {
        viewModelScope.launch {
            CustomerRepository().addCustomer(customerAddLiveData, customer)
        }
    }

    fun saveCustomerAddress(address: CustomerAddressDataItem, customerId: Int) {
        viewModelScope.launch {
            AddressRepository().addAddress(addAddressLiveData, customerId, address)
        }
    }

    fun confirmOrder(orderData: OrderData) {
        viewModelScope.launch {
            CartRepository().confirmOrder(confirmOrderLiveData, orderData)
        }
    }

    fun addNewLead(leadModel: LeadLisDataItem) {
        viewModelScope.launch {
            LeadRepository().createNewLead(addLeadLiveData, leadModel)
        }
    }

    fun recordActivity(model: CustomerFollowUpDataItem) {
        viewModelScope.launch {
            StaffActivityRepository().addFeedbackFollowUp(
                    addFeedbackFollowUpLiveData,
                    model
            )
        }
    }

    fun uploadExpenseHead(model: ExpenseTrackerDataItem) {
        viewModelScope.launch {
            ExpenseRepository().addTotalExpenseTracker(addTotalExpenseLiveData, model)
        }
    }

    fun uploadExpenseList(model: ExpenseDataItem) {
        viewModelScope.launch {
            ExpenseRepository().addExpenseTracker(addExpenseListLiveData, model)
        }
    }

    fun updateLeadRelatedActivity(oldLeadId: Int, newLeadId: Int) {
        DatabaseLogManager.getInstance().updateLeadRelatedActivity(updateLeadLiveData,oldLeadId, newLeadId)
    }

    fun recordPayment(paymentData: RecordPaymentData) {
        viewModelScope.launch {
            RecordPaymentRepository().addRecordPayment(paymentRecordLiveData, paymentData)
        }
    }

    fun updateCustomerRelatedActivity(oldCustomerId: Int, newCustomerId: Int) {
        DatabaseLogManager.getInstance().updateCustomerRelatedActivity(updateCustomerLiveData, oldCustomerId, newCustomerId)
    }

    fun deleteOfflineOrders(orderId: Int?) {
        DatabaseLogManager.getInstance().deleteOfflineOrder(orderId)
    }

    fun deleteCustomerActivity(id: Int?) {
        DatabaseLogManager.getInstance().deleteCustomerActivity(id)
    }

    fun updateOrderAddressModel(oldAddressId: Int?, newAddressId: Int?) {
        DatabaseLogManager.getInstance().updateAddressIdInOrders(updateAddressLiveData, oldAddressId, newAddressId)
    }

    fun updateCustomerError(customerId: Int, message: String?) {
        DatabaseLogManager.getInstance().updateCustomerError(customerId, message)
    }

    fun updateExpenseHeadRelatedModel(oldId: Int?, newId: Int?) {
        DatabaseLogManager.getInstance().updateExpenseHeadRelatedModel(updateExpenseLiveData, oldId, newId)
    }

    fun deleteAttendanceData() {
        DatabaseLogManager.getInstance().deleteAttendanceData()
    }

    fun deleteExpenseList(id: Int?) {
        DatabaseLogManager.getInstance().deleteExpenseList(id)
    }

    fun deletePaymentRecords(id: Int?) {
        DatabaseLogManager.getInstance().deletePaymentRecords(id)
    }
}