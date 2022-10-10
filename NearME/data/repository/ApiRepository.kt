package com.ongraph.nearme.data.repository

import com.ongraph.nearme.data.network.Service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiRepository(private val service: Service, private val result: Result) {

    fun getPlaces(query: String) {
        service.getPlaces(query).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                response.body().let { result.onSuccess(response.body()) }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                result.onFailure(t)
            }
        })
    }

    fun getPlacesWithNextPageToken(query: String, token: String) {
        service.getPlacesWithNextPageToken(query, token).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                response.body().let { result.onSuccess(response.body()) }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                result.onFailure(t)
            }
        })
    }

    fun getNearByPlaces(location: String, type: String) {
        service.getNearByPlaces(location, type).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                response.body().let { result.onSuccess(response.body()) }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                result.onFailure(t)
            }
        })
    }

    fun getPlaceDetailsByID(place_id: String) {
        service.getPlaceDetailsByID(place_id).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                response.body().let { result.onSuccess(response.body()) }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                result.onFailure(t)
            }
        })
    }

    fun getDistance(destinations: String, origins: String) {
        service.getDistance(destinations, origins).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                response.body().let { result.onSuccessDistance(response.body()) }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                result.onFailure(t)
            }
        })
    }

    fun getWeather() {
        service.getWeather().enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                response.body().let { result.onSuccessWeather(response.body()) }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                result.onFailure(t)
            }
        })
    }
}