package com.nisha.mvvmstructure.data.remote

import com.nisha.mvvmstructure.data.model.MovieResponse
import com.nisha.mvvmstructure.utils.NetworkConstants.FILMS
import retrofit2.Response
import retrofit2.http.GET

interface WebService {

    /**
     * replace it with your api endpoints
     * */
    @GET(FILMS)
    suspend fun getMovieList(): Response<MovieResponse>
}