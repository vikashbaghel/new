package com.app.rupyz.ui.discovery

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.rupyz.model_kt.*

class DiscoveryViewModel : ViewModel() {

    var discoveryListLiveData = MutableLiveData<DiscoveryResponseModel>()
    var recentSearchLiveData = MutableLiveData<RecentSearchResponseModel>()

    fun getDiscoveryList(type: String, name: String, state: String, badge: Int) {
        DiscoveryRepository().getDiscoveryList(
            discoveryListLiveData, type, name, state, badge, false
        )
    }

    fun getDiscoveryListWithHardSearch(type: String, name: String, state: String, badge: Int) {
        DiscoveryRepository().getDiscoveryList(
            discoveryListLiveData, type, name, state, badge, true
        )
    }

    fun getDiscoveryListWithPagination(type: String, name: String, state: String, badge: Int, page: Int) {
        DiscoveryRepository().getDiscoveryListWithPagination(
            discoveryListLiveData, type, name, state, badge, page
        )
    }

    fun getDiscoverySearchHistory(page: Int, type: String) {
        DiscoveryRepository().getDiscoverySearchHistory(
            recentSearchLiveData, page, type
        )
    }

}