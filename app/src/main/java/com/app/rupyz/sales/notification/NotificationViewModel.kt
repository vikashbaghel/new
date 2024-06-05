package com.app.rupyz.sales.notification

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.model_kt.NotificationResponseModel
import com.google.gson.JsonObject

class NotificationViewModel : ViewModel() {

    var notificationLiveData = MutableLiveData<NotificationResponseModel>()
    var readNotificationLiveData = MutableLiveData<GenericResponseModel>()

    fun getNotificationList(page: Int) {
        NotificationRepository().getNotificationList(notificationLiveData, page)
    }

    fun readNotification(jsonObject: JsonObject) {
        NotificationRepository().readNotification(readNotificationLiveData, jsonObject)
    }
}