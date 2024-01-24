package com.ongraph.mvvmcode.data.repository

import com.ongraph.mvvmcode.data.model.MovieListItem
import com.ongraph.mvvmcode.data.remote.RemoteDataSource
import com.ongraph.mvvmcode.utils.BaseApiResponse
import com.ongraph.mvvmcode.utils.NetworkResult
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


@ActivityRetainedScoped
class MainRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : BaseApiResponse() {

    suspend fun getMovieList(): Flow<NetworkResult<List<MovieListItem>>> {
        return flow<NetworkResult<List<MovieListItem>>> {
            emit(safeApiCall { remoteDataSource.getMovieList() })
        }.flowOn(Dispatchers.IO)
    }

}
