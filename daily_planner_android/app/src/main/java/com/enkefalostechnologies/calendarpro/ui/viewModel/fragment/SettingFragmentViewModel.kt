package com.enkefalostechnologies.calendarpro.ui.viewModel.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.amplifyframework.datastore.DataStoreException
import com.amplifyframework.datastore.generated.model.User
import com.enkefalostechnologies.calendarpro.database.AmplifyAuth
import com.enkefalostechnologies.calendarpro.database.DatabaseHandler
import com.enkefalostechnologies.calendarpro.ui.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingFragmentViewModel(
    private val repository: Repository
):ViewModel() {

//    var isLoggedIn:Boolean=false
    var user:User?=null
    val loginSession:LiveData<Boolean> get()= repository.isUserLoggedIn
    val userAttributes:LiveData<com.enkefalostechnologies.calendarpro.model.User> get()=repository.userAttributes
    val userData:LiveData<User>  get() = repository.loggedInUser
    val onError:LiveData<DataStoreException>  get() = repository.onError
    val isDataSynced:LiveData<Boolean> get()=repository.isDataSynced

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingFragmentViewModel(repository = Repository(AmplifyAuth(), DatabaseHandler()))
            }
        }
    }
    fun fetchUserAttributes()=repository.fetchUserAttributes()

    fun getSessionValue() : LiveData<Boolean> {
        return repository.isSignedin()
    }
    fun fetchUserData(userId:String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchUserData(userId)
        }
    }
    fun checkSessionValue(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.checkSession()
        }
    }
    fun signOut(){
        viewModelScope.launch(Dispatchers.IO){
            repository.signOut()
        }

    }
    fun signOutConfirmation()=repository.signOutConfirmation()



}