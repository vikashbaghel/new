package com.app.rupyz.sales.gallery.adapter

import android.view.View

open class SafeClickListener(
    private var defaultInterval: Int = 1000, private val onSafeClick: (View) -> Unit) : View.OnClickListener {
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View) {
        if (System.currentTimeMillis() - lastTimeClicked < defaultInterval) {
            return
        } else {
            lastTimeClicked = System.currentTimeMillis()
            onSafeClick (v)
        }
    }
}