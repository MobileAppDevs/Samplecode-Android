package com.enkefalostechnologies.calendarpro.broadcast_receiver

import android.content.Context
import android.util.Log
import com.amplifyframework.datastore.generated.model.ReminderEnum
import com.amplifyframework.datastore.generated.model.RepeatDays
import com.amplifyframework.datastore.generated.model.RepeatType
import com.amplifyframework.datastore.generated.model.Tasks
import com.calldorado.util.DeviceUtil.getAndroidId
import com.carterchen247.alarmscheduler.AlarmScheduler
import com.carterchen247.alarmscheduler.model.AlarmConfig
import com.carterchen247.alarmscheduler.model.DataPayload
import com.carterchen247.alarmscheduler.model.ScheduleResult
import com.carterchen247.alarmscheduler.task.AlarmTask
import com.enkefalostechnologies.calendarpro.App
import com.enkefalostechnologies.calendarpro.constant.StorageConstant
import com.enkefalostechnologies.calendarpro.util.AmplifyDataModelUtil
import com.enkefalostechnologies.calendarpro.util.AppUtil.getDayFromDate
import com.enkefalostechnologies.calendarpro.util.AppUtil.showPushNotification
import com.enkefalostechnologies.calendarpro.util.Extension.calculateReminderTime
import com.enkefalostechnologies.calendarpro.util.Extension.getNextDayFromDate
import com.enkefalostechnologies.calendarpro.util.PreferenceHandler
import com.enkefalostechnologies.calendarpro.util.Scheduler
import com.enkefalostechnologies.calendarpro.util.Scheduler.scheduleNotification
import com.enkefalostechnologies.calendarpro.util.Scheduler.scheduleTaskReminder
import java.util.Calendar
import java.util.Date

class TaskAlarm(var context: Context) : AlarmTask {
    companion object {
        const val TYPE = 1
        const val KEY_TASK_ID = "task_id"
        const val KEY_CALL_BACK = "call_back"
        const val KEY_PURPOSE = "purpose"
        const val CALLBACK_DAILY_SCHEDULER = "daily_scheduler"
        const val CALLBACK_TASK = "task"
        const val PURPOSE_NOTIFICATION = "Notification"
        const val PURPOSE_REMINDER = "Reminder"
    }

    override fun onAlarmFires(alarmId: Int, dataPayload: DataPayload) {
        Log.d("daily_scheduler", "call back received $alarmId, $dataPayload")
        val isNotificationOn =
            PreferenceHandler(context).readBoolean(StorageConstant.IS_NOTIFICATION_ON, true)
        val isReminderOn =
            PreferenceHandler(context).readBoolean(StorageConstant.IS_REMINDER_ON, true)
        when (dataPayload.dataMap[KEY_CALL_BACK].toString()) {
            CALLBACK_TASK -> {
                val taskId = dataPayload.dataMap[KEY_TASK_ID].toString()
                val purpose = dataPayload.dataMap[KEY_PURPOSE].toString()
                AmplifyDataModelUtil(context).fetchTaskById(taskId, {
                    if (it.hasNext()) {
                        val task = it.next()
                        if (purpose == "Notification" && isNotificationOn) {
                            context.showPushNotification(
                                "You have a task for today....",
                                task.title
                            )
                        }
                        if (purpose == "Reminder" && isReminderOn) {
                            context.showPushNotification(
                                "your task is pending...",
                                task.title
                            )
                        }
                    }
                }, {})
            }

            CALLBACK_DAILY_SCHEDULER -> {
                setNextScheduler()
            }

            else -> {
                ""
            }
        }


    }

    private fun getRepeatDays() = when (Date().getDayFromDate()) {
        "Mon" -> RepeatDays.MON
        "Tue" -> RepeatDays.TUE
        "Wed" -> RepeatDays.WED
        "Thu" -> RepeatDays.THU
        "Fri" -> RepeatDays.FRI
        "Sat" -> RepeatDays.SAT
        "Sun" -> RepeatDays.SUN
        else -> RepeatDays.MON
    }

    private fun Date.isValidatedTimeToShowReminder(reminderTime: Date/*, taskTime: Date*/): Boolean {
        val currentDateTime = this
        return (currentDateTime.time - reminderTime.time <= 2 || currentDateTime.time - reminderTime.time >= -2)
    }

    private fun setNextScheduler() {
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 11)
            set(Calendar.MINUTE, 50)
            set(Calendar.SECOND, 0)
        }

        val payload = mutableMapOf<String, String>()
        payload[KEY_CALL_BACK] = TaskAlarm.CALLBACK_DAILY_SCHEDULER

        val config = AlarmConfig(
            calendar.time.getNextDayFromDate().time,
            TYPE
        ) {
            dataPayload(DataPayload(payload))
        }
        AlarmScheduler.schedule(config) { result ->
            when (result) {
                is ScheduleResult.Success -> {
                    Log.d(
                        "daily_scheduler",
                        "Scheduled At ${Date(calendar.time.getNextDayFromDate().time)}"
                    )
                    fetchNextDaySTaskAndSchedule()
                }

                is ScheduleResult.Failure.Error -> {
                    // handle error
                    Log.d("daily_scheduler", "error nextDayScheduler: ")
                }

                else -> {}
            }
        }
    }

    private fun fetchNextDaySTaskAndSchedule() {
        val email = PreferenceHandler(context).readString(StorageConstant.USER_EMAIL, "")
        AmplifyDataModelUtil(context).fetchTodaySTask(email, getAndroidId(), {
            while (it.hasNext()) {
                val task = it.next()
                if(task.reminder!= ReminderEnum.NONE){
                    App.getInstance()?.scheduleTaskReminder(task)
                }
                App.getInstance()?.scheduleNotification(task)
            }
        }, {})
    }



}