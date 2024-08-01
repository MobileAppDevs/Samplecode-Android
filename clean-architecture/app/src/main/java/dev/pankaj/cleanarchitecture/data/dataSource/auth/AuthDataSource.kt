package dev.pankaj.cleanarchitecture.data.dataSource.auth

import dev.pankaj.cleanarchitecture.data.remote.api.ApiService
import dev.pankaj.cleanarchitecture.data.remote.model.LoginRequest
import dev.pankaj.cleanarchitecture.data.remote.model.LoginResponse
import retrofit2.Response
import javax.inject.Inject

class AuthDataSource @Inject constructor(private val api: ApiService) : IAuthDataSource {

    /**
     * Performs a login request bycalling the corresponding API endpoint.
     *
     * @param loginRequest The login request data.
     * @return A Response object containing the login response from the API.
     */
    override suspend fun login(loginRequest: LoginRequest): Response<LoginResponse> {
        return api.login(loginRequest)
    }
}