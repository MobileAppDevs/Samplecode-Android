package com.enkefalostechnologies.calendarpro.ui.viewModel


import android.devicelock.DeviceId
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.generated.model.KnowYourDay
import com.amplifyframework.datastore.generated.model.ListGroup
import com.amplifyframework.datastore.generated.model.Tasks
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.WaterInTake
import com.enkefalostechnologies.calendarpro.database.AmplifyAuth
import com.enkefalostechnologies.calendarpro.database.DatabaseHandler
import com.enkefalostechnologies.calendarpro.model.BarEntry
import com.enkefalostechnologies.calendarpro.ui.adapter.BarData
import com.enkefalostechnologies.calendarpro.ui.repository.Repository
import dagger.hilt.android.ViewModelLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileActivityViewModel(
    private val repository: Repository
):ViewModel() {
    var isLoggedIn:Boolean=false
    var user:User?=null
    val loginSession:LiveData<Boolean> get()= repository.isUserLoggedIn
    val userAttributes:LiveData<com.enkefalostechnologies.calendarpro.model.User> get()=repository.userAttributes
    val userData:LiveData<User>  get() = repository.loggedInUser

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ProfileActivityViewModel(repository = Repository(AmplifyAuth(), DatabaseHandler()))
            }
        }
    }
    init {
         checkSessionValue()
     }

    fun checkSessionValue(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.checkSession()
        }
    }
    fun fetchUserAttributes()=repository.fetchUserAttributes()
    fun fetchUserData(email:String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchUserData(email)
        }
    }
}