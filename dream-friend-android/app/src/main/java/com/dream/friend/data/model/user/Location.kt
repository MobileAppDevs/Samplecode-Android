package com.dream.friend.data.model.user

data class Location(
    var lat: Double?=null,
    var long: Double?=null,
    val type: String? = null,
    val coordinates: ArrayList<Double> = arrayListOf()
)