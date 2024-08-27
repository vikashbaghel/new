package com.app.rupyz.ui.imageupload

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.rupyz.model_kt.GenericResponseModel
import kotlinx.coroutines.launch

class ImageUploadViewModel : ViewModel() {

    private var uploadCredLiveData = MutableLiveData<GenericResponseModel>()

    fun uploadCredentials(path: String?) {
        viewModelScope.launch {
            ImageUploadRepository(uploadCredLiveData).uploadCred(path!!, 0)
        }
    }

    fun uploadCredentialsWithPrevS3Id(path: String?, prevS3Id: Int?) {
        viewModelScope.launch {
            ImageUploadRepository(uploadCredLiveData).uploadCred(path!!, prevS3Id ?: 0)
        }
    }

    fun getCredLiveData(): MutableLiveData<GenericResponseModel> {
        return uploadCredLiveData
    }
}