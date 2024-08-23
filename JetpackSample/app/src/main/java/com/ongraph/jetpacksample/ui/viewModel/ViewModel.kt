package com.ongraph.jetpacksample.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ongraph.jetpacksample.remote.models.ApiErrorResponse

/**
 * base view model class for all view models
 * */
open class ViewModel: ViewModel() {
    var apiError = MutableLiveData<ApiErrorResponse>()
    var onFailure = MutableLiveData<Throwable>()
    var isLoading = MutableLiveData<Boolean>()
}