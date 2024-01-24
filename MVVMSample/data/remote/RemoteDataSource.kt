package com.ongraph.mvvmcode.data.remote

import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val webService: WebService) {
    suspend fun getMovieList() = webService.getMovieList()
}