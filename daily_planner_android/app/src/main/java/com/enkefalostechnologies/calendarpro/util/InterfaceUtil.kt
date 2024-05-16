package com.enkefalostechnologies.calendarpro.util

import android.app.Dialog
import com.amplifyframework.datastore.generated.model.ReminderEnum
import com.amplifyframework.datastore.generated.model.RepeatDays
import com.amplifyframework.datastore.generated.model.RepeatType
import com.amplifyframework.datastore.generated.model.SubscriptionType
import com.amplifyframework.datastore.generated.model.TaskType
import com.amplifyframework.datastore.generated.model.Tasks
import com.enkefalostechnologies.calendarpro.model.LocalContact
import com.android.billingclient.api.Purchase
import com.enkefalostechnologies.calendarpro.model.CountryModel
import com.enkefalostechnologies.calendarpro.model.DateTimePickerModel
import java.util.Date

interface WaterIntakeDialogListener {
    fun onDone(date:com.amplifyframework.core.model.temporal.Temporal.Date,count:Int)
}
interface KnowYourDialogListener {
    fun onDone(dialog:Dialog,date: com.amplifyframework.core.model.temporal.Temporal.Date,healthCount:Int,productivityCount:Int,moodCount:Int)
}
interface NewListCreateListener{
    fun onCreateClicked(dialog: Dialog, listName: String)
}
interface NewTaskBottomSheetListener{
    fun onDoneClicked(
        dialog:Any,
        taskType:TaskType,
        title: String,
        emailPhone:String,
        description: String,
        date: Date,
        isTimeSelected:Boolean,
        hours: Int,
        minute: Int,
        reminder: ReminderEnum?,
        repeatType: RepeatType,
        repeatDays: List<RepeatDays>,
        isImportant: Boolean,
        customDate:Date,
        endDate: Date?,
        rc:Int
    )
}

interface SubscriptionBottomSheetListener {
    fun onPurchaseSuccess(
        purchases: Purchase,
        validUpTo: Date,
        subscriptionType: SubscriptionType
    )

    fun onClosed()
    fun onError(error: String)
}

interface repeatDialogListener{
    fun onDoneClicked(repeatType: RepeatType, repeatDays: List<RepeatDays>, endDate:Date?,repeatCount:Int,dialog: Dialog)
}
interface ReminderDialogListener{
    fun onDoneClicked(dialog:Dialog,reminder: ReminderEnum?,reminderTime: Date)
}
interface DateTimePickerListener{
    fun onDateTimeSelected(dateTimePickerModel: DateTimePickerModel)
    //fun onTimeSelected(hours: Int,minute: Int)
}
interface ListAdapterListener{
    fun onItemClicked(listId: String, name: String)
}
interface CountryAdapterListener{
    fun onItemClicked(item:CountryModel)
}
interface WaterIntakeDateAdapterListener{
    fun onItemClicked(date:com.amplifyframework.core.model.temporal.Temporal.Date)
}

interface TaskAdapterItemListener{
    fun onItemClicked(task:Tasks)
    fun onTaskCheck(task:Tasks,position:Int)
    fun onReminderClicked(task:Tasks,position:Int)
    fun onRepeatClicked(task:Tasks,position:Int)
    fun onMarkedDone(task:Tasks){}
    fun onMarkedUnDone(task:Tasks){}
    fun setEvents(task: Tasks){}
}

interface RepeatAdapterListener{
    fun onItemClicked(item:RepeatDays)
}

interface PhoneBookAdapterItemListener {
    fun onClick(contact: LocalContact)
}

interface MsgAlertDialogListener{
    fun onDoneClicked()
}

interface DatePickerListener{
    fun onDateSelected(date:Date)
}

interface CalenderListener{
    fun onDateSelected(date:Date?)

}