package com.ongraph.onetoonecall.data.networks

import com.google.gson.GsonBuilder
import com.ongraph.onetoonecall.common.Constants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Provides a singleton instance of Retrofit for making API calls.
 */
object ApiClient {
    /**
     * Returns an instanceof the RtcTokenService for fetching RTC tokens.
     */
    fun getRtcService(): RtcTokenService {
        return getRetrofitBuilder().create(RtcTokenService::class.java)
    }

    /**
     * Creates and configures an OkHttpClient instance for network requests.
     */
    private fun getOkHttpClient() : OkHttpClient{
        return getOkHttpBuilder()
            .writeTimeout(10, TimeUnit.MINUTES)
            .readTimeout(10, TimeUnit.MINUTES)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Builds a Retrofit instance with the base URL, OkHttpClient, and GsonConverterFactory.
     */
    private fun getRetrofitBuilder(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getOkHttpClient()).addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
    }

    /**
     * Creates and configures an OkHttpClient.Builder with logging and authentication interceptors.
     */
    private fun getOkHttpBuilder(): OkHttpClient.Builder {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val builder = OkHttpClient.Builder()
        builder.addInterceptor(AuthInterceptor())
        builder.addInterceptor(logging)
        val client = builder.build()
        return client.newBuilder()
    }
}
