package com.ongraph.jetpackloginsample.remote.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Helper {
    private const val BASE_URL = "http://api.babysizeme.com"
    private var mRetrofitApp: Retrofit

    init {
        val gson = GsonBuilder().setLenient().create()

        mRetrofitApp = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(getAppClient())
            .build()
    }

    // Creating OkHttpclient Object
    private fun getAppClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient().newBuilder().connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .addInterceptor(interceptor)
            .build()
    }

    fun createAppService(): Service {
        return mRetrofitApp.create(Service::class.java)
    }
}