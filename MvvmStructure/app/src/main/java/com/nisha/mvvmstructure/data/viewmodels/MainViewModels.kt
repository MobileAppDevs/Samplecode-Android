package com.nisha.mvvmstructure.data.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.nisha.mvvmstructure.data.model.MovieResponse
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
    private val _response: MutableLiveData<NetworkResult<MovieResponse>> = MutableLiveData()
    val response: LiveData<NetworkResult<MovieResponse>> = _response

    fun fetchMovieResponse() = viewModelScope.launch {
        repository.getMovieList().collect { values ->
            _response.value = values
        }
    }

}


