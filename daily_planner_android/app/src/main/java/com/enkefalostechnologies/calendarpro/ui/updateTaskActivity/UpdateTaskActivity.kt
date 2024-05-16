package com.enkefalostechnologies.calendarpro.ui.updateTaskActivity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.amplifyframework.datastore.generated.model.ReminderEnum
import com.amplifyframework.datastore.generated.model.RepeatDays
import com.amplifyframework.datastore.generated.model.RepeatType
import com.amplifyframework.datastore.generated.model.TaskType
import com.amplifyframework.datastore.generated.model.Tasks
import com.amplifyframework.datastore.generated.model.User
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.constant.Constants
import com.enkefalostechnologies.calendarpro.databinding.ActivityUpdateTaskBinding
import com.enkefalostechnologies.calendarpro.databinding.DateAndTimePickerBinding
import com.enkefalostechnologies.calendarpro.databinding.MsgDialogBinding
import com.enkefalostechnologies.calendarpro.databinding.ReminderDialogBinding
import com.enkefalostechnologies.calendarpro.databinding.RepeatDialogBinding
import com.enkefalostechnologies.calendarpro.model.DateTimePickerModel
import com.enkefalostechnologies.calendarpro.model.TaskModel
import com.enkefalostechnologies.calendarpro.model.TaskModel2
import com.enkefalostechnologies.calendarpro.ui.PhoneBookActivity
import com.enkefalostechnologies.calendarpro.ui.base.BaseActivity
import com.enkefalostechnologies.calendarpro.ui.bottomSheet.DeleteOptionBottomSheet
import com.enkefalostechnologies.calendarpro.ui.bottomSheet.OptionListener
import com.enkefalostechnologies.calendarpro.ui.bottomSheet.UpdateOptionBottomSheet
import com.enkefalostechnologies.calendarpro.ui.viewModel.ViewTaskActivityViewModel
import com.enkefalostechnologies.calendarpro.util.AppUtil.getDateAfter
import com.enkefalostechnologies.calendarpro.util.AppUtil.getDateAfterMonths
import com.enkefalostechnologies.calendarpro.util.AppUtil.getDateAfterWeeks
import com.enkefalostechnologies.calendarpro.util.AppUtil.getDateAfterYears
import com.enkefalostechnologies.calendarpro.util.AppUtil.getDateStringFromDate
import com.enkefalostechnologies.calendarpro.util.AppUtil.getMonthFromDate
import com.enkefalostechnologies.calendarpro.util.AppUtil.getYearYYYYFromDate
import com.enkefalostechnologies.calendarpro.util.AppUtil.requestPermission
import com.enkefalostechnologies.calendarpro.util.AppUtil.showToast
import com.enkefalostechnologies.calendarpro.util.DateTimeHelper.minuteBefore
import com.enkefalostechnologies.calendarpro.util.DateTimePickerListener
import com.enkefalostechnologies.calendarpro.util.DialogUtil.dateAndTimePicker
import com.enkefalostechnologies.calendarpro.util.DialogUtil.reminderDialog
import com.enkefalostechnologies.calendarpro.util.Extension.formatDateToDDMMYY
import com.enkefalostechnologies.calendarpro.util.Extension.formatDateToDDMMYYHHMMSSA
import com.enkefalostechnologies.calendarpro.util.Extension.formatDateToHHMMSS
import com.enkefalostechnologies.calendarpro.util.Extension.getNextDayFromDate
import com.enkefalostechnologies.calendarpro.util.Extension.gone
import com.enkefalostechnologies.calendarpro.util.Extension.visible
import com.enkefalostechnologies.calendarpro.util.MsgAlertDialogListener
import com.enkefalostechnologies.calendarpro.util.ReminderDialogListener
import com.enkefalostechnologies.calendarpro.util.dialog.messageAlertDialog
import com.enkefalostechnologies.calendarpro.util.dialog.repeatDialog
import com.enkefalostechnologies.calendarpro.util.repeatDialogListener
import java.util.Date

class UpdateTaskActivity : BaseActivity<ActivityUpdateTaskBinding>(R.layout.activity_update_task) {

    val viewModel: ViewTaskActivityViewModel by viewModels { ViewTaskActivityViewModel.Factory }
    var isMarkedImportant: Boolean = false
    var isNormalTask: Boolean = true
    var reminderEnum: ReminderEnum? = null
    var repeatTypes: RepeatType = RepeatType.NONE
    var repeatDayss = listOf<RepeatDays>()
    var selectedDate: Date? = null
    var endDate: Date? = null
    var repeatCount: Int? = 1
    var customDateTime: Date? = null
    var task: Tasks? = null
    var isTimeSelected: Boolean = false
    var singleOccurrance: Boolean? = null
    var isContactCategorySelected: Boolean = false
    var contactSelected: String = ""

    override fun onViewBindingCreated() {
//        viewModel.checkSessionValue()
        setListeners()
        viewModel.getTasKDetail(intent.extras?.get(Constants.INTENT_TASK_ID).toString())
    }

    private fun setListeners() {
        binding.ivBack.setOnClickListener(this)
        binding.ivMore.setOnClickListener(this)
        binding.btnUpdate.setOnClickListener(this)
        binding.tvReminderDone.setOnClickListener(this)
        binding.tvRepeatAdd.setOnClickListener(this)
        binding.ivImportant.setOnClickListener(this)
        binding.tvDate.setOnClickListener(this)
        binding.tvTime.setOnClickListener(this)
        binding.ivEmailPhone.setOnClickListener(this)

    }

    val taskDetailObserver = Observer<Tasks> { task ->
        setData(task)
    }

    //    val userDataObserver = Observer<User> { user ->
//        viewModel.user = user
//        viewModel.getTasKDetail(intent.extras?.get(Constants.INTENT_TASK_ID).toString())
//    }
//    val userAttributesObserver =
//        Observer<com.enkefalostechnologies.calendarpro.model.User> { user ->
//            viewModel.fetchUserData(user.userId)
//        }
//    val loginSessionObserver = Observer<Boolean> {
//        if (it) {
//            viewModel.fetchUserAttributes()
//        }
//        viewModel.isLoggedIn = it
//        viewModel.user = null
//        viewModel.getTasKDetail(intent.extras?.get(Constants.INTENT_TASK_ID).toString())
//    }
    val isTaskDeletedObserver = Observer<Boolean> {
        setResult(Activity.RESULT_OK, Intent())
        showToast(Constants.TOAST_TASK_DELETED_SUCCESSFULLY)
        finish()

    }
    val isTaskMarkedDoneObserver = Observer<TaskModel> {
        setResult(Activity.RESULT_OK, Intent())
        showToast(Constants.TOAST_TASK_MARKED_AS_DONE)
        finish()
    }
    val isTaskMarkedUnDoneObserver = Observer<TaskModel> {
        setResult(Activity.RESULT_OK, Intent())
        showToast(Constants.TOAST_TASK_MARKED_AS_UN_DONE_SUCCESSFULLY)
        finish()
    }
    val isTaskUpdatedObserver = Observer<MutableList<Tasks>> { task ->
//            task?.let { it1 -> updateScheduledTaskReminder(it1) }
        //  showToast(Constants.TOAST_TASK_UPDATED_SUCCESSFULLY,Toast.LENGTH_SHORT)
        setResult(Activity.RESULT_OK, Intent())
        finish()
    }
    val isTaskCreatedObserver = Observer<MutableList<Tasks>> { task ->
        setResult(Activity.RESULT_OK, Intent())
        finish()
//            task?.let { it1 -> updateScheduledTaskReminder(it1) }
        //  showToast(Constants.TOAST_TASK_UPDATED_SUCCESSFULLY,Toast.LENGTH_SHORT)

    }
    val isAllTaskDeletedObserver = Observer<Boolean> { task ->
        setResult(Activity.RESULT_OK, Intent())
        //  showToast(Constants.TOAST_TASK_UPDATED_SUCCESSFULLY,Toast.LENGTH_SHORT)
        finish()
    }

    override fun addObserver() {
        viewModel.taskDetail.observe(this, taskDetailObserver)
//        viewModel.userData.observe(this, userDataObserver)
//        viewModel.userAttributes.observe(this, userAttributesObserver)
//        viewModel.loginSession.observe(this, loginSessionObserver)
        viewModel.isTaskDeleted.observe(this, isTaskDeletedObserver)
        viewModel.isTaskMarkedDone.observe(this, isTaskMarkedDoneObserver)
        viewModel.isTaskMarkedUnDone.observe(this, isTaskMarkedUnDoneObserver)
        viewModel.isTaskUpdated.observe(this, isTaskUpdatedObserver)
        viewModel.isTaskCreated.observe(this, isTaskCreatedObserver)
        viewModel.isAllTaskDeleted.observe(this, isAllTaskDeletedObserver)
    }

    override fun removeObserver() {
        viewModel.taskDetail.removeObserver(taskDetailObserver)
//        viewModel.userData.removeObserver(userDataObserver)
//        viewModel.userAttributes.removeObserver(userAttributesObserver)
//        viewModel.loginSession.removeObserver(loginSessionObserver)
        viewModel.isTaskDeleted.removeObserver(isTaskDeletedObserver)
        viewModel.isTaskMarkedDone.removeObserver(isTaskMarkedDoneObserver)
        viewModel.isTaskMarkedUnDone.removeObserver(isTaskMarkedUnDoneObserver)
        viewModel.isTaskUpdated.removeObserver(isTaskUpdatedObserver)
        viewModel.isTaskCreated.removeObserver(isTaskCreatedObserver)
        viewModel.isAllTaskDeleted.removeObserver(isAllTaskDeletedObserver)
    }

    private var resultLauncherContacts =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            isContactCategorySelected = true
            if (result.resultCode == Activity.RESULT_OK) {
                contactSelected = result.data?.getStringExtra("Contact").toString()
                binding.etEmailPhone.setText(contactSelected)
            }
        }

    @SuppressLint("RestrictedApi")
    override fun onClick(p0: View?) {
        when (p0) {
            binding.ivBack -> {
                setResult(Activity.RESULT_CANCELED, Intent());
                finish()
            }

            binding.btnUpdate -> {
                if (isFormValidated()) {
                    updateTask()
                }
            }

            binding.ivEmailPhone -> {
                resultLauncherContacts.launch(Intent(this, PhoneBookActivity::class.java))
            }

            binding.tvReminderDone -> {
                if (reminderEnum == ReminderEnum.NONE && !isTimeSelected) {
                    showToast("Please select time first...")
                    return
                }
                reminderDialog(
                    customTime = customDateTime!!,
                    reminder = reminderEnum ?: ReminderEnum.NONE,
                    view = ReminderDialogBinding.inflate(LayoutInflater.from(this)).root,
                    listener = object : ReminderDialogListener {
                        override fun onDoneClicked(
                            dialog: Dialog,
                            reminder: ReminderEnum?,
                            reminderTime: Date
                        ) {
                            reminderEnum = reminder
                            customDateTime = when (reminderEnum) {
                                ReminderEnum.MIN05 -> selectedDate?.minuteBefore(5)
                                ReminderEnum.MIN10 -> selectedDate?.minuteBefore(10)
                                ReminderEnum.MIN30 -> selectedDate?.minuteBefore(30)
                                ReminderEnum.MIN60 -> selectedDate?.minuteBefore(60)
                                ReminderEnum.CUSTOM -> reminderTime
                                else -> selectedDate
                            }
                            binding.tvReminderDone.text =
                                customDateTime?.formatDateToDDMMYYHHMMSSA()
//                            /** reset repeat when user changes reminder**/
//                            repeatTypes = RepeatType.NONE
//                            repeatDayss = mutableListOf()
//                            repeatCount = 1
//                            endDate = selectedDate?.getNextDayFromDate()
//                            binding.tvRepeatAdd.text = "Add"
                            dialog.dismiss()

                        }

                    }
                ).show()
            }

            binding.tvDate -> {
                dateAndTimePicker(
                    DateTimePickerModel(
                        dateTime = selectedDate!!,
                        isDateSelected = true,
                        isTimeSelected = false
                    ),
                    DateAndTimePickerBinding.inflate(
                        LayoutInflater.from(this)
                    ).root,
                    object : DateTimePickerListener {
                        override fun onDateTimeSelected(dateTimePickerModel: DateTimePickerModel) {
                            selectedDate = dateTimePickerModel.dateTime
                            if (dateTimePickerModel.isDateSelected == true) {
                                binding.tvDate.text =
                                    dateTimePickerModel.dateTime.formatDateToDDMMYY()
                            }
                            if (dateTimePickerModel.isTimeSelected == true) {
                                binding.tvTime.text =
                                    dateTimePickerModel.dateTime.formatDateToHHMMSS()
                                isTimeSelected = true
                            }
                            /** reset reminder when user changes date **/
                            reminderEnum = ReminderEnum.NONE
                            customDateTime = selectedDate
                            binding.tvReminderDone.text = "Add"
                            /** reset repeat when user changes reminder**/
                            repeatTypes = RepeatType.NONE
                            repeatDayss = mutableListOf()
                            repeatCount = 1
                            endDate = selectedDate?.getNextDayFromDate()
                            binding.tvRepeatAdd.text = "Add"

                        }


                    }
                )
            }

            binding.tvTime -> {
                dateAndTimePicker(
                    DateTimePickerModel(
                        dateTime = selectedDate!!,
                        isDateSelected = false,
                        isTimeSelected = true
                    ),
                    DateAndTimePickerBinding.inflate(
                        LayoutInflater.from(this)
                    ).root,
                    object : DateTimePickerListener {
                        override fun onDateTimeSelected(dateTimePickerModel: DateTimePickerModel) {
                            selectedDate = dateTimePickerModel.dateTime
                            if (dateTimePickerModel.isDateSelected == true) {
                                binding.tvDate.text =
                                    dateTimePickerModel.dateTime.formatDateToDDMMYY()
                            }
                            if (dateTimePickerModel.isTimeSelected == true) {
                                binding.tvTime.text =
                                    dateTimePickerModel.dateTime.formatDateToHHMMSS()
                                isTimeSelected = true
                            }
                            /** reset reminder when user changes date **/
                            reminderEnum = ReminderEnum.NONE
                            customDateTime = selectedDate
                            binding.tvReminderDone.text = "Add"
                            /** reset repeat when user changes reminder**/
                            repeatTypes = RepeatType.NONE
                            repeatDayss = mutableListOf()
                            repeatCount = 1
                            endDate = selectedDate?.getNextDayFromDate()
                            binding.tvRepeatAdd.text = "Add"

                        }


                    }
                )
            }

            binding.tvRepeatAdd -> {
                if (repeatTypes == RepeatType.NONE && !isTimeSelected) {
                    showToast("Please select time first...")
                    return
                }
                repeatDialog(
                    repeatTypes = repeatTypes,
                    repeatDay = repeatDayss.toMutableList(),
                    taskDate = selectedDate,
                    endDate = if (repeatTypes == RepeatType.NONE) null else endDate,
                    binding = RepeatDialogBinding.inflate(LayoutInflater.from(this)),
                    listener = object : repeatDialogListener {
                        override fun onDoneClicked(
                            repeatType: RepeatType,
                            repeatDays: List<RepeatDays>,
                            eDate: Date?,
                            rc: Int,
                            d: Dialog
                        ) {
                            d.dismiss()
                            repeatDayss = repeatDays
                            repeatTypes = repeatType
                            repeatCount = rc
                            endDate = eDate
                            var txt = if (repeatDays.isEmpty()) "Add" else ""
                            if (repeatDays.isNotEmpty()) {
                                repeatDays.map {
                                    txt += when (it) {
                                        RepeatDays.MON -> "Mon"
                                        RepeatDays.TUE -> "Tue"
                                        RepeatDays.WED -> "Wed"
                                        RepeatDays.THU -> "Thu"
                                        RepeatDays.FRI -> "Fri"
                                        RepeatDays.SAT -> "Sat"
                                        RepeatDays.SUN -> "Sun"
                                        else -> ""
                                    }
                                    txt += ", "
                                }
                                binding.tvRepeatAdd.text = txt.dropLast(2)
                                binding.tvRepeatAdd.isSingleLine = true
                                binding.tvRepeatAdd.ellipsize = TextUtils.TruncateAt.END
                            } else {
                                // binding.tvRepeatAdd.text = repeatType.name.capitalize()
                                binding.tvRepeatAdd.text =
                                    if (repeatType == RepeatType.NONE) "Add" else repeatType.name?.capitalize()
                            }
                            singleOccurrance = repeatTypes == RepeatType.NONE
                        }
                    },
                    repeatCount = repeatCount ?: 1
                ).show()
            }

            binding.ivMore -> {
                openMenuPopup(p0)
            }

            binding.ivImportant -> {
                if (!isMarkedImportant) {
                    binding.ivImportant.setImageDrawable(getDrawable(R.drawable.ic_star))
                    isMarkedImportant = true
                } else {
                    binding.ivImportant.setImageDrawable(getDrawable(R.drawable.ic_star_unselected))
                    isMarkedImportant = false
                }
            }

        }
    }


    @SuppressLint("RestrictedApi")
    fun openMenuPopup(p0: View) {
        val menuBuilder = MenuBuilder(this@UpdateTaskActivity)
        val inflater = MenuInflater(this)
        inflater.inflate(
            if (task?.isCompleted == true) R.menu.menu_undone else R.menu.task_menu,
            menuBuilder
        )

        val optionMenu = MenuPopupHelper(this@UpdateTaskActivity, menuBuilder, p0);
        optionMenu.setForceShowIcon(true);
        menuBuilder.setCallback(object : MenuBuilder.Callback {
            override fun onMenuItemSelected(menu: MenuBuilder, item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.markAsDone -> {
                        task?.let {
                            viewModel.markDone(TaskModel(0, it))
                        }
                    }

                    R.id.markAsUnDone -> {
                        task?.let {
                            viewModel.markUnDone(TaskModel(0, it))
                        }
                    }

                    R.id.delete -> {
                        if (task?.repeatType != RepeatType.NONE) {
                            DeleteOptionBottomSheet(object : OptionListener {
                                override fun onOption1Selected() {
                                    messageAlertDialog(
                                        MsgDialogBinding.inflate(LayoutInflater.from(this@UpdateTaskActivity)),
                                        title = task?.title ?: "Alert",
                                        negativeBtnText = "Cancel",
                                        msg = "This occurrence will be permanently deleted.",
                                        btnText = "Delete",
                                        listener = object : MsgAlertDialogListener {
                                            override fun onDoneClicked() {
                                                task?.id?.let {
                                                    viewModel.deleteTask(it)
                                                }
                                            }
                                        }).show()
                                }

                                override fun onOption2Selected() {
                                    messageAlertDialog(
                                        MsgDialogBinding.inflate(LayoutInflater.from(this@UpdateTaskActivity)),
                                        title = task?.title ?: "Alert",
                                        msg = "This series will be permanently deleted.",
                                        btnText = "Delete",
                                        negativeBtnText = "Cancel",
                                        listener = object : MsgAlertDialogListener {
                                            override fun onDoneClicked() {
                                                task?.taskId?.let {
                                                    viewModel.deleteAllTask(it)
                                                }
                                            }
                                        }).show()
                                }

                            }).show(supportFragmentManager, "DeleteBottomSheet")
                        } else {
                            messageAlertDialog(
                                MsgDialogBinding.inflate(LayoutInflater.from(this@UpdateTaskActivity)),
                                title = task?.title ?: "Alert",
                                negativeBtnText = "Cancel",
                                msg = "This occurrence will be permanently deleted.",
                                btnText = "Delete",
                                listener = object : MsgAlertDialogListener {
                                    override fun onDoneClicked() {
                                        task?.taskId?.let {
                                            viewModel.deleteAllTask(it)
                                        }
                                    }
                                }).show()

                        }
                    }
                }
                return true
            }

            override fun onMenuModeChange(menu: MenuBuilder) {

            }
        })


        optionMenu.show();

    }

    private fun isFormValidated(): Boolean {
        if (binding.etTitle.text.toString().trim().isBlank()) {
            showToast("Enter valid title...")
            return false
        }
        if (selectedDate == null) {
            showToast("Select date...")
            return false
        }
        if ((reminderEnum != ReminderEnum.NONE && !isTimeSelected) || (repeatTypes != RepeatType.NONE && !isTimeSelected)) {
            showToast("Time should not be empty...")
            return false
        }
        if (repeatTypes == RepeatType.WEEK && repeatDayss.isEmpty()) {
            showToast("Please select week days...")
            return false
        }
//        when (repeatTypes) {
//            RepeatType.NONE -> {
////                endDate = if (repeatCount == 1) endDate else selectedDate?.getNextDayFromDate()
//                endDate =selectedDate?.getNextDayFromDate()
//            }
//
//            RepeatType.DAY -> {
//                val eDate =selectedDate?.getDateAfter(repeatCount ?: 1)
//                if (endDate?.compareTo(eDate)!! != 0) {
//                    showToast("End date should be equal to ${eDate?.getDateStringFromDate()} ${eDate?.getMonthFromDate()} ${eDate?.getYearYYYYFromDate()}")
//                    return false
//                }
//                endDate = eDate
//            }
//
//            RepeatType.WEEK -> {
//                val eDate =  selectedDate?.getDateAfterWeeks(
//                    repeatCount ?: 1
//                )
//                if (endDate?.compareTo(eDate)!! != 0) {
//                    showToast("End date should be equal to ${eDate?.getDateStringFromDate()} ${eDate?.getMonthFromDate()} ${eDate?.getYearYYYYFromDate()}")
//                    return false
//                }
//                endDate = eDate
//            }
//
//            RepeatType.MONTH -> {
//                val eDate = if (repeatCount == 1) endDate else selectedDate?.getDateAfterMonths(
//                    repeatCount ?: 1
//                )
//                if (endDate?.compareTo(eDate)!! != 0) {
//                    showToast("End date should be equal to ${eDate?.getDateStringFromDate()} ${eDate?.getMonthFromDate()} ${eDate?.getYearYYYYFromDate()}")
//                    return false
//                }
//                endDate = eDate
//            }
//
//            RepeatType.YEAR -> {
//                val eDate = if (repeatCount == 1) endDate else selectedDate?.getDateAfterYears(
//                    repeatCount ?: 1
//                )
//                if (endDate?.compareTo(eDate)!! != 0) {
//                    showToast("End date should be equal to ${eDate?.getDateStringFromDate()} ${eDate?.getMonthFromDate()} ${eDate?.getYearYYYYFromDate()}")
//                    return false
//                }
//                endDate = eDate
//            }
//        }
        return true
    }


    private fun setData(task: Tasks) {
        this.task = task
        endDate = task.endDate?.toDate()
        isTimeSelected = task.isTimeSelected
        repeatCount = task.repeatCount
        binding.tvTitle.text = task.title
        binding.etTitle.setText(task.title)
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbTodo -> {
                    isContactCategorySelected = false
                    group.check(R.id.rbTodo)
                    binding.ivEmailPhone.gone()
                    binding.tvEmailPhone.gone()
                    binding.etEmailPhone.gone()
                    contactSelected=""
                    binding.etEmailPhone.setText(contactSelected)
                    isNormalTask = true
                }

                R.id.rbCallsEmails -> {
                    if (!hasPermissions(this, Manifest.permission.READ_CONTACTS)) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.READ_CONTACTS
                            )
                        ) {
                            messageAlertDialog(
                                MsgDialogBinding.inflate(LayoutInflater.from(this)),
                                title = "Permission",
                                msg = "Calendar Pro app will fetch contacts for your convenience.",
                                btnText = "Give Permission",
                                negativeBtnText = "Dismiss",
                                listener = object : MsgAlertDialogListener {
                                    override fun onDoneClicked() {
                                        dialog?.visible()
                                        val intent = Intent()

                                        // For API 29 (Android 10) and above, use the general settings screen
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                            intent.action = Settings.ACTION_APPLICATION_SETTINGS
                                        } else {
                                            // For API 28 (Android 9) and below, use the specific app details screen
                                            intent.action =
                                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                            val uri = Uri.fromParts(
                                                "package",
                                                application.packageName,
                                                null
                                            )
                                            intent.data = uri
                                        }
                                        startActivity(intent)
                                    }
                                }).show()
                        } else {
                            requestPermissions(
                                arrayOf(Manifest.permission.READ_CONTACTS),
                                1358
                            )
                        }
                        binding.ivEmailPhone.gone()
                        binding.tvEmailPhone.gone()
                        binding.etEmailPhone.gone()
                        isNormalTask = true
                        group.check(R.id.rbTodo)
                        return@setOnCheckedChangeListener
                    }
                    binding.ivEmailPhone.visible()
                    binding.tvEmailPhone.visible()
                    binding.etEmailPhone.visible()
                    isContactCategorySelected=true
                    contactSelected=task.emailOrPhone
                    binding.etEmailPhone.setText(task.emailOrPhone)
                    isNormalTask = false
                }
            }


        }
        if (isContactCategorySelected) {
            binding.radioGroup.check(R.id.rbCallsEmails)
        } else {
            when (task.taskType) {
                TaskType.CALL_EMAIL -> {
                    binding.radioGroup.check(R.id.rbCallsEmails)
                }

                TaskType.TODO -> {
                    binding.radioGroup.check(R.id.rbTodo)
                }

                else -> {

                }
            }
        }
        binding.tvSelectCategory.visible()
        binding.radioGroup.visible()
        if (task.taskType == TaskType.CALL_EMAIL || isContactCategorySelected) {
            binding.tvEmailPhone.visible()
            if (isContactCategorySelected) {
                binding.etEmailPhone.setText(contactSelected)
            }
            if(!isContactCategorySelected){
                contactSelected=task.emailOrPhone
                binding.etEmailPhone.setText(task.emailOrPhone)
                isContactCategorySelected=true
            }
            binding.etEmailPhone.visible()
            isNormalTask = false
        } else {
            isContactCategorySelected=false
            binding.tvEmailPhone.gone()
            binding.etEmailPhone.gone()
            contactSelected=""
            binding.etEmailPhone.setText(contactSelected)
            isNormalTask = true
        }
        binding.etDescription.setText(task.description)
        repeatTypes = task.repeatType!!
        repeatDayss = task.repeatDays
        reminderEnum = task.reminder
//        hour = task?.time?.toDate()?.hours!!
//        minutes = task?.time?.toDate()?.minutes!!
        var txt = if (task.repeatDays?.isEmpty() == true) "Add" else ""
        if (task.repeatDays?.isNotEmpty() == true) {
            task.repeatDays?.map {
                txt += when (it) {
                    RepeatDays.MON -> "Mon"
                    RepeatDays.TUE -> "Tue"
                    RepeatDays.WED -> "Wed"
                    RepeatDays.THU -> "Thu"
                    RepeatDays.FRI -> "Fri"
                    RepeatDays.SAT -> "Sat"
                    else -> ""
                }
                txt += ", "
            }
            binding.tvRepeatAdd.text = txt.dropLast(2)
            binding.tvRepeatAdd.isSingleLine = true
            binding.tvRepeatAdd.ellipsize = TextUtils.TruncateAt.END
        } else {
            //binding.tvRepeatAdd.text = repeatTypes.name.capitalize()
            binding.tvRepeatAdd.text =
                if (task?.repeatType == RepeatType.NONE) "Add" else task?.repeatType?.name?.capitalize()
        }
        if (task.reminder != ReminderEnum.NONE) {
            binding.tvReminderDone.text = task.customTime.toDate().formatDateToDDMMYYHHMMSSA()
        }
        binding.tvDate.text = task.date?.toDate()?.formatDateToDDMMYY()
        if (isTimeSelected) {
            binding.tvTime.text = task.time?.toDate()?.formatDateToHHMMSS()
        }
        selectedDate = task.time.toDate()
        customDateTime = task.customTime.toDate()
        if (task.isImportant == true) {
            isMarkedImportant = true
            binding.ivImportant.setImageDrawable(
                AppCompatResources.getDrawable(
                    binding.ivImportant.context,
                    R.drawable.ic_star
                )
            )
        } else {
            isMarkedImportant = false
            binding.ivImportant.setImageDrawable(
                AppCompatResources.getDrawable(
                    binding.ivImportant.context,
                    R.drawable.ic_star_unselected
                )
            )
        }
        intent.extras?.get("updateAll")?.let {
            singleOccurrance = (it as Boolean)
            if (it == true) {
                (0 until binding.linearLayout2.childCount).map {
                    binding.linearLayout2.getChildAt(it)?.isEnabled =
                        task.repeatType == RepeatType.NONE
                }
            }
        }
    }


    private fun updateTask() {
//        repeatCount = task?.repeatCount
        viewModel.updateTask(
            TaskModel2(
                id = task?.id!!,
                taskId = task?.taskId!!,
                deviceId = deviceId(),
                title = binding.etTitle.text.toString(),
                description = binding.etDescription.text.toString(),
                date = selectedDate!!,
                emailOrPhone = binding.etEmailPhone.text.toString(),
                hour = selectedDate!!.hours,
                minute = selectedDate!!.minutes,
                reminder = reminderEnum!!,
                repeatType = repeatTypes,
                repeatDays = repeatDayss,
                endDate = endDate ?: selectedDate!!.getNextDayFromDate(),
                isImportant = isMarkedImportant,
                taskType = if (isNormalTask) TaskType.TODO else TaskType.CALL_EMAIL,
                customTime = customDateTime!!,
                listGroupId = task?.listGroupId ?: "",
                repeatCount = repeatCount ?: 1,
                singleOccurrance = singleOccurrance ?: true,
                task = task!!,
                isTimeSelected = isTimeSelected,
                selectedDate =selectedDate!!,
                email = getUserEmail()
            )
        )
    }

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean {

        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }
}