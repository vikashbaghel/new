package com.app.rupyz.sales.pdfupload

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.rupyz.model_kt.GenericResponseModel

class PdfUploadViewModel: ViewModel() {

    var uploadCredLiveData = MutableLiveData<GenericResponseModel>()

    fun uploadCredentials(path: String){
        PdfUploadRepository(uploadCredLiveData).uploadCred(path)
    }

}