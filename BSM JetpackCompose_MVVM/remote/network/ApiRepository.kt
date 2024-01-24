package com.ongraph.jetpackloginsample.remote.network

import android.util.Log
import com.google.gson.Gson
import com.ongraph.jetpackloginsample.remote.Response
import com.ongraph.jetpackloginsample.remote.models.ApiErrorResponse
import com.ongraph.jetpackloginsample.remote.models.LoginReq
import com.ongraph.jetpackloginsample.remote.models.RegisterReq
import com.ongraph.jetpackloginsample.remote.models.VerifyOtpReq
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

object ApiRepository {
    private val webService = Helper.createAppService()

    fun registerUser (
        successHandler: (Response) -> Unit,
        failureHandler: (ApiErrorResponse) -> Unit,
        onFailure: (Throwable) -> Unit,
        request: RegisterReq
    ) {
        webService.register(request)
            .enqueue(object : Callback<Response> {
                override fun onResponse(
                    call: Call<Response>,
                    response: retrofit2.Response<Response>
                ) {

                    response.body()?.let {
                        successHandler(it)
                    }

                    response.errorBody()?.let {
                        val jsonObject = JSONObject(it.charStream().readText())
                        val gson = Gson().fromJson(jsonObject.toString(), ApiErrorResponse::class.java)
                        failureHandler(gson)
                    }
                }

                override fun onFailure(call: Call<Response>, t: Throwable) {
                    onFailure(t)
                }
            })
    }

    fun loginUser (
        successHandler: (Response) -> Unit,
        failureHandler: (ApiErrorResponse) -> Unit,
        onFailure: (Throwable) -> Unit,
        request: LoginReq
    ) {
        webService.login(request)
            .enqueue(object : Callback<Response> {
                override fun onResponse(
                    call: Call<Response>,
                    response: retrofit2.Response<Response>
                ) {

                    response.body()?.let {
                        successHandler(it)
                    }

                    response.errorBody()?.let {
                        val jsonObject = JSONObject(it.charStream().readText())
                        Log.e("ERROR ", jsonObject.toString())
                        val gson = Gson().fromJson(jsonObject.toString(), ApiErrorResponse::class.java)
                        failureHandler(gson)
                    }
                }

                override fun onFailure(call: Call<Response>, t: Throwable) {
                    onFailure(t)
                }
            })
    }

    fun verifyOtp (
        successHandler: (Response) -> Unit,
        failureHandler: (ApiErrorResponse) -> Unit,
        onFailure: (Throwable) -> Unit,
        request: VerifyOtpReq
    ) {
        webService.verifyOTP(request)
            .enqueue(object : Callback<Response> {
                override fun onResponse(
                    call: Call<Response>,
                    response: retrofit2.Response<Response>
                ) {

                    response.body()?.let {
                        successHandler(it)
                    }

                    response.errorBody()?.let {
                        val jsonObject = JSONObject(it.charStream().readText())
                        val gson = Gson().fromJson(jsonObject.toString(), ApiErrorResponse::class.java)
                        failureHandler(gson)
                    }
                }

                override fun onFailure(call: Call<Response>, t: Throwable) {
                    onFailure(t)
                }
            })
    }
}