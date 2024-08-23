package com.ongraph.jetpacksample.remote.network

import android.util.Log
import com.google.gson.Gson
import com.ongraph.jetpacksample.remote.Response
import com.ongraph.jetpacksample.remote.models.ApiErrorResponse
import com.ongraph.jetpacksample.remote.models.LoginReq
import com.ongraph.jetpacksample.remote.models.RegisterReq
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

/*
* api repository class
* */
object ApiRepository {
    private val webService = Helper.createAppService()

    /*
    * register api response callbacks
    * */
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

    /*
    * login api response callbacks
    * */
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
}