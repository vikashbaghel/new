package com.app.rupyz.ui.connections

import com.app.rupyz.model_kt.ConnectionListItem
import com.app.rupyz.model_kt.NetworkDataItem

interface ConnectionActionListener {
    fun onAccept(target_id: Int, position: Int)
    fun onShareConnection(model: ConnectionListItem, position: Int)
    fun onRemoveConnection(model: ConnectionListItem, position: Int)
    fun onDecline(target_id: Int, position: Int)
    fun onShowInfo(model: ConnectionListItem)
}