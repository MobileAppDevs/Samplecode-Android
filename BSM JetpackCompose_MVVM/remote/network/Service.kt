package com.ongraph.jetpackloginsample.remote.network

import com.ongraph.jetpackloginsample.remote.Response
import com.ongraph.jetpackloginsample.remote.models.LoginReq
import com.ongraph.jetpackloginsample.remote.models.RegisterReq
import com.ongraph.jetpackloginsample.remote.models.VerifyOtpReq
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface Service {

    @POST("/api/auth/register")
    fun register(
        @Body registerRequest: RegisterReq
    ) : Call<Response>

    @POST("/api/auth/login")
    fun login(
        @Body loginRequest: LoginReq
    ) : Call<Response>

    @POST("/api/auth/verify_otp")
    fun verifyOTP(
        @Body request: VerifyOtpReq
    ) : Call<Response>
}