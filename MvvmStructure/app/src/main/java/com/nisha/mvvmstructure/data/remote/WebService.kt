package com.nisha.mvvmstructure.data.remote

import com.nisha.mvvmstructure.utils.NetworkConstants.END_POINT1
import retrofit2.Response
import retrofit2.http.GET

interface WebService {

    /**
     * replace it with your api endpoints
     * */
    @GET(END_POINT1)
    suspend fun getList(): Response<com.nisha.mvvmstructure.data.model.Response>
}