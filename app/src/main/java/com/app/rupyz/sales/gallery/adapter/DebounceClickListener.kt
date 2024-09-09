package com.app.rupyz.sales.gallery.adapter

import com.app.rupyz.model_kt.gallery.FilterData

interface DebounceClickListener {
    fun onDebounceClick(position: Int, data: FilterData)
}