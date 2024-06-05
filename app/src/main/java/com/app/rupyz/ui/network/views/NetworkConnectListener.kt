package com.app.rupyz.ui.network.views

import com.app.rupyz.model_kt.NetworkDataItem

interface NetworkConnectListener {
    fun onConnect(model: NetworkDataItem, position: Int)
    fun openProfile(slug: String)
}