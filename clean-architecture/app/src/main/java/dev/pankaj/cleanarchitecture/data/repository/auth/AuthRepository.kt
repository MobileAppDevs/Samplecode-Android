package dev.pankaj.cleanarchitecture.data.repository.auth

import dev.pankaj.cleanarchitecture.data.dataSource.auth.IAuthDataSource
import dev.pankaj.cleanarchitecture.data.local.prefmanager.SharedPreferencesUtil
import dev.pankaj.cleanarchitecture.data.remote.model.LoginRequest
import dev.pankaj.cleanarchitecture.data.remote.model.LoginResponse
import dev.pankaj.cleanarchitecture.domain.repository.IAuthRepository
import dev.pankaj.cleanarchitecture.utils.*

/**
 * Repository responsible for handling authentication-related operations,
 * including login and managing authentication tokens.
 */
class AuthRepository(private val authDataSource: IAuthDataSource,
                     private val sharedPreferencesUtil: SharedPreferencesUtil
) : IAuthRepository {

    /**
     * Performs a login request and handles the response, saving the token locally if successful.
     *
     * @param loginRequest The LoginRequest objectcontaining the user's credentials.
     * @return A Result object indicating success, failure, or an error.
     */
    override suspend fun login(loginRequest: LoginRequest): Result<LoginResponse> {
        return try {
            val response = authDataSource.login(loginRequest)
            if (response.isSuccessful) {
                // Login successful, save token and return success result
                response.body()?.let {
                    sharedPreferencesUtil.saveString("token", it.token)
                    Result.Success(it)
                } ?: Result.Message("Response body is null") // Handle null response body
            } else {
                // Login failed, return message with error code
                Result.Message("Login failed with code: ${response.code()}")
            }
        } catch (e: Exception) {
            // Handle exceptions during login process
            Result.Error(e)
        }
    }
}