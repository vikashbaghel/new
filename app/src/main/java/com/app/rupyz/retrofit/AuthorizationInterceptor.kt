package com.app.rupyz.retrofit

import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor : Interceptor {
	
	override fun intercept(chain : Interceptor.Chain) : Response {
		val mainResponse = chain.proceed(chain.request())
		@Suppress("DEPRECATED_IDENTITY_EQUALS")
		if ( mainResponse.code === 403) {
		
		}
		return mainResponse
	}
}