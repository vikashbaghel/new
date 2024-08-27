package com.app.rupyz.generic.base

import com.google.gson.JsonObject
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

abstract class NetworkCallback<T> : Callback<T> {
    companion object {
        const val NO_INTERNET = 9
    }

    abstract fun onSuccess(t: T)

    abstract fun onFailure(failureResponse: FailureResponse?)

    abstract fun onError(t: Throwable?)

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            response.body()?.let { onSuccess(it) }
        } else {
            var failureErrorBody: FailureResponse? = null
            try {
                failureErrorBody = getFailureErrorBody(response)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            onFailure(failureErrorBody)
        }
    }


    override fun onFailure(call: Call<T>, t: Throwable) {
        if (t is SocketTimeoutException || t is UnknownHostException) {
            val failureResponseForNoNetwork = getFailureResponseForNoNetwork()
            onFailure(failureResponseForNoNetwork)
        } else {
            onError(t)
        }
    }

    private fun getFailureResponseForNoNetwork(): FailureResponse {
        val failureResponse = FailureResponse()
        val jsonObject = JsonObject()
        jsonObject.addProperty("message", "No Network")
        jsonObject.addProperty("Error Code", NO_INTERNET)
        failureResponse.errorBody = jsonObject.toString()
        failureResponse.errorCode = NO_INTERNET
        failureResponse.setErrorMessage("No Network")
        return failureResponse
    }

    /**
     * Create your custom failure response out of server response
     * Also save Url for any further use
     */
    @Throws(JSONException::class)
    private fun getFailureErrorBody(errorBody: Response<T>): FailureResponse {
        val failureResponse = FailureResponse()
        failureResponse.errorCode = errorBody.code()
        failureResponse.setErrorMessage(errorBody.message())
        failureResponse.errorBody = errorBody.errorBody()?.string()
        return failureResponse
    }


}