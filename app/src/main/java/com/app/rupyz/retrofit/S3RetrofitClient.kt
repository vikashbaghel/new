package com.app.rupyz.retrofit

import com.app.rupyz.BuildConfig
import com.app.rupyz.generic.utils.AppConstant
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object S3RetrofitClient {
    var s3ApiInterface: ApiInterface

    init {
        s3ApiInterface = getRetrofitService()
    }

    private fun getRetrofitService(): ApiInterface {
        val httpClient = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS)
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            httpClient.addInterceptor(interceptor)
        }

        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit: Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(AppConstant.BASE_URL)
            .client(httpClient.build())
            .build()
        return retrofit.create(ApiInterface::class.java)
    }

}