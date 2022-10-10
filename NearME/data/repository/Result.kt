package com.ongraph.nearme.data.repository

interface Result {
    fun onFailure(t: Throwable)
    fun onSuccess(data: Any?)
    fun onSuccessDistance(data: Any?)
    fun onSuccessWeather(data: Any?)
}