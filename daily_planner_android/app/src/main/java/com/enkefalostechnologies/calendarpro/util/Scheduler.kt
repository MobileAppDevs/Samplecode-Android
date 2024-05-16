package com.enkefalostechnologies.calendarpro.util

import android.content.Context
import android.util.Log
import com.amplifyframework.datastore.generated.model.ReminderEnum
import com.amplifyframework.datastore.generated.model.Tasks
import com.carterchen247.alarmscheduler.AlarmScheduler
import com.carterchen247.alarmscheduler.model.AlarmConfig
import com.carterchen247.alarmscheduler.model.DataPayload
import com.carterchen247.alarmscheduler.model.ScheduleResult
import com.enkefalostechnologies.calendarpro.broadcast_receiver.TaskAlarm
import com.enkefalostechnologies.calendarpro.util.Scheduler.scheduleTaskReminder
import java.util.Calendar
import java.util.Date


object Scheduler {


    fun Context.scheduleTaskReminder(task: Tasks) {

        val payload = mutableMapOf<String, String>()
        payload[TaskAlarm.KEY_CALL_BACK] = TaskAlarm.CALLBACK_TASK
        payload[TaskAlarm.KEY_TASK_ID] = task.id
        payload[TaskAlarm.KEY_PURPOSE] = TaskAlarm.PURPOSE_REMINDER
        val alarmId=if(task.reminderRequestCode!=null)task.reminderRequestCode else AppUtil.generateRequestCode()
        val config = AlarmConfig(
            getReminderTime(task)?.time?:Date().time,
            TaskAlarm.TYPE
        ) {
            id(alarmId)
            dataPayload(DataPayload(payload))
        }
        AlarmScheduler.schedule(config) {result->
            when (result) {
                is ScheduleResult.Success -> {
                    Log.d("daily_task", "alarmId=${task.notiRequestCode} title=${task.title}  Reminder Scheduled At ${Date(getReminderTime(task)?.time?:Date().time)}")
                    if(task.reminderRequestCode==null){
                        AmplifyDataModelUtil(this).updateReminderRequestCode(task,alarmId)
                    }
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

    fun Context.scheduleNotification(task: Tasks) {
        val payload = mutableMapOf<String, String>()
        payload[TaskAlarm.KEY_CALL_BACK] = TaskAlarm.CALLBACK_TASK
        payload[TaskAlarm.KEY_TASK_ID] = task.id
        payload[TaskAlarm.KEY_PURPOSE] = TaskAlarm.PURPOSE_NOTIFICATION
        val alarmId=if(task.notiRequestCode!=null)task.notiRequestCode else AppUtil.generateRequestCode()
        val config = AlarmConfig(
            getNotificationTime(task)?.time?:Date().time,
            TaskAlarm.TYPE
        ) {
            id(alarmId)
            dataPayload(DataPayload(payload))
        }
        AlarmScheduler.schedule(config) {result->
            when (result) {
                is ScheduleResult.Success -> {
                    Log.d("daily_task", "alarmId=${task.notiRequestCode} title=${task.title}   Notification Scheduled At ${Date(getNotificationTime(task)?.time?:Date().time)}")
                    if(task.notiRequestCode==null){
                        AmplifyDataModelUtil(this).updateNotificationRequestCode(task,alarmId)
                    }
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

     fun getReminderTime(task:Tasks): Date? {
        return when (task.reminder) {
            ReminderEnum.MIN05 -> {
                val calendar = Calendar.getInstance()
                calendar.time = task.time.toDate()
                calendar.add(Calendar.MINUTE, -5)
                calendar.time
            }
            ReminderEnum.MIN10 -> {
                val calendar = java.util.Calendar.getInstance()
                calendar.time = task.time.toDate()
                calendar.add(java.util.Calendar.MINUTE, -10)
                calendar.time
            }
            ReminderEnum.MIN30 -> {
                val calendar = Calendar.getInstance()
                calendar.time = task.time.toDate()
                calendar.add(Calendar.MINUTE, -30)
                calendar.time
            }
            ReminderEnum.MIN60 -> {
                val calendar = Calendar.getInstance()
                calendar.time = task.time.toDate()
                calendar.add(Calendar.HOUR, -1)
                calendar.time
            }
            ReminderEnum.CUSTOM -> {
                val calendar = Calendar.getInstance()
                calendar.time = task.customTime.toDate()
                calendar.time
            }
            else -> {
                val calendar = Calendar.getInstance()
                calendar.time = task.time.toDate()
                calendar.time
            }
        }
    }
    fun getNotificationTime(task:Tasks): Date? {
       val calendar=Calendar.getInstance()
        calendar.time=task.time.toDate()
        calendar.add(Calendar.HOUR,-2)
        return calendar.time
    }

}