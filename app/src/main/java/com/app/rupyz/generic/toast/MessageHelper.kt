package com.app.rupyz.generic.toast

import android.view.View
import android.widget.TextView
import com.app.rupyz.R
import com.google.android.material.snackbar.Snackbar

class MessageHelper {
    fun initMessage(message: String?, mView: View?) {
        try {
            if (mView != null) {
                val snackBar = Snackbar.make(mView, message!!, Snackbar.LENGTH_LONG)
                val snackBarView = snackBar.view
                snackBar.setTextColor(mView.resources.getColor(R.color.black))
                snackBarView.setBackgroundColor(mView.resources.getColor(R.color.toast_warning_color))
                snackBar.show()
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    fun initLongMessageWithAction(message: String?, mView: View) {
        try {
            val snackBar = Snackbar.make(mView, message!!, Snackbar.LENGTH_INDEFINITE)
            val snackBarView = snackBar.view
            snackBar.setTextColor(mView.resources.getColor(R.color.black))
            snackBarView.setBackgroundColor(mView.resources.getColor(R.color.toast_warning_color))
            val textView = snackBarView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView?
            textView?.maxLines = 4

            snackBar.setAction("Ok"){}
            snackBar.show()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }
}
