package dev.pankaj.cleanarchitecture.domain.usecase

import dev.pankaj.cleanarchitecture.data.remote.model.LoginRequest
import dev.pankaj.cleanarchitecture.data.remote.model.LoginResponse
import dev.pankaj.cleanarchitecture.domain.repository.IAuthRepository
import dev.pankaj.cleanarchitecture.utils.*

/**
 * Use case responsible for handling the login functionality, interacting with the authentication repository.
 */
class AuthUseCase(private val iUserRepository: IAuthRepository) {

    /**
     * Executes the login use case, delegating the login operation to the authentication repository.
     *
     * @param loginRequest The LoginRequest object containing the user's credentials.
     * @return A Result object indicating the outcome of the login operation.
     */
    suspend fun login(loginRequest: LoginRequest): Result<LoginResponse> {
        return iUserRepository.login(loginRequest)
    }
}
