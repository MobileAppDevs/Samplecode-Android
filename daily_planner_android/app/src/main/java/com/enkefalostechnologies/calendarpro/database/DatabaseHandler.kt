package com.enkefalostechnologies.calendarpro.database

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.DataStoreChannelEventName
import com.amplifyframework.datastore.DataStoreException
import com.amplifyframework.datastore.events.ModelSyncedEvent
import com.amplifyframework.datastore.events.OutboxStatusEvent
import com.amplifyframework.datastore.generated.model.KnowYourDay
import com.amplifyframework.datastore.generated.model.ListGroup
import com.amplifyframework.datastore.generated.model.ReminderEnum
import com.amplifyframework.datastore.generated.model.RepeatDays
import com.amplifyframework.datastore.generated.model.RepeatType
import com.amplifyframework.datastore.generated.model.SubscriptionStatus
import com.amplifyframework.datastore.generated.model.SubscriptionType
import com.amplifyframework.datastore.generated.model.TaskType
import com.amplifyframework.datastore.generated.model.Tasks
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.WaterInTake
import com.amplifyframework.hub.HubChannel
import com.amplifyframework.hub.HubEvent
import com.enkefalostechnologies.calendarpro.App
import com.enkefalostechnologies.calendarpro.model.TaskModel
import com.enkefalostechnologies.calendarpro.model.TaskModel2
import com.enkefalostechnologies.calendarpro.util.AppUtil
import com.enkefalostechnologies.calendarpro.util.AppUtil.getDayFromDate
import com.enkefalostechnologies.calendarpro.util.Extension.dateToTemporalDate
import com.enkefalostechnologies.calendarpro.util.Extension.dateToTemporalDateTime
import com.enkefalostechnologies.calendarpro.util.Scheduler
import com.enkefalostechnologies.calendarpro.util.Scheduler.scheduleNotification
import com.enkefalostechnologies.calendarpro.util.Scheduler.scheduleTaskReminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

//Amplify Database operations

class DatabaseHandler {
    // interface handler


    private val TAG = "DatabaseHandler"
    val LoggedInUser: MutableLiveData<User> = MutableLiveData()
    val currentDayTaskList: MutableLiveData<List<Tasks>> = MutableLiveData()
    val taskListByDate: MutableLiveData<List<Tasks>> = MutableLiveData()
    val taskList: MutableLiveData<List<Tasks>> = MutableLiveData()
    val eventList: MutableLiveData<List<Date>> = MutableLiveData()
    val hEventList: MutableLiveData<List<Date>> = MutableLiveData()
    val weeklyTaskList: MutableLiveData<List<Tasks>> = MutableLiveData()
    val monthlyTaskList: MutableLiveData<List<Tasks>> = MutableLiveData()
    val waterInTakeDataList: MutableLiveData<List<WaterInTake>> = MutableLiveData()
    val knowYourDayDataList: MutableLiveData<List<KnowYourDay>> = MutableLiveData()
    val listGroupDataList: MutableLiveData<List<ListGroup>> = MutableLiveData()
    val listGroupTasksDataList: MutableLiveData<List<Tasks>> = MutableLiveData()
    val taskDetail: MutableLiveData<Tasks> = MutableLiveData()

    val onError: MutableLiveData<DataStoreException> = MutableLiveData()

    val isUserCreated: MutableLiveData<User?> = MutableLiveData()
    val isReminderUpdated: MutableLiveData<TaskModel> = MutableLiveData()
    val isRepeatTypeUpdated: MutableLiveData<TaskModel> = MutableLiveData()
    val isTaskDeleted: MutableLiveData<Boolean> = MutableLiveData()
    val isAllTaskDeleted: MutableLiveData<Boolean> = MutableLiveData()
    val isTaskUpdated: MutableLiveData<MutableList<Tasks>> = MutableLiveData()
    val isTaskCreated: MutableLiveData<MutableList<Tasks>> = MutableLiveData()
    val isListDeleted: MutableLiveData<Boolean> = MutableLiveData()
    val isMarkedDone: MutableLiveData<TaskModel> = MutableLiveData()
    val isMarkedUnDone: MutableLiveData<TaskModel> = MutableLiveData()
    val isDataSynced: MutableLiveData<Boolean> = MutableLiveData()
    val taskSyncingCompleted: MutableLiveData<Boolean> = MutableLiveData()
    val listGroupSyncingCompleted: MutableLiveData<Boolean> = MutableLiveData()
    val waterInTakeSyncingCompleted: MutableLiveData<Boolean> = MutableLiveData()
    val knowYourDaySyncingCompleted: MutableLiveData<Boolean> = MutableLiveData()
    val isInitialSyncStarted: MutableLiveData<Boolean> = MutableLiveData()
    val isInitialSyncCompleted: MutableLiveData<Boolean> = MutableLiveData()
    val initialSyncingModel: MutableLiveData<String> = MutableLiveData()

    init {
        Amplify.Hub.subscribe(
            HubChannel.DATASTORE,
            { hubEvent: HubEvent<*> -> DataStoreChannelEventName.SYNC_QUERIES_READY.toString() == hubEvent.name }
        ) { hubEvent: HubEvent<*> ->
            GlobalScope.launch(Dispatchers.IO) {
                isInitialSyncCompleted.postValue(true)
            }
        }
        Amplify.Hub.subscribe(
            HubChannel.DATASTORE,
            { hubEvent: HubEvent<*> -> DataStoreChannelEventName.OUTBOX_MUTATION_FAILED.toString() == hubEvent.name }
        ) { hubEvent: HubEvent<*> ->
            Amplify.DataStore.start({}, {})
        }
        Amplify.Hub.subscribe(
            HubChannel.DATASTORE,
            { hubEvent: HubEvent<*> -> DataStoreChannelEventName.OUTBOX_STATUS.toString() == hubEvent.name }
        ) { hubEvent: HubEvent<*> ->
            val event = hubEvent.data as OutboxStatusEvent
            GlobalScope.launch(Dispatchers.IO) {
                isDataSynced.postValue(event.isEmpty)
            }
        }
        Amplify.Hub.subscribe(
            HubChannel.DATASTORE,
            { hubEvent: HubEvent<*> -> DataStoreChannelEventName.SYNC_QUERIES_STARTED.toString() == hubEvent.name }
        ) { hubEvent: HubEvent<*> ->
            GlobalScope.launch(Dispatchers.IO) {
                isInitialSyncStarted.postValue(true)
            }
        }
        Amplify.Hub.subscribe(
            HubChannel.DATASTORE,
            { hubEvent: HubEvent<*> -> DataStoreChannelEventName.MODEL_SYNCED.toString() == hubEvent.name }
        ) { hubEvent: HubEvent<*> ->
            val event = hubEvent.data as ModelSyncedEvent
            GlobalScope.launch(Dispatchers.IO) {
                initialSyncingModel.postValue(event.model)
            }
        }
    }

    fun syncTasks(email: String, deviceId: String, holidayEventsTo: Boolean) {
        val query = if (holidayEventsTo)
            Where.matches(Tasks.DEVICE_ID.eq(deviceId))
        else
            Where.matches(Tasks.DEVICE_ID.eq(deviceId).and(Tasks.TASK_TYPE.ne(TaskType.HOLIDAY)))
        Amplify.DataStore.query(
            Tasks::class.java, query, { data ->
                while (data.hasNext()) {
                    val task = data.next().copyOfBuilder().email(email).deviceId("").build()
                    Amplify.DataStore.save(task, {
                        GlobalScope.launch(Dispatchers.Main) {
                            taskSyncingCompleted.setValue(false)
                        }
                    }, this::onError)
                }
                GlobalScope.launch(Dispatchers.Main) {
                    taskSyncingCompleted.setValue(true)
                }
            }, this::onError
        )
    }

    fun syncListGroups(email: String, deviceId: String) {
        Amplify.DataStore.query(
            ListGroup::class.java, Where.matches(ListGroup.DEVICE_ID.eq(deviceId)), { data ->
                while (data.hasNext()) {
                    val task = data.next().copyOfBuilder().email(email).deviceId("").build()
                    Amplify.DataStore.save(task, {
                        GlobalScope.launch(Dispatchers.Main) {
                            listGroupSyncingCompleted.setValue(false)
                        }
                    }, this::onError)
                }
                GlobalScope.launch(Dispatchers.Main) {
                    listGroupSyncingCompleted.setValue(true)
                }
            }, this::onError
        )
    }

    fun syncWaterIntake(email: String, deviceId: String) {
        Amplify.DataStore.query(
            WaterInTake::class.java, Where.matches(WaterInTake.DEVICE_ID.eq(deviceId)), { data ->
                while (data.hasNext()) {
                    val task = data.next().copyOfBuilder().email(email).deviceId("").build()
                    Amplify.DataStore.save(task, {
                        GlobalScope.launch(Dispatchers.Main) {
                            waterInTakeSyncingCompleted.setValue(false)
                        }
                    }, this::onError)
                }
                GlobalScope.launch(Dispatchers.Main) {
                    waterInTakeSyncingCompleted.setValue(true)
                }
            }, this::onError
        )
    }

    fun syncKnowYourDay(email: String, deviceId: String) {
        Amplify.DataStore.query(
            KnowYourDay::class.java, Where.matches(KnowYourDay.DEVICE_ID.eq(deviceId)), { data ->
                while (data.hasNext()) {
                    val task = data.next().copyOfBuilder().email(email).deviceId("").build()
                    Amplify.DataStore.save(task, {
                        GlobalScope.launch(Dispatchers.Main) {
                            knowYourDaySyncingCompleted.setValue(false)
                        }
                    }, this::onError)
                }
                GlobalScope.launch(Dispatchers.Main) {
                    knowYourDaySyncingCompleted.setValue(true)
                }
            }, this::onError
        )
    }

    fun updateRepeatType(
        taskModel: TaskModel,
        repeatType: RepeatType,
        repeatDays: List<RepeatDays>,
        endDate: Date?,
        repeatCount: Int
    ) {
        when (repeatType) {
            RepeatType.DAY -> {
                AppUtil.getDatesBetween(
                    taskModel.task.date.toDate(),
                    endDate!!,
                    taskModel.task.repeatType,
                    taskModel.task.repeatCount,
                    taskModel.task.repeatDays
                ).map { dt ->
                    Amplify.DataStore.query(
                        Tasks::class.java, Where.matches(
                            Tasks.TASK_ID.eq(taskModel.task.taskId)
                                .and(Tasks.DATE.eq(dt.dateToTemporalDate()))
                        ), { data ->
                            if (data.hasNext()) {
                                val task = data.next().copyOfBuilder().repeatType(repeatType)
                                    .repeatDays(repeatDays).repeatCount(repeatCount)
                                    .endDate(endDate.dateToTemporalDate())
                                    .updatedAt(AppUtil.getCurrentTemporalDateTime()).build()
                                Amplify.DataStore.save(
                                    task, {
                                        setReminderAndNotificationIfItIsTodaySTask(it.item())
                                        isTaskUpdated.postValue(mutableListOf(it.item()))
                                    }, this::onError
                                )
                            } else {
                                val task = Tasks.builder().title(taskModel.task.title)
                                    .taskId(taskModel.task.taskId)
                                    .description(taskModel.task.description)
                                    .email(taskModel.task.email)
                                    .notiRequestCode(AppUtil.generateRequestCode())
                                    .date(dt.dateToTemporalDate()).time(taskModel.task.time)
                                    .reminder(taskModel.task.reminder).repeatType(repeatType)
                                    .repeatDays(repeatDays)
                                    .emailOrPhone(taskModel.task.emailOrPhone)
                                    .taskType(taskModel.task.taskType)
                                    .listGroupId(taskModel.task.listGroupId)
                                    .isImportant(taskModel.task.isImportant)
                                    .isCompleted(taskModel.task.isCompleted)
                                    .endDate(endDate.dateToTemporalDate()).repeatCount(repeatCount)
                                    .customTime(if (taskModel.task.reminder == ReminderEnum.CUSTOM) taskModel.task.customTime else AppUtil.getCurrentTemporalDateTime())
                                    .createdAt(AppUtil.getCurrentTemporalDateTime())
                                    .updatedAt(AppUtil.getCurrentTemporalDateTime()).build()
                                Amplify.DataStore.save(task, {
                                    setReminderAndNotificationIfItIsTodaySTask(it.item())
                                    isTaskCreated.postValue(mutableListOf(it.item()))
                                }, this::onError)
                            }
                        }, this::onError
                    )
                }
                Amplify.DataStore.delete(Tasks::class.java,
                    Tasks.TASK_ID.eq(taskModel.task.taskId)
                        .and(Tasks.DATE.ge(endDate.dateToTemporalDate())),
                    {},
                    {})
            }

            RepeatType.WEEK -> {
                AppUtil.getDatesBetween(
                    taskModel.task.date.toDate(),
                    endDate!!,
                    taskModel.task.repeatType,
                    taskModel.task.repeatCount,
                    taskModel.task.repeatDays
                ).map { dt ->
                    if (repeatDays.contains(getRepeatDays(dt))) {
                        Amplify.DataStore.query(
                            Tasks::class.java, Where.matches(
                                Tasks.TASK_ID.eq(taskModel.task.taskId)
                                    .and(Tasks.DATE.eq(dt.dateToTemporalDate()))
                            ), { data ->
                                if (data.hasNext()) {
                                    val task = data.next().copyOfBuilder().repeatType(repeatType)
                                        .repeatDays(repeatDays)
                                        .endDate(endDate.dateToTemporalDate())
                                        .repeatCount(repeatCount)
                                        .updatedAt(AppUtil.getCurrentTemporalDateTime()).build()
                                    Amplify.DataStore.save(
                                        task, {
                                            setReminderAndNotificationIfItIsTodaySTask(it.item())
                                            isTaskUpdated.postValue(mutableListOf(it.item()))
                                        }, this::onError
                                    )
                                } else {
                                    val task = Tasks.builder().title(taskModel.task.title)
                                        .taskId(taskModel.task.taskId)
                                        .description(taskModel.task.description)
                                        .email(taskModel.task.email)
                                        .notiRequestCode(AppUtil.generateRequestCode())
                                        .date(dt.dateToTemporalDate()).time(taskModel.task.time)
                                        .reminder(taskModel.task.reminder).repeatType(repeatType)
                                        .repeatDays(repeatDays)
                                        .emailOrPhone(taskModel.task.emailOrPhone)
                                        .taskType(taskModel.task.taskType)
                                        .listGroupId(taskModel.task.listGroupId)
                                        .isImportant(taskModel.task.isImportant)
                                        .isCompleted(taskModel.task.isCompleted)
                                        .endDate(endDate.dateToTemporalDate())
                                        .repeatCount(repeatCount)
                                        .customTime(if (taskModel.task.reminder == ReminderEnum.CUSTOM) taskModel.task.customTime else AppUtil.getCurrentTemporalDateTime())
                                        .createdAt(AppUtil.getCurrentTemporalDateTime())
                                        .updatedAt(AppUtil.getCurrentTemporalDateTime()).build()
                                    Amplify.DataStore.save(task, {
                                        setReminderAndNotificationIfItIsTodaySTask(it.item())
                                        isTaskCreated.postValue(mutableListOf(it.item()))
                                    }, this::onError)
                                }
                            }, this::onError
                        )
                    } else {
                        Amplify.DataStore.delete(Tasks::class.java,
                            Tasks.TASK_ID.eq(taskModel.task.taskId)
                                .and(Tasks.DATE.eq(dt.dateToTemporalDate())),
                            {},
                            {})
                    }
                }
                Amplify.DataStore.delete(Tasks::class.java,
                    Tasks.TASK_ID.eq(taskModel.task.taskId)
                        .and(Tasks.DATE.ge(endDate.dateToTemporalDate())),
                    {},
                    {})
            }

            RepeatType.MONTH -> {
                AppUtil.getDatesBetween(
                    taskModel.task.date.toDate(),
                    endDate!!,
                    taskModel.task.repeatType,
                    taskModel.task.repeatCount,
                    taskModel.task.repeatDays
                ).map { dt ->
                    if (dt.date == taskModel.task.date.toDate().date) {
                        Amplify.DataStore.query(
                            Tasks::class.java, Where.matches(
                                Tasks.TASK_ID.eq(taskModel.task.taskId)
                                    .and(Tasks.DATE.eq(dt.dateToTemporalDate()))
                            ), { data ->
                                if (data.hasNext()) {
                                    val task = data.next().copyOfBuilder().repeatType(repeatType)
                                        .repeatDays(repeatDays)
                                        .endDate(endDate.dateToTemporalDate())
                                        .repeatCount(repeatCount)
                                        .updatedAt(AppUtil.getCurrentTemporalDateTime()).build()
                                    Amplify.DataStore.save(
                                        task, {
                                            setReminderAndNotificationIfItIsTodaySTask(it.item())
                                            isTaskUpdated.postValue(mutableListOf(it.item()))
                                        }, this::onError
                                    )
                                } else {
                                    val task = Tasks.builder().title(taskModel.task.title)
                                        .taskId(taskModel.task.taskId)
                                        .description(taskModel.task.description)
                                        .email(taskModel.task.email)
                                        .notiRequestCode(AppUtil.generateRequestCode())
                                        .date(dt.dateToTemporalDate()).time(taskModel.task.time)
                                        .reminder(taskModel.task.reminder).repeatType(repeatType)
                                        .repeatDays(repeatDays)
                                        .emailOrPhone(taskModel.task.emailOrPhone)
                                        .taskType(taskModel.task.taskType)
                                        .listGroupId(taskModel.task.listGroupId)
                                        .isImportant(taskModel.task.isImportant)
                                        .isCompleted(taskModel.task.isCompleted)
                                        .endDate(endDate.dateToTemporalDate())
                                        .repeatCount(repeatCount)
                                        .customTime(if (taskModel.task.reminder == ReminderEnum.CUSTOM) taskModel.task.customTime else AppUtil.getCurrentTemporalDateTime())
                                        .createdAt(AppUtil.getCurrentTemporalDateTime())
                                        .updatedAt(AppUtil.getCurrentTemporalDateTime()).build()
                                    Amplify.DataStore.save(task, {
                                        setReminderAndNotificationIfItIsTodaySTask(it.item())
                                        isTaskCreated.postValue(mutableListOf(it.item()))
                                    }, this::onError)
                                }
                            }, this::onError
                        )
                    } else {
                        Amplify.DataStore.delete(Tasks::class.java,
                            Tasks.TASK_ID.eq(taskModel.task.taskId)
                                .and(Tasks.DATE.eq(dt.dateToTemporalDate())),
                            {},
                            {})
                    }
                }
                Amplify.DataStore.delete(Tasks::class.java,
                    Tasks.TASK_ID.eq(taskModel.task.taskId)
                        .and(Tasks.DATE.ge(endDate.dateToTemporalDate())),
                    {},
                    {})
            }

            RepeatType.YEAR -> {
                AppUtil.getDatesBetween(
                    taskModel.task.date.toDate(),
                    endDate!!,
                    taskModel.task.repeatType,
                    taskModel.task.repeatCount,
                    taskModel.task.repeatDays
                ).map { dt ->
                    if (dt.date == taskModel.task.date.toDate().date && dt.month == taskModel.task.date.toDate().month) {
                        Amplify.DataStore.query(
                            Tasks::class.java, Where.matches(
                                Tasks.TASK_ID.eq(taskModel.task.taskId)
                                    .and(Tasks.DATE.eq(dt.dateToTemporalDate()))
                            ), { data ->
                                if (data.hasNext()) {
                                    val task = data.next().copyOfBuilder().repeatType(repeatType)
                                        .repeatDays(repeatDays)
                                        .endDate(endDate.dateToTemporalDate())
                                        .repeatCount(repeatCount)
                                        .updatedAt(AppUtil.getCurrentTemporalDateTime()).build()
                                    Amplify.DataStore.save(
                                        task, {
                                            setReminderAndNotificationIfItIsTodaySTask(it.item())
                                            isTaskUpdated.postValue(mutableListOf(it.item()))
                                        }, this::onError
                                    )
                                } else {
                                    val task = Tasks.builder().title(taskModel.task.title)
                                        .taskId(taskModel.task.taskId)
                                        .description(taskModel.task.description)
                                        .email(taskModel.task.email)
                                        .notiRequestCode(AppUtil.generateRequestCode())
                                        .date(dt.dateToTemporalDate()).time(taskModel.task.time)
                                        .reminder(taskModel.task.reminder).repeatType(repeatType)
                                        .repeatDays(repeatDays)
                                        .emailOrPhone(taskModel.task.emailOrPhone)
                                        .taskType(taskModel.task.taskType)
                                        .listGroupId(taskModel.task.listGroupId)
                                        .isImportant(taskModel.task.isImportant)
                                        .isCompleted(taskModel.task.isCompleted)
                                        .endDate(endDate.dateToTemporalDate())
                                        .repeatCount(repeatCount)
                                        .customTime(if (taskModel.task.reminder == ReminderEnum.CUSTOM) taskModel.task.customTime else AppUtil.getCurrentTemporalDateTime())
                                        .createdAt(AppUtil.getCurrentTemporalDateTime())
                                        .updatedAt(AppUtil.getCurrentTemporalDateTime()).build()
                                    Amplify.DataStore.save(task, {
                                        setReminderAndNotificationIfItIsTodaySTask(it.item())
                                        isTaskCreated.postValue(mutableListOf(it.item()))
                                    }, this::onError)
                                }
                            }, this::onError
                        )
                    } else {
                        Amplify.DataStore.delete(Tasks::class.java,
                            Tasks.TASK_ID.eq(taskModel.task.taskId)
                                .and(Tasks.DATE.eq(dt.dateToTemporalDate())),
                            {},
                            {})
                    }
                }
                Amplify.DataStore.delete(Tasks::class.java,
                    Tasks.TASK_ID.eq(taskModel.task.taskId)
                        .and(Tasks.DATE.ge(endDate.dateToTemporalDate())),
                    {},
                    {})
            }

            RepeatType.NONE -> {
                val task =
                    taskModel.task.copyOfBuilder().repeatType(repeatType).repeatDays(repeatDays)
                        .endDate(endDate?.dateToTemporalDate()).repeatCount(repeatCount)
                        .updatedAt(AppUtil.getCurrentTemporalDateTime()).build()
                Amplify.DataStore.save(
                    task, {
                        setReminderAndNotificationIfItIsTodaySTask(it.item())
                        isTaskUpdated.postValue(mutableListOf(it.item()))
                    }, this::onError
                )
            }
        }
        isReminderUpdated.postValue(taskModel)
    }

    fun updateReminder(taskModel: TaskModel, reminderType: ReminderEnum, timeStamp: Date) {
        val task = if (reminderType == ReminderEnum.CUSTOM) {
            taskModel.task.copyOfBuilder().reminder(ReminderEnum.CUSTOM)
                .customTime(timeStamp.dateToTemporalDateTime())
                .updatedAt(AppUtil.getCurrentTemporalDateTime()).build()
        } else {
            taskModel.task.copyOfBuilder().reminder(reminderType)
                .updatedAt(AppUtil.getCurrentTemporalDateTime()).build()
        }
        Amplify.DataStore.save(
            task, {
                taskModel.task = it.item()
                setReminderAndNotificationIfItIsTodaySTask(it.item())
                isReminderUpdated.postValue(taskModel)
            }, this::onError
        )
    }

    fun createUser(user: com.enkefalostechnologies.calendarpro.model.User) {
        Amplify.DataStore.query(
            User::class.java, Where.matches(User.EMAIL.eq(user.email.trim())), { data ->
                val isPresent=data.hasNext()
                Log.d("userData", "createUser: $isPresent ,${user.email}")
                if (isPresent) {
                    GlobalScope.launch(Dispatchers.Main) {
                        isUserCreated.setValue(data.next())
                    }
                } else {
                    val user = User.builder().name(user.name)
                        .email(user.email)
                        .picUrl(user.picUrl)
                        .deviceId(user.deviceId)
                        .isSocialLoggedIn(user.isSocialLoggedIn)
                        .isEmailVerified(user.isEmailVerified)
                        .isNotificationEnabled(user.isNotificationEnabled)
                        .isReminderEnabled(user.isReminderEnabled)
                        .subscriptionType(SubscriptionType.NONE)
                        .subscriptionValidUpTo(Date().dateToTemporalDate())
                        .subscriptionStatus(SubscriptionStatus.NONE)
                        .build()
                    Amplify.DataStore.save(user, {
                        GlobalScope.launch(Dispatchers.Main) {
                            isUserCreated.setValue(it.item())
                        }
                    }, {
                        GlobalScope.launch(Dispatchers.Main) {
                            isUserCreated.setValue(null)
                        }
                    })
                }
            }, this::onError
        )
    }


    private fun onError(exception: DataStoreException) {
        Log.d("Syncing", "onError====>${exception.message}")
        GlobalScope.launch(Dispatchers.IO) {
            onError.postValue(exception)
        }
    }


    fun fetchUser(email: String) {
        Amplify.DataStore.query(
            User::class.java, Where.matches(User.EMAIL.eq(email)), { response ->
                response.forEach {
                    LoggedInUser.postValue(it)
                }
            }, this::onError
        )

    }

    fun getCurrentDayTask(email: String, deviceId: String) {
        val query = if (email == "") Tasks.DEVICE_ID.eq(deviceId)
            .and(Tasks.DATE.eq(AppUtil.getCurrentTemporalDate()))
        else Tasks.EMAIL.eq(email).and(Tasks.DATE.eq(AppUtil.getCurrentTemporalDate()))
        Amplify.DataStore.query(
            Tasks::class.java, Where.matches(
                query
            ), { data ->
                val taskList = mutableListOf<Tasks>()
                while (data.hasNext()) {
                    taskList.add(data.next())
                }
                GlobalScope.launch(Dispatchers.Main) {
                    currentDayTaskList.postValue(taskList)
                }
            }, this::onError
        )

    }


    fun getWeeklyTask(email: String, deviceId: String) {
        val query = if (email == "") Tasks.DEVICE_ID.eq(deviceId).and(
            Tasks.DATE.ge(AppUtil.getTemporalDateOfCurrentWeek()[0])
                .and(Tasks.DATE.le(AppUtil.getTemporalDateOfCurrentWeek()[6]))
        )
        else Tasks.EMAIL.eq(email).and(
            Tasks.DATE.ge(AppUtil.getTemporalDateOfCurrentWeek()[0])
                .and(Tasks.DATE.le(AppUtil.getTemporalDateOfCurrentWeek()[6]))
        )
        Amplify.DataStore.query(
            Tasks::class.java, Where.matches(
                query
            ), { data ->
                val taskList = mutableListOf<Tasks>()
                while (data.hasNext()) {
                    taskList.add(data.next())
                }
                GlobalScope.launch(Dispatchers.Main) {
                    weeklyTaskList.postValue(taskList)
                }
            }, this::onError
        )

    }


    fun getMonthlyTask(email: String, deviceId: String, startDate: Date, endDate: Date) {
        val query = if (email == "") Tasks.DEVICE_ID.eq(deviceId).and(
            Tasks.DATE.ge(startDate.dateToTemporalDate())
                .and(Tasks.DATE.le(endDate.dateToTemporalDate()))
        )
        else Tasks.EMAIL.eq(email).and(
            Tasks.DATE.ge(startDate.dateToTemporalDate())
                .and(Tasks.DATE.le(endDate.dateToTemporalDate()))
        )
        Amplify.DataStore.query(
            Tasks::class.java, Where.matches(
                query
            ), { data ->
                val taskList = mutableListOf<Tasks>()
                while (data.hasNext()) {
                    taskList.add(data.next())
                }
                GlobalScope.launch(Dispatchers.Main) {
                    monthlyTaskList.postValue(taskList)
                }
            }, this::onError
        )

    }


    fun getWaterIntake(email: String, deviceId: String) {
        val query = if (email == "") WaterInTake.DEVICE_ID.eq(deviceId).and(
            WaterInTake.DATE.ge(AppUtil.getTemporalDateOfCurrentWeek()[0])
                .and(WaterInTake.DATE.le(AppUtil.getTemporalDateOfCurrentWeek()[6]))
        )
        else WaterInTake.EMAIL.eq(email).and(
            WaterInTake.DATE.ge(AppUtil.getTemporalDateOfCurrentWeek()[0])
                .and(WaterInTake.DATE.le(AppUtil.getTemporalDateOfCurrentWeek()[6]))
        )
        Amplify.DataStore.query(
            WaterInTake::class.java, Where.matches(
                query
            ), { data ->
                val waterInTakeList = mutableListOf<WaterInTake>()
                while (data.hasNext()) {
                    waterInTakeList.add(data.next())
                }
                GlobalScope.launch(Dispatchers.Main) {
                    waterInTakeDataList.postValue(waterInTakeList)
                }
            }, this::onError
        )
    }


    fun getRateYourDay(email: String, deviceId: String) {
        val query = if (email == "") KnowYourDay.DEVICE_ID.eq(deviceId).and(
            KnowYourDay.DATE.ge(AppUtil.getTemporalDateOfCurrentWeek()[0])
                .and(KnowYourDay.DATE.le(AppUtil.getTemporalDateOfCurrentWeek()[6]))
        )
        else KnowYourDay.EMAIL.eq(email).and(
            KnowYourDay.DATE.ge(AppUtil.getTemporalDateOfCurrentWeek()[0])
                .and(KnowYourDay.DATE.le(AppUtil.getTemporalDateOfCurrentWeek()[6]))
        )
        Amplify.DataStore.query(
            KnowYourDay::class.java, Where.matches(
                query
            ), { data ->
                val knowYourDayList = mutableListOf<KnowYourDay>()
                while (data.hasNext()) {
                    knowYourDayList.add(data.next())
                }
                GlobalScope.launch(Dispatchers.Main) {
                    knowYourDayDataList.postValue(knowYourDayList)
                }
            }, this::onError
        )
    }


    fun getSaveWaterInTake(email: String, deviceId: String, date: Temporal.Date, count: Int) {
        val query = if (email == "") Where.matches(
            WaterInTake.DATE.eq(date).and(WaterInTake.DEVICE_ID.eq(deviceId))
        )
        else
            Where.matches(
                WaterInTake.DATE.eq(date).and(WaterInTake.EMAIL.eq(email))
            )

        Amplify.DataStore.query(
            WaterInTake::class.java, query, { data ->
                if (data.hasNext()) {
                    val d = data.next().copyOfBuilder().count(count).build()
                    Amplify.DataStore.save(d, {
                        GlobalScope.launch(Dispatchers.Main) {
                            getWaterIntake(email, deviceId)
                        }
                    }, this::onError)
                } else {
                    val waterIntakeItem =
                        WaterInTake.Builder().date(date).count(count).email(email)
                            .deviceId(deviceId)
                            .build()
                    Amplify.DataStore.save(waterIntakeItem, {
                        GlobalScope.launch(Dispatchers.Main) {
                            getWaterIntake(email, deviceId)
                        }
                    }, this::onError)
                }

            }, this::onError
        )
    }


    fun saveRateYourDay(
        email: String,
        deviceId: String,
        date: Temporal.Date,
        healthCount: Int,
        productivityCount: Int,
        moodCount: Int
    ) {
        val query = if (email == "")
            Where.matches(
                KnowYourDay.DATE.eq(date)
                    .and(KnowYourDay.DEVICE_ID.eq(deviceId))
            )
        else
            Where.matches(
                KnowYourDay.DATE.eq(date)
                    .and(KnowYourDay.EMAIL.eq(email))
            )
        Amplify.DataStore.query(
            KnowYourDay::class.java, query, { data ->
                if (data.hasNext()) {
                    val d = data.next().copyOfBuilder().healthCount(healthCount)
                        .productivityCount(productivityCount).moodCount(moodCount).build()
                    Amplify.DataStore.save(d, {
                        GlobalScope.launch(Dispatchers.Main) {
                            getRateYourDay(email, deviceId)
                        }
                    }, this::onError)
                } else {
                    val knowYourDayItem = KnowYourDay.Builder().date(date).healthCount(healthCount)
                        .productivityCount(productivityCount).moodCount(moodCount).email(email)
                        .deviceId(deviceId).build()
                    Amplify.DataStore.save(knowYourDayItem, {
                        GlobalScope.launch(Dispatchers.Main) {
                            getRateYourDay(email, deviceId)
                        }
                    }, this::onError)
                }

            }, this::onError
        )
    }


    fun markTaskAsDone(taskModel: TaskModel) {
        val edited = taskModel.task.copyOfBuilder().isCompleted(true)
            .updatedAt(AppUtil.getCurrentTemporalDateTime()).build()
        Amplify.DataStore.save(
            edited, {
                taskModel.task = it.item()
                isMarkedDone.postValue(taskModel)
            }, this::onError
        )
    }

    fun markTaskAsUnDone(taskModel: TaskModel) {
        val edited = taskModel.task.copyOfBuilder().isCompleted(false)
            .updatedAt(AppUtil.getCurrentTemporalDateTime()).build()
        Amplify.DataStore.save(
            edited, {
                taskModel.task = it.item()
                isMarkedUnDone.postValue(taskModel)
            }, this::onError
        )
    }

    fun fetchListGroup(email: String, deviceId: String) {
        val query = if (email == "")
            Where.matches(ListGroup.DEVICE_ID.eq(deviceId))
        else
            Where.matches(ListGroup.EMAIL.eq(email))
        Amplify.DataStore.query(ListGroup::class.java,
            query,
            { data ->
                val list = mutableListOf<ListGroup>()
                while (data.hasNext()) {
                    list.add(data.next())
                }
                listGroupDataList.postValue(list)
            },
            {
                listGroupDataList.postValue(listOf())
            })
    }


    fun fetchListGroupTask(email: String, deviceId: String, listGroupId: String) {
        val query = if (email == "")
            Tasks.DEVICE_ID.eq(deviceId).and(Tasks.LIST_GROUP_ID.eq(listGroupId))
        else
            Tasks.EMAIL.eq(email).and(Tasks.LIST_GROUP_ID.eq(listGroupId))

        Amplify.DataStore.query(Tasks::class.java, Where.matches(
            query
        ), { data ->
            val list = mutableListOf<Tasks>()
            while (data.hasNext()) {
                list.add(data.next())
            }
            listGroupTasksDataList.postValue(list)
        }, {
            listGroupTasksDataList.postValue(listOf())
        })
    }

    fun createListGroup(email: String, deviceId: String, listName: String) {
        val listGroup = ListGroup.builder().name(listName).email(email).deviceId(deviceId)
            .createdAt(AppUtil.getCurrentTemporalDateTime())
            .updatedAt(AppUtil.getCurrentTemporalDateTime()).build()
        Amplify.DataStore.save(listGroup, {
            fetchListGroup(email, deviceId)
        }, this::onError)
    }

    fun deleteList(listGroupId: String) {
        Amplify.DataStore.delete(
            ListGroup::class.java, ListGroup.ID.eq(listGroupId), {
                Amplify.DataStore.delete(Tasks::class.java, Tasks.LIST_GROUP_ID.eq(listGroupId), {
                    isListDeleted.postValue(true)
                }, this::onError)
            }, this::onError
        )
    }

    fun deleteTask(taskId: String) {
        Amplify.DataStore.delete(
            Tasks::class.java, Tasks.ID.eq(taskId), {
                isTaskDeleted.postValue(true)
            }, this::onError
        )

    }

    fun deleteAllTask(taskId: String) {
        Amplify.DataStore.delete(
            Tasks::class.java, Tasks.TASK_ID.eq(taskId), {
                isAllTaskDeleted.postValue(true)
            }, this::onError
        )
    }


    fun getTaskDetail(taskId: String) {
        Amplify.DataStore.query(
            Tasks::class.java, Where.matches(Tasks.ID.eq(taskId)), { data ->
                taskDetail.postValue(data.next())
            }, this::onError
        )
    }

    fun updateTask(
        taskModel: TaskModel2
    ) {
        val taskTime = taskModel.date
        taskTime.hours = taskModel.hour
        taskTime.minutes = taskModel.minute
        val customTime = calculateReminderTime(taskTime,taskModel)
        val task = taskModel.task
            ?.copyOfBuilder()
            ?.date(taskModel.date.dateToTemporalDate())
            ?.isTimeSelected(taskModel.isTimeSelected)
            ?.time(taskTime.dateToTemporalDateTime())
            ?.emailOrPhone(taskModel.emailOrPhone)
            ?.taskType(taskModel.taskType)
            ?.reminder(taskModel.reminder)
            ?.title(taskModel.title)
            ?.description(taskModel.description)
            ?.isImportant(taskModel.isImportant)
            ?.reminder(taskModel.reminder)
            ?.customTime(customTime.dateToTemporalDateTime())
            ?.updatedAt(AppUtil.getCurrentTemporalDateTime())
            ?.build()
        Amplify.DataStore.save(
            task!!, {
                setReminderAndNotificationIfItIsTodaySTask(it.item())
                isTaskUpdated.postValue(mutableListOf(it.item()))
            },
            this::onError
        )

    }

    fun updateAllTask(
        taskModel: TaskModel2
    ) {
        when (taskModel.repeatType) {
            RepeatType.DAY -> updateAllTasksForRepeatTypeDay(taskModel)


            RepeatType.WEEK -> updateAllTasksForRepeatTypeWeeK(taskModel)


            RepeatType.MONTH -> updateAllTasksForRepeatTypeMonth(taskModel)


            RepeatType.YEAR -> updateAllTasksForRepeatTypeYear(taskModel)


            RepeatType.NONE -> updateAllTasksForRepeatTypeNone(taskModel)

        }
    }

    fun createTask(
        taskModel: TaskModel2
    ) {
        when (taskModel.repeatType) {
            RepeatType.DAY -> createTasksForRepeatTypeDay(taskModel)


            RepeatType.WEEK -> createTasksForRepeatTypeWeeK(taskModel)


            RepeatType.MONTH -> createTasksForRepeatTypeMonth(taskModel)


            RepeatType.YEAR -> createTasksForRepeatTypeYear(taskModel)


            RepeatType.NONE -> createTasksForRepeatTypeNone(taskModel)
        }


    }

    fun getTaskListByDate(email: String, deviceId: String, date: Date) {
        val query = if (email == "")
            Where.matches(
                Tasks.DATE.eq(date.dateToTemporalDate())
                    .and(Tasks.DEVICE_ID.eq(deviceId))
            )
        else
            Where.matches(
                Tasks.DATE.eq(date.dateToTemporalDate())
                    .and(Tasks.EMAIL.eq(email))
            )
        Amplify.DataStore.query(
            Tasks::class.java, query, { data ->
                val list = mutableListOf<Tasks>()
                while (data.hasNext()) {
                    list.add(data.next())
                }
                taskListByDate.postValue(list)
            },
            this::onError
        )
    }

    fun getEventsForCalendar(email: String, deviceId: String, startDate: Date, endDate: Date) {
        val query = if (email == "")
            Where.matches(
                Tasks.DEVICE_ID.eq(deviceId).and(
                    Tasks.DATE.ge(startDate.dateToTemporalDate()).and(
                        Tasks.DATE.le(endDate.dateToTemporalDate())
                            .and(Tasks.TASK_TYPE.ne(TaskType.HOLIDAY))
                    )
                )
            )
        else
            Where.matches(
                Tasks.EMAIL.eq(email).and(
                    Tasks.DATE.ge(startDate.dateToTemporalDate()).and(
                        Tasks.DATE.le(endDate.dateToTemporalDate())
                            .and(Tasks.TASK_TYPE.ne(TaskType.HOLIDAY))
                    )
                )
            )
        Amplify.DataStore.query(
            Tasks::class.java, query, { data ->
                val list = mutableListOf<Tasks>()
                while (data.hasNext()) {
                    list.add(data.next())
                }
                eventList.postValue(list.map { it.date.toDate() })
            }, this::onError
        )
    }

    fun getHEventsForCalendar(email: String, deviceId: String, startDate: Date, endDate: Date) {
        val query = if (email == "")
            Where.matches(
                Tasks.DEVICE_ID.eq(deviceId).and(
                    Tasks.DATE.ge(startDate.dateToTemporalDate()).and(
                        Tasks.DATE.le(endDate.dateToTemporalDate())
                            .and(Tasks.TASK_TYPE.eq(TaskType.HOLIDAY))
                    )
                )
            )
        else
            Where.matches(
                Tasks.EMAIL.eq(email).and(
                    Tasks.DATE.ge(startDate.dateToTemporalDate()).and(
                        Tasks.DATE.le(endDate.dateToTemporalDate())
                            .and(Tasks.TASK_TYPE.eq(TaskType.HOLIDAY))
                    )
                )
            )
        Amplify.DataStore.query(
            Tasks::class.java, query, { data ->
                val list = mutableListOf<Tasks>()
                while (data.hasNext()) {
                    list.add(data.next())
                }
                hEventList.postValue(list.map { it.date.toDate() })
            }, this::onError
        )
    }

    fun getTaskList(email: String, deviceId: String) {
        val query = if (email == "")
            Where.matches(Tasks.DEVICE_ID.eq(deviceId))
        else
            Where.matches(Tasks.EMAIL.eq(email))

        Amplify.DataStore.query(
            Tasks::class.java,
            query,
            { data ->
                val list = mutableListOf<Tasks>()
                while (data.hasNext()) {
                    list.add(data.next())
                }
                taskList.postValue(list)
            },
            this::onError
        )
    }

    private fun getRepeatDays(taskDate: Date) = when (taskDate.getDayFromDate()) {
        "Mon" -> RepeatDays.MON
        "Tue" -> RepeatDays.TUE
        "Wed" -> RepeatDays.WED
        "Thu" -> RepeatDays.THU
        "Fri" -> RepeatDays.FRI
        "Sat" -> RepeatDays.SAT
        "Sun" -> RepeatDays.SUN
        else -> RepeatDays.MON
    }

    fun deleteHolidayEvents(email: String, deviceId: String) {
        Amplify.DataStore.delete(Tasks::class.java,
            Tasks.TASK_TYPE.eq(TaskType.HOLIDAY)
                .and(Tasks.EMAIL.eq(email).or(Tasks.DEVICE_ID.eq(deviceId))),
            {},
            {})
    }

    private fun updateAllTasksForRepeatTypeDay(taskModel: TaskModel2) {
        Amplify.DataStore.delete(Tasks::class.java,
            Tasks.TASK_ID.eq(taskModel.taskId)
                .and(Tasks.DATE.ge(taskModel.date.dateToTemporalDate()))
                .and(Tasks.DATE.le(taskModel.endDate.dateToTemporalDate())),
            {},
            {})
        val dateList = AppUtil.getDatesBetween(
            taskModel.date,
            taskModel.endDate,
            taskModel.repeatType,
            taskModel.repeatCount,
            taskModel.repeatDays!!
        )
        updateTaskRecursively(dateList.toMutableList(), taskModel, mutableListOf(), mutableListOf())
        Amplify.DataStore.delete(Tasks::class.java,
            Tasks.TASK_ID.eq(taskModel.taskId)
                .and(Tasks.DATE.ge(taskModel.endDate.dateToTemporalDate())),
            {},
            {})
    }

    private fun createTasksForRepeatTypeDay(taskModel: TaskModel2) {
        val dateList = AppUtil.getDatesBetween(
            taskModel.date,
            taskModel.endDate,
            taskModel.repeatType,
            taskModel.repeatCount,
            taskModel.repeatDays!!
        )
        createTaskRecursively(dateList.toMutableList(), taskModel, mutableListOf())
        Amplify.DataStore.delete(Tasks::class.java,
            Tasks.TASK_ID.eq(taskModel.taskId)
                .and(Tasks.DATE.ge(taskModel.endDate.dateToTemporalDate())),
            {},
            {})
    }

    private fun updateAllTasksForRepeatTypeWeeK(taskModel: TaskModel2) {
        Amplify.DataStore.delete(Tasks::class.java,
            Tasks.TASK_ID.eq(taskModel.taskId)
                .and(Tasks.DATE.ge(taskModel.date.dateToTemporalDate()))
                .and(Tasks.DATE.le(taskModel.endDate.dateToTemporalDate())),
            {},
            {})
        val dBtn = AppUtil.getDatesBetween(
            taskModel.date,
            taskModel.endDate,
            taskModel.repeatType,
            taskModel.repeatCount,
            taskModel.repeatDays!!
        )
        val dateList =
            dBtn.filter { taskModel.repeatDays?.contains(getRepeatDays(it)) == true || (it.date == taskModel.date.date) && (it.month == taskModel.date.month) && (it.year == taskModel.date.year) }
        val expectedTaskDeleteDate = dBtn.filter { !dateList.contains(it) }
        updateTaskRecursively(dateList.toMutableList(), taskModel, mutableListOf(), mutableListOf())
        deleteTaskRecursively(expectedTaskDeleteDate.toMutableList(), taskModel)
        Amplify.DataStore.delete(Tasks::class.java,
            Tasks.TASK_ID.eq(taskModel.taskId)
                .and(Tasks.DATE.ge(taskModel.endDate.dateToTemporalDate())),
            {},
            {})
    }

    private fun createTasksForRepeatTypeWeeK(taskModel: TaskModel2) {
        val dBtn = AppUtil.getDatesBetween(
            taskModel.date,
            taskModel.endDate,
            taskModel.repeatType,
            taskModel.repeatCount,
            taskModel.repeatDays!!
        )
        val dateList =
            dBtn.filter { taskModel.repeatDays?.contains(getRepeatDays(it)) == true || (it.date == taskModel.date.date) && (it.month == taskModel.date.month) && (it.year == taskModel.date.year) }
        val expectedTaskDeleteDate = dBtn.filter { !dateList.contains(it) }
        createTaskRecursively(dateList.toMutableList(), taskModel, mutableListOf())
        deleteTaskRecursively(expectedTaskDeleteDate.toMutableList(), taskModel)
        Amplify.DataStore.delete(Tasks::class.java,
            Tasks.TASK_ID.eq(taskModel.taskId)
                .and(Tasks.DATE.ge(taskModel.endDate.dateToTemporalDate())),
            {},
            {})
    }

    private fun updateAllTasksForRepeatTypeMonth(taskModel: TaskModel2) {
        Amplify.DataStore.delete(Tasks::class.java,
            Tasks.TASK_ID.eq(taskModel.taskId)
                .and(Tasks.DATE.ge(taskModel.date.dateToTemporalDate()))
                .and(Tasks.DATE.le(taskModel.endDate.dateToTemporalDate())),
            {},
            {})
        val dBtn = AppUtil.getDatesBetween(
            taskModel.date,
            taskModel.endDate,
            taskModel.repeatType,
            taskModel.repeatCount,
            taskModel.repeatDays!!
        )
        val dateList = dBtn.filter { it.date == taskModel.date.date }
        val expectedTaskDeleteDate = dBtn.filter { !dateList.contains(it) }
        updateTaskRecursively(dateList.toMutableList(), taskModel, mutableListOf(), mutableListOf())
        deleteTaskRecursively(expectedTaskDeleteDate.toMutableList(), taskModel)
        Amplify.DataStore.delete(Tasks::class.java,
            Tasks.TASK_ID.eq(taskModel.taskId)
                .and(Tasks.DATE.ge(taskModel.endDate.dateToTemporalDate())),
            {},
            {})
    }

    private fun createTasksForRepeatTypeMonth(taskModel: TaskModel2) {
        val dBtn = AppUtil.getDatesBetween(
            taskModel.date,
            taskModel.endDate,
            taskModel.repeatType,
            taskModel.repeatCount,
            taskModel.repeatDays!!
        )
        val dateList = dBtn.filter { it.date == taskModel.date.date }
        val expectedTaskDeleteDate = dBtn.filter { !dateList.contains(it) }
        createTaskRecursively(dateList.toMutableList(), taskModel, mutableListOf())
        deleteTaskRecursively(expectedTaskDeleteDate.toMutableList(), taskModel)
        Amplify.DataStore.delete(Tasks::class.java,
            Tasks.TASK_ID.eq(taskModel.taskId)
                .and(Tasks.DATE.ge(taskModel.endDate.dateToTemporalDate())),
            {},
            {})
    }

    private fun updateAllTasksForRepeatTypeYear(taskModel: TaskModel2) {
        Amplify.DataStore.delete(Tasks::class.java,
            Tasks.TASK_ID.eq(taskModel.taskId)
                .and(Tasks.DATE.ge(taskModel.date.dateToTemporalDate()))
                .and(Tasks.DATE.le(taskModel.endDate.dateToTemporalDate())),
            {},
            {})
        val dBtn = AppUtil.getDatesBetween(
            taskModel.date,
            taskModel.endDate,
            taskModel.repeatType,
            taskModel.repeatCount,
            taskModel.repeatDays!!
        )
        val dateList =
            dBtn.filter { it.date == taskModel.date.date && it.month == taskModel.date.month }
        val expectedTaskDeleteDate = dBtn.filter { !dateList.contains(it) }
        updateTaskRecursively(dateList.toMutableList(), taskModel, mutableListOf(), mutableListOf())
        deleteTaskRecursively(expectedTaskDeleteDate.toMutableList(), taskModel)
        Amplify.DataStore.delete(Tasks::class.java,
            Tasks.TASK_ID.eq(taskModel.taskId)
                .and(Tasks.DATE.ge(taskModel.endDate.dateToTemporalDate())),
            {},
            {})
    }

    private fun createTasksForRepeatTypeYear(taskModel: TaskModel2) {
        val dBtn = AppUtil.getDatesBetween(
            taskModel.date,
            taskModel.endDate,
            taskModel.repeatType,
            taskModel.repeatCount,
            taskModel.repeatDays!!
        )
        val dateList =
            dBtn.filter { it.date == taskModel.date.date && it.month == taskModel.date.month }
        val expectedTaskDeleteDate = dBtn.filter { !dateList.contains(it) }
        createTaskRecursively(dateList.toMutableList(), taskModel, mutableListOf())
        deleteTaskRecursively(expectedTaskDeleteDate.toMutableList(), taskModel)
        Amplify.DataStore.delete(Tasks::class.java,
            Tasks.TASK_ID.eq(taskModel.taskId)
                .and(Tasks.DATE.ge(taskModel.endDate.dateToTemporalDate())),
            {},
            {})
    }

    private fun updateAllTasksForRepeatTypeNone(taskModel: TaskModel2) {
        Amplify.DataStore.delete(Tasks::class.java,
            Tasks.TASK_ID.eq(taskModel.taskId)
                .and(Tasks.DATE.ge(taskModel.date.dateToTemporalDate()))
                .and(Tasks.DATE.le(taskModel.endDate.dateToTemporalDate())),
            {},
            {})
        val dBtn = AppUtil.getDatesBetween(
            taskModel.date,
            taskModel.endDate,
            taskModel.repeatType,
            taskModel.repeatCount,
            taskModel.repeatDays!!
        )
        val dateList =
            dBtn.filter { it.date == taskModel.date.date && it.month == taskModel.date.month && it.year == taskModel.date.year }
        val expectedTaskDeleteDate = dBtn.filter { !dateList.contains(it) }
        updateTaskRecursively(dateList.toMutableList(), taskModel, mutableListOf(), mutableListOf())
        deleteTaskRecursively(expectedTaskDeleteDate.toMutableList(), taskModel)
        Amplify.DataStore.delete(Tasks::class.java,
            Tasks.TASK_ID.eq(taskModel.taskId)
                .and(Tasks.DATE.ge(taskModel.endDate.dateToTemporalDate())),
            {},
            {})
    }

    private fun createTasksForRepeatTypeNone(taskModel: TaskModel2) {
        val dBtn = AppUtil.getDatesBetween(
            taskModel.date,
            taskModel.endDate,
            taskModel.repeatType,
            taskModel.repeatCount,
            taskModel.repeatDays!!
        )
        val dateList =
            dBtn.filter { it.date == taskModel.date.date && it.month == taskModel.date.month && it.year == taskModel.date.year }
        val expectedTaskDeleteDate = dBtn.filter { !dateList.contains(it) }
        createTaskRecursively(dateList.toMutableList(), taskModel, mutableListOf())
        deleteTaskRecursively(expectedTaskDeleteDate.toMutableList(), taskModel)
        Amplify.DataStore.delete(Tasks::class.java,
            Tasks.TASK_ID.eq(taskModel.taskId)
                .and(Tasks.DATE.ge(taskModel.endDate.dateToTemporalDate())),
            {},
            {})
    }

    fun updateTaskRecursively(
        dateList: MutableList<Date>,
        taskModel: TaskModel2,
        createdTaskList: MutableList<Tasks>,
        updatedTaskList: MutableList<Tasks>
    ) {
        if (createdTaskList.any { it.date.toDate().month == taskModel.selectedDate.month + 1 }) {
            isTaskCreated.postValue(createdTaskList)
        }
        if (updatedTaskList.any { it.date.toDate().month == taskModel.selectedDate.month + 1 }) {
            isTaskUpdated.postValue(createdTaskList)
        }
        if (dateList.isNotEmpty()) {
            val taskDate = dateList[0]
            val taskTime = taskDate
            taskTime.hours = taskModel.hour
            taskTime.minutes = taskModel.minute
            val customTime = calculateReminderTime(taskTime,taskModel)
            Amplify.DataStore.query(
                Tasks::class.java, Where.matches(
                    Tasks.TASK_ID.eq(taskModel.taskId)
                        .and(Tasks.DATE.eq(taskDate.dateToTemporalDate()))
                ), { data ->
                    if (data.hasNext()) {
                        val task = data.next().copyOfBuilder()
                            .title(taskModel.title)
                            .description(taskModel.description)
                            .date(taskDate.dateToTemporalDate())
                            .isTimeSelected(taskModel.isTimeSelected)
                            .time(taskTime.dateToTemporalDateTime())
                            .reminder(taskModel.reminder)
                            .repeatType(taskModel.repeatType)
                            .repeatDays(taskModel.repeatDays)
                            .emailOrPhone(taskModel.emailOrPhone)
                            .taskType(taskModel.taskType)
                            .isImportant(taskModel.isImportant)
                            .repeatCount(taskModel.repeatCount)
                            .customTime(customTime.dateToTemporalDateTime())
                            .endDate(taskModel.endDate.dateToTemporalDate())
                            .updatedAt(AppUtil.getCurrentTemporalDateTime()).build()
                        Amplify.DataStore.save(
                            task, {
                                setReminderAndNotificationIfItIsTodaySTask(it.item())
                                updatedTaskList.add(task)
                                dateList.remove(dateList[0])
                                updateTaskRecursively(
                                    dateList = dateList,
                                    taskModel = taskModel,
                                    createdTaskList,
                                    updatedTaskList
                                )
                            }, this::onError
                        )
                    } else {
                        val task =
                            Tasks.builder()
                                .title(taskModel.title)
                                .taskId(taskModel.taskId)
                                .description(taskModel.description)
                                .email(taskModel.email)
                                .deviceId(taskModel.deviceId)
                                .notiRequestCode(AppUtil.generateRequestCode())
                                .date(taskDate.dateToTemporalDate())
                                .isTimeSelected(taskModel.isTimeSelected)
                                .time(taskTime.dateToTemporalDateTime())
                                .reminder(taskModel.reminder)
                                .repeatType(taskModel.repeatType)
                                .repeatDays(taskModel.repeatDays)
                                .emailOrPhone(taskModel.emailOrPhone)
                                .taskType(taskModel.taskType)
                                .listGroupId(taskModel.listGroupId)
                                .isImportant(taskModel.isImportant)
                                .isCompleted(false)
                                .endDate(taskModel.endDate?.dateToTemporalDate())
                                .repeatCount(taskModel.repeatCount)
                                .customTime(customTime.dateToTemporalDateTime())
                                .createdAt(AppUtil.getCurrentTemporalDateTime())
                                .updatedAt(AppUtil.getCurrentTemporalDateTime())
                                .build()
                        Amplify.DataStore.save(task, {
                            setReminderAndNotificationIfItIsTodaySTask(it.item())
                            createdTaskList.add(task)
                            dateList.remove(dateList[0])
                            updateTaskRecursively(
                                dateList = dateList,
                                taskModel = taskModel,
                                createdTaskList,
                                updatedTaskList
                            )
                        }, this::onError)
                    }
                }, this::onError
            )


        } else {
            isTaskCreated.postValue(createdTaskList)
            isTaskUpdated.postValue(updatedTaskList)
        }
    }

    fun createTaskRecursively(
        dateList: MutableList<Date>,
        taskModel: TaskModel2,
        createdTaskList: MutableList<Tasks>
    ) {
        /** wait till the selected month to send call back **/
        if (createdTaskList.any { it.date.toDate().month == taskModel.selectedDate.month + 1 }) {
            isTaskCreated.postValue(createdTaskList)
        }

        if (dateList.isNotEmpty()) {
            val taskDate = dateList[0]
            val taskTime = taskDate
            taskTime.hours = taskModel.hour
            taskTime.minutes = taskModel.minute
            val day = taskDate
            if(taskModel.reminder==ReminderEnum.CUSTOM){
                if(taskDate.date==taskModel.date.date && taskDate.month==taskModel.date.month && taskDate.year==taskModel.date.year ){

                }else{
                    taskModel.customTime=taskModel.customTime.addDays(1)
                }
            }

            val customTime = calculateReminderTime(taskTime,taskModel)

            val task =
                Tasks.builder()
                    .title(taskModel.title)
                    .taskId(taskModel.taskId)
                    .description(taskModel.description)
                    .email(taskModel.email)
                    .deviceId(taskModel.deviceId)
                    .notiRequestCode(AppUtil.generateRequestCode())
                    .date(taskDate.dateToTemporalDate())
                    .isTimeSelected(taskModel.isTimeSelected)
                    .time(taskTime.dateToTemporalDateTime())
                    .reminder(taskModel.reminder)
                    .repeatType(taskModel.repeatType)
                    .repeatDays(taskModel.repeatDays)
                    .emailOrPhone(taskModel.emailOrPhone)
                    .taskType(taskModel.taskType)
                    .listGroupId(taskModel.listGroupId)
                    .isImportant(taskModel.isImportant)
                    .isCompleted(false)
                    .endDate(taskModel.endDate?.dateToTemporalDate())
                    .repeatCount(taskModel.repeatCount)
                    .countryCode(taskModel.countryCode)
                    .customTime(customTime.dateToTemporalDateTime())
                    .createdAt(AppUtil.getCurrentTemporalDateTime())
                    .updatedAt(AppUtil.getCurrentTemporalDateTime())
                    .build()
            Amplify.DataStore.save(task, {
                setReminderAndNotificationIfItIsTodaySTask(it.item())
                createdTaskList.add(it.item())
                dateList.remove(dateList[0])
                createTaskRecursively(
                    dateList = dateList,
                    taskModel = taskModel,
                    createdTaskList
                )
            }, this::onError)
        } else {
            isTaskCreated.postValue(createdTaskList)
        }
    }


    fun deleteTaskRecursively(dateList: MutableList<Date>, taskModel: TaskModel2) {
        if (dateList.isNotEmpty()) {
            Amplify.DataStore.delete(Tasks::class.java,
                Tasks.TASK_ID.eq(taskModel.taskId)
                    .and(Tasks.DATE.eq(dateList[0].dateToTemporalDate())),
                {
                    dateList.remove(dateList[0])
                    deleteTaskRecursively(dateList, taskModel)
                },
                {
                    dateList.remove(dateList[0])
                    deleteTaskRecursively(dateList, taskModel)
                })
        }
    }


    private fun isTaskForToday(task: Tasks): Boolean {
        val todaysDate = Date()
        return task.date.toDate().date == todaysDate.date && task.date.toDate().month == todaysDate.month && task.date.toDate().year == todaysDate.year
    }


    private fun setReminderAndNotificationIfItIsTodaySTask(task: Tasks) {
        if (isTaskForToday(task)) {
            if (task.reminder != ReminderEnum.NONE && task.isQualifiedForDisplayingReminder()) {
                App.getInstance()?.scheduleTaskReminder(task)
            }
            if (task.isQualifiedForDisplayingNotification()) {
                App.getInstance()?.scheduleNotification(task)
            }
        }
    }

    private fun Tasks.isQualifiedForDisplayingNotification(): Boolean {
        return Date().before(Scheduler.getNotificationTime(this))
    }

    private fun Tasks.isQualifiedForDisplayingReminder(): Boolean {
        return Date().before(Scheduler.getReminderTime(this))
    }


    fun syncDataFromCloud(){

    }

    fun syncLocalDataWithCloud(){

    }


    fun calculateReminderTime(taskTime:Date,taskModel: TaskModel2): Date {
        val calendar = java.util.Calendar.getInstance()
        calendar.time = taskTime
        return when (taskModel.reminder) {
            ReminderEnum.MIN05 -> {
                calendar.add(Calendar.MINUTE, -5)
                calendar.time
            }
            ReminderEnum.MIN10 -> {

                calendar.add(java.util.Calendar.MINUTE, -10)
                calendar.time
            }
            ReminderEnum.MIN30 -> {
                calendar.add(Calendar.MINUTE, -30)
                calendar.time
            }
            ReminderEnum.MIN60 -> {
                calendar.add(Calendar.HOUR, -1)
                calendar.time
            }
            ReminderEnum.CUSTOM -> {
                calendar.time=Date(taskTime.time-getTimeDifference(taskModel.customTime,taskTime))
                Log.d("calculateReminderTime", "calculateReminderTime: $taskTime, remindertime ${calendar.time}")
                calendar.time
            }
            else -> {
                calendar.time
            }
        }
    }
    private fun getTimeDifference(startDateTime:Date, endDateTime: Date): Long {

        val startCalendar = Calendar.getInstance()
            startCalendar.time=startDateTime
        val endCalendar = Calendar.getInstance()
        endCalendar.time=endDateTime
        val startTimeInMillis = startCalendar.timeInMillis
        val endTimeInMillis = endCalendar.timeInMillis
        return endTimeInMillis - startTimeInMillis
    }

}
fun Date.addDays(days: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.DATE, days)
    return calendar.time
}