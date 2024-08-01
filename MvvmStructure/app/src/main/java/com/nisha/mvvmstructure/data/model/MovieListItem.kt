package com.nisha.mvvmstructure.data.model

import com.google.gson.annotations.SerializedName

/**
 * demo data classes
 * */
data class MovieListItem(
    val id: String,
    val title: String,
    val overview: String,
    val director: String,
    @SerializedName("poster_path")
    val posterPath: String,
    @SerializedName("backdrop_path")
    val backdropPath: String,
    val adult: Boolean,
)

data class MovieResponse(
    val results: List<MovieListItem>
)