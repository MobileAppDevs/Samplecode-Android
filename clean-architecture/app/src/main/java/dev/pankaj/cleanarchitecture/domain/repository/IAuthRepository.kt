package dev.pankaj.cleanarchitecture.domain.repository

import dev.pankaj.cleanarchitecture.data.remote.model.LoginRequest
import dev.pankaj.cleanarchitecture.data.remote.model.LoginResponse
import dev.pankaj.cleanarchitecture.utils.*

/**
 * Defines the contract for a repository responsible for handling authentication-related operations.
 */
interface IAuthRepository {

    /*** Performs a login request.
     *
     * @param loginRequest The LoginRequest object containing the user's credentials.
     * @return A Result object indicating the outcome of the login operation (Success, Error, Message, or Loading).
     */
    suspend fun login(loginRequest: LoginRequest): Result<LoginResponse>
}