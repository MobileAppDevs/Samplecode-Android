package com.enkefalostechnologies.calendarpro.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.amplifyframework.datastore.DataStoreException
import com.amplifyframework.datastore.generated.model.ReminderEnum
import com.amplifyframework.datastore.generated.model.RepeatDays
import com.amplifyframework.datastore.generated.model.RepeatType
import com.amplifyframework.datastore.generated.model.SubscriptionStatus
import com.amplifyframework.datastore.generated.model.TaskType
import com.amplifyframework.datastore.generated.model.Tasks
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.constant.Constants
import com.enkefalostechnologies.calendarpro.databinding.FragmentTaskBinding
import com.enkefalostechnologies.calendarpro.databinding.ReminderDialogBinding
import com.enkefalostechnologies.calendarpro.databinding.RepeatDialogBinding
import com.enkefalostechnologies.calendarpro.model.TaskModel
import com.enkefalostechnologies.calendarpro.model.TaskModel2
import com.enkefalostechnologies.calendarpro.ui.adapter.TaskAdapter
import com.enkefalostechnologies.calendarpro.ui.base.BaseFragment
import com.enkefalostechnologies.calendarpro.ui.bottomSheet.NewTaskBottomSheet
import com.enkefalostechnologies.calendarpro.ui.bottomSheet.NewTaskHolidayBottomSheet
import com.enkefalostechnologies.calendarpro.ui.bottomSheet.OptionListener
import com.enkefalostechnologies.calendarpro.ui.bottomSheet.UpdateOptionBottomSheet
import com.enkefalostechnologies.calendarpro.ui.updateTaskActivity.UpdateTaskActivity
import com.enkefalostechnologies.calendarpro.ui.viewModel.fragment.TaskFragmentViewModel
import com.enkefalostechnologies.calendarpro.ui.viewTaskScreen.ViewTaskActivity
import com.enkefalostechnologies.calendarpro.util.AmplifyUtil.handleExceptions
import com.enkefalostechnologies.calendarpro.util.AppUtil
import com.enkefalostechnologies.calendarpro.util.AppUtil.getDateStringFromDate
import com.enkefalostechnologies.calendarpro.util.AppUtil.getDayFromDate
import com.enkefalostechnologies.calendarpro.util.AppUtil.getMonthFromDate
import com.enkefalostechnologies.calendarpro.util.AppUtil.isLeapYear
import com.enkefalostechnologies.calendarpro.util.AppUtil.isNotificationPermissionAvailable
import com.enkefalostechnologies.calendarpro.util.AppUtil.loadBannerAd
import com.enkefalostechnologies.calendarpro.util.AppUtil.requestPermission
import com.enkefalostechnologies.calendarpro.util.AppUtil.showToast
import com.enkefalostechnologies.calendarpro.util.CalenderUtil
import com.enkefalostechnologies.calendarpro.util.CalenderUtil.getEnabledDatesForWeekView
import com.enkefalostechnologies.calendarpro.util.DateTimeHelper.minuteBefore
import com.enkefalostechnologies.calendarpro.util.DialogUtil.reminderDialog
import com.enkefalostechnologies.calendarpro.util.DialogUtil.reminderDialogDisabled
import com.enkefalostechnologies.calendarpro.util.Extension.close
import com.enkefalostechnologies.calendarpro.util.Extension.getNextDayFromDate
import com.enkefalostechnologies.calendarpro.util.Extension.gone
import com.enkefalostechnologies.calendarpro.util.Extension.visible
import com.enkefalostechnologies.calendarpro.util.NewTaskBottomSheetListener
import com.enkefalostechnologies.calendarpro.util.ReminderDialogListener
import com.enkefalostechnologies.calendarpro.util.TaskAdapterItemListener
import com.enkefalostechnologies.calendarpro.util.dialog.repeatDialog
import com.enkefalostechnologies.calendarpro.util.dialog.repeatDialogDisabled
import com.enkefalostechnologies.calendarpro.util.repeatDialogListener
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.LoadAdError
import com.shrikanthravi.collapsiblecalendarview.data.Day
import com.shrikanthravi.collapsiblecalendarview.data.Event
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar
import java.util.Calendar
import java.util.Date
import java.util.UUID
import kotlin.math.log

class TaskFragment : BaseFragment<FragmentTaskBinding>(R.layout.fragment_task) {
    private val MY_PERMISSION_REQUEST_REMINDER = 1234
    val viewModel: TaskFragmentViewModel by viewModels { TaskFragmentViewModel.Factory }

    //    var selectedDate: Date? = Date()
    var isThisWeekOptionSelected: Boolean = false
    var isThisMonthOptionSelected: Boolean = false

    var isDateSelected:Boolean?=false

    //    var isDateSelected = false
//    var isWeekOptionSelected = false
//    var ismonthOptionSelected = false
    var currentYear: Int = 0
    var currentMonth: Int = 0
    var weeKIndex: Int = 0
    var progressCount = 0
    var startDateForCalendar: Date? = null
    var endDateForCalendar: Date? = null
    var eventList = mutableListOf<Date>()
    var hEventList = mutableListOf<Date>()
    var taskList = mutableListOf<Tasks>()
    var showDate = true
    var filter: Int = 0 //0->custom,1->this week, 2-> this month
    var pendingTask = mutableListOf<Tasks>()
    var completedTask = mutableListOf<Tasks>()
    var pendingTaskAdapter = TaskAdapter(pendingTask, object : TaskAdapterItemListener {
        override fun onItemClicked(task: Tasks) {
            if(task.taskType==TaskType.HOLIDAY){
                NewTaskHolidayBottomSheet(task,object:NewTaskBottomSheetListener{
                    override fun onDoneClicked(
                        dialog: Any,
                        taskType: TaskType,
                        title: String,
                        emailPhone: String,
                        description: String,
                        date: Date,
                        isTimeSelected: Boolean,
                        hours: Int,
                        minute: Int,
                        reminder: ReminderEnum?,
                        repeatType: RepeatType,
                        repeatDays: List<RepeatDays>,
                        isImportant: Boolean,
                        customDate: Date,
                        endDate: Date?,
                        rc: Int
                    ) {
                        (dialog as NewTaskHolidayBottomSheet).dismiss()
                        viewModel.updateTask(
                            TaskModel2(
                                id = task?.id!!,
                                taskId = task?.taskId!!,
                                deviceId = deviceId(),
                                title = title,
                                description = "",
                                date = date,
                                emailOrPhone = "",
                                hour = date!!.hours,
                                minute = date!!.minutes,
                                reminder = reminder!!,
                                repeatType = RepeatType.NONE,
                                repeatDays = mutableListOf(),
                                endDate =date!!.getNextDayFromDate(),
                                isImportant = isImportant,
                                taskType = TaskType.HOLIDAY,
                                customTime =customDate,
                                listGroupId = "",
                                repeatCount = 1,
                                singleOccurrance = true,
                                task =task!!,
                                isTimeSelected = isTimeSelected,
                                countryCode = task.countryCode,
                                selectedDate = if(isDateSelected == true)getSelectedDate() else Date(),
                                email=getUserEmail()
                            )
                        )
                        val startDate = CalenderUtil.getDatesForCalenderView(currentYear, currentMonth).first()
                        val endDate = CalenderUtil.getDatesForCalenderView(currentYear, currentMonth).last()
                        viewModel.getTaskListForShowingEvents(
                            getUserEmail(), deviceId(), startDate = startDate, endDate = endDate
                        )
                        viewModel.getTaskListForShowingHEvents(
                            getUserEmail(), deviceId(), startDate = startDate, endDate = endDate
                        )
                    }
                }).show(requireActivity().supportFragmentManager,"jverjv")
            }else {
                requireActivity().startActivity(
                    Intent(
                        requireActivity(),
                        ViewTaskActivity::class.java
                    ).putExtra(Constants.INTENT_TASK_ID, task.id)
                )
            }
        }

        override fun onTaskCheck(task: Tasks, position: Int) {
            //dialog.visible()
            viewModel.markTaskAsDone(TaskModel(position, task))
        }

        override fun onReminderClicked(task: Tasks, position: Int) {
            requireActivity().reminderDialogDisabled(customTime = task.customTime.toDate(),
                reminder = task.reminder,
                view = ReminderDialogBinding.inflate(LayoutInflater.from(requireActivity())).root,
                listener = object : ReminderDialogListener {
                    override fun onDoneClicked(
                        d: Dialog, reminder: ReminderEnum?, reminderTime: Date
                    ) {
                        if(!requireActivity().isNotificationPermissionAvailable()){
                            requestPermission(
                                Manifest.permission.POST_NOTIFICATIONS,
                                MY_PERMISSION_REQUEST_REMINDER
                            )
                            return
                        }
                        if (task?.repeatType != RepeatType.NONE) {
                            UpdateOptionBottomSheet(object : OptionListener {
                                override fun onOption1Selected() {
                                    d.dismiss()
                                    if(task.taskType==TaskType.HOLIDAY){
                                        NewTaskHolidayBottomSheet(task,object:NewTaskBottomSheetListener{
                                            override fun onDoneClicked(
                                                dialog: Any,
                                                taskType: TaskType,
                                                title: String,
                                                emailPhone: String,
                                                description: String,
                                                date: Date,
                                                isTimeSelected: Boolean,
                                                hours: Int,
                                                minute: Int,
                                                reminder: ReminderEnum?,
                                                repeatType: RepeatType,
                                                repeatDays: List<RepeatDays>,
                                                isImportant: Boolean,
                                                customDate: Date,
                                                endDate: Date?,
                                                rc: Int
                                            ) {
                                                (dialog as NewTaskHolidayBottomSheet).dismiss()
                                                viewModel.updateTask(
                                                    TaskModel2(
                                                        id = task?.id!!,
                                                        taskId = task?.taskId!!,
                                                        deviceId = deviceId(),
                                                        title = title,
                                                        description = "",
                                                        date = date,
                                                        emailOrPhone = "",
                                                        hour = date!!.hours,
                                                        minute = date!!.minutes,
                                                        reminder = reminder!!,
                                                        repeatType = RepeatType.NONE,
                                                        repeatDays = mutableListOf(),
                                                        endDate =date!!.getNextDayFromDate(),
                                                        isImportant = isImportant,
                                                        taskType = TaskType.HOLIDAY,
                                                        customTime =customDate,
                                                        listGroupId = "",
                                                        repeatCount = 1,
                                                        singleOccurrance = true,
                                                        task =task!!,
                                                        isTimeSelected = isTimeSelected,
                                                        countryCode = task.countryCode,
                                                        selectedDate = if(isDateSelected == true)getSelectedDate() else Date(),
                                                        email=getUserEmail()
                                                    )
                                                )
                                                val startDate = CalenderUtil.getDatesForCalenderView(currentYear, currentMonth).first()
                                                val endDate = CalenderUtil.getDatesForCalenderView(currentYear, currentMonth).last()
                                                viewModel.getTaskListForShowingEvents(
                                                    getUserEmail(), deviceId(), startDate = startDate, endDate = endDate
                                                )
                                                viewModel.getTaskListForShowingHEvents(
                                                    getUserEmail(), deviceId(), startDate = startDate, endDate = endDate
                                                )
                                            }
                                        }).show(requireActivity().supportFragmentManager,"jverjv")
                                    }else {
                                        startActivityForResult(
                                            Intent(
                                                requireActivity(),
                                                UpdateTaskActivity::class.java
                                            )
                                                .putExtra(Constants.INTENT_TASK_ID, task?.id)
                                                .putExtra("updateAll", true)
                                                .putExtra(
                                                    Constants.INTENT_FROM_LIST_GROUP,
                                                    task.listGroupId
                                                ), 1001
                                        )
                                    }

                                }

                                override fun onOption2Selected() {
                                    d.dismiss()
                                    if(task.taskType==TaskType.HOLIDAY){
                                        NewTaskHolidayBottomSheet(task,object:NewTaskBottomSheetListener{
                                            override fun onDoneClicked(
                                                dialog: Any,
                                                taskType: TaskType,
                                                title: String,
                                                emailPhone: String,
                                                description: String,
                                                date: Date,
                                                isTimeSelected: Boolean,
                                                hours: Int,
                                                minute: Int,
                                                reminder: ReminderEnum?,
                                                repeatType: RepeatType,
                                                repeatDays: List<RepeatDays>,
                                                isImportant: Boolean,
                                                customDate: Date,
                                                endDate: Date?,
                                                rc: Int
                                            ) {
                                                (dialog as NewTaskHolidayBottomSheet).dismiss()
                                                viewModel.updateTask(
                                                    TaskModel2(
                                                        id = task?.id!!,
                                                        taskId = task?.taskId!!,
                                                        deviceId = deviceId(),
                                                        title = title,
                                                        description = "",
                                                        date = date,
                                                        emailOrPhone = "",
                                                        hour = date!!.hours,
                                                        minute = date!!.minutes,
                                                        reminder = reminder!!,
                                                        repeatType = RepeatType.NONE,
                                                        repeatDays = mutableListOf(),
                                                        endDate =date!!.getNextDayFromDate(),
                                                        isImportant = isImportant,
                                                        taskType = TaskType.HOLIDAY,
                                                        customTime =customDate,
                                                        listGroupId = "",
                                                        repeatCount = 1,
                                                        singleOccurrance = true,
                                                        task =task!!,
                                                        isTimeSelected = isTimeSelected,
                                                        countryCode = task.countryCode,
                                                        selectedDate = if(isDateSelected == true)getSelectedDate() else Date(),
                                                        email=getUserEmail()
                                                    )
                                                )
                                                val startDate = CalenderUtil.getDatesForCalenderView(currentYear, currentMonth).first()
                                                val endDate = CalenderUtil.getDatesForCalenderView(currentYear, currentMonth).last()
                                                viewModel.getTaskListForShowingEvents(
                                                    getUserEmail(), deviceId(), startDate = startDate, endDate = endDate
                                                )
                                                viewModel.getTaskListForShowingHEvents(
                                                    getUserEmail(), deviceId(), startDate = startDate, endDate = endDate
                                                )
                                            }
                                        }).show(requireActivity().supportFragmentManager,"jverjv")
                                    }else {
                                        startActivityForResult(
                                            Intent(
                                                requireActivity(),
                                                UpdateTaskActivity::class.java
                                            )
                                                .putExtra(Constants.INTENT_TASK_ID, task?.id)
                                                .putExtra("updateAll", false)
                                                .putExtra(
                                                    Constants.INTENT_FROM_LIST_GROUP,
                                                    task.listGroupId
                                                ), 1001
                                        )
                                    }
                                }

                            }).show(requireActivity().supportFragmentManager, "UpdateBottomSheet")
                        } else {
                            d.dismiss()
                            if(task.taskType==TaskType.HOLIDAY){
                                NewTaskHolidayBottomSheet(task,object:NewTaskBottomSheetListener{
                                    override fun onDoneClicked(
                                        dialog: Any,
                                        taskType: TaskType,
                                        title: String,
                                        emailPhone: String,
                                        description: String,
                                        date: Date,
                                        isTimeSelected: Boolean,
                                        hours: Int,
                                        minute: Int,
                                        reminder: ReminderEnum?,
                                        repeatType: RepeatType,
                                        repeatDays: List<RepeatDays>,
                                        isImportant: Boolean,
                                        customDate: Date,
                                        endDate: Date?,
                                        rc: Int
                                    ) {
                                        (dialog as NewTaskHolidayBottomSheet).dismiss()
                                        viewModel.updateTask(
                                            TaskModel2(
                                                id = task?.id!!,
                                                taskId = task?.taskId!!,
                                                deviceId = deviceId(),
                                                title = title,
                                                description = "",
                                                date = date,
                                                emailOrPhone = "",
                                                hour = date!!.hours,
                                                minute = date!!.minutes,
                                                reminder = reminder!!,
                                                repeatType = RepeatType.NONE,
                                                repeatDays = mutableListOf(),
                                                endDate =date!!.getNextDayFromDate(),
                                                isImportant = isImportant,
                                                taskType = TaskType.HOLIDAY,
                                                customTime =customDate,
                                                listGroupId = "",
                                                repeatCount = 1,
                                                singleOccurrance = true,
                                                task =task!!,
                                                isTimeSelected = isTimeSelected,
                                                countryCode = task.countryCode,
                                                selectedDate = if(isDateSelected == true)getSelectedDate() else Date(),
                                                email=getUserEmail()
                                            )
                                        )
                                        val startDate = CalenderUtil.getDatesForCalenderView(currentYear, currentMonth).first()
                                        val endDate = CalenderUtil.getDatesForCalenderView(currentYear, currentMonth).last()
                                        viewModel.getTaskListForShowingEvents(
                                            getUserEmail(), deviceId(), startDate = startDate, endDate = endDate
                                        )
                                        viewModel.getTaskListForShowingHEvents(
                                           getUserEmail(), deviceId(), startDate = startDate, endDate = endDate
                                        )
                                    }
                                }).show(requireActivity().supportFragmentManager,"jverjv")
                            }else {
                                startActivityForResult(
                                    Intent(requireActivity(), UpdateTaskActivity::class.java)
                                        .putExtra(Constants.INTENT_TASK_ID, task?.id)
                                        .putExtra("updateAll", true)
                                        .putExtra(
                                            Constants.INTENT_FROM_LIST_GROUP,
                                            task.listGroupId
                                        ), 1001
                                )
                            }
                        }


                    }
                }).show()
        }

        override fun onRepeatClicked(task: Tasks, position: Int) {
            requireActivity().repeatDialogDisabled(
                task.repeatType,
                task.repeatDays,
                task.date.toDate(),
                task.endDate.toDate(),
                RepeatDialogBinding.inflate(LayoutInflater.from(requireActivity())),
                object : repeatDialogListener {
                    override fun onDoneClicked(
                        repeatType: RepeatType,
                        repeatDays: List<RepeatDays>,
                        eDate: Date?,
                        rc: Int,
                        d: Dialog
                    ) {
                        if(!requireActivity().isNotificationPermissionAvailable()){
                            requestPermission(
                                Manifest.permission.POST_NOTIFICATIONS,
                                MY_PERMISSION_REQUEST_REMINDER
                            )
                            return
                        }
                        UpdateOptionBottomSheet(object : OptionListener {
                            override fun onOption1Selected() {
                                d.dismiss()
                                startActivityForResult(
                                    Intent(requireActivity(), UpdateTaskActivity::class.java)
                                        .putExtra(Constants.INTENT_TASK_ID, task?.id)
                                        .putExtra("updateAll", true)
                                        .putExtra(
                                            Constants.INTENT_FROM_LIST_GROUP, task.listGroupId
                                        ), 1001
                                )

                            }

                            override fun onOption2Selected() {
                                d.dismiss()
                                startActivityForResult(
                                    Intent(requireActivity(), UpdateTaskActivity::class.java)
                                        .putExtra(Constants.INTENT_TASK_ID, task?.id)
                                        .putExtra("updateAll", false)
                                        .putExtra(
                                            Constants.INTENT_FROM_LIST_GROUP,
                                            task.listGroupId
                                        ), 1001
                                )
                            }

                        }).show(requireActivity().supportFragmentManager, "UpdateBottomSheet")
                    }

                },
                task.repeatCount
            ).show()
        }

        override fun setEvents(task: Tasks) {

        }

    })
    var completedTaskAdapter = TaskAdapter(completedTask, object : TaskAdapterItemListener {
        override fun onItemClicked(task: Tasks) {
            if(task.taskType!=TaskType.HOLIDAY){
                requireActivity().startActivity(
                    Intent(
                        requireActivity(),
                        ViewTaskActivity::class.java
                    ).putExtra(Constants.INTENT_TASK_ID, task.id)
                )
            }
        }

        override fun onTaskCheck(task: Tasks, position: Int) {
            viewModel.markTaskAsUnDone(TaskModel(position, task))
        }

        override fun onReminderClicked(task: Tasks, position: Int) {
            //not required in case of completed tasks
        }

        override fun onRepeatClicked(task: Tasks, position: Int) {

        }

        override fun setEvents(task: Tasks) {

        }

    })
    var taskType: TaskType? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        setCalender()
        return super.onCreateView(inflater, container, savedInstanceState)
    }
    fun checkAndLoadSubscriptionFeatures(){
        if (getSubscriptionStatus()==SubscriptionStatus.ACTIVE) {
            requireActivity().runOnUiThread{
                binding.adView.gone()
            }
        }else{
            binding.adView.visible()
            setAds()
        }
    }
    override fun setupViews() {
        progressCount = 0
        checkAndLoadSubscriptionFeatures()
        setAdapter()
        when (binding.rgTab.checkedRadioButtonId) {
            R.id.rbAll -> {
                taskType = null
                filterByTaskType(null)
            }

            R.id.rbCallsEmails -> {
                taskType = TaskType.CALL_EMAIL
                filterByTaskType(taskType)
            }

            R.id.rbToList -> {
                taskType = TaskType.TODO
                filterByTaskType(taskType)
            }

            R.id.rbList -> {
                taskType = TaskType.LIST_GROUP_TASK
                filterByTaskType(taskType)
            }
            R.id.rbEvents -> {
                taskType = TaskType.HOLIDAY
                filterByTaskType(taskType)
            }


        }

    }


    override fun setupListeners() {
        binding.rgTab.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbAll -> {
                    taskType = null
                    filterByTaskType(null)
                }

                R.id.rbCallsEmails -> {
                    taskType = TaskType.CALL_EMAIL
                    filterByTaskType(taskType)
                }

                R.id.rbToList -> {
                    taskType = TaskType.TODO
                    filterByTaskType(taskType)
                }

                R.id.rbList -> {
                    taskType = TaskType.LIST_GROUP_TASK
                    filterByTaskType(taskType)
                }
                R.id.rbEvents -> {
                    taskType = TaskType.HOLIDAY
                    filterByTaskType(taskType)
                }

            }
        }
        binding.fab.ibFabAdd.setOnClickListener {
            if(!requireActivity().isNotificationPermissionAvailable()){
                requestPermission(
                    Manifest.permission.POST_NOTIFICATIONS,
                    MY_PERMISSION_REQUEST_REMINDER
                )
                return@setOnClickListener
            }
            val day = binding.calendar.selectedDay
            val calender = Calendar.getInstance()
            calender.set(day!!.year, day.month, day.day)
            val tempDate = if(isDateSelected==true) calender.time else Date()
            NewTaskBottomSheet(tempDate, object : NewTaskBottomSheetListener {
                override fun onDoneClicked(
                    sheet: Any,
                    taskType: TaskType,
                    title: String,
                    emailPhone: String,
                    description: String,
                    date: Date,
                    isTimeSelected: Boolean,
                    hours: Int,
                    minute: Int,
                    reminder: ReminderEnum?,
                    repeatType: RepeatType,
                    repeatDays: List<RepeatDays>,
                    isImportant: Boolean,
                    customDateTime: Date,
                    endDate: Date?,
                    rc: Int
                ) {
                    (sheet as NewTaskBottomSheet).dismiss()
                    viewModel.createNormalTask(
                        TaskModel2(
                            taskId = UUID.randomUUID().toString(),
                            deviceId = deviceId(),
                            title = title,
                            description = description,
                            date = date,
                            emailOrPhone = emailPhone,
                            isTimeSelected = isTimeSelected,
                            hour = hours,
                            minute = minute,
                            reminder = reminder!!,
                            repeatType = repeatType,
                            repeatDays = repeatDays,
                            isImportant = isImportant,
                            taskType = taskType,
                            customTime = customDateTime,
                            endDate = endDate ?: date.getNextDayFromDate(),
                            repeatCount = rc,
                            listGroupId = "",
                            singleOccurrance = false,
                            task = null,
                            id = "",
                            selectedDate = if(isDateSelected == true)getSelectedDate() else Date(),
                            email=getUserEmail()
                        )
                    )
                }
            }).show(childFragmentManager, "NewTaskBottomSheet")
        }
    }

    override fun fetchInitialData() {
        if(arguments?.getString("open")?.equals("createTask",true)==true){
            binding.fab.ibFabAdd.performClick()
            arguments?.clear()
        }else if (arguments?.getString("taskType")?.equals(TaskType.TODO.name) == true) {
            isThisWeekOptionSelected = true
            isThisMonthOptionSelected = true
            showDate = true
            if (arguments?.getString("monthOrWeek")?.equals("This Week") == true) {
                setWeeklyDataFromHub(TaskType.TODO)
                setThisWeek()
            }
            if (arguments?.getString("monthOrWeek")?.equals("This Month") == true) {
                setMonthlyDataFromHub(TaskType.TODO)
                setThisViewMonth()
            }
        } else if (arguments?.getString("taskType")?.equals(TaskType.CALL_EMAIL.name) == true) {
            isThisWeekOptionSelected = true
            isThisMonthOptionSelected = true
            showDate = true
            if (arguments?.getString("monthOrWeek")?.equals("This Week") == true) {
                setWeeklyDataFromHub(TaskType.CALL_EMAIL)
                setThisWeek()
            }
            if (arguments?.getString("monthOrWeek")?.equals("This Month") == true) {
                setMonthlyDataFromHub(TaskType.CALL_EMAIL)
                setThisMonth()
            }
        } else {
            val day = binding.calendar.selectedDay
            val calender = Calendar.getInstance()
            calender.set(day!!.year, day.month, day.day)
            viewModel.getTaskByDate(getUserEmail(), deviceId(), calender.time)
//            viewModel.getTodaySTask(getUserEmail(), deviceId())
        }
        currentMonth = binding.calendar.month + 1
        currentYear = binding.calendar.year
        val startDate = CalenderUtil.getDatesForCalenderView(currentYear, currentMonth).first()
        val endDate = CalenderUtil.getDatesForCalenderView(currentYear, currentMonth).last()
        viewModel.getTaskListForShowingEvents(
            getUserEmail(), deviceId(), startDate = startDate, endDate = endDate
        )
        viewModel.getTaskListForShowingHEvents(
            getUserEmail(), deviceId(), startDate = startDate, endDate = endDate
        )
    }

    fun setThisViewMonth() {
        currentYear = Date().year + 1900
        currentMonth = Date().month + 1
        binding.tvToday.text = "Month,"
        binding.tvDate.text = "${
            CalenderUtil.getDatesForCurrentMonth(currentYear, currentMonth).first()
                .getDateStringFromDate()
        } ${
            CalenderUtil.getDatesForCurrentMonth(currentYear, currentMonth).first()
                .getMonthFromDate()
        }-${
            CalenderUtil.getDatesForCurrentMonth(currentYear, currentMonth).last()
                .getDateStringFromDate()
        } ${
            CalenderUtil.getDatesForCurrentMonth(currentYear, currentMonth).last()
                .getMonthFromDate()
        }"

        //dialog.visible()

        viewModel.getMonthlyTask(
            getUserEmail(),
            deviceId(),
            CalenderUtil.getDatesForCurrentMonth(currentYear, currentMonth).first(),
            CalenderUtil.getDatesForCurrentMonth(currentYear, currentMonth).last()
        )
        showDate = true

    }

    override fun setupObservers() {
        viewModel.eventList.observe(this, eventListObserver)
        viewModel.hEventList.observe(this, hEventListObserver)
        viewModel.isReminderUpdated.observe(this, isReminderUpdatedObserver)
        viewModel.isRepeatTypeUpdated.observe(this, isRepeatTypeUpdatedObserver)
//        viewModel.userData.observe(this, userDataObserver)
//        viewModel.userAttributes.observe(this, userAttributesObserver)
        viewModel.onError.observe(this, onErrorObserver)
//        viewModel.loginSession.observe(this, loginSessionObserver)
        viewModel.isTaskCreated.observe(this, isTaskCreatedObserver)
        viewModel.isTaskUpdated.observe(this, isTaskUpdatedObserver)
        viewModel.todaySTaskList.observe(this, todayTaskListObserver)
        viewModel.weeklyTasksList.observe(this, weeklyTaskListObserver)
        viewModel.taskList.observe(this, taskListObserver)
        viewModel.monthlyTasksList.observe(this, monthlyTaskListObserver)
        viewModel.taskListByDate.observe(this, taskListByDate)
        viewModel.isMarkedDone.observe(this, isMarkDoneObserver)
        viewModel.isMarkedUnDone.observe(this, isMarkedUnDoneObserver)

    }

    override fun removeObserver() {
        viewModel.eventList.removeObserver(eventListObserver)
        viewModel.hEventList.removeObserver(hEventListObserver)
        viewModel.isReminderUpdated.removeObserver(isReminderUpdatedObserver)
        viewModel.isRepeatTypeUpdated.removeObserver(isRepeatTypeUpdatedObserver)
//        viewModel.userData.removeObserver(userDataObserver)
//        viewModel.userAttributes.removeObserver(userAttributesObserver)
        viewModel.onError.removeObserver(onErrorObserver)
//        viewModel.loginSession.removeObserver(loginSessionObserver)
        viewModel.isTaskCreated.removeObserver(isTaskCreatedObserver)
        viewModel.isTaskUpdated.observe(this, isTaskUpdatedObserver)
        viewModel.todaySTaskList.removeObserver(todayTaskListObserver)
        viewModel.weeklyTasksList.removeObserver(weeklyTaskListObserver)
        viewModel.taskList.removeObserver(taskListObserver)
        viewModel.monthlyTasksList.removeObserver(monthlyTaskListObserver)
        viewModel.taskListByDate.removeObserver(taskListByDate)
        viewModel.isMarkedDone.removeObserver(isMarkDoneObserver)
        viewModel.isMarkedUnDone.removeObserver(isMarkedUnDoneObserver)
    }

    private fun filterByTaskType(taskType: TaskType? = null) {
        Log.d("splashcheck ", "filterByTaskType: $taskList")
        var filteredTask = when (taskType) {
            TaskType.TODO -> taskList.filter { it.taskType == TaskType.TODO && it.listGroupId == ""}.toMutableList()
            TaskType.CALL_EMAIL -> taskList.filter { it.taskType == TaskType.CALL_EMAIL }
                .toMutableList()
            TaskType.LIST_GROUP_TASK -> taskList.filter { it.listGroupId != "" }.toMutableList()
            TaskType.HOLIDAY -> taskList.filter { it.taskType == TaskType.HOLIDAY }.toMutableList()
            else -> taskList
        }

        filteredTask.sortedBy { it.date }
        completedTask =
            filteredTask.filter { it.isCompleted == true }.sortedBy { it.date }.toMutableList()
        pendingTask =
            filteredTask.filter { it.isCompleted == false }.sortedBy { it.date }.toMutableList()
        if (pendingTask.isEmpty()) {
            binding.tvNoTaskFound1.visible()
            binding.rvPendingTasks.gone()
        } else {
            binding.tvNoTaskFound1.gone()
            binding.rvPendingTasks.visible()
        }
        pendingTaskAdapter.setList(pendingTask, showDate)
        pendingTaskAdapter.notifyDataSetChanged()
        if (completedTask.isEmpty()) {
            binding.tvNoTaskFound2.visible()
            binding.rvCompletedTasks.gone()
        } else {
            binding.tvNoTaskFound2.gone()
            binding.rvCompletedTasks.visible()
        }
        completedTaskAdapter.setList(completedTask, showDate)
        completedTaskAdapter.notifyDataSetChanged()
    }

    private fun setAdapter() {
        binding.rvPendingTasks.adapter = pendingTaskAdapter
        binding.rvCompletedTasks.adapter = completedTaskAdapter
    }

    private fun setCalender() {
        val c = Calendar.getInstance()
        currentYear = c.get(Calendar.YEAR)
        currentMonth = c.get(Calendar.MONTH) + 1
        binding.calendar.setCalendarListener(object : CollapsibleCalendar.CalendarListener {

            override fun onDaySelect() {
                isDateSelected=true
                isThisWeekOptionSelected = false
                isThisMonthOptionSelected = false
                Log.d("cState", "onDaySelect")
                val day = binding.calendar.selectedDay
                val calender = Calendar.getInstance()
                calender.set(day!!.year, day.month, day.day)
                setHeaderForSelectedDateAndFetchTasks(calender.time)
            }

            override fun onDayUnSelected() {
                isDateSelected=false
                //TODO: check for week or month case here
                /** if no date is selected then check calendar mode **/
                /**
                 * expanded -> true -> monthView
                 * expanded -> false ->weekView
                 * ***/
                if (binding.calendar.expanded) {
                    //monthview
                    val startDate = getStartAndEndDateOfCurrentMonth(
                        binding.calendar.month,
                        binding.calendar.year
                    ).first
                    val endDate = getStartAndEndDateOfCurrentMonth(
                        binding.calendar.month,
                        binding.calendar.year
                    ).second
                    setHeaderForMontViewAndFetchTasks(startDate, endDate)
                    return
                }

                val startDate =
                    getStartAndEndDateOfWeek(
                        binding.calendar.month,
                        binding.calendar.year,
                        binding.calendar.mCurrentWeekIndex
                    ).first
                val endDate = getStartAndEndDateOfWeek(
                    binding.calendar.month,
                    binding.calendar.year,
                    binding.calendar.mCurrentWeekIndex
                ).second
                setHeaderForWeekViewAndFetchTasks(startDate, endDate)

            }

            override fun onItemClick(v: View) {

            }

            override fun onDataUpdate() {

            }

            override fun onMonthChange() {
                /** fetch events of current month **/
                currentMonth = binding.calendar.month + 1
                currentYear = binding.calendar.year
                val sD = CalenderUtil.getDatesForCalenderView(currentYear, currentMonth).first()
                val eD = CalenderUtil.getDatesForCalenderView(currentYear, currentMonth).last()
                viewModel.getTaskListForShowingEvents(
                    getUserEmail(), deviceId(), startDate = sD, endDate = eD
                )
                viewModel.getTaskListForShowingHEvents(
                    getUserEmail(), deviceId(), startDate = sD, endDate = eD
                )

                Log.d(
                    "cState",
                    "monthchange${binding.calendar.month + 1},${binding.calendar.year}---$sD,$eD"
                )
                /** check for selected Date **/
                if (binding.calendar.isDaySelected && binding.calendar.selectedDay?.day != 0 && !isThisMonthOptionSelected) {
                    val day = binding.calendar.selectedDay
                    val calender = Calendar.getInstance()
                    calender.set(binding.calendar.year, binding.calendar.month, day?.day ?: 1)
                    setHeaderForSelectedDateAndFetchTasks(calender.time)
                    return
                }
                /** if no date is selected then check calendar mode **/
                /**
                 * expanded -> true -> monthView
                 * expanded -> false ->weekView
                 * ***/
                if (binding.calendar.expanded) {
                    //monthview
                    val startDate = getStartAndEndDateOfCurrentMonth(
                        binding.calendar.month,
                        binding.calendar.year
                    ).first
                    val endDate = getStartAndEndDateOfCurrentMonth(
                        binding.calendar.month,
                        binding.calendar.year
                    ).second
                    setHeaderForMontViewAndFetchTasks(startDate, endDate)
                    return
                }

//                val startDate =
//                    getStartAndEndDateOfWeek(binding.calendar.month, binding.calendar.year, binding.calendar.mCurrentWeekIndex).first
//                val endDate = getStartAndEndDateOfWeek(
//                    binding.calendar.month,
//                    binding.calendar.year,
//                    binding.calendar.mCurrentWeekIndex
//                ).second
//                setHeaderForWeekViewAndFetchTasks(startDate, endDate)

            }

            override fun onWeekChange(position: Int) {
                /** check for selected Date **/
                if (binding.calendar.isDaySelected && binding.calendar.selectedDay != null && !isThisWeekOptionSelected) {
                    val day = binding.calendar.selectedDay
                    val calender = Calendar.getInstance()
                    calender.set(day!!.year, day.month, day.day)
                    setHeaderForSelectedDateAndFetchTasks(calender.time)
                    return
                }

                val startDate = getStartAndEndDateOfWeek(
                    binding.calendar.month,
                    binding.calendar.year,
                    position
                ).first
                val endDate = getStartAndEndDateOfWeek(
                    binding.calendar.month,
                    binding.calendar.year,
                    position
                ).second
                setHeaderForWeekViewAndFetchTasks(startDate, endDate)


            }

            override fun onClickListener() {

            }

            override fun onDayChanged() {
                Log.d("cState", "daychange")
            }

            override fun onMoreIconClicked(v: View) {
                openCalenderPopup(v)
            }

            override fun onCollapse(mCurrentWeekIndex: Int) {
                onWeekChange(mCurrentWeekIndex)
            }

            override fun onExpand() {
                onMonthChange()
            }
        })
        if (arguments?.getString("monthOrWeek")?.equals("This Month") == true) {
            binding.calendar.expand(0)
        } else if (arguments?.getString("monthOrWeek")?.equals("This Week") == true) {
            binding.calendar.collapse(0)
        } else {
            binding.calendar.expand(0)
        }


    }


    @SuppressLint("RestrictedApi")
    fun openCalenderPopup(p0: View) {
        val menuBuilder = MenuBuilder(requireActivity())
        val inflater = MenuInflater(requireActivity())
        inflater.inflate(R.menu.calender_menu, menuBuilder)

        val optionMenu = MenuPopupHelper(requireActivity(), menuBuilder, p0);
        optionMenu.setForceShowIcon(true);
        menuBuilder.setCallback(object : MenuBuilder.Callback {
            override fun onMenuItemSelected(menu: MenuBuilder, item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.thisWeek -> {
                        filter = 1
                        setThisWeek()
                    }

                    R.id.thisMonth -> {
                        filter = 2
                        setThisMonth()
                    }

                    else -> {}
                }
                return true
            }

            override fun onMenuModeChange(menu: MenuBuilder) {

            }
        })


        optionMenu.show();

    }


    fun setThisWeek() {
        showDate = true
        isThisWeekOptionSelected = true
        currentYear = Date().year + 1900
        currentMonth = Date().month

        if (binding.calendar.isDaySelected) binding.calendar.unSelectDay(binding.calendar.selectedDay!!)
//        binding.calendar.selectedItemPosition=-1
        binding.calendar.collapse(0)
        binding.calendar.navigateToCurrentWeek(currentYear, currentMonth, Date().date)
//         binding.calendar.init(requireActivity())
        val sD = getStartAndEndDateOfWeek(
            binding.calendar.month,
            binding.calendar.year,
            binding.calendar.mCurrentWeekIndex
        ).first
        val eD = getStartAndEndDateOfWeek(
            binding.calendar.month,
            binding.calendar.year,
            binding.calendar.mCurrentWeekIndex
        ).second
        val startDate = CalenderUtil.getDatesForCalenderView(currentYear, currentMonth + 1).first()
        val endDate = CalenderUtil.getDatesForCalenderView(currentYear, currentMonth + 1).last()
        setHeaderForWeekViewAndFetchTasks(sD, eD)
        viewModel.getTaskListForShowingEvents(
            getUserEmail(), deviceId(), startDate = startDate, endDate = endDate
        )
        viewModel.getTaskListForShowingHEvents(
            getUserEmail(), deviceId(), startDate = startDate, endDate = endDate
        )


    }

    fun setThisMonth() {
        showDate = true
        isThisMonthOptionSelected = true
        currentYear = Date().year + 1900
        currentMonth = Date().month
        val sD = getStartAndEndDateOfCurrentMonth(currentMonth, currentYear).first
        val eD = getStartAndEndDateOfCurrentMonth(currentMonth, currentYear).second
        setHeaderForMontViewAndFetchTasks(sD, eD)
        binding.calendar.navigateToCurrentMonth(currentYear, currentMonth, Date().date)
        val startDate = CalenderUtil.getDatesForCalenderView(currentYear, currentMonth + 1).first()
        val endDate = CalenderUtil.getDatesForCalenderView(currentYear, currentMonth + 1).last()
        viewModel.getTaskListForShowingEvents(
            getUserEmail(), deviceId(), startDate = startDate, endDate = endDate
        )
        viewModel.getTaskListForShowingHEvents(
            getUserEmail(), deviceId(), startDate = startDate, endDate = endDate
        )

    }

    private fun setWeeklyDataFromHub(taskType: TaskType) {
        if (taskType == TaskType.CALL_EMAIL) {
            binding.rbCallsEmails.isChecked = true
        }
        if (taskType == TaskType.TODO) {
            binding.rbToList.isChecked = true
        }
    }

    private fun setMonthlyDataFromHub(taskType: TaskType) {
        if (taskType == TaskType.CALL_EMAIL) {
            binding.rbCallsEmails.isChecked = true
        }
        if (taskType == TaskType.TODO) {
            binding.rbToList.isChecked = true
        }
    }


    private fun setAds() {
        binding.adView.loadBannerAd()
        binding.adView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.adView.loadBannerAd()
                },2000)
            }

            override fun onAdLoaded() {
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.adView.loadBannerAd()
                },2000)
            }

            override fun onAdClosed() {
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.adView.loadBannerAd()
                },2000)
            }
        }
    }

    override fun onResume() {
        Log.d("Lifecycle", "TaskFragment -> onResume")
        super.onResume()
    }


    override fun onStart() {
        Log.d("Lifecycle", "TaskFragment -> onStart")
        super.onStart()
    }

    override fun onStop() {
        Log.d("Lifecycle", "TaskFragment -> onStop")
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("Lifecycle", "TaskFragment -> onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        Log.d("Lifecycle", "TaskFragment -> onDestroy")
        super.onDestroy()
    }

    private fun dialogDismiss() {
        Log.d("ProgressDialog", "dialogDismiss()->counter$progressCount HubFragment")
        if (progressCount >= 2) {
            dialog.close()
        }
    }

    private val todayTaskListObserver = Observer<List<Tasks>> { list ->
        progressCount++
        Log.d("ProgressDialog", "todayTaskListObserver->counter$progressCount TaskFragment")
        taskList = list.toSet().toMutableList()
        filterByTaskType(taskType)
        dialogDismiss()
    }

    private val weeklyTaskListObserver = Observer<List<Tasks>> { list ->
        taskList = list.toSet().toMutableList()
        filterByTaskType(taskType)
        dialogDismiss()

    }

    private val taskListObserver = Observer<List<Tasks>> { list ->
    }
    private val eventListObserver = Observer<List<Date>> { eventDateList ->
        eventList = eventDateList.toMutableList()
        Log.d("eventLog", "date received->${eventDateList.size}, ${eventList.size}")
        setEvents(eventList)
        progressCount++
        dialogDismiss()
    }
    private val hEventListObserver = Observer<List<Date>> { eventDateList ->
        hEventList = eventDateList.toMutableList()
        Log.d("eventLog", "date received->${eventDateList.size}, ${eventList.size}")
        setHEvents(hEventList)
        progressCount++
        dialogDismiss()
    }

    private val monthlyTaskListObserver = Observer<List<Tasks>> { list ->
        taskList = list.toSet().toMutableList()
        filterByTaskType(taskType)
        dialogDismiss()
    }

    private val taskListByDate = Observer<List<Tasks>> { list ->
        taskList = list.toSet().toMutableList()
        filterByTaskType(taskType)
        dialogDismiss()
    }

    private val isMarkDoneObserver = Observer<TaskModel> { taskModel ->
        val filteredTask = taskList.filter { it.id == taskModel.task.id }
        if (filteredTask.isNotEmpty()) {
            val position = taskList.indexOf(filteredTask[0])
            taskList.removeAt(position)
            taskList.add(position, taskModel.task)
        }
        filterByTaskType(taskType)
        dialogDismiss()

    }

    private val isMarkedUnDoneObserver = Observer<TaskModel> { taskModel ->
        val filteredTask = taskList.filter { it.id == taskModel.task.id }
        if (filteredTask.isNotEmpty()) {
            val position = taskList.indexOf(filteredTask[0])
            taskList.removeAt(position)
            taskList.add(position, taskModel.task)
        }
        filterByTaskType(taskType)
        dialogDismiss()
    }

    private val isReminderUpdatedObserver = Observer<TaskModel> { taskmodel ->
        taskList[taskmodel.position] = taskmodel.task
        filterByTaskType(taskType)
        dialogDismiss()
        binding.rvPendingTasks.adapter?.notifyItemChanged(taskmodel.position)
    }
    private val isRepeatTypeUpdatedObserver = Observer<TaskModel> { taskmodel ->
//        setHeaderAndFetchData(selectedDate, currentYear, currentMonth)
        if (binding.calendar.isDaySelected && binding.calendar.selectedDay?.day != 0) {
            val day = binding.calendar.selectedDay
            val calender = Calendar.getInstance()
            calender.set(binding.calendar.year, binding.calendar.month, day?.day ?: 1)
            setHeaderForSelectedDateAndFetchTasks(calender.time)
            return@Observer
        }
        /** if no date is selected then check calendar mode **/
        /**
         * expanded -> true -> monthView
         * expanded -> false ->weekView
         * ***/
        if (binding.calendar.expanded) {
            //monthview
            val startDate = getStartAndEndDateOfCurrentMonth(
                binding.calendar.month,
                binding.calendar.year
            ).first
            val endDate = getStartAndEndDateOfCurrentMonth(
                binding.calendar.month,
                binding.calendar.year
            ).second
            setHeaderForMontViewAndFetchTasks(startDate, endDate)
            return@Observer
        }
        val startDate =
            getStartAndEndDateOfWeek(
                binding.calendar.month,
                binding.calendar.year,
                binding.calendar.mCurrentWeekIndex
            ).first
        val endDate = getStartAndEndDateOfWeek(
            binding.calendar.month,
            binding.calendar.year,
            binding.calendar.mCurrentWeekIndex
        ).second
        setHeaderForWeekViewAndFetchTasks(startDate, endDate)


    }

    private val onErrorObserver = Observer<DataStoreException> { ex ->
        progressCount++
        Log.d("ProgressDialog", "onErrorObserver->counter$progressCount TaskFragment")
        dialogDismiss()
        requireActivity().handleExceptions(ex)
    }

    private val isTaskCreatedObserver = Observer<MutableList<Tasks>> { taskList ->
        val sD = CalenderUtil.getDatesForCalenderView(binding.calendar.year, binding.calendar.month+1).first()
        val eD = CalenderUtil.getDatesForCalenderView(binding.calendar.year, binding.calendar.month+1).last()
        viewModel.getTaskListForShowingEvents(
            getUserEmail(), deviceId(), startDate = sD, endDate = eD
        )
        viewModel.getTaskListForShowingHEvents(
            getUserEmail(), deviceId(), startDate = sD, endDate = eD
        )
//        setHeaderAndFetchData(this@TaskFragment.selectedDate, currentYear, currentMonth)
        if (binding.calendar.isDaySelected && binding.calendar.selectedDay?.day != 0) {
            val day = binding.calendar.selectedDay
            val calender = Calendar.getInstance()
            calender.set(binding.calendar.year, binding.calendar.month, day?.day ?: 1)
            setHeaderForSelectedDateAndFetchTasks(calender.time)
            return@Observer
        }
        /** if no date is selected then check calendar mode **/
        /**
         * expanded -> true -> monthView
         * expanded -> false ->weekView
         * ***/
        if (binding.calendar.expanded) {
            //monthview
            val startDate = getStartAndEndDateOfCurrentMonth(
                binding.calendar.month,
                binding.calendar.year
            ).first
            val endDate = getStartAndEndDateOfCurrentMonth(
                binding.calendar.month,
                binding.calendar.year
            ).second
            setHeaderForMontViewAndFetchTasks(startDate, endDate)
            return@Observer
        }
        val startDate =
            getStartAndEndDateOfWeek(
                binding.calendar.month,
                binding.calendar.year,
                binding.calendar.mCurrentWeekIndex
            ).first
        val endDate = getStartAndEndDateOfWeek(
            binding.calendar.month,
            binding.calendar.year,
            binding.calendar.mCurrentWeekIndex
        ).second
        setHeaderForWeekViewAndFetchTasks(startDate, endDate)

    }
    private val isTaskUpdatedObserver = Observer<MutableList<Tasks>> { taskList ->
//        setEvent(task.date.toDate())
        val sD = CalenderUtil.getDatesForCalenderView(binding.calendar.year, binding.calendar.month+1).first()
        val eD = CalenderUtil.getDatesForCalenderView(binding.calendar.year, binding.calendar.month+1).last()
        viewModel.getTaskListForShowingEvents(
            getUserEmail(), deviceId(), startDate = sD, endDate = eD
        )
        viewModel.getTaskListForShowingHEvents(
            getUserEmail(), deviceId(), startDate = sD, endDate = eD
        )
//        setHeaderAndFetchData(this@TaskFragment.selectedDate, currentYear, currentMonth)
        if (binding.calendar.isDaySelected && binding.calendar.selectedDay?.day != 0) {
            val day = binding.calendar.selectedDay
            val calender = Calendar.getInstance()
            calender.set(binding.calendar.year, binding.calendar.month, day?.day ?: 1)
            setHeaderForSelectedDateAndFetchTasks(calender.time)
            return@Observer
        }
        /** if no date is selected then check calendar mode **/
        /**
         * expanded -> true -> monthView
         * expanded -> false ->weekView
         * ***/
        if (binding.calendar.expanded) {
            //monthview
            val startDate = getStartAndEndDateOfCurrentMonth(
                binding.calendar.month,
                binding.calendar.year
            ).first
            val endDate = getStartAndEndDateOfCurrentMonth(
                binding.calendar.month,
                binding.calendar.year
            ).second
            setHeaderForMontViewAndFetchTasks(startDate, endDate)
            return@Observer
        }
        val startDate =
            getStartAndEndDateOfWeek(
                binding.calendar.month,
                binding.calendar.year,
                binding.calendar.mCurrentWeekIndex
            ).first
        val endDate = getStartAndEndDateOfWeek(
            binding.calendar.month,
            binding.calendar.year,
            binding.calendar.mCurrentWeekIndex
        ).second
        setHeaderForWeekViewAndFetchTasks(startDate, endDate)

    }
    override fun onPause() {
        super.onPause()
        Log.i("Lifecycle", "TaskFragment -> onPause()")
        dialog.close()
        removeObserver()
    }

    private fun setEvents(eventDates: List<Date>) {
        binding.calendar.addEventTagList(eventDates)
    }
    private fun setHEvents(eventDates: List<Date>) {
        binding.calendar.addHEventTagList(eventDates)
    }

    private fun setEvent(eventDate: Date) {
        binding.calendar.addEventTag(eventDate.year + 1900, eventDate.month, eventDate.date)
    }

    /** here month ranges from 0 to 11 **/
    fun getStartAndEndDateOfCurrentMonth(month: Int, year: Int): Pair<Date, Date> {
        val monthsEndsWith31 = listOf(0, 2, 4, 6, 7, 9, 11)
        val monthsEndsWith30 = listOf(3, 5, 8, 10)
        val sdCalender = Calendar.getInstance()
        sdCalender.set(year, month, 1)
        val startDate = sdCalender.time

        val ed = if (monthsEndsWith31.contains(month)) {
            31
        } else if (monthsEndsWith30.contains(month)) {
            30
        } else {
            /** feb month here check for leap year **/
            if (year.isLeapYear()) 29 else 28
        }

        val edCalender = Calendar.getInstance()
        edCalender.set(year, month, ed)
        val endDate = edCalender.time

        return Pair(startDate, endDate)
    }


    fun setHeaderForSelectedDateAndFetchTasks(selectedDate: Date) {
        /** setHeader for selected date and fetch tasks for selected **/
        binding.tvToday.setText(selectedDate?.getDayFromDate())
        binding.tvDate.setText(",${selectedDate?.getDateStringFromDate()} ${selectedDate?.getMonthFromDate()}")
        viewModel.getTaskByDate(getUserEmail(), deviceId(), selectedDate)
    }

    fun setHeaderForMontViewAndFetchTasks(startDate: Date, endDate: Date) {
        /** setHeader for selected date and fetch tasks for selected **/
        binding.tvToday.text = "Month,"
        binding.tvDate.text = "${
            startDate.getDateStringFromDate()
        } ${
            startDate.getMonthFromDate()
        }-${
            endDate.getDateStringFromDate()
        } ${
            endDate.getMonthFromDate()
        }"
        viewModel.getMonthlyTask(getUserEmail(), deviceId(), startDate, endDate)
    }

    fun setHeaderForWeekViewAndFetchTasks(startDate: Date, endDate: Date) {
        /** setHeader for selected date and fetch tasks for selected **/
        binding.tvToday.text = "Week,"
        binding.tvDate.text = "${
            startDate.getDateStringFromDate()
        } ${
            startDate.getMonthFromDate()
        }-${
            endDate.getDateStringFromDate()
        } ${
            endDate.getMonthFromDate()
        }"
        viewModel.getMonthlyTask(getUserEmail(), deviceId(), startDate, endDate)
    }

    fun getStartAndEndDateOfWeek(month: Int, year: Int, weekIndex: Int): Pair<Date, Date> {
        val dates = getEnabledDatesForWeekView(month + 1, year, weekIndex)
        return Pair(dates.first(), dates.last())
    }

    fun getSelectedDate(): Date {
        val day = binding.calendar.selectedDay
        val calender = Calendar.getInstance()
        calender.set(day!!.year, day.month, day.day)
        return calender.time
    }


}
