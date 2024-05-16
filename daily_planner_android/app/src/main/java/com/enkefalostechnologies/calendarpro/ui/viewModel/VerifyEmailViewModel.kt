package com.enkefalostechnologies.calendarpro.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.enkefalostechnologies.calendarpro.database.AmplifyAuth
import com.enkefalostechnologies.calendarpro.database.DatabaseHandler
import com.enkefalostechnologies.calendarpro.model.User
import com.enkefalostechnologies.calendarpro.ui.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VerifyEmailViewModel(
    private val repository: Repository
):ViewModel() {

    var isLoggedIn:Boolean=false
    var user: com.amplifyframework.datastore.generated.model.User?=null
    val loginSession:LiveData<Boolean> get()= repository.isUserLoggedIn
    val userAttributes:LiveData<com.enkefalostechnologies.calendarpro.model.User> get()=repository.userAttributes
    val userData:LiveData<com.amplifyframework.datastore.generated.model.User>  get() = repository.loggedInUser
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                VerifyEmailViewModel(repository = Repository(AmplifyAuth(), DatabaseHandler()))
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
    fun fetchUserData(email:String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchUserData(email)
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