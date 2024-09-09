package com.app.rupyz.ui.organization.profile.activity.addphotos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.rupyz.model_kt.AddPhotoModel
import com.app.rupyz.model_kt.AddPhotoResponseModel
import com.app.rupyz.model_kt.AddProductModel
import com.app.rupyz.model_kt.AddProductResponseModel

class AddPhotoViewModel: ViewModel() {
    private var addPhotoLiveData = MutableLiveData<AddPhotoResponseModel>()

    fun addPhoto(photo: AddPhotoModel){
        AddPhotoRepository().addPhoto(addPhotoLiveData, photo)
    }


    fun getPhotoLiveData(): MutableLiveData<AddPhotoResponseModel> {
        return addPhotoLiveData
    }
}