package dev.pankaj.cleanarchitecture.presentation.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.pankaj.cleanarchitecture.App
import dev.pankaj.cleanarchitecture.domain.usecase.AuthUseCase
import dev.pankaj.cleanarchitecture.domain.usecase.UserUseCase

/**
 * Factory for creating instances of AuthViewModel with the required dependencies.
 */
class AuthViewModelFactory(
    private val app:App,
    private val userUseCase: UserUseCase,
    private val authUseCase: AuthUseCase,
) : ViewModelProvider.Factory {

    /**
     * Creates an instance of AuthViewModel.
     *
     * @param modelClass The class representing the ViewModel to create.
     *@return An instance of AuthViewModel.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(app, userUseCase, authUseCase) as T
    }
}