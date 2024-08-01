package com.nisha.mvvmstructure.data.remote

import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val webService: WebService) {
    /**
     * demo remote data source function call
     * */
    suspend fun getMovieList() = webService.getMovieList()
}