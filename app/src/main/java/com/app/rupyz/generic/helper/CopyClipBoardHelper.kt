package com.app.rupyz.generic.helper

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context


object CopyClipBoardHelper {

    fun copyText(text: String, context: Context) {
        val clipboard: ClipboardManager? =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData.newPlainText(text, text)
        clipboard?.setPrimaryClip(clip)
    }
}