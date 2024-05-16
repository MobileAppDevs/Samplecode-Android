package com.enkefalostechnologies.calendarpro.api.network

import com.enkefalostechnologies.calendarpro.api.model.SuccessResponse
import com.enkefalostechnologies.calendarpro.constant.Constants.GOOGLE_CALENDAR_API_URL
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface WebService {

    @GET
    fun getEvents(@Url url:String): Call<SuccessResponse>
}