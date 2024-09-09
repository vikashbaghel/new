package com.app.rupyz.generic.helper

import android.util.Patterns
import android.webkit.URLUtil
import java.net.MalformedURLException
import java.net.URL

object UrlValidationHelper {

    fun isValidUrl(urlString: String?): Boolean {
        try {
            URL(urlString)
            return URLUtil.isValidUrl(urlString) && Patterns.WEB_URL.matcher(urlString).matches()
        } catch (ignored: MalformedURLException) {
            ignored.printStackTrace()
        }
        return false
    }
}