package com.enkefalostechnologies.calendarpro

import android.R.attr.rating
import android.util.Log
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.predicate.QueryPredicates
import com.amplifyframework.datastore.AWSDataStorePlugin
import com.amplifyframework.datastore.DataStoreConfiguration
import com.amplifyframework.datastore.generated.model.KnowYourDay
import com.amplifyframework.datastore.generated.model.ListGroup
import com.amplifyframework.datastore.generated.model.ReminderEnum
import com.amplifyframework.datastore.generated.model.Tasks
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.WaterInTake
import com.amplifyframework.storage.s3.AWSS3StoragePlugin
import com.calldorado.Calldorado
import com.calldorado.util.DeviceUtil
import com.carterchen247.alarmscheduler.AlarmScheduler
import com.carterchen247.alarmscheduler.model.AlarmConfig
import com.carterchen247.alarmscheduler.model.DataPayload
import com.carterchen247.alarmscheduler.model.ScheduleResult
import com.carterchen247.alarmscheduler.task.AlarmTask
import com.carterchen247.alarmscheduler.task.AlarmTaskFactory
import com.enkefalostechnologies.calendarpro.broadcast_receiver.TaskAlarm
import com.enkefalostechnologies.calendarpro.constant.StorageConstant
import com.enkefalostechnologies.calendarpro.util.AmplifyDataModelUtil
import com.enkefalostechnologies.calendarpro.util.AppUtil.getAndroidId
import com.enkefalostechnologies.calendarpro.util.PreferenceHandler
import com.enkefalostechnologies.calendarpro.util.Scheduler
import com.enkefalostechnologies.calendarpro.util.Scheduler.scheduleNotification
import com.enkefalostechnologies.calendarpro.util.Scheduler.scheduleTaskReminder
import com.google.android.gms.ads.MobileAds
import java.util.Calendar
import java.util.Date


class App : androidx.multidex.MultiDexApplication() {
    lateinit var preferenceManager: PreferenceHandler
    override fun onCreate() {
        super.onCreate()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S_V2) {
            setTheme(R.style.SplashTheme)
        }

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            Calldorado.start(this)
        }
        preferenceManager = PreferenceHandler(this)
        MobileAds.initialize(this)
        Amplify.addPlugin(AWSCognitoAuthPlugin())
        Amplify.addPlugin(AWSDataStorePlugin())
        Amplify.addPlugin(AWSApiPlugin())
        Amplify.addPlugin(AWSS3StoragePlugin())
        AlarmScheduler.setAlarmTaskFactory(object : AlarmTaskFactory {
            override fun createAlarmTask(alarmType: Int): AlarmTask {
                return TaskAlarm(this@App)
            }
        })
        Amplify.addPlugin<AWSDataStorePlugin>(AWSDataStorePlugin.builder().dataStoreConfiguration(
            DataStoreConfiguration.builder()
                .syncExpression(User::class.java) {
                        QueryPredicates.all()
                }
                .build())
            .build())
        Amplify.configure(applicationContext)
        Amplify.DataStore.start({},{})
        if(!isTodaySTaskScheduled()) {
            setFirstSchedulerAlarm()
            fetchTodaySTaskAndSchedule()
        }

//        Amplify.Auth.fetchAuthSession(
//            { result -> PreferenceHandler(this).writeBoolean(StorageConstant.IS_USER_LOGGED_IN,result.isSignedIn) },
//            { error ->PreferenceHandler(this).writeBoolean(StorageConstant.IS_USER_LOGGED_IN,false) }
//        )
    }

    init {
        instances = this
    }

    companion object {
        var instances: App? = null
        fun getInstance(): App? {
            return instances
        }
    }


    private fun setFirstSchedulerAlarm() {
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 50)
            set(Calendar.SECOND, 0)
        }

        val payload = mutableMapOf<String, String>()
        payload[TaskAlarm.KEY_CALL_BACK] = TaskAlarm.CALLBACK_DAILY_SCHEDULER

        val config = AlarmConfig(
            calendar.time.time,
            TaskAlarm.TYPE
        ) {
            dataPayload(DataPayload(payload))
        }
        AlarmScheduler.schedule(config) { result ->
            when (result) {
                is ScheduleResult.Success -> {
                    Log.d("daily_scheduler", "Scheduled At ${Date(calendar.time.time)}")
                    fetchNextDaySTaskAndSchedule()
                }

                is ScheduleResult.Failure -> {
                    when (result) {
                        ScheduleResult.Failure.CannotScheduleExactAlarm -> {

                        }

                        is ScheduleResult.Failure.Error -> {

                        }
                    }
                }
            }
        }


    }

    private fun fetchTodaySTaskAndSchedule() {
        Log.d(
            "daily_scheduler",
            "==========================================fetchTodaySTaskAndSchedule: ==============================================="
        )
        AmplifyDataModelUtil(this).fetchTodaySTask(getUserEmail(), getAndroidId(), {
            while (it.hasNext()) {
                val task = it.next()
                Log.d(
                    "nextDayTask", "today  date=${task.date.toDate()}," +
                            "reminderQualified=${task.isQualifiedForDisplayingReminder()}, " +
                            "notificationQualified=${task.isQualifiedForDisplayingReminder()}," +
                            "notiCode=${task.notiRequestCode}, reminderCode= "
                )
                if (task.isQualifiedForDisplayingNotification()) {
                    scheduleNotification(task)
                }
                if (task.isQualifiedForDisplayingReminder()) {
                    scheduleTaskReminder(task)
                }

            }
            setIsTodaySTaskScheduled(true)
        }, {})
    }

    private fun fetchNextDaySTaskAndSchedule() {
        Log.d(
            "daily_scheduler",
            "==========================================fetchNextDaySTaskAndSchedule: ==============================================="
        )
        AmplifyDataModelUtil(this).fetchNextDaysTask(getUserEmail(),getAndroidId(), {
            while (it.hasNext()) {
                val task = it.next()
                Log.d(
                    "nextDayTask", "next  date=${task.date.toDate()}," +
                            "reminderQualified=${task.isQualifiedForDisplayingReminder()}, " +
                            "notificationQualified=${task.isQualifiedForDisplayingReminder()}," +
                            "notiCode=${task.notiRequestCode}, reminderCode= "
                )
                if (task.reminder != ReminderEnum.NONE) {
                    scheduleTaskReminder(task)
                }
                scheduleNotification(task)
            }
            setIsTodaySTaskScheduled(true)
        }, {})
    }

    fun Tasks.isQualifiedForDisplayingNotification(): Boolean {
        return Date().before(Scheduler.getNotificationTime(this))
    }

    fun Tasks.isQualifiedForDisplayingReminder(): Boolean {
        return Date().before(Scheduler.getReminderTime(this))
    }

    fun deviceId(): String = if(isUserLoggedIn()) "" else getAndroidId()

    fun isUserLoggedIn() = preferenceManager.readBoolean(StorageConstant.IS_USER_LOGGED_IN, false)
    fun isTodaySTaskScheduled() = preferenceManager.readBoolean(StorageConstant.IS_TODAY_S_TASK_SCHEDULED, false)
    fun setIsTodaySTaskScheduled(value:Boolean) = preferenceManager.writeBoolean(StorageConstant.IS_TODAY_S_TASK_SCHEDULED, value)
    fun getUserEmail(): String = if(isUserLoggedIn()) preferenceManager.readString(StorageConstant.USER_EMAIL, "") else ""
}