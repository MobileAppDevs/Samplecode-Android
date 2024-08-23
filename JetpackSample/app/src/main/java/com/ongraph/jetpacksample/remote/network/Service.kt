package com.ongraph.jetpacksample.remote.network

import com.ongraph.jetpacksample.remote.Response
import com.ongraph.jetpacksample.remote.models.LoginReq
import com.ongraph.jetpacksample.remote.models.RegisterReq
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/*
* api endpoint service class
* */
interface Service {

    @POST("/api/auth/register")
    fun register(
        @Body registerRequest: RegisterReq
    ) : Call<Response>

    @POST("/api/auth/login")
    fun login(
        @Body loginRequest: LoginReq
    ) : Call<Response>
}