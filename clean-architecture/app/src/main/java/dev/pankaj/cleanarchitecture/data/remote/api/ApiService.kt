package dev.pankaj.cleanarchitecture.data.remote.api

import dev.pankaj.cleanarchitecture.data.remote.model.LoginRequest
import dev.pankaj.cleanarchitecture.data.remote.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Defines the API endpoints for network requests.
 */
interface ApiService {

    /**
     * Performs a login request.*
     * @param loginRequest The LoginRequest object containing the user's credentials.
     * @return A Response object containing the LoginResponse from the server.
     */
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
}