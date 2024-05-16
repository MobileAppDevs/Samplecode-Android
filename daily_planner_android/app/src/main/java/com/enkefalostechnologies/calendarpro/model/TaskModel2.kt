package com.enkefalostechnologies.calendarpro.model

import com.amplifyframework.datastore.generated.model.ReminderEnum
import com.amplifyframework.datastore.generated.model.RepeatDays
import com.amplifyframework.datastore.generated.model.RepeatType
import com.amplifyframework.datastore.generated.model.TaskType
import com.amplifyframework.datastore.generated.model.Tasks
import org.joda.time.Minutes
import java.util.Date

data class TaskModel2 (
    var id:String,
    var title:String,
    var email:String,
    var emailOrPhone:String,
    var description:String,
    var date: Date,
    var isTimeSelected:Boolean,
    var hour:Int,
    var minute:Int,
    var reminder:ReminderEnum,
    var repeatType:RepeatType,
    var isImportant:Boolean,
    var createdAt:Date?=Date(),
    var repeatDays:List<RepeatDays>?= mutableListOf(),
    var isCompleted:Boolean?=false,
    var taskType:TaskType,
    var listGroupId:String,
    var customTime:Date,
    var repeatCount:Int,
    var endDate:Date,
    var notiRequestCode:Int?=0,
    var notificationVisibilityCount:Int?=0,
    var deviceId:String,
    var taskId:String,
    var singleOccurrance:Boolean,
    var countryCode:String?="",
     var task:Tasks?,
     var selectedDate:Date
    )