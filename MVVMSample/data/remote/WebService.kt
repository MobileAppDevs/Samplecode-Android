package com.ongraph.mvvmcode.data.remote

import com.google.gson.JsonObject
import com.ongraph.mvvmcode.data.model.MovieListItem
import com.ongraph.mvvmcode.utils.NetworkConstants.FILMS
import retrofit2.Response
import retrofit2.http.GET

interface WebService {

    @GET(FILMS)
    suspend fun getMovieList(): Response<List<MovieListItem>>
}