package com.ongraph.jetpacksample.ui.viewModel

import androidx.lifecycle.MutableLiveData
import com.ongraph.jetpacksample.remote.Response
import com.ongraph.jetpacksample.remote.models.LoginReq
import com.ongraph.jetpacksample.remote.network.ApiRepository

/**
 * login view model
 * */
class LoginViewModel: ViewModel() {

    var loginResponse = MutableLiveData<Response>()

    fun loginUser (request: LoginReq) {
        isLoading.value = true

        ApiRepository.loginUser({
            loginResponse.value = it
            isLoading.value = false
        }, {
            apiError.value = it
            isLoading.value = false
        }, {
            onFailure.value = it
            isLoading.value = false
        }, request)
    }
}