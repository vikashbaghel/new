package com.app.rupyz.sales.payment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.rupyz.databse.DatabaseLogManager
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.createID
import com.app.rupyz.model_kt.Data
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.model_kt.order.payment.PaymentRecordResponseModel
import com.app.rupyz.model_kt.order.payment.RecordPaymentData
import com.app.rupyz.model_kt.order.payment.RecordPaymentDetailModel
import com.app.rupyz.model_kt.order.payment.RecordPaymentInfoModel
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import java.util.Calendar

class RecordPaymentViewModel : ViewModel() {

    private var recordPaymentListLiveData = MutableLiveData<RecordPaymentInfoModel>()
    var updatePaymentRecordLiveData = MutableLiveData<PaymentRecordResponseModel>()
    var getPaymentRecordDetailLiveData = MutableLiveData<RecordPaymentDetailModel>()
    var deletePaymentLiveData = MutableLiveData<GenericResponseModel>()

    fun getRecordPaymentListData(): MutableLiveData<RecordPaymentInfoModel> {
        return recordPaymentListLiveData
    }

    fun getRecordPaymentList(id: Int, pageNo: Int) {
        RecordPaymentRepository().getRecordPaymentListById(recordPaymentListLiveData, id, pageNo)

    }

    fun getPaymentList(pageNo: Int, status: String, hasInternetConnection: Boolean) {
        viewModelScope.launch {
            if (hasInternetConnection) {
                RecordPaymentRepository().getRecordPaymentList(
                        recordPaymentListLiveData,
                        status,
                        pageNo,
                        null,
                        null
                )
            } else {
                RecordPaymentRepository().getOfflinePaymentList(
                        recordPaymentListLiveData,
                        status,
                        pageNo
                )
            }
        }
    }

    fun recordPayment(paymentData: RecordPaymentData, hasInternetConnection: Boolean) {
        viewModelScope.launch {
            if (hasInternetConnection) {
                RecordPaymentRepository().addRecordPayment(updatePaymentRecordLiveData, paymentData)
            } else {
                paymentData.id = createID()
                paymentData.createdAt = DateFormatHelper.convertDateToIsoUTCFormat(Calendar.getInstance().time)
                paymentData.source = AppConstant.ANDROID_OFFLINE_TAG
                paymentData.status = ""
                paymentData.isSyncedToServer = false
                DatabaseLogManager.getInstance().addOfflinePaymentData(updatePaymentRecordLiveData, paymentData)
            }
        }
    }

    fun updateRecordPayment(jsonData: RecordPaymentData, id: Int) {
        RecordPaymentRepository().updateRecordPayment(updatePaymentRecordLiveData, jsonData, id)
    }

    fun getRecordPaymentDetails(id: Int, hasInternet: Boolean) {
        viewModelScope.launch {
            if (hasInternet) {
                RecordPaymentRepository().getRecordPaymentDetails(getPaymentRecordDetailLiveData, id)
            } else {
                DatabaseLogManager.getInstance().getOfflinePaymentDetails(getPaymentRecordDetailLiveData, id)
            }
        }
    }

    fun deletePayment(jsonObject: JsonObject, id: Int, hasInternet: Boolean) {
        viewModelScope.launch {
            if (hasInternet) {
                RecordPaymentRepository().deletePayment(deletePaymentLiveData, jsonObject, id)
            } else {
                DatabaseLogManager.getInstance().deleteOfflinePayment(deletePaymentLiveData, id)
            }
        }
    }

    fun dumpPaymentList(pageNo: Int, pageSize: Int, offlineDataLastSyncedTime: String?) {
        viewModelScope.launch {
            RecordPaymentRepository().getRecordPaymentList(
                    recordPaymentListLiveData,
                    "",
                    pageNo,
                    pageSize, offlineDataLastSyncedTime
            )
        }
    }

}