package com.ongraph.nearme.common

object Constants {

    //keys
    const val distKey = "key-here"
    const val placesApiKey = "key-here"
    private const val weatherKey = "key-here"

    const val baseUrl = "https://maps.googleapis.com/maps/api/"
    const val photoURL = "${baseUrl}photo?maxwidth=400&maxheight=400&key=$placesApiKey"
    const val next_page_tokenURL = baseUrl+"nearbysearch/json?key=$placesApiKey"
    const val weatherURL = "https://api.weatherbit.io/v2.0/current?key=$weatherKey"
}