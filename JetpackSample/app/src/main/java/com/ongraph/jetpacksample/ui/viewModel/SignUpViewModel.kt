package com.ongraph.jetpacksample.ui.viewModel

import androidx.lifecycle.MutableLiveData
import com.ongraph.jetpacksample.remote.Response
import com.ongraph.jetpacksample.remote.models.RegisterReq
import com.ongraph.jetpacksample.remote.network.ApiRepository

/**
 * view model class for sign up
 * */
class SignUpViewModel: ViewModel() {

    var registerResponse = MutableLiveData<Response>()

    fun registerUser (request: RegisterReq) {
        isLoading.value = true

        ApiRepository.registerUser({
            registerResponse.value = it
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