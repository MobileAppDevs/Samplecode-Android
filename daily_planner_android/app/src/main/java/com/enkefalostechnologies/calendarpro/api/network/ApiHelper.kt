package com.enkefalostechnologies.calendarpro.api.network

import android.content.Context
import com.google.gson.GsonBuilder
import com.enkefalostechnologies.calendarpro.constant.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class ApiHelper(private val context: Context) {

    private var mRetrofit: Retrofit
//    private var mRetrofitWithToken: Retrofit

    init {
        val gson = GsonBuilder().setLenient().create()

        mRetrofit = Retrofit.Builder()
            .baseUrl(Constants.GOOGLE_CALENDAR_API_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(getClient())
            .build()

//        mRetrofitWithToken = Retrofit.Builder()
//            .baseUrl(Constants.BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create(gson))
//            .client(getAppClient())
//            .build()
    }

    // Creating OkHttpclient Object
    private fun getClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient().newBuilder().connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .addInterceptor(interceptor)
            .build()
    }

    // Creating OkHttpclient Object
//    private fun getAppClient(): OkHttpClient {
//        val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
//        return OkHttpClient().newBuilder().connectTimeout(5, TimeUnit.MINUTES)
//            .readTimeout(5, TimeUnit.MINUTES)
//            .writeTimeout(5, TimeUnit.MINUTES)
//            .addInterceptor(interceptor)
//            .addNetworkInterceptor(AddHeaderInterceptor(PreferenceHandler(context)))
//            .build()
//    }

    //val apiService: WebService = mRetrofit.create(WebService::class.java)
    //Creating service class for calling the web services
    fun createService(): WebService {
        return mRetrofit.create(WebService::class.java)
    }

//    fun createAppService(): WebService {
//        return mRetrofitWithToken.create(WebService::class.java)
//    }
}