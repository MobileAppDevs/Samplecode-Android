package com.enkefalostechnologies.calendarpro.ui.viewModel.fragment


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.amplifyframework.datastore.DataStoreException
import com.amplifyframework.datastore.generated.model.ReminderEnum
import com.amplifyframework.datastore.generated.model.RepeatDays
import com.amplifyframework.datastore.generated.model.RepeatType
import com.amplifyframework.datastore.generated.model.Tasks
import com.enkefalostechnologies.calendarpro.database.AmplifyAuth
import com.enkefalostechnologies.calendarpro.database.DatabaseHandler
import com.enkefalostechnologies.calendarpro.model.TaskModel
import com.enkefalostechnologies.calendarpro.model.TaskModel2
import com.enkefalostechnologies.calendarpro.ui.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class ListTaskFragmentViewModel(
    private val repository: Repository
):ViewModel() {
//    var isLoggedIn:Boolean=false
//    var user:User?=null
//    val loginSession:LiveData<Boolean> get()= repository.isUserLoggedIn
//    val userAttributes:LiveData<com.enkefalostechnologies.calendarpro.model.User> get()=repository.userAttributes
//    val userData:LiveData<User>  get() = repository.loggedInUser
    val listGroupTaskList:LiveData<List<Tasks>> get() = repository.listGroupTaskDataList
    val isTaskCreated:LiveData<MutableList<Tasks>> get()=repository.isTaskCreated
    val isListDeleted:LiveData<Boolean> get()=repository.isListDeleted
    val isReminderUpdated:LiveData<TaskModel> get() = repository.isReminderUpdated
    val isRepeatTypeUpdated:LiveData<TaskModel> get() = repository.isRepeatTypeUpdated
    val onError:LiveData<DataStoreException> get()=repository.onError

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ListTaskFragmentViewModel(repository = Repository(AmplifyAuth(), DatabaseHandler()))
            }
        }
    }
    init {
         checkSessionValue()
     }

    fun updateRepeatType(taskModel:TaskModel, repeatType: RepeatType, repeatDays: List<RepeatDays>, endDate: Date?,repeatCount:Int){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRepeatType(taskModel,repeatType,repeatDays,endDate,repeatCount)
        }
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

    fun fetchListGroupTasks(email: String,deviceId:String,listGroupId:String){
        viewModelScope.launch(Dispatchers.IO) {
                repository.fetchListGroupTasks(email,deviceId,listGroupId)
        }
    }
    fun deleteList(listGroupId: String){
        viewModelScope.launch(Dispatchers.IO) {
                repository.deleteList(listGroupId)
        }
    }
    fun createTask(
        taskModel: TaskModel2
    ){
        viewModelScope.launch(Dispatchers.IO) {
                repository.createTask(
                    taskModel
                )
        }
    }

    fun updateReminder(taskModel:TaskModel,reminder: ReminderEnum,hrs:Int,min:Int){
        viewModelScope.launch(Dispatchers.IO) {
            val d=taskModel.task.date.toDate()
            d.hours=hrs
            d.minutes=min
            repository.updateReminder(taskModel,reminder,d)
        }
    }
}