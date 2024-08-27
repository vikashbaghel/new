package com.app.rupyz.sales.gallery

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.rupyz.model_kt.gallery.GalleryResponseData
import kotlinx.coroutines.launch

class PictureGalleryViewModel : ViewModel() {
    var galleryLiveData = MutableLiveData<GalleryResponseData>()

    fun getPictureList(
        userId: ArrayList<Int>,
        customerId: ArrayList<Int>,
        moduleType: ArrayList<String?>,
        moduleID: Int,
        subModuleType: ArrayList<String?>,
        byDateRange: String,
        startDate: String,
        endDate: String,
        state: ArrayList<String>,
        sortByOrder: String,
        pageNo:Int,
        hasInternetConnection: Boolean
    ) {
        viewModelScope.launch {
            if (hasInternetConnection) {
                PictureGalleryRepository().getPictureData(
                    galleryLiveData, userId, customerId, moduleType, moduleID, subModuleType,
                    byDateRange, startDate, endDate, state, sortByOrder,pageNo
                )

            }
        }
    }

}