package com.ongraph.nearme.data.network

import com.ongraph.nearme.common.Constants.baseUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NearByMeRetrofit {
    private var instance: Service? = null
    private val interceptor = HttpLoggingInterceptor()
    private val client = OkHttpClient.Builder()
        .readTimeout(120, TimeUnit.SECONDS)
        .connectTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)

    fun getInstance(): Service{
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        client.addInterceptor(interceptor).build()
        if (instance == null){
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            instance = retrofit.create(Service::class.java)
        }
        return instance!!
    }

}