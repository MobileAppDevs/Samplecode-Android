package dev.pankaj.cleanarchitecture.data.dataSource.auth

import dev.pankaj.cleanarchitecture.data.remote.model.LoginRequest
import dev.pankaj.cleanarchitecture.data.remote.model.LoginResponse
import retrofit2.Response

interface IAuthDataSource {
    suspend fun login(loginRequest: LoginRequest): Response<LoginResponse>
}