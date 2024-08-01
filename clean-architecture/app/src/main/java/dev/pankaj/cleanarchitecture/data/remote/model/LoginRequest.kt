package dev.pankaj.cleanarchitecture.data.remote.model

/**
 * Represents a login request containing the user's credentials.
 */
data class LoginRequest(
    /**
     * Theusername of the user attempting to log in.
     */
    val username: String,
    /**
     * The password of the user attempting to log in.
     */
    val password: String,
)