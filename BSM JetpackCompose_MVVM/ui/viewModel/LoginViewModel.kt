package com.ongraph.jetpackloginsample.ui.viewModel

import androidx.lifecycle.MutableLiveData
import com.ongraph.jetpackloginsample.remote.Response
import com.ongraph.jetpackloginsample.remote.models.LoginReq
import com.ongraph.jetpackloginsample.remote.network.ApiRepository

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