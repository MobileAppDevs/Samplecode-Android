package com.enkefalostechnologies.calendarpro.ui.repository

import android.app.Activity
import android.content.Context
import android.devicelock.DeviceId
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amplifyframework.auth.AuthUser
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
import com.enkefalostechnologies.calendarpro.api.network.ApiHelper
import com.enkefalostechnologies.calendarpro.constant.Constants
import com.enkefalostechnologies.calendarpro.constant.Constants.GOOGLE_API_KEY
import com.enkefalostechnologies.calendarpro.database.AmplifyAuth
import com.enkefalostechnologies.calendarpro.database.DatabaseHandler
import com.enkefalostechnologies.calendarpro.model.TaskModel
import com.enkefalostechnologies.calendarpro.model.TaskModel2
import com.enkefalostechnologies.calendarpro.util.PreferenceHandler
import com.google.android.gms.tasks.Task
import java.util.Date

class Repository(
    private val auth: AmplifyAuth,
    private val db: DatabaseHandler
) {

    val loggedInUser: LiveData<User> get() = db.LoggedInUser
    val SignInResultConfirmation: LiveData<Boolean> get() = auth.SignInResultConfirmation
    val taskDetail: LiveData<Tasks> get() = db.taskDetail

    val isUserLoggedIn: LiveData<Boolean> get() = auth.isUserSignedIn
    val currentDayTasks: LiveData<List<Tasks>> get() = db.currentDayTaskList
    val weeklyTasks: LiveData<List<Tasks>> get() = db.weeklyTaskList
    val monthlyTasks: LiveData<List<Tasks>> get() = db.monthlyTaskList
    val taskListByDate: LiveData<List<Tasks>> get() = db.taskListByDate
    val taskList: LiveData<List<Tasks>> get() = db.taskList
    val eventList: LiveData<List<Date>> get() = db.eventList
    val hEventList: LiveData<List<Date>> get() = db.hEventList
    val waterInTakeDataList: LiveData<List<WaterInTake>> get() = db.waterInTakeDataList
    val knowYourDayDataList: LiveData<List<KnowYourDay>> get() = db.knowYourDayDataList
    val listGroupDataList: LiveData<List<ListGroup>> get() = db.listGroupDataList
    val listGroupTaskDataList: LiveData<List<Tasks>> get() = db.listGroupTasksDataList
    val userAttributes: LiveData<com.enkefalostechnologies.calendarpro.model.User> get() = auth.userAttributes
    val isUserCreated: LiveData<User?> get() = db.isUserCreated
    val taskSyncingCompleted:LiveData<Boolean> =db.taskSyncingCompleted
    val listGroupSyncingCompleted:LiveData<Boolean> = db.listGroupSyncingCompleted
    val waterInTakeSyncingCompleted:LiveData<Boolean> = db.waterInTakeSyncingCompleted
    val knowYourDaySyncingCompleted:LiveData<Boolean> = db.knowYourDaySyncingCompleted
    val isReminderUpdated: LiveData<TaskModel> get() = db.isReminderUpdated
    val isRepeatTypeUpdated: LiveData<TaskModel> get() = db.isRepeatTypeUpdated
    val isTaskDeleted: LiveData<Boolean> get() = db.isTaskDeleted
    val isAllTaskDeleted: LiveData<Boolean> get() = db.isAllTaskDeleted
    val isTaskUpdated: LiveData<MutableList<Tasks>> get() = db.isTaskUpdated
    val isTaskCreated: LiveData<MutableList<Tasks>> get() = db.isTaskCreated
    val isListDeleted: LiveData<Boolean> get() = db.isListDeleted
    val isTaskMarkedDone: LiveData<TaskModel> get() = db.isMarkedDone
    val isTaskMarkedUnDone: LiveData<TaskModel> get() = db.isMarkedUnDone
    val onError: LiveData<DataStoreException> get() = db.onError
    val isDataSynced: LiveData<Boolean> get() = db.isDataSynced
    val isInitialSyncStarted: LiveData<Boolean> get() = db.isInitialSyncStarted
    val isInitialSyncCompleted: LiveData<Boolean> get() = db.isInitialSyncCompleted
    val initialSyncingModel: LiveData<String> get() = db.initialSyncingModel
    fun getError() = auth.isErrorOccurred

    fun isSignedin(): MutableLiveData<Boolean> {
        return auth.isUserSignedIn
    }

    fun updateReminder(taskModel: TaskModel,reminderType:ReminderEnum,timestamp:Date)=db.updateReminder(taskModel,reminderType,timestamp)
    fun updateRepeatType(taskModel: TaskModel,repeatType: RepeatType,repeatDays: List<RepeatDays>,endDate: Date?,repeatCount:Int)=db.updateRepeatType(taskModel,repeatType,repeatDays,endDate,repeatCount)

    fun checkSession() = auth.checkSignIn()

    fun signIn(email: String, Password: String) = auth.SignIn(email, Password)

    fun signInConfirmation() = auth.SignInResultConfirmation

    fun fetchUserAttributes() = auth.fetchUserAttributes()

    fun getSignupValue() = auth.SignupValue

    fun signUp(name: String, email: String, password: String) = auth.SignUp(name, email, password)

    fun signOut() = auth.SignOut()
    fun signOutConfirmation() = auth.SignOutResultConfirmation

    fun googleLogin(activity: Activity) = auth.GoogleLogin(activity)
    fun facebookLogin(activity: Activity) = auth.FacebookLogin(activity)

    fun createUser(user: com.enkefalostechnologies.calendarpro.model.User) = db.createUser(user)
    fun isUserCreated() = db.isUserCreated
    fun sendUserConfirmationCode(email: String) = auth.sendOtp(email)
    fun isCodeSent() = auth.isCodeSent
    fun getUserAttributes() = auth.userAttributes
    fun getUserData() = db.LoggedInUser

    fun listenDataStoreError() = db.onError

    fun fetchUserData(email: String) {
        db.fetchUser(email)
    }

    fun getCurrentDayTask(email: String,deviceId: String) {
        db.getCurrentDayTask(email,deviceId)
    }


    fun getWeeklyTask(email: String,deviceId: String) {
        db.getWeeklyTask(email,deviceId)
    }

    fun getMonthlyTasks(email: String,deviceId: String,startDate: Date, endDate: Date) {
        db.getMonthlyTask(email=email,deviceId=deviceId, startDate =startDate, endDate=endDate)
    }


    fun getWaterIntake(email: String,deviceId: String) {
        db.getWaterIntake(email =email,deviceId=deviceId)
    }


    fun getRateYourDay(email: String,deviceId: String) {
        db.getRateYourDay(email=email,deviceId=deviceId)
    }


    fun saveWaterIntake(email: String,deviceId: String, date: Temporal.Date, count: Int) {
        db.getSaveWaterInTake(email,deviceId, date, count)
    }


    fun saveRateYourDay(
        email: String,
        deviceId: String,
        date: Temporal.Date,
        healthCount: Int,
        productivityCount: Int,
        moodCount: Int
    ) {
        db.saveRateYourDay(email,deviceId, date, healthCount, productivityCount, moodCount)
    }

    fun markTaskAsDone(taskModel: TaskModel) {
        db.markTaskAsDone(taskModel)
    }
    fun markTaskAsUnDone(taskModel: TaskModel) {
        db.markTaskAsUnDone(taskModel)
    }



    fun createListGroup(email: String,deviceId: String, listName: String) {
        db.createListGroup(email,deviceId,listName)
    }


    fun fetchListGroup(email: String,deviceId: String) {
        db.fetchListGroup(email=email,deviceId=deviceId)
    }


    fun fetchListGroupTasks(email: String,deviceId: String, listGroupId: String) {
        db.fetchListGroupTask(email=email,deviceId=deviceId, listGroupId= listGroupId)
    }



    fun deleteTask( taskId: String) {
        db.deleteTask( taskId)
    }
    fun deleteAllTask( taskId: String) {
        db.deleteAllTask(taskId)
    }
    fun deleteList( listGroupId: String) {
        db.deleteList(listGroupId)
    }
    fun syncTasks(email: String,deviceId: String, holidaysToo:Boolean)=db.syncTasks(email,deviceId,holidaysToo)
    fun syncWaterIntake(email: String,deviceId: String)=db.syncWaterIntake(email,deviceId)
    fun syncKnowYourDay(email: String,deviceId: String)=db.syncKnowYourDay(email,deviceId)
    fun syncListGroups(email: String,deviceId: String)=db.syncListGroups(email,deviceId)

    fun getTaskDetail(taskId: String) {
        db.getTaskDetail(taskId)
    }

    fun updateTask(
        taskModel: TaskModel2
    ) {
        if(taskModel.singleOccurrance){
            db.updateTask(taskModel)
        }else {
            db.updateAllTask(taskModel)
        }
    }

   fun  createTask(
       taskModel: TaskModel2
    ){db.createTask(taskModel)}
    fun  deleteHolidayEvents(
        email: String,deviceId: String
    ){db.deleteHolidayEvents(email,deviceId)}

    fun getTaskListByDate(email: String,deviceId: String,date: Date){
        db.getTaskListByDate(email,deviceId,date)
    }
    fun getTaskList(email: String,deviceId: String){
        db.getTaskList(email, deviceId = deviceId)
    }
    fun getEventListForCalendar(email: String,deviceId: String,startDate: Date,endDate: Date){
        db.getEventsForCalendar(email,deviceId,startDate,endDate)
    }
    fun getHEventListForCalendar(email: String,deviceId: String,startDate: Date,endDate: Date){
        db.getHEventsForCalendar(email,deviceId,startDate,endDate)
    }

    fun getHolidays(context: Context,tag:String)=ApiHelper(context = context).createService().getEvents("${Constants.GOOGLE_CALENDAR_API_URL}$tag/events?key=$GOOGLE_API_KEY")

}