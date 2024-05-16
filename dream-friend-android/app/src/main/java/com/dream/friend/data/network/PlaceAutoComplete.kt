package com.dream.friend.data.network

import com.dream.friend.data.model.cititesModel.Predictions
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

class PlaceAutoComplete {
    companion object {
        const val baseURL = "https://maps.googleapis.com/maps/api/place/autocomplete/"
    }
    private var instance: Service? = null
    private val interceptor = HttpLoggingInterceptor()
    private val client = OkHttpClient.Builder()
        .readTimeout(2, TimeUnit.MINUTES)
        .connectTimeout(2, TimeUnit.SECONDS)
        .writeTimeout(2, TimeUnit.SECONDS)

    fun getInstance(): Service{
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        client.addInterceptor(interceptor).build()
        if (instance == null){
            val retrofit = Retrofit.Builder()
                .baseUrl(baseURL)
                .client(client.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            instance = retrofit.create(Service::class.java)
        }
        return instance!!
    }

    interface Service {
        @GET suspend fun getCities(@Url url: String): Response<Predictions>
    }
}