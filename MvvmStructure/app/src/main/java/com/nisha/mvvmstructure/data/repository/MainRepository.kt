package com.nisha.mvvmstructure.data.repository

import com.nisha.mvvmstructure.data.model.MovieResponse
import com.nisha.mvvmstructure.data.remote.RemoteDataSource
import com.nisha.mvvmstructure.utils.BaseApiResponse
import com.nisha.mvvmstructure.utils.NetworkResult
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

    /**
     * Demo repository function
     * */
    suspend fun getMovieList(): Flow<NetworkResult<MovieResponse>> {
        return flow {
            emit(safeApiCall { remoteDataSource.getMovieList() })
        }.flowOn(Dispatchers.IO)
    }

}
