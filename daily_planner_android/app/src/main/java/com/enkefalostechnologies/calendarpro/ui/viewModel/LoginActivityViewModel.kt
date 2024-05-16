package com.enkefalostechnologies.calendarpro.ui.viewModel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.amplifyframework.auth.AuthUser
import com.enkefalostechnologies.calendarpro.database.AmplifyAuth
import com.enkefalostechnologies.calendarpro.database.DatabaseHandler
import com.enkefalostechnologies.calendarpro.model.User
import com.enkefalostechnologies.calendarpro.ui.HomeActivity
import com.enkefalostechnologies.calendarpro.ui.repository.Repository
import com.enkefalostechnologies.calendarpro.util.PreferenceHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivityViewModel(
    private val repository: Repository
):ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                LoginActivityViewModel(repository = Repository(AmplifyAuth(), DatabaseHandler()))
            }
        }
    }

    var user:User?=null
    val userAttributes:LiveData<com.enkefalostechnologies.calendarpro.model.User> get()=repository.userAttributes
    val isUserCreated:LiveData<com.amplifyframework.datastore.generated.model.User?> get()=repository.isUserCreated
    val isTaskSyncCompleted:LiveData<Boolean> get()=repository.taskSyncingCompleted
    val isListGroupSyncCompleted:LiveData<Boolean> get()=repository.listGroupSyncingCompleted
    val isWaterInTakeSyncCompleted:LiveData<Boolean> get()=repository.waterInTakeSyncingCompleted
    val isKnowYourDayCompleted:LiveData<Boolean> get()=repository.knowYourDaySyncingCompleted
    val SignInResultConfirmation:LiveData<Boolean> get() = repository.SignInResultConfirmation
    fun getSessionValue() : LiveData<Boolean> {
        return repository.isSignedin()
    }
    val isInitialSyncStarted:LiveData<Boolean> get()= repository.isInitialSyncStarted
    val isInitialSyncCompleted:LiveData<Boolean> get()= repository.isInitialSyncCompleted
    val initialSyncingModel:LiveData<String> get()= repository.initialSyncingModel
    fun signOut(){
        viewModelScope.launch(Dispatchers.IO){
            repository.signOut()
        }

    }

    fun error()=repository.getError()
    fun signOutConfirmation()=repository.signOutConfirmation()
    fun checkSessionValue(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.checkSession()
        }

    }
     fun syncTasks(userId:String, deviceId:String, holidaysToo:Boolean){
         viewModelScope.launch(Dispatchers.Main) {
             repository.syncTasks(userId, deviceId,holidaysToo)
         }
     }
    fun syncWaterIntake(userId:String, deviceId:String){
        viewModelScope.launch(Dispatchers.Main) {
            repository.syncWaterIntake(userId, deviceId)
        }
    }
   fun syncKnowYourDay(userId:String, deviceId:String){
        viewModelScope.launch(Dispatchers.Main) {
            repository.syncKnowYourDay(userId, deviceId)
        }
   }
   fun syncListGroups(userId:String, deviceId:String){
        viewModelScope.launch(Dispatchers.Main) {
            repository.syncListGroups(userId, deviceId)
        }
   }
    fun signInUser(email: String, password: String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.signIn(email, password)
        }
    }
    fun getSignedInConfirmation(): LiveData<Boolean>{
        return repository.signInConfirmation()
    }

//    fun getAuthUser()=repository.getAuthUser()

    fun getSignUpValue()=repository.getSignupValue()

    fun signUp(name:String,email:String,password:String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.signUp(name, email, password)
        }
    }


    fun googleLogin(activity:Activity){
        viewModelScope.launch(Dispatchers.IO) {
            repository.googleLogin(activity)
        }
    }
    fun facebookLogin(activity:Activity){
        viewModelScope.launch(Dispatchers.IO) {
            repository.facebookLogin(activity)
        }
    }

    fun sendUserVerificationCode(email:String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.sendUserConfirmationCode(email)
        }
    }

    fun isUserConfirmationCodeSent()=repository.isCodeSent()

    fun createUser(user: User){
        viewModelScope.launch(Dispatchers.IO) {
            repository.createUser(user)
        }
    }
    fun getUserAttributes()=repository.getUserAttributes()
    fun isUserCreated()=repository.isUserCreated()

    fun fetchUserAttributes()=repository.fetchUserAttributes()

}