package com.nisha.mvvmstructure.utils

object NetworkConstants {
    //TODO : Replace with your base url
    const val BASE_URL = "https://api.themoviedb.org/3/"
    const val READ_TIMEOUT = 30
    const val WRITE_TIMEOUT = 30
    const val CONNECTION_TIMEOUT = 10
    const val CACHE_SIZE_BYTES = 10 * 1024 * 1024L

    //ROUTES
    const val FILMS = "trending/movie/day"
}