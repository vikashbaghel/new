package com.app.rupyz.sales.reminder

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.rupyz.model_kt.*
import kotlinx.coroutines.launch

class RemindersViewModel : ViewModel() {
    var reminderListLiveData = MutableLiveData<ReminderListResponseModel>()
    var deleteReminderLiveData = MutableLiveData<GenericResponseModel>()
    fun getReminderList(category: String, particularDate: String?, page: Int) {
        viewModelScope.launch {
            ReminderRepository().getReminderList(reminderListLiveData, category, particularDate, page)
        }
    }

    fun deleteReminder(id: Int?) {
        viewModelScope.launch {
            ReminderRepository().deleteReminder(deleteReminderLiveData, id)
        }
    }
}