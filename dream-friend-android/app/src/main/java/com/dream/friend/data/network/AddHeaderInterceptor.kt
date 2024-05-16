package com.dream.friend.data.network

import android.util.Log
import com.dream.friend.common.PreferenceHandler
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.*
import javax.inject.Inject

class AddHeaderInterceptor @Inject constructor(private val preferenceHandler: PreferenceHandler)  : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = preferenceHandler.readString(
            preferenceHandler.authorizationToken,
            ""
        )
        val builder = chain.request().newBuilder()
        token.let { Log.v("---TOKEN---", it) }
        builder.addHeader("Authorization", "Bearer $token")
        builder.addHeader("Accept", "application/json")
        builder.addHeader("Content-Type", "application/x-www-form-urlencoded")
        return chain.proceed(builder.build())
    }
}