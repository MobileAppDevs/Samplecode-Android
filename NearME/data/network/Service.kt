package com.ongraph.nearme.data.network

import com.ongraph.nearme.common.Constants.distKey
import com.ongraph.nearme.common.Constants.placesApiKey
import com.ongraph.nearme.common.Constants.weatherURL
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface Service {
    @GET
    fun getWeather(@Url url: String = weatherURL): Call<Any>

    @GET("place/textsearch/json?" +
            "&key=${placesApiKey}")
    fun getPlaces(@Query("query") query: String): Call<Any>

    @GET(/* value = */ "place/textsearch/json?" +
            "&key=${placesApiKey}")
    fun getPlacesWithNextPageToken(@Query("query") query: String,
                                                  @Query("pagetoken") token: String): Call<Any>

    @GET("place/details/json?key=${placesApiKey}")
    fun getPlaceDetailsByID(@Query("place_id") place_id: String): Call<Any>

    @GET("place/nearbysearch/json?" +
            "&radius=20000" +
            "&key=${placesApiKey}")
    fun getNearByPlaces(@Query("location") location: String,
                                @Query("type"    ) type : String): Call<Any>

    @GET("distancematrix/json?&key=${distKey}")
    fun getDistance(@Query("destinations") destinations: String,
                            @Query("origins") origins: String): Call<Any>
}