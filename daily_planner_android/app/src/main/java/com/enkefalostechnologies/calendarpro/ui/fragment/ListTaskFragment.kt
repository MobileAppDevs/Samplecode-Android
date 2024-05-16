package com.enkefalostechnologies.calendarpro.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.amplifyframework.datastore.DataStoreException
import com.amplifyframework.datastore.generated.model.ReminderEnum
import com.amplifyframework.datastore.generated.model.RepeatDays
import com.amplifyframework.datastore.generated.model.RepeatType
import com.amplifyframework.datastore.generated.model.TaskType
import com.amplifyframework.datastore.generated.model.Tasks
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.constant.Constants
import com.enkefalostechnologies.calendarpro.databinding.CreateListDialogBinding
import com.enkefalostechnologies.calendarpro.databinding.FragmentListTaskBinding
import com.enkefalostechnologies.calendarpro.databinding.MsgDialogBinding
import com.enkefalostechnologies.calendarpro.databinding.ReminderDialogBinding
import com.enkefalostechnologies.calendarpro.databinding.RepeatDialogBinding
import com.enkefalostechnologies.calendarpro.model.TaskModel
import com.enkefalostechnologies.calendarpro.model.TaskModel2
import com.enkefalostechnologies.calendarpro.ui.HomeActivity
import com.enkefalostechnologies.calendarpro.ui.adapter.PendingTaskAdapter
import com.enkefalostechnologies.calendarpro.ui.base.BaseFragment
import com.enkefalostechnologies.calendarpro.ui.bottomSheet.NativeSharingBottomSheet
import com.enkefalostechnologies.calendarpro.ui.bottomSheet.NewTaskBottomSheet
import com.enkefalostechnologies.calendarpro.ui.bottomSheet.OptionListener
import com.enkefalostechnologies.calendarpro.ui.bottomSheet.UpdateOptionBottomSheet
import com.enkefalostechnologies.calendarpro.ui.updateTaskActivity.UpdateTaskActivity
import com.enkefalostechnologies.calendarpro.ui.viewModel.fragment.ListTaskFragmentViewModel
import com.enkefalostechnologies.calendarpro.ui.viewTaskScreen.ViewTaskActivity
import com.enkefalostechnologies.calendarpro.util.AmplifyUtil.handleExceptions
import com.enkefalostechnologies.calendarpro.util.AppUtil.getDayFromDate
import com.enkefalostechnologies.calendarpro.util.AppUtil.isNotificationPermissionAvailable
import com.enkefalostechnologies.calendarpro.util.AppUtil.requestPermission
import com.enkefalostechnologies.calendarpro.util.AppUtil.showToast
import com.enkefalostechnologies.calendarpro.util.DialogUtil.reminderDialogDisabled
import com.enkefalostechnologies.calendarpro.util.DialogUtil.renameListDialog
import com.enkefalostechnologies.calendarpro.util.Extension.close
import com.enkefalostechnologies.calendarpro.util.Extension.getNextDayFromDate
import com.enkefalostechnologies.calendarpro.util.Extension.gone
import com.enkefalostechnologies.calendarpro.util.Extension.visible
import com.enkefalostechnologies.calendarpro.util.MsgAlertDialogListener
import com.enkefalostechnologies.calendarpro.util.NewListCreateListener
import com.enkefalostechnologies.calendarpro.util.NewTaskBottomSheetListener
import com.enkefalostechnologies.calendarpro.util.ReminderDialogListener
import com.enkefalostechnologies.calendarpro.util.TaskAdapterItemListener
import com.enkefalostechnologies.calendarpro.util.dialog.messageAlertDialog
import com.enkefalostechnologies.calendarpro.util.dialog.repeatDialogDisabled
import com.enkefalostechnologies.calendarpro.util.repeatDialogListener
import java.util.Date
import java.util.UUID

class ListTaskFragment : BaseFragment<FragmentListTaskBinding>(R.layout.fragment_list_task) {
    val MY_PERMISSION_REQUEST_REMINDER=1234
    val viewModel: ListTaskFragmentViewModel by viewModels { ListTaskFragmentViewModel.Factory }

    lateinit var listGroupId: String
    lateinit var listGroupName: String
    var sheet:NewTaskBottomSheet?=null
    var taskAdapter = PendingTaskAdapter(object : TaskAdapterItemListener {
        override fun onItemClicked(task: Tasks) {
            requireActivity().startActivity(
                Intent(requireActivity(), ViewTaskActivity::class.java)
                    .putExtra(Constants.INTENT_TASK_ID, task.id)
                    .putExtra(Constants.INTENT_FROM_LIST_GROUP, true)
            )

        }

        override fun onTaskCheck(task: Tasks, position: Int) {
            if (!task.isCompleted) {
                markAsDone(task)
            } else {
                markAsUnDone(task)
            }
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
                        if(task?.repeatType!= RepeatType.NONE) {
                            UpdateOptionBottomSheet(object : OptionListener {
                                override fun onOption1Selected() {
                                    d.dismiss()
                                    startActivityForResult(
                                        Intent(requireActivity(), UpdateTaskActivity::class.java)
                                            .putExtra(Constants.INTENT_TASK_ID, task?.id)
                                            .putExtra("updateAll", true)
                                            .putExtra(
                                                Constants.INTENT_FROM_LIST_GROUP,task.listGroupId
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
                                                Constants.INTENT_FROM_LIST_GROUP,task.listGroupId
                                            ), 1001
                                    )
                                }

                            }).show(requireActivity().supportFragmentManager, "UpdateBottomSheet")
                        }else{
                            d.dismiss()
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
                }).show()
        }

        override fun onRepeatClicked(task: Tasks, position: Int) {
            //only required in case of incomplete tasks
            if (task.isCompleted) return

            requireActivity().repeatDialogDisabled(
                task.repeatType,
                task.repeatDays,
                task.date.toDate(),
                task.endDate.toDate(),
                RepeatDialogBinding.inflate(LayoutInflater.from(requireActivity())),
                object : repeatDialogListener {
                    override fun onDoneClicked(
                        repeatType: RepeatType, repeatDays: List<RepeatDays>, eDate: Date?, rc: Int, d:Dialog
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
                                            Constants.INTENT_FROM_LIST_GROUP,task.listGroupId
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

    })

    fun setTitle(title: String) {
        binding.tvTitle.text = title
        listGroupName = title
    }


    override fun setupViews() {
        //dialog.visible()
        binding.rvPendingTasks.adapter = taskAdapter
        arguments?.getString("title")?.let { setTitle(it) }
        arguments?.getString("listGroupId")?.let {
            listGroupId = it
        }
    }

    override fun setupListeners() {
        binding.ivBack.setOnClickListener {
            (requireActivity() as HomeActivity).openListFragment()
        }
        binding.ivMore.setOnClickListener { view ->
            openMenuPopup(view)
        }
        binding.fab.ibFabAdd.setOnClickListener {
            if(!requireActivity().isNotificationPermissionAvailable()){
                requestPermission(
                    Manifest.permission.POST_NOTIFICATIONS,
                    MY_PERMISSION_REQUEST_REMINDER
                )
                return@setOnClickListener
            }
            sheet=NewTaskBottomSheet(sD = Date(),object : NewTaskBottomSheetListener {
                override fun onDoneClicked(
                    sheet: Any,
                    taskType: TaskType,
                    title: String,
                    emailPhone: String,
                    description: String,
                    date: Date,
                    isTimeSelected:Boolean,
                    hours: Int,
                    minute: Int,
                    reminder: ReminderEnum?,
                    repeatType: RepeatType,
                    repeatDays: List<RepeatDays>,
                    isImportant: Boolean,
                    customDate: Date,
                    edate: Date?,
                    repeatCount:Int
                ) {
                    (sheet as NewTaskBottomSheet).dismiss()
                    viewModel.createTask(
                        TaskModel2(
                            taskId = UUID.randomUUID().toString(),
                            deviceId = deviceId(),
                            title = title,
                            description = description,
                            date = date,
                            emailOrPhone = emailPhone,
                            hour = hours,
                            minute = minute,
                            reminder = reminder!!,
                            repeatType = repeatType,
                            repeatDays = repeatDays,
                            isImportant = isImportant,
                            taskType = taskType,
                            customTime = customDate,
                            endDate = edate?: date.getNextDayFromDate(),
                            repeatCount =repeatCount,
                            listGroupId = listGroupId,
                            singleOccurrance = false,
                            task = null,
                            id = "",
                            isTimeSelected = isTimeSelected,
                            selectedDate = date,
                            email = getUserEmail()
                        )
                    )
                }
            })
            sheet?.show(childFragmentManager, "NewTaskBottomSheet")
        }
        binding.ivShare.setOnClickListener {
            NativeSharingBottomSheet().show(childFragmentManager, "nativeshare")
        }
    }

    override fun fetchInitialData() {
        viewModel.fetchListGroupTasks(getUserEmail(), deviceId(), listGroupId)
//        viewModel.checkSessionValue()
    }

    private val isRepeatTypeUpdatedObserver = Observer<TaskModel> { taskmodel ->
        Log.d("lifecycle","-----------------isRepeatTypeUpdatedObserver-------------------")
        viewModel.fetchListGroupTasks(getUserEmail(), deviceId(), listGroupId)
    }
    override fun setupObservers() {
        Log.d("lifecycle","-----------------setupObservers()-------------------")
        viewModel.isReminderUpdated.observe(this, isReminderUpdatedObserver)
//        viewModel.userData.observe(this, userDataObserver)
//        viewModel.userAttributes.observe(this,userAttributeObserver)
//        viewModel.loginSession.observe(this, loginSessionObserver)
        viewModel.isRepeatTypeUpdated.observe(this,isRepeatTypeUpdatedObserver)
        viewModel.listGroupTaskList.observe(this ,listGroupTaskObserver)
        viewModel.isTaskCreated.observe(this, isTaskCreated)
        viewModel.isListDeleted.observe(this, isListDeletedObserver)
        viewModel.onError.observe(this,onErrorObserver)
        amplifyDataModelUtil.observeTask{ task->
            if(task.item().listGroupId==listGroupId) {
                viewModel.fetchListGroupTasks(getUserEmail(), deviceId(), listGroupId)
            }
        }
    }
    private val onErrorObserver= Observer<DataStoreException> {
        dialog.close()
    }
    private val isListDeletedObserver = Observer<Boolean> {
        requireActivity().showToast(Constants.TOAST_LIST_GROUP_DELETED_SUCCESSFULLY,Toast.LENGTH_SHORT)
        (requireActivity() as HomeActivity).openListFragment()

    }

    private val isTaskCreated = Observer<MutableList<Tasks>> {task ->
//            requireActivity().scheduleTaskReminder(task)
//            requireActivity().scheduleNotification(task)
        if (sheet?.isVisible==true) {
            sheet?.dismiss()
        }
        viewModel.fetchListGroupTasks(
        getUserEmail(),
        deviceId(),
        listGroupId
        )
    }
    var list = mutableListOf<Tasks>()
    private val listGroupTaskObserver = Observer<List<Tasks>> { data ->
        list = data.sortedBy { it.date }.toMutableList()
        if (list.isEmpty()) {
            binding.tvNoTaskFound.visible()
            binding.rvPendingTasks.gone()
        } else {
            taskAdapter.setList(list)
            binding.tvNoTaskFound.gone()
            binding.rvPendingTasks.visible()
        }
        dialog.close()
    }

//    private val loginSessionObserver = Observer<Boolean> {
//        if (it) {
//        viewModel.fetchUserAttributes()
//    }
//        viewModel.isLoggedIn = it
//        viewModel.user = null
//        viewModel.fetchListGroupTasks(getUserId(), deviceId(), listGroupId)
//
//    }

//    private val userAttributeObserver = Observer<com.enkefalostechnologies.calendarpro.model.User> { user ->
//        viewModel.fetchUserData(user.userId)
// //        viewModel.fetchListGroupTasks(user.userId, deviceId(), listGroupId)
//    }

    private val isReminderUpdatedObserver = Observer<TaskModel> { taskmodel ->
        list[taskmodel.position] = taskmodel.task
        taskAdapter.notifyItemChanged(taskmodel.position)
        dialog.close()
    }

//    private val userDataObserver = Observer<User> { user ->
//        dialog.close()
//        viewModel.user = user
//        viewModel.fetchListGroupTasks(user.userId, deviceId(), listGroupId)
//    }

    override fun removeObserver() {
        Log.d("lifecycle","-----------------removeObserver()-------------------")
        viewModel.isReminderUpdated.removeObserver(isReminderUpdatedObserver)
        viewModel.isRepeatTypeUpdated.removeObserver(isRepeatTypeUpdatedObserver)
//        viewModel.userData.removeObserver(userDataObserver)
//        viewModel.userAttributes.removeObserver(userAttributeObserver)
//        viewModel.loginSession.removeObserver(loginSessionObserver)
        viewModel.listGroupTaskList.removeObserver(listGroupTaskObserver)
        viewModel.isTaskCreated.removeObserver(isTaskCreated)
        viewModel.isListDeleted.removeObserver(isListDeletedObserver)
        viewModel.onError.removeObserver(onErrorObserver)
    }

    @SuppressLint("RestrictedApi")
    fun openMenuPopup(p0: View) {
        val menuBuilder = MenuBuilder(requireActivity())
        val inflater = MenuInflater(requireActivity())
        inflater.inflate(R.menu.task_menu2, menuBuilder)

        val optionMenu = MenuPopupHelper(requireActivity(), menuBuilder, p0);
        optionMenu.setForceShowIcon(true);
        menuBuilder.setCallback(object : MenuBuilder.Callback {
            override fun onMenuItemSelected(menu: MenuBuilder, item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.rename -> {
                        requireActivity().renameListDialog(listGroupName,
                            CreateListDialogBinding.inflate(LayoutInflater.from(requireActivity())).root,
                            object : NewListCreateListener {
                                override fun onCreateClicked(dialog: Dialog, listName: String) {
                                    renameListGroup(listName, dialog)
                                }
                            }).show()
                    }

                    R.id.delete -> {
                        requireActivity().messageAlertDialog(
                            MsgDialogBinding.inflate(LayoutInflater.from(requireActivity())),
                            title="Alert",
                            msg="Are you sure want to delete this List? All the tasks related to this list will be deleted.",
                            btnText="Delete",
                            negativeBtnText = "Cancel",
                            listener = object : MsgAlertDialogListener {
                                override fun onDoneClicked() {
                                    viewModel.deleteList(listGroupId)
                                }
                            }).show()

                    }
                }
                return true
            }

            override fun onMenuModeChange(menu: MenuBuilder) {

            }
        })


        optionMenu.show();

    }


    fun markAsDone(task: Tasks) {
            //dialog.visible()
            amplifyDataModelUtil.markTaskDone(
                taskId = task.id,
                email = getUserEmail(),
                deviceId=deviceId(),
                {
                    requireActivity().runOnUiThread {
                        dialog.close()
                        //requireActivity().showToast(Constants.TOAST_TASK_MARKED_AS_DONE_SUCCESSFULLY)
                        viewModel.fetchListGroupTasks(
                            getUserEmail(),
                            deviceId(),
                            listGroupId
                        )
                    }
                },
                {
                    requireActivity().runOnUiThread {
                        requireActivity().handleExceptions(it)
                    }
                })

    }

    private fun markAsUnDone(task: Tasks) {
            //dialog.visible()
            amplifyDataModelUtil.markTaskAsUnDone(
                taskId = task.id,
                email = getUserEmail(),
                deviceId=deviceId(),
                {
                    requireActivity().runOnUiThread {
                        dialog.close()
                        // requireActivity().showToast(Constants.TOAST_TASK_MARKED_AS_UN_DONE_SUCCESSFULLY)
                        viewModel.fetchListGroupTasks(
                            getUserEmail(),
                            deviceId(),
                            listGroupId
                        )
                    }
                },
                {
                    requireActivity().runOnUiThread {
                        requireActivity().handleExceptions(it)
                    }
                })

    }

    fun renameListGroup(listName: String, renameDialog: Dialog) {
            //dialog.visible()
            amplifyDataModelUtil.renameListGroup(
                listName,
                listGroupId,
                {
                    requireActivity().runOnUiThread {
                        dialog.close()
                        renameDialog.close()
                        // requireActivity().showToast(Constants.TOAST_LIST_GROUP_RENAMED_SUCCESSFULLY)
                        binding.tvTitle.text = listName
                        listGroupName = listName
//                                                (requireActivity() as HomeActivity).openListFragment()
                    }
                },
                {
                    requireActivity().runOnUiThread {
                        requireActivity().handleExceptions(it)
                    }
                })
    }


    fun isCorrectDateToCreateTask(
        repeatType: RepeatType,
        repeatDays: List<RepeatDays>,
        taskDate: Date
    ): Boolean {
        return (repeatType == RepeatType.WEEK && repeatDays.contains(
            getRepeatDays(taskDate)
        )) || repeatType == RepeatType.DAY || repeatType == RepeatType.MONTH || repeatType == RepeatType.YEAR
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


    override fun onPause() {
        super.onPause()
        Log.i("Lifecycle", "ListTaskFragment -> onPause()")
        removeObserver()
    }

    override fun onResume() {
        Log.i("Lifecycle", "ListTaskFragment -> onResume()")
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        Log.i("Lifecycle", "ListTaskFragment -> onStop()")
    }

    override fun onDestroy() {
        Log.i("Lifecycle", "ListTaskFragment -> onDestroy()")
        super.onDestroy()
    }
}