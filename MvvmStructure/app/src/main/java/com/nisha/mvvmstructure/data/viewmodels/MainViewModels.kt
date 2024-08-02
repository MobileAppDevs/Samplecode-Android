package com.nisha.mvvmstructure.data.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.nisha.mvvmstructure.data.model.Response
import com.nisha.mvvmstructure.data.repository.MainRepository
import com.nisha.mvvmstructure.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModels @Inject constructor(
    private val repository: MainRepository,
    application: Application
) : AndroidViewModel(application) {

    /**
     * testing live data
     * */
    private val _response: MutableLiveData<NetworkResult<Response>> = MutableLiveData()
    val response: LiveData<NetworkResult<Response>> = _response

    fun fetchDetailsResponse() = viewModelScope.launch {
        repository.getList().collect { values ->
            _response.value = values
        }
    }

}


