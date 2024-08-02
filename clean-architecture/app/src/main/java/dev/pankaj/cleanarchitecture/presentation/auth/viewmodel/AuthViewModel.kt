package dev.pankaj.cleanarchitecture.presentation.auth.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.scopes.ViewModelScoped
import dev.pankaj.cleanarchitecture.App
import dev.pankaj.cleanarchitecture.data.remote.model.LoginRequest
import dev.pankaj.cleanarchitecture.data.remote.model.LoginResponse
import dev.pankaj.cleanarchitecture.domain.usecase.AuthUseCase
import dev.pankaj.cleanarchitecture.domain.usecase.UserUseCase
import kotlinx.coroutines.launch
import dev.pankaj.cleanarchitecture.utils.*
import dev.pankaj.cleanarchitecture.utils.NetworkUtils.isNetworkAvailable

/**
 * ViewModel responsible for handling authentication logic, such as login.
 */
@ViewModelScoped
class AuthViewModel(
    private val app: App,
    private val userUseCase: UserUseCase,
    private val authUseCase: AuthUseCase,
) : AndroidViewModel(app) {

    private val _loginResponse: MutableLiveData<Result<LoginResponse>> = MutableLiveData()
    /**
     * LiveData exposing the result of the loginoperation.
     */
    val loginResponse: LiveData<Result<LoginResponse>> = _loginResponse

    /**
     * Initiates the login process.
     *
     * @param loginRequest The LoginRequest object containing the user's credentials.
     */
    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch {
            _loginResponse.value = Result.Loading(true) // Indicate loading state
            if (isNetworkAvailable(app)) { // Check network availability
                authUseCase.login(loginRequest).apply {
                    _loginResponse.value = this // Update login response
                    _loginResponse.value = Result.Loading(false) // Indicate loading finished
                }
            } else {
                _loginResponse.value = Result.Message(NO_NETWORK_CONNECTION) // Show error message
                _loginResponse.value = Result.Loading(false) // Indicate loading finished
            }
        }
    }
}