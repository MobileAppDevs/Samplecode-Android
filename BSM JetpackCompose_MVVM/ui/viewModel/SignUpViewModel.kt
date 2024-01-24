package com.ongraph.jetpackloginsample.ui.viewModel

import androidx.lifecycle.MutableLiveData
import com.ongraph.jetpackloginsample.remote.Response
import com.ongraph.jetpackloginsample.remote.models.RegisterReq
import com.ongraph.jetpackloginsample.remote.models.VerifyOtpReq
import com.ongraph.jetpackloginsample.remote.network.ApiRepository

class SignUpViewModel: ViewModel() {

    var registerResponse = MutableLiveData<Response>()
    var otpResponse = MutableLiveData<Response>()

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

    fun otpVerification (request: VerifyOtpReq) {
        isLoading.value = true

        ApiRepository.verifyOtp({
            otpResponse.value = it
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