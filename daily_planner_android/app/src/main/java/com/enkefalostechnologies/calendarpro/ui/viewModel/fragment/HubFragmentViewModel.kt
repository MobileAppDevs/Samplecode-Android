package com.enkefalostechnologies.calendarpro.ui.viewModel.fragment


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
import com.amplifyframework.datastore.generated.model.ReminderEnum
import com.amplifyframework.datastore.generated.model.RepeatDays
import com.amplifyframework.datastore.generated.model.RepeatType
import com.amplifyframework.datastore.generated.model.Tasks
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.WaterInTake
import com.enkefalostechnologies.calendarpro.database.AmplifyAuth
import com.enkefalostechnologies.calendarpro.database.DatabaseHandler
import com.enkefalostechnologies.calendarpro.model.BarEntry
import com.enkefalostechnologies.calendarpro.model.TaskModel
import com.enkefalostechnologies.calendarpro.ui.adapter.BarData
import com.enkefalostechnologies.calendarpro.ui.repository.Repository
import dagger.hilt.android.ViewModelLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class HubFragmentViewModel(
    private val repository: Repository
):ViewModel() {
    val todaySTaskList:LiveData<List<Tasks>> get() = repository.currentDayTasks
    val weeklyTasksList:LiveData<List<Tasks>> get() = repository.weeklyTasks
    val monthlyTasksList:LiveData<List<Tasks>> get() = repository.monthlyTasks
    val waterInTakeDataList:LiveData<List<WaterInTake>> get() = repository.waterInTakeDataList
    val knowYourDayDataList:LiveData<List<KnowYourDay>> get() = repository.knowYourDayDataList
    val isReminderUpdated:LiveData<TaskModel> get() = repository.isReminderUpdated
    val isRepeatTypeUpdated:LiveData<TaskModel> get() = repository.isRepeatTypeUpdated
    val onError: LiveData<DataStoreException> get() = repository.onError

    var barEntriesArrayList = MutableLiveData<MutableList<BarEntry>>()
    var lineChartData = MutableLiveData<MutableList<KnowYourDay>>()

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HubFragmentViewModel(repository = Repository(AmplifyAuth(), DatabaseHandler()))
            }
        }
    }
    init {

     }


    fun updateRepeatType(taskModel:TaskModel, repeatType: RepeatType, repeatDays: List<RepeatDays>, endDate: Date?,repeatCount:Int){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRepeatType(taskModel,repeatType,repeatDays,endDate,repeatCount)
        }
    }
    fun getCurrentDayTask(email: String,deviceId: String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCurrentDayTask(email, deviceId)
        }
    }
    fun getWeeklyTask(userId: String,deviceId: String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getWeeklyTask(userId, deviceId)
        }
    }
    fun getMonthlyTask(userId: String,deviceId: String,startDate: Date, endDate: Date){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getMonthlyTasks(userId, deviceId, startDate, endDate)
        }
    }

    fun getWaterInTakeData(userId:String,deviceId: String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getWaterIntake(userId, deviceId)
        }
    }
    fun getRateYourDay(email:String,deviceId: String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getRateYourDay(email, deviceId)
        }
    }
    fun updateReminder(taskModel:TaskModel, reminder: ReminderEnum, hrs:Int, min:Int){
        viewModelScope.launch(Dispatchers.IO) {
            val d=taskModel.task.date.toDate()
            d.hours=hrs
            d.minutes=min
            repository.updateReminder(taskModel,reminder,d)
        }
    }
    fun saveWaterInTake(userId: String,deviceId: String,date:Temporal.Date,count:Int){
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveWaterIntake(userId, deviceId, date, count)
        }
    }

    fun saveRateYourDay(email: String,deviceId: String,date:Temporal.Date,healthCount:Int, productivityCount:Int,moodCount:Int){
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveRateYourDay(
                email,
                deviceId,
                date,
                healthCount,
                productivityCount,
                moodCount
            )
        }
    }

    fun markTaskAsDone(taskModel: TaskModel){
        viewModelScope.launch(Dispatchers.IO) {
            repository.markTaskAsDone(taskModel)
        }
    }



}