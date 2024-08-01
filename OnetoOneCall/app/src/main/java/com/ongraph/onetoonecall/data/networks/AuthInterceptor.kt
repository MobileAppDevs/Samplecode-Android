package com.ongraph.onetoonecall.data.networks

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor to add authentication headers to network requests.
 */
class AuthInterceptor : Interceptor {
    /**
     * Intercepts network requests and adds necessary authentication headers.
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        // Add authentication headers here, e.g.,
        // requestBuilder.addHeader("Authorization", "Bearer your_token")
        return chain.proceed(requestBuilder.build())
    }
}