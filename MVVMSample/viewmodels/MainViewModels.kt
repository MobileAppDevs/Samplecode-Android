package com.ongraph.mvvmcode.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.ongraph.mvvmcode.data.model.MovieListItem
import com.ongraph.mvvmcode.data.repository.MainRepository
import com.ongraph.mvvmcode.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModels @Inject constructor(
    private val repository: MainRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _response: MutableLiveData<NetworkResult<List<MovieListItem>>> = MutableLiveData()
    val response: LiveData<NetworkResult<List<MovieListItem>>> = _response

    fun fetchMovieResponse() = viewModelScope.launch {
        repository.getMovieList().collect { values ->
            _response.value = values
        }
    }

}


