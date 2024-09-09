package com.app.rupyz.ui.discovery

interface FilterSelectedListener {
    fun onTypeChange(fileName: String?)
    fun onLocationChange(fileName: String?)
    fun onBadgeChange(fileName: String?)
}