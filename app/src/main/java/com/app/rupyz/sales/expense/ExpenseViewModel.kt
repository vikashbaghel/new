package com.app.rupyz.sales.expense

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.rupyz.databse.DatabaseLogManager
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.createID
import com.app.rupyz.model_kt.*
import kotlinx.coroutines.launch
import java.util.Calendar

class ExpenseViewModel : ViewModel() {
    var expenseTrackerLiveData = MutableLiveData<ExpenseTrackerResponseModel>()
    var approvalRequestLiveData = MutableLiveData<ApprovalRequestResponseModel>()
    var expenseLiveData = MutableLiveData<ExpenseResponseModel>()
    var addTotalExpenseLiveData = MutableLiveData<AddTotalExpenseResponseModel>()
    var updateExpenseStatusLiveData = MutableLiveData<AddTotalExpenseResponseModel>()
    var addExpenseLiveData = MutableLiveData<AddExpenseResponseModel>()
    var totalExpenseDetailsLiveData = MutableLiveData<AddTotalExpenseResponseModel>()
    var expenseDetailsLiveData = MutableLiveData<AddExpenseResponseModel>()

    fun getTotalExpenseList(status: String, currentPage: Int, hasInternetConnection: Boolean) {
        viewModelScope.launch {
            if (hasInternetConnection) {
                ExpenseRepository().getTotalExpenseList(
                    expenseTrackerLiveData,
                    status,
                    currentPage,
                    null,
                    null
                )
            } else {
                ExpenseRepository().getOfflineTotalExpenseList(
                    expenseTrackerLiveData,
                    status,
                    currentPage
                )
            }
        }
    }

    fun getExpenseList(rtId: Int, hasInternetConnection: Boolean) {
        viewModelScope.launch {
            if (hasInternetConnection) {
                ExpenseRepository().getExpenseList(
                    expenseLiveData,
                    rtId,
                    null,
                    null,
                    null
                )
            } else {
                ExpenseRepository().getOfflineExpenseList(expenseLiveData, rtId)
            }
        }
    }

    fun getApprovalRequestList(status: String) {
        ExpenseRepository().getApprovalRequestList(approvalRequestLiveData, status)
    }

    fun getTotalExpenseDetails(rtId: Int, hasInternetConnection: Boolean) {
        viewModelScope.launch {
            if (hasInternetConnection) {
                ExpenseRepository().getTotalExpenseDetails(totalExpenseDetailsLiveData, rtId)
            } else {
                ExpenseRepository().getOfflineExpenseHeadDetails(totalExpenseDetailsLiveData, rtId)
            }
        }
    }

    fun getExpenseDetails(rmId: Int, hasInternet: Boolean) {
        viewModelScope.launch {
            if (hasInternet) {
                ExpenseRepository().getExpenseDetails(expenseDetailsLiveData, rmId)
            } else {
                DatabaseLogManager.getInstance().getOfflineExpenseDetails(expenseDetailsLiveData, rmId)
            }
        }
    }

    fun addTotalExpense(model: ExpenseTrackerDataItem, hasInternet: Boolean) {
        if (hasInternet) {
            ExpenseRepository().addTotalExpenseTracker(addTotalExpenseLiveData, model)
        } else {
            model.id = createID()
            model.createdAt = DateFormatHelper.convertDateToIsoUTCFormat(Calendar.getInstance().time)
            model.source = AppConstant.ANDROID_OFFLINE_TAG
            model.status = ""
            model.totalAmount = 0.0
            model.totalItems = 0
            model.isSyncedToServer = false
            DatabaseLogManager.getInstance().addOfflineExpenseHead(addTotalExpenseLiveData, model)
        }
    }

    fun addExpense(model: ExpenseDataItem, hasInternet: Boolean) {
        viewModelScope.launch {
            if (hasInternet) {
                ExpenseRepository().addExpenseTracker(addExpenseLiveData, model)
            } else {
                model.id = createID()
                model.createdAt = DateFormatHelper.convertDateToIsoUTCFormat(Calendar.getInstance().time)
                model.source = AppConstant.ANDROID_OFFLINE_TAG
                model.isSyncedToServer = false
                DatabaseLogManager.getInstance().addOfflineExpense(addExpenseLiveData, model)
            }
        }
    }

    fun deleteTotalExpenses(rtId: Int, hasInternet: Boolean) {
        viewModelScope.launch {
            if (hasInternet) {
                ExpenseRepository().deleteExpensesTracker(updateExpenseStatusLiveData, rtId)
            } else {
                DatabaseLogManager.getInstance().deleteOfflineExpenseHead(updateExpenseStatusLiveData, rtId)
            }
        }
    }

    fun deleteExpenses(remData: ExpenseDataItem, hasInternet: Boolean) {
        if (hasInternet) {
            ExpenseRepository().deleteExpense(addExpenseLiveData, remData.id!!)
        } else {
            DatabaseLogManager.getInstance().deleteOfflineExpense(addExpenseLiveData, remData)
        }
    }

    fun updateExpensesStatus(rt_id: Int, status: String, reason: String) {
        ExpenseRepository().updateExpensesTrackerStatus(
            updateExpenseStatusLiveData,
            rt_id,
            status,
            reason
        )
    }

    fun updateTotalExpenses(rt_id: Int, model: ExpenseTrackerDataItem) {
        ExpenseRepository().updateExpensesTracker(updateExpenseStatusLiveData, rt_id, model)
    }

    fun updateExpenses(remId: Int, model: ExpenseDataItem, hasInternet: Boolean) {
        viewModelScope.launch {
            if (hasInternet) {
                ExpenseRepository().updateExpenses(addExpenseLiveData, remId, model)
            } else {
                model.id = remId
                model.createdAt = DateFormatHelper.convertDateToIsoUTCFormat(Calendar.getInstance().time)
                model.source = AppConstant.ANDROID_OFFLINE_TAG
                model.isSyncedToServer = false
                DatabaseLogManager.getInstance().updateOfflineExpense(addExpenseLiveData, model)
            }
        }
    }
}