package com.enkefalostechnologies.calendarpro.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.enkefalostechnologies.calendarpro.database.AmplifyAuth
import com.enkefalostechnologies.calendarpro.database.DatabaseHandler
import com.enkefalostechnologies.calendarpro.model.User
import com.enkefalostechnologies.calendarpro.ui.HomeActivity
import com.enkefalostechnologies.calendarpro.ui.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserConfirmationViewModel(
    private val repository: Repository
):ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                UserConfirmationViewModel(repository = Repository(AmplifyAuth(), DatabaseHandler()))
            }
        }
    }
    fun getSessionValue() : LiveData<Boolean> {
        return repository.isSignedin()
    }

    fun checkSessionValue(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.checkSession()
        }
    }

    fun createUser(user: User){
        viewModelScope.launch(Dispatchers.IO) {
            repository.createUser(user)
        }
    }
    fun error()=repository.getError()
    fun getUserAttributes()=repository.getUserAttributes()
    fun isUserCreated()=repository.isUserCreated()

    fun fetchUserAttributes()=repository.fetchUserAttributes()

}