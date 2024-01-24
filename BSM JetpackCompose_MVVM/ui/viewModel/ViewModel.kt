package com.ongraph.jetpackloginsample.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ongraph.jetpackloginsample.remote.models.ApiErrorResponse

open class ViewModel: ViewModel() {
    var apiError = MutableLiveData<ApiErrorResponse>()
    var onFailure = MutableLiveData<Throwable>()
    var isLoading = MutableLiveData<Boolean>()
}