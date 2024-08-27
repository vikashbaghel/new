package com.app.rupyz.retrofit

import android.util.Log
import com.app.rupyz.BuildConfig
import com.app.rupyz.generic.utils.SharePrefConstant.TOKEN
import com.app.rupyz.generic.utils.SharedPref
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    var apiInterface: ApiInterface

    init {
        apiInterface = getRetrofitService()
    }

    private fun getRetrofitService(): ApiInterface {
        val httpClient = OkHttpClient.Builder()
            .callTimeout(2, TimeUnit.MINUTES)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS);

        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS)
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            httpClient.addInterceptor(interceptor)
        }

        httpClient.addInterceptor(Interceptor { chain: Interceptor.Chain ->
            val original = chain.request()
            val requestBuilder: Request.Builder = original.newBuilder()
            val maxStale = 60 * 60 * 24 * 3 // Offline cache available for 3 days
            requestBuilder.header("Origin", BuildConfig.BASE_URL)

            requestBuilder.header(
                "Authorization",
                "Bearer " + SharedPref.getInstance().getString(TOKEN)
            )
            Log.e("token",""+SharedPref.getInstance().getString(TOKEN))
            requestBuilder.header("Client", "Android Master")
            requestBuilder.header("Client_Version_Code", "${BuildConfig.VERSION_CODE}")
            requestBuilder.header("Client_Version_Name", BuildConfig.VERSION_NAME)
            requestBuilder.method(original.method, original.body)
                .header("cache-control", "public, max-stale=$maxStale")
            chain.proceed(requestBuilder.build())
        })

        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit: Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(BuildConfig.BASE_URL)
            .client(httpClient.build())
            .build()
        return retrofit.create(ApiInterface::class.java)
    }

}