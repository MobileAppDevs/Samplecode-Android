package com.enkefalostechnologies.calendarpro.ui.viewTaskScreen

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.amplifyframework.datastore.DataStoreException
import com.amplifyframework.datastore.generated.model.ReminderEnum
import com.amplifyframework.datastore.generated.model.RepeatDays
import com.amplifyframework.datastore.generated.model.RepeatType
import com.amplifyframework.datastore.generated.model.TaskType
import com.amplifyframework.datastore.generated.model.Tasks
import com.amplifyframework.datastore.generated.model.User
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.constant.Constants
import com.enkefalostechnologies.calendarpro.databinding.ActivityViewTaskBinding
import com.enkefalostechnologies.calendarpro.databinding.MsgDialogBinding
import com.enkefalostechnologies.calendarpro.model.TaskModel
import com.enkefalostechnologies.calendarpro.ui.base.BaseActivity
import com.enkefalostechnologies.calendarpro.ui.bottomSheet.DeleteOptionBottomSheet
import com.enkefalostechnologies.calendarpro.ui.bottomSheet.OptionListener
import com.enkefalostechnologies.calendarpro.ui.bottomSheet.UpdateOptionBottomSheet
import com.enkefalostechnologies.calendarpro.ui.updateTaskActivity.UpdateTaskActivity
import com.enkefalostechnologies.calendarpro.ui.viewModel.ViewTaskActivityViewModel
import com.enkefalostechnologies.calendarpro.util.AppUtil.isNotificationPermissionAvailable
import com.enkefalostechnologies.calendarpro.util.AppUtil.requestPermission
import com.enkefalostechnologies.calendarpro.util.AppUtil.showToast
import com.enkefalostechnologies.calendarpro.util.Extension.formatDateToDDMMYY
import com.enkefalostechnologies.calendarpro.util.Extension.formatDateToDDMMYYHHMMSSA
import com.enkefalostechnologies.calendarpro.util.Extension.formatDateToHHMMSS
import com.enkefalostechnologies.calendarpro.util.Extension.gone
import com.enkefalostechnologies.calendarpro.util.Extension.visible
import com.enkefalostechnologies.calendarpro.util.MsgAlertDialogListener
import com.enkefalostechnologies.calendarpro.util.dialog.messageAlertDialog


class ViewTaskActivity : BaseActivity<ActivityViewTaskBinding>(R.layout.activity_view_task) {

    val viewModel: ViewTaskActivityViewModel by viewModels { ViewTaskActivityViewModel.Factory }
    var task: Tasks? = null
    var isSeriesEditSelected:Boolean?=null
    override fun onViewBindingCreated() {
        setListeners()
        viewModel.getTasKDetail(intent.extras?.get(Constants.INTENT_TASK_ID).toString())
    }

    val taskDetailObserver=Observer<Tasks> { task ->
        setData(task)
    }
//    val userDataObserver=Observer<User> { user ->
//        viewModel.user = user
////        viewModel.getTasKDetail(intent.extras?.get(Constants.INTENT_TASK_ID).toString())
//    }
//    val userAttributeObserver=Observer<com.enkefalostechnologies.calendarpro.model.User> { user ->
//        viewModel.fetchUserData(user.userId)
//    }
//    val loginSessionObserver=Observer<Boolean> {
//        if (it) {
//            viewModel.fetchUserAttributes()
//        }
//        viewModel.isLoggedIn = it
//        viewModel.user = null
//        viewModel.getTasKDetail(intent.extras?.get(Constants.INTENT_TASK_ID).toString())
//    }
    val isTaskDeletedObserver=Observer<Boolean>{
        finish()
        showToast(Constants.TOAST_TASK_DELETED_SUCCESSFULLY,Toast.LENGTH_SHORT)
    }
    val isAllTaskDeletedObserver=Observer<Boolean>{
        finish()
        showToast(Constants.TOAST_TASK_DELETED_SUCCESSFULLY,Toast.LENGTH_SHORT)
    }
    val isTaskMarkedDoneObserver=Observer<TaskModel>{
        showToast(Constants.TOAST_TASK_MARKED_AS_DONE,Toast.LENGTH_SHORT)
        setResult(Activity.RESULT_OK, Intent())
        finish()
    }
    val isTaskMarkedUnDoneObserver=Observer<TaskModel>{
        showToast(Constants.TOAST_TASK_MARKED_AS_UN_DONE_SUCCESSFULLY,Toast.LENGTH_SHORT)
        setResult(Activity.RESULT_OK, Intent())
        finish()
    }

    val onErrorObserver=Observer<DataStoreException>{
        finish()
    }
    override fun addObserver() {
        viewModel.taskDetail.observe(this,taskDetailObserver)
        viewModel.onError.observe(this,onErrorObserver)
//        viewModel.userData.observe(this, userDataObserver)
//        viewModel.userAttributes.observe(this,userAttributeObserver )
//        viewModel.loginSession.observe(this,loginSessionObserver )
        viewModel.isTaskDeleted.observe(this,isTaskDeletedObserver)
        viewModel.isAllTaskDeleted.observe(this,isAllTaskDeletedObserver)
        viewModel.isTaskMarkedDone.observe(this,isTaskMarkedDoneObserver)
        viewModel.isTaskMarkedUnDone.observe(this,isTaskMarkedUnDoneObserver)
    }

    override fun removeObserver() {
        viewModel.taskDetail.removeObserver(taskDetailObserver)
//        viewModel.userData.removeObserver(userDataObserver)
//        viewModel.userAttributes.removeObserver(userAttributeObserver )
//        viewModel.loginSession.removeObserver(loginSessionObserver )
        viewModel.onError.removeObserver(onErrorObserver)
        viewModel.isTaskDeleted.removeObserver(isTaskDeletedObserver)
        viewModel.isAllTaskDeleted.removeObserver(isAllTaskDeletedObserver)
        viewModel.isTaskMarkedDone.removeObserver(isTaskMarkedDoneObserver)
        viewModel.isTaskMarkedUnDone.removeObserver(isTaskMarkedUnDoneObserver)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.ivBack -> {
                finish()
            }

            binding.ivMore -> {
                openMenuPopup(p0)
            }

            binding.btnEdit -> {
                if(!isNotificationPermissionAvailable()){
                    val permissionList = ArrayList<String>()
                    permissionList.add(Manifest.permission.POST_NOTIFICATIONS)
                    ActivityCompat.requestPermissions(
                        this,
                        permissionList.toTypedArray<String>(),
                        1234
                    )
                    return
                }
                if(task?.repeatType!= RepeatType.NONE) {
                    UpdateOptionBottomSheet(object : OptionListener {
                        override fun onOption1Selected() {
                            isSeriesEditSelected=false
                            startActivityForResult(
                                Intent(this@ViewTaskActivity, UpdateTaskActivity::class.java)
                                    .putExtra(Constants.INTENT_TASK_ID, task?.id)
                                    .putExtra("updateAll", true)
                                    .putExtra(
                                        Constants.INTENT_FROM_LIST_GROUP,
                                        intent.extras?.getBoolean(Constants.INTENT_FROM_LIST_GROUP)
                                    ), 1001
                            )

                        }

                        override fun onOption2Selected() {
                            isSeriesEditSelected=true
                            startActivityForResult(
                                Intent(this@ViewTaskActivity, UpdateTaskActivity::class.java)
                                    .putExtra(Constants.INTENT_TASK_ID, task?.id)
                                    .putExtra("updateAll", false)
                                    .putExtra(
                                        Constants.INTENT_FROM_LIST_GROUP,
                                        intent.extras?.getBoolean(Constants.INTENT_FROM_LIST_GROUP)
                                    ), 1001
                            )
                        }

                    }).show(supportFragmentManager, "UpdateBottomSheet")
                }else{
                    startActivityForResult(
                        Intent(this@ViewTaskActivity, UpdateTaskActivity::class.java)
                            .putExtra(Constants.INTENT_TASK_ID, task?.id)
                            .putExtra("updateAll", true)
                            .putExtra(
                                Constants.INTENT_FROM_LIST_GROUP,
                                intent.extras?.getBoolean(Constants.INTENT_FROM_LIST_GROUP)
                            ), 1001
                    )
                }

            }

        }

    }

    private fun setListeners() {
        binding.ivBack.setOnClickListener(this)
        binding.ivMore.setOnClickListener(this)
        binding.btnEdit.setOnClickListener(this)
    }

    @SuppressLint("RestrictedApi")
    private fun openMenuPopup(p0: View) {
        val menuBuilder = MenuBuilder(this@ViewTaskActivity)
        val inflater = MenuInflater(this)
        inflater.inflate(if(task?.isCompleted==true) R.menu.menu_undone else R.menu.task_menu, menuBuilder)

        val optionMenu = MenuPopupHelper(this@ViewTaskActivity, menuBuilder, p0);
        optionMenu.setForceShowIcon(true);
        menuBuilder.setCallback(object : MenuBuilder.Callback {
            override fun onMenuItemSelected(menu: MenuBuilder, item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.markAsDone -> {
                        task?.let {
                            viewModel.markDone( TaskModel(0,it)
                            )
                        }
                    }
                    R.id.markAsUnDone -> {
                        task?.let {
                            viewModel.markUnDone( TaskModel(0,it))
                        }
                    }
                    R.id.delete -> {
                        if(task?.repeatType!= RepeatType.NONE) {
                            DeleteOptionBottomSheet(object : OptionListener {
                                override fun onOption1Selected() {
                                    messageAlertDialog(
                                        MsgDialogBinding.inflate(LayoutInflater.from(this@ViewTaskActivity)),
                                        title = task?.title ?:"Alert",
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
                                        MsgDialogBinding.inflate(LayoutInflater.from(this@ViewTaskActivity)),
                                        title = task?.title ?:"Alert",
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
                        }else{
                            messageAlertDialog(
                                MsgDialogBinding.inflate(LayoutInflater.from(this@ViewTaskActivity)),
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==1001 && resultCode==Activity.RESULT_OK) {
            finish()
        }else if (requestCode == 1001 && resultCode == Activity.RESULT_CANCELED) {
            isSeriesEditSelected?.let {
                if(it){
                    finish()
                }else{
                    viewModel.getTasKDetail(intent.extras?.get(Constants.INTENT_TASK_ID).toString())
                }
            }

        }

    }

    private fun setData(task: Tasks) {
        this.task=task
        if(task.isCompleted){
            binding.tvTitle.paintFlags = binding.tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }
        binding.tvTitle.text = task?.title
        binding.tvTitle.visible()
        binding.tvDescription.visible()
        if(!task.description.isBlank()){
            val sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                binding.etDescription.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.textarea_bg) );
            } else {
                binding.etDescription.setBackground(ContextCompat.getDrawable(this, R.drawable.textarea_bg));
            }
        }else{
            val sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                binding.etDescription.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.textarea_bg_white) );
            } else {
                binding.etDescription.setBackground(ContextCompat.getDrawable(this, R.drawable.textarea_bg_white));
            }
        }
        binding.etDescription.setText(task?.description)
        binding.etDescription.visible()
        if (task?.taskType == TaskType.CALL_EMAIL) {
            binding.tvEmailPhone.visible()
            binding.etEmailPhone.setText(task?.emailOrPhone)
            binding.etEmailPhone.visible()
        } else {
            binding.tvEmailPhone.gone()
            binding.etEmailPhone.gone()
        }
        when (task?.taskType) {
            TaskType.CALL_EMAIL -> {
                binding.radioGroup.check(R.id.rbCallsEmails)

            }

            TaskType.TODO -> {
                binding.radioGroup.check(R.id.rbTodo)
            }

            else -> {

            }
        }
        binding.tvSelectCategory.text="Category"
        binding.tvSelectCategory.visible()
        binding.radioGroup.visible()
        binding.rbTodo.isClickable = false
        binding.rbCallsEmails.isClickable = false

        var txt = if (task?.repeatDays?.isEmpty() == true) "Add" else ""
        if (task?.repeatDays?.isNotEmpty() == true) {
            task?.repeatDays?.map {
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
            binding.tvRepeatAdd.text = if(task?.repeatType==RepeatType.NONE) "Add" else task?.repeatType?.name?.capitalize()
        }
        if(task.reminder!=ReminderEnum.NONE) {
            binding.tvReminderDone.text = task.customTime.toDate().formatDateToDDMMYYHHMMSSA()
        }else{
            binding.tvReminderDone.text = getString(R.string.add)
        }
        binding.tvDate.text = task.date?.toDate()?.formatDateToDDMMYY()
        if(task.isTimeSelected) {
            binding.tvTime.text = task.time?.toDate()?.formatDateToHHMMSS()
        }
        binding.ll1.visible()
        binding.ll2.visible()
        if (task?.isImportant == true) {
            binding.ivImportant.setImageDrawable(
                AppCompatResources.getDrawable(
                    binding.ivImportant.context,
                    R.drawable.ic_star
                )
            )
        } else {
            binding.ivImportant.setImageDrawable(
                AppCompatResources.getDrawable(
                    binding.ivImportant.context,
                    R.drawable.ic_star_unselected
                )
            )
        }
        binding.linearLayout3.visible()
        binding.linearLayout2.visible()
        binding.linearLayout.visible()
        binding.btnEdit.visible()
        if(task.isCompleted){
            binding.btnEdit.gone()
        }
//                            dialog.close()


//        val animation: Animation = AnimationUtils.loadAnimation(this, R.anim.fall_down)
//        for (i in 0 until binding.scrollView.childCount) {
//            val child: View = binding.scrollView.getChildAt(i)
//            child.startAnimation(animation)
//        }
    }


}