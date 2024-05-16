package com.enkefalostechnologies.calendarpro.ui.viewModel.fragment


import android.devicelock.DeviceId
import android.util.Log
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
import com.enkefalostechnologies.calendarpro.util.AppUtil.getMonthFromDate
import com.google.android.gms.tasks.Task
import dagger.hilt.android.ViewModelLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class TaskFragmentViewModel(
    private val repository: Repository
) : ViewModel() {
    var isLoggedIn: Boolean = false

    //    var user:User?=null
//    val loginSession:LiveData<Boolean> get()= repository.isUserLoggedIn
//    val userAttributes:LiveData<com.enkefalostechnologies.calendarpro.model.User> get()=repository.userAttributes
//    val userData:LiveData<User>  get() = repository.loggedInUser
    val onError: LiveData<DataStoreException> get() = repository.onError

    val isTaskCreated: LiveData<MutableList<Tasks>> get() = repository.isTaskCreated
    val isTaskUpdated: LiveData<MutableList<Tasks>> get() = repository.isTaskUpdated
    val isMarkedDone: LiveData<TaskModel> get() = repository.isTaskMarkedDone
    val isMarkedUnDone: LiveData<TaskModel> get() = repository.isTaskMarkedUnDone
    val todaySTaskList: LiveData<List<Tasks>> get() = repository.currentDayTasks
    val weeklyTasksList: LiveData<List<Tasks>> get() = repository.weeklyTasks
    val monthlyTasksList: LiveData<List<Tasks>> get() = repository.monthlyTasks
    val taskListByDate: LiveData<List<Tasks>> get() = repository.taskListByDate
    val isReminderUpdated: LiveData<TaskModel> get() = repository.isReminderUpdated
    val isRepeatTypeUpdated: LiveData<TaskModel> get() = repository.isRepeatTypeUpdated
    val taskList: LiveData<List<Tasks>> get() = repository.taskList
    val eventList: LiveData<List<Date>> get() = repository.eventList
    val hEventList: LiveData<List<Date>> get() = repository.hEventList

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TaskFragmentViewModel(repository = Repository(AmplifyAuth(), DatabaseHandler()))
            }
        }
    }

    //    fun checkSessionValue(){
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.checkSession()
//        }
//    }
    fun updateReminder(taskModel: TaskModel, reminder: ReminderEnum, hrs: Int, min: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val d = taskModel.task.date.toDate()
            d.hours = hrs
            d.minutes = min
            repository.updateReminder(taskModel, reminder, d)
        }
    }

    fun updateRepeatType(
        taskModel: TaskModel,
        repeatType: RepeatType,
        repeatDays: List<RepeatDays>,
        endDate: Date?,
        repeatCount: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRepeatType(taskModel, repeatType, repeatDays, endDate, repeatCount)
        }
    }

    fun getCurrentDayTask(email: String, deviceId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCurrentDayTask(email, deviceId)
        }
    }

    fun getWeeklyTask(email: String, deviceId: String) {
        viewModelScope.launch(Dispatchers.IO) {
                repository.getWeeklyTask(email,deviceId)
        }
    }

    fun getMonthlyTask(email: String, deviceId: String, startDate: Date, endDate: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getMonthlyTasks(email,deviceId, startDate = startDate, endDate = endDate)
        }
    }

    fun getTodaySTask(email: String, deviceId: String) {
        viewModelScope.launch(Dispatchers.IO) {
                getCurrentDayTask(email,deviceId)
        }
    }

    fun getTaskByDate(email: String, deviceId: String, date: Date) {
        viewModelScope.launch(Dispatchers.IO) {
                repository.getTaskListByDate(email,deviceId, date)
        }
    }

    fun saveWaterInTake(email: String, deviceId: String, date: Temporal.Date, count: Int) {
            repository.saveWaterIntake(email,deviceId, date, count)
    }

    fun saveRateYourDay(
        email: String,
        deviceId: String,
        date: Temporal.Date,
        healthCount: Int,
        productivityCount: Int,
        moodCount: Int
    ) {
            repository.saveRateYourDay(
                email,
                deviceId,
                date,
                healthCount,
                productivityCount,
                moodCount
            )

    }

    fun markTaskAsDone(taskModel: TaskModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.markTaskAsDone(taskModel)
        }
    }

    fun markTaskAsUnDone(taskModel: TaskModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.markTaskAsUnDone(taskModel)
        }
    }

    fun getTaskListForShowingEvents(
        email: String,
        deviceId: String,
        startDate: Date,
        endDate: Date
    ) {
        Log.d("eventLog", "${startDate}-${endDate}")
        viewModelScope.launch(Dispatchers.IO) {
            repository.getEventListForCalendar(email, deviceId,startDate,endDate)
        }
    }
    fun getTaskListForShowingHEvents(
        email: String,
        deviceId: String,
        startDate: Date,
        endDate: Date
    ) {
        Log.d("eventLog", "${startDate}-${endDate}")
        viewModelScope.launch(Dispatchers.IO) {
            repository.getHEventListForCalendar(email, deviceId,startDate,endDate)
        }
    }

    fun createNormalTask(
        taskModel: TaskModel2
    ) {
        viewModelScope.launch(Dispatchers.IO) {
                repository.createTask(taskModel)
        }
    }
    fun updateTask(taskModel: TaskModel2){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTask(taskModel)
        }
    }


}