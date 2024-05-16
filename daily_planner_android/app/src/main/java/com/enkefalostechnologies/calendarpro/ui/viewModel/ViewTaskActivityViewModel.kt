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
import com.amplifyframework.datastore.DataStoreException
import com.amplifyframework.datastore.generated.model.KnowYourDay
import com.amplifyframework.datastore.generated.model.ListGroup
import com.amplifyframework.datastore.generated.model.ReminderEnum
import com.amplifyframework.datastore.generated.model.RepeatDays
import com.amplifyframework.datastore.generated.model.RepeatType
import com.amplifyframework.datastore.generated.model.TaskType
import com.amplifyframework.datastore.generated.model.Tasks
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.WaterInTake
import com.enkefalostechnologies.calendarpro.database.AmplifyAuth
import com.enkefalostechnologies.calendarpro.database.DatabaseHandler
import com.enkefalostechnologies.calendarpro.model.BarEntry
import com.enkefalostechnologies.calendarpro.model.TaskModel
import com.enkefalostechnologies.calendarpro.model.TaskModel2
import com.enkefalostechnologies.calendarpro.ui.adapter.BarData
import com.enkefalostechnologies.calendarpro.ui.repository.Repository
import com.google.android.gms.tasks.Task
import dagger.hilt.android.ViewModelLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class ViewTaskActivityViewModel(
    private val repository: Repository
):ViewModel() {
   // var isLoggedIn:Boolean=false
   // var user:User?=null
    //val loginSession:LiveData<Boolean> get()= repository.isUserLoggedIn
    //val userAttributes:LiveData<com.enkefalostechnologies.calendarpro.model.User> get()=repository.userAttributes
    //val userData:LiveData<User>  get() = repository.loggedInUser
   val  onError: LiveData<DataStoreException> get()=repository.onError
    val taskDetail:LiveData<Tasks>  get() = repository.taskDetail
    val isTaskDeleted:LiveData<Boolean> get()=repository.isTaskDeleted
    val isTaskMarkedDone:LiveData<TaskModel> get()=repository.isTaskMarkedDone
    val isTaskMarkedUnDone:LiveData<TaskModel> get()=repository.isTaskMarkedUnDone
    val isTaskUpdated: LiveData<MutableList<Tasks>> get() = repository.isTaskUpdated
    val isTaskCreated: LiveData<MutableList<Tasks>> get() = repository.isTaskCreated
    val isAllTaskDeleted:LiveData<Boolean> get()=repository.isAllTaskDeleted
    val listGroupDataList:LiveData<List<ListGroup>> get() = repository.listGroupDataList

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ViewTaskActivityViewModel(repository = Repository(AmplifyAuth(), DatabaseHandler()))
            }
        }
    }
    init {
//         checkSessionValue()
     }

//    fun checkSessionValue(){
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.checkSession()
//        }
//    }
//    fun fetchUserAttributes()=repository.fetchUserAttributes()
//    fun fetchUserData(userId:String){
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.fetchUserData(userId)
//        }
//    }

    fun fetchList(userId: String,deviceId:String){
        viewModelScope.launch(Dispatchers.IO) {
                repository.fetchListGroup(userId,deviceId)
        }
    }
    fun createList(userId: String,deviceId: String,listName:String){
        viewModelScope.launch(Dispatchers.IO) {
               repository.createListGroup(userId,deviceId,listName)
        }
    }
    fun markDone(taskModel:TaskModel){
        viewModelScope.launch(Dispatchers.IO) {
                repository.markTaskAsDone(taskModel)
        }
    }
    fun markUnDone(taskModel:TaskModel){
        viewModelScope.launch(Dispatchers.IO) {
            repository.markTaskAsUnDone(taskModel)
        }
    }
    fun deleteTask(taskId:String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTask(taskId)
        }
    }
    fun deleteAllTask(taskId:String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllTask(taskId)
        }
    }
    fun getTasKDetail(taskId:String){
        viewModelScope.launch(Dispatchers.IO) {
                repository.getTaskDetail(taskId)
        }
    }
    fun updateTask(taskModel: TaskModel2){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTask(taskModel)
        }
    }
}