package dev.pankaj.cleanarchitecture.data.remote.model

/**
 * Represents a response to a successful login request, containing an authentication token.
 */
data class LoginResponse(
    /*** The authentication token provided by the server upon successful login.
     */
    val token: String,
)