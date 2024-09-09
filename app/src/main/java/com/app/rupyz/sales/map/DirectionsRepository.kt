package com.app.rupyz.sales.map

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class DirectionsRepository {
    private val client = OkHttpClient()

    fun fetchDirections(url: String, callback: (String?) -> Unit) {
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
                callback(null)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                callback(response.body?.string())
            }
        })
    }
}