package com.enkefalostechnologies.calendarpro.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.DataStoreException
import com.amplifyframework.datastore.generated.model.KnowYourDay
import com.amplifyframework.datastore.generated.model.ReminderEnum
import com.amplifyframework.datastore.generated.model.RepeatDays
import com.amplifyframework.datastore.generated.model.RepeatType
import com.amplifyframework.datastore.generated.model.SubscriptionStatus
import com.amplifyframework.datastore.generated.model.TaskType
import com.amplifyframework.datastore.generated.model.Tasks
import com.amplifyframework.datastore.generated.model.WaterInTake
import com.android.billingclient.api.Purchase
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.SubscriptionType
import com.enkefalostechnologies.calendarpro.constant.Constants
import com.enkefalostechnologies.calendarpro.databinding.DialogWaterIntakeBinding
import com.enkefalostechnologies.calendarpro.databinding.FragmentHubBinding
import com.enkefalostechnologies.calendarpro.databinding.KnowYourDayDialogBinding
import com.enkefalostechnologies.calendarpro.databinding.ReminderDialogBinding
import com.enkefalostechnologies.calendarpro.databinding.RepeatDialogBinding
import com.enkefalostechnologies.calendarpro.model.TaskModel
import com.enkefalostechnologies.calendarpro.router.Router.navigateToLoginScreen
import com.enkefalostechnologies.calendarpro.ui.HomeActivity
import com.enkefalostechnologies.calendarpro.ui.LoginActivity
import com.enkefalostechnologies.calendarpro.ui.ProfileActivity
import com.enkefalostechnologies.calendarpro.ui.adapter.BarData
import com.enkefalostechnologies.calendarpro.ui.adapter.BarGraphAdapter
import com.enkefalostechnologies.calendarpro.ui.adapter.BarGraphAdapter2
import com.enkefalostechnologies.calendarpro.ui.adapter.PageAdapter
import com.enkefalostechnologies.calendarpro.ui.adapter.TodayTaskAdapter
import com.enkefalostechnologies.calendarpro.ui.base.BaseFragment
import com.enkefalostechnologies.calendarpro.ui.bottomSheet.OptionListener
import com.enkefalostechnologies.calendarpro.ui.bottomSheet.SubscriptionBottomSheet
import com.enkefalostechnologies.calendarpro.ui.bottomSheet.UpdateOptionBottomSheet
import com.enkefalostechnologies.calendarpro.ui.updateTaskActivity.UpdateTaskActivity
import com.enkefalostechnologies.calendarpro.ui.viewModel.fragment.HubFragmentViewModel
import com.enkefalostechnologies.calendarpro.ui.viewTaskScreen.ViewTaskActivity
import com.enkefalostechnologies.calendarpro.util.AppUtil
import com.enkefalostechnologies.calendarpro.util.AppUtil.getDateStringFromDate
import com.enkefalostechnologies.calendarpro.util.AppUtil.getDayFromDate
import com.enkefalostechnologies.calendarpro.util.AppUtil.getMonthFromDate
import com.enkefalostechnologies.calendarpro.util.AppUtil.getTemporalDateOfCurrentWeek
import com.enkefalostechnologies.calendarpro.util.AppUtil.isNotificationPermissionAvailable
import com.enkefalostechnologies.calendarpro.util.AppUtil.loadBannerAd
import com.enkefalostechnologies.calendarpro.util.AppUtil.requestPermission
import com.enkefalostechnologies.calendarpro.util.AppUtil.showToast
import com.enkefalostechnologies.calendarpro.util.DialogUtil.reminderDialogDisabled
import com.enkefalostechnologies.calendarpro.util.DialogUtil.waterIntakeUtil
import com.enkefalostechnologies.calendarpro.util.Extension.close
import com.enkefalostechnologies.calendarpro.util.Extension.gone
import com.enkefalostechnologies.calendarpro.util.Extension.invisible
import com.enkefalostechnologies.calendarpro.util.Extension.setImageFromUrl
import com.enkefalostechnologies.calendarpro.util.Extension.visible
import com.enkefalostechnologies.calendarpro.util.KnowYourDialogListener
import com.enkefalostechnologies.calendarpro.util.ReminderDialogListener
import com.enkefalostechnologies.calendarpro.util.SubscriptionBottomSheetListener
import com.enkefalostechnologies.calendarpro.util.TaskAdapterItemListener
import com.enkefalostechnologies.calendarpro.util.WaterIntakeDialogListener
import com.enkefalostechnologies.calendarpro.util.dialog.knowYourDialog
import com.enkefalostechnologies.calendarpro.util.dialog.repeatDialogDisabled
import com.enkefalostechnologies.calendarpro.util.repeatDialogListener
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.LoadAdError
import java.util.Date


class HubFragment : BaseFragment<FragmentHubBinding>(R.layout.fragment_hub) {

    private val MY_PERMISSION_REQUEST_REMINDER = 1234
    val viewModel: HubFragmentViewModel by viewModels { HubFragmentViewModel.Factory }
    var waterIntakeGraphAdapter = BarGraphAdapter()
    lateinit var pageAdapter: PageAdapter
    val KnowYourDayAdapter = BarGraphAdapter2()
    val moodView = MoodViewFragment()
    val healthView1 = HealthViewFragment()
    val healthView2 = HealthViewFragment()
    val healthData = mutableListOf<BarData>()
    val productivityData = mutableListOf<BarData>()
    val moodData = mutableListOf<BarData>()
    var todaySTaskList = mutableListOf<Tasks>()
    var progressCount = 0

    /************** observers**********************/
    val isReminderUpdatedObserver = Observer<TaskModel> {
        viewModel.getCurrentDayTask(getUserEmail(), deviceId())
    }
    private val isRepeatTypeUpdatedObserver = Observer<TaskModel> { taskmodel ->
        viewModel.getCurrentDayTask(getUserEmail(), deviceId())
    }

    val todaySTaskListObserver = Observer<List<Tasks>> { list ->
        progressCount++
        Log.d("ProgressDialog", "viewModel.todaySTaskList->counter$progressCount HubFragment")
        todaySTaskList.clear()
        todaySTaskList.addAll(list.filter { it.isCompleted == false && it.taskType != TaskType.HOLIDAY }
            .toMutableList())
        Log.d("HubFragment", "todaySTaskList size ${todaySTaskList.size} ")
        visibleTasksBasedOnData()
        todaySTaskAdapter.notifyDataSetChanged()

        dialogDismiss()
    }
    val weeklyTaskListObserver = Observer<List<Tasks>> { list ->
        progressCount++
        Log.d("ProgressDialog", "viewModel.weeklyTasksList->counter$progressCount HubFragment")
        val totalTasks = list.filter { it.taskType == TaskType.TODO }
        val completedTasks = totalTasks.filter { it.isCompleted == true }
        binding.tvTaskCount.text = "${String.format("%02d", completedTasks.size)}/${
            String.format(
                "%02d", totalTasks.size
            )
        }"
        val totalCallEmails = list.filter { it.taskType == TaskType.CALL_EMAIL }
        val completedCallEmails = totalCallEmails.filter { it.isCompleted == true }
        binding.tvCallEmailCount.text = "${String.format("%02d", completedCallEmails.size)}/${
            String.format(
                "%02d", totalCallEmails.size
            )
        }"
        dialogDismiss()
    }

    val monthlyTaskList = Observer<List<Tasks>> { list ->
        progressCount++
        Log.d("ProgressDialog", "viewModel.monthlyTasksList->counter$progressCount HubFragment")
        val totalTasks = list.filter { it.taskType == TaskType.TODO }
        val completedTasks = totalTasks.filter { it.isCompleted == true }
        binding.tvTaskCount.text = "${String.format("%02d", completedTasks.size)}/${
            String.format(
                "%02d", totalTasks.size
            )
        }"
        val totalCallEmails = list.filter { it.taskType == TaskType.CALL_EMAIL }
        val completedCallEmails = totalCallEmails.filter { it.isCompleted == true }
        binding.tvCallEmailCount.text = "${String.format("%02d", completedCallEmails.size)}/${
            String.format(
                "%02d", totalCallEmails.size
            )
        }"
        dialogDismiss()
    }
    val waterInTakeObserver = Observer<List<WaterInTake>> { list ->
        progressCount++
        Log.d("ProgressDialog", "viewModel.waterInTakeDataList->counter$progressCount HubFragment")
        val barList = mutableListOf<BarData>()
        getTemporalDateOfCurrentWeek().mapIndexed { index, waterInTakeDate ->
            if (list.any { it.date == waterInTakeDate }) {
                barList.add(
                    BarData(
                        waterInTakeDate.toDate().getDayFromDate(),
                        list.filter { it.date == waterInTakeDate }[0].count.toFloat()
                    )
                )
            } else {
                barList.add(BarData(waterInTakeDate.toDate().getDayFromDate(), 0.toFloat()))
            }
        }
        waterIntakeGraphAdapter.setItems(barList)
        waterIntakeGraphAdapter.notifyDataSetChanged()
        dialogDismiss()
    }
    private val onErrorObserver = Observer<DataStoreException> {
        progressCount++
        Log.d("ProgressDialog", "viewModel.onError->counter$progressCount HubFragment")
        dialogDismiss()
    }
    val knowYourDayObserver = Observer<List<KnowYourDay>> { list ->
        progressCount++
        Log.d("ProgressDialog", "viewModel.knowYourDayDataList->counter$progressCount HubFragment")
        healthData.clear()
        productivityData.clear()
        moodData.clear()
        getTemporalDateOfCurrentWeek().mapIndexed { index, waterInTakeDate ->
            if (list.any { it.date == waterInTakeDate }) {
                healthData.add(
                    BarData(
                        waterInTakeDate.toDate().getDayFromDate(),
                        (list.filter { it.date == waterInTakeDate }[0].healthCount + 1).toFloat()
                    )
                )
                productivityData.add(
                    BarData(
                        waterInTakeDate.toDate().getDayFromDate(),
                        (list.filter { it.date == waterInTakeDate }[0].productivityCount + 1).toFloat()
                    )
                )
                moodData.add(
                    BarData(
                        waterInTakeDate.toDate().getDayFromDate(),
                        (list.filter { it.date == waterInTakeDate }[0].moodCount + 1).toFloat()
                    )
                )
            } else {
                healthData.add(BarData(waterInTakeDate.toDate().getDayFromDate(), 0.toFloat()))
                productivityData.add(
                    BarData(
                        waterInTakeDate.toDate().getDayFromDate(),
                        0.toFloat()
                    )
                )
                moodData.add(BarData(waterInTakeDate.toDate().getDayFromDate(), 0.toFloat()))
            }
        }
        when (binding.viewPager.currentItem) {
            0 -> {
                binding.tvExcellentLabel.text = "Excellent"
                binding.tvPoorLabel.text = "Poor"
                binding.tvGoodLabel.text = "Good"
                KnowYourDayAdapter.setItems(healthData)
                KnowYourDayAdapter.notifyDataSetChanged()
            }

            1 -> {
                binding.tvExcellentLabel.text = "Excellent"
                binding.tvPoorLabel.text = "Poor"
                binding.tvGoodLabel.text = "Good"
                KnowYourDayAdapter.setItems(productivityData)
                KnowYourDayAdapter.notifyDataSetChanged()
            }

            2 -> {
                binding.tvExcellentLabel.text = "Happy"
                binding.tvPoorLabel.text = "Angry"
                binding.tvGoodLabel.text = "Sad"
                KnowYourDayAdapter.setItems(moodData)
                KnowYourDayAdapter.notifyDataSetChanged()
            }

            else -> {
                binding.tvExcellentLabel.text = "Excellent"
                binding.tvPoorLabel.text = "Poor"
                binding.tvGoodLabel.text = "Good"
                KnowYourDayAdapter.setItems(healthData)
                KnowYourDayAdapter.notifyDataSetChanged()
            }
        }
        dialogDismiss()
    }


    private var listPopupWindow: ListPopupWindow? = null
    val todaySTaskAdapter = TodayTaskAdapter(todaySTaskList, object : TaskAdapterItemListener {
        override fun onItemClicked(task: Tasks) {
            requireActivity().startActivity(
                Intent(requireActivity(), ViewTaskActivity::class.java).putExtra(
                    Constants.INTENT_TASK_ID,
                    task.id
                ).putExtra(Constants.INTENT_TASK_ID, task.id)
            )
        }

        override fun onTaskCheck(task: Tasks, position: Int) {
            viewModel.markTaskAsDone(TaskModel(position = position, task = task))
        }

        override fun onReminderClicked(task: Tasks, position: Int) {
            requireActivity().reminderDialogDisabled(customTime = task.customTime.toDate(),
                reminder = task.reminder,
                view = ReminderDialogBinding.inflate(LayoutInflater.from(requireActivity())).root,
                listener = object : ReminderDialogListener {
                    override fun onDoneClicked(
                        d: Dialog, reminder: ReminderEnum?, reminderTime: Date
                    ) {
                        if (!requireActivity().isNotificationPermissionAvailable()) {
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
                                                Constants.INTENT_FROM_LIST_GROUP, task.listGroupId
                                            ), 1001
                                    )
                                }

                            }).show(requireActivity().supportFragmentManager, "UpdateBottomSheet")
                        } else {
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
                        if (!requireActivity().isNotificationPermissionAvailable()) {
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

    })
    private var adapter: ArrayAdapter<String>? = null
    val viewList = mutableListOf<Fragment>()


    override fun setupViews() {
        progressCount = 0
        Log.d("ProgressDialog", "setupViews()->counter$progressCount HubFragment")
        //dialog.visible()
        initializeKonYourDayGraph()
        initializeWaterIntakeGraph()
        binding.tvSpinnerText.text = "This Week"
        setSpinner()
        binding.rvTodayTasks.adapter = todaySTaskAdapter
        binding.rvTodayTasks.visible()
        binding.tvNoTaskFound2.gone()
        setProfileData()
        checkAndLoadSubscriptionFeatures()

    }

    fun checkAndLoadSubscriptionFeatures() {
        if (getSubscriptionStatus() == SubscriptionStatus.ACTIVE) {
            requireActivity().runOnUiThread {
                binding.btnUpgrade.gone()
                binding.adView.gone()
                binding.ivPremium.visible()
            }
        } else {
            binding.btnUpgrade.visible()
            binding.ivPremium.gone()
            setAds()
        }
    }

    override fun setupListeners() {
        binding.btnRateYourDay.setOnClickListener {
            requireActivity().knowYourDialog(
                isLoggedIn = isUserLoggedIn(),
                email = getUserEmail(),
                deviceId(),
                KnowYourDayDialogBinding.inflate(LayoutInflater.from(requireActivity())),
                object : KnowYourDialogListener {
                    override fun onDone(
                        dialog: Dialog,
                        selectedDate: Temporal.Date,
                        healthCount: Int,
                        productivityCount: Int,
                        moodCount: Int
                    ) {
                        viewModel.saveRateYourDay(
                            getUserEmail(),
                            deviceId(),
                            selectedDate,
                            healthCount,
                            productivityCount,
                            moodCount
                        )
                        dialog.dismiss()
                    }
                }).show()
        }
        binding.btnAddConsumption.setOnClickListener {
            openWaterIntakeDialog()

        }
        binding.ivProfile.setOnClickListener {
            if (isUserLoggedIn()) {
                openProfileScreen()
            } else {
                openLoginScreen()
            }
        }
        binding.btnUpgrade.setOnClickListener {
            if (isUserLoggedIn()) {
                SubscriptionBottomSheet(
                    getUserEmail(),
                    amplifyDataModelUtil, object : SubscriptionBottomSheetListener {
                        override fun onPurchaseSuccess(
                            purchases: Purchase,
                            validUpTo: Date,
                            subscriptionType: com.amplifyframework.datastore.generated.model.SubscriptionType
                        ) {
                            setSubscriptionStatus(SubscriptionStatus.ACTIVE)
                            saveSubscribedPlan(subscriptionType)
                            requireActivity().showToast("Subscribed Successfully.")
                            setupViews()
                        }

                        override fun onClosed() {

                        }

                        override fun onError(error: String) {
                            requireActivity().showToast(error)
                        }

                    }
                ).show(
                    childFragmentManager, "Subscription bottom-sheet"
                )
            } else {
                requireActivity().navigateToLoginScreen()
            }
        }
        binding.tvViewAll.setOnClickListener {
            (requireActivity() as HomeActivity).openTaskFragment()
        }
        binding.tvSpinnerText.text = "This Week"
        binding.tvSpinnerText.setOnClickListener {
            setSpinner()
            listPopupWindow?.show()
        }
        binding.crdTask.setOnClickListener {
            (requireActivity() as HomeActivity).openTaskFragmentAndFilterData(
                TaskType.TODO, binding.tvSpinnerText.text.toString()
            )
        }
        binding.crdCall.setOnClickListener {
            (requireActivity() as HomeActivity).openTaskFragmentAndFilterData(
                TaskType.CALL_EMAIL, binding.tvSpinnerText.text.toString()
            )
        }
    }

    override fun fetchInitialData() {
        viewModel.getCurrentDayTask(getUserEmail(), deviceId())
        viewModel.getWeeklyTask(getUserEmail(), deviceId())
        viewModel.getWaterInTakeData(getUserEmail(), deviceId())
        viewModel.getRateYourDay(getUserEmail(), deviceId())
//        viewModel.checkSessionValue()
    }

    override fun setupObservers() {
        viewModel.isReminderUpdated.observe(this, isReminderUpdatedObserver)
        viewModel.isRepeatTypeUpdated.observe(this, isRepeatTypeUpdatedObserver)
        viewModel.todaySTaskList.observe(this, todaySTaskListObserver)
        viewModel.weeklyTasksList.observe(this, weeklyTaskListObserver)
        viewModel.monthlyTasksList.observe(this, monthlyTaskList)
        viewModel.waterInTakeDataList.observe(this, waterInTakeObserver)
        viewModel.knowYourDayDataList.observe(this, knowYourDayObserver)
        viewModel.onError.observe(this, onErrorObserver)
    }

    override fun removeObserver() {
        viewModel.isReminderUpdated.removeObserver(isReminderUpdatedObserver)
        viewModel.isRepeatTypeUpdated.removeObserver(isRepeatTypeUpdatedObserver)
        viewModel.todaySTaskList.removeObserver(todaySTaskListObserver)
        viewModel.weeklyTasksList.removeObserver(weeklyTaskListObserver)
        viewModel.monthlyTasksList.removeObserver(monthlyTaskList)
        viewModel.waterInTakeDataList.removeObserver(waterInTakeObserver)
        viewModel.knowYourDayDataList.removeObserver(knowYourDayObserver)
        viewModel.onError.removeObserver(onErrorObserver)
    }

    private fun visibleTasksBasedOnData() {
//        val filteredList = list.filter { it.isCompleted == false }

        if (todaySTaskList.isEmpty()) {
            binding.rvTodayTasks.gone()
            binding.tvNoTaskFound2.visible()
        } else {
            binding.rvTodayTasks.visible()
            binding.tvNoTaskFound2.gone()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun initializeKonYourDayGraph() {
        binding.tvDates.text = "${
            getTemporalDateOfCurrentWeek()[0].toDate().getDateStringFromDate()
        } ${
            getTemporalDateOfCurrentWeek()[0].toDate().getMonthFromDate()
        } to ${
            getTemporalDateOfCurrentWeek()[6].toDate().getDateStringFromDate()
        } ${getTemporalDateOfCurrentWeek()[6].toDate().getMonthFromDate()}"
        val data: List<BarData> = arrayListOf(
            BarData("Mon", 0.toFloat()),
            BarData("Tue", 0.toFloat()),
            BarData("Wed", 0.toFloat()),
            BarData("Thus", 0.toFloat()),
            BarData("Fri", 0.toFloat()),
            BarData("Sat", 0.toFloat()),
            BarData("Sun", 0.toFloat())
        )
        viewList.clear()
        healthView1.setOrUpdateData(data)
        healthView2.setOrUpdateData(data)
        moodView.setOrUpdateData(data)
        viewList.add(healthView1)
        viewList.add(healthView2)
        viewList.add(moodView)
        pageAdapter = PageAdapter(childFragmentManager, viewList)
        KnowYourDayAdapter?.setItems(data)
        binding.rvBarGraph2.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvBarGraph2.adapter = KnowYourDayAdapter
        binding.viewPager.adapter = pageAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                when (position) {
                    0 -> healthView1.setOrUpdateData(healthData)
                    1 -> healthView2.setOrUpdateData(productivityData)
                    2 -> moodView.setOrUpdateData(moodData)
                    else -> healthView1.setOrUpdateData(healthData)
                }
            }

            override fun onPageSelected(position: Int) {
                when (binding.viewPager.currentItem) {
                    0 -> {
                        binding.tvExcellentLabel.text = "Excellent"
                        binding.tvPoorLabel.text = "Poor"
                        binding.tvGoodLabel.text = "Good"
                        KnowYourDayAdapter.setItems(healthData)
                        KnowYourDayAdapter.notifyDataSetChanged()
                    }

                    1 -> {
                        binding.tvExcellentLabel.text = "Excellent"
                        binding.tvPoorLabel.text = "Poor"
                        binding.tvGoodLabel.text = "Good"
                        KnowYourDayAdapter.setItems(productivityData)
                        KnowYourDayAdapter.notifyDataSetChanged()
                    }

                    2 -> {
                        binding.tvExcellentLabel.text = "Happy"
                        binding.tvPoorLabel.text = "Angry"
                        binding.tvGoodLabel.text = "Sad"
                        KnowYourDayAdapter.setItems(moodData)
                        KnowYourDayAdapter.notifyDataSetChanged()
                    }

                    else -> {
                        binding.tvExcellentLabel.text = "Excellent"
                        binding.tvPoorLabel.text = "Poor"
                        binding.tvGoodLabel.text = "Good"
                        KnowYourDayAdapter.setItems(healthData)
                        KnowYourDayAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                when (binding.viewPager.currentItem) {
                    0 -> {
                        binding.tvExcellentLabel.text = "Excellent"
                        binding.tvPoorLabel.text = "Poor"
                        binding.tvGoodLabel.text = "Good"
                        KnowYourDayAdapter.setItems(healthData)
                        KnowYourDayAdapter.notifyDataSetChanged()
                    }

                    1 -> {
                        binding.tvExcellentLabel.text = "Excellent"
                        binding.tvPoorLabel.text = "Poor"
                        binding.tvGoodLabel.text = "Good"
                        KnowYourDayAdapter.setItems(productivityData)
                        KnowYourDayAdapter.notifyDataSetChanged()
                    }

                    2 -> {
                        binding.tvExcellentLabel.text = "Happy"
                        binding.tvPoorLabel.text = "Angry"
                        binding.tvGoodLabel.text = "Sad"
                        KnowYourDayAdapter.setItems(moodData)
                        KnowYourDayAdapter.notifyDataSetChanged()
                    }

                    else -> {
                        binding.tvExcellentLabel.text = "Excellent"
                        binding.tvPoorLabel.text = "Poor"
                        binding.tvGoodLabel.text = "Good"
                        KnowYourDayAdapter.setItems(healthData)
                        KnowYourDayAdapter.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    private fun openWaterIntakeDialog() {
        requireActivity().waterIntakeUtil(isUserLoggedIn(),
            getUserEmail(), deviceId(),
            DialogWaterIntakeBinding.inflate(
                LayoutInflater.from(requireActivity())
            ).root,
            object : WaterIntakeDialogListener {
                override fun onDone(date: Temporal.Date, count: Int) {
                    viewModel.saveWaterInTake(getUserEmail(), deviceId(), date, count)
                }
            }).show()
    }

    private fun setProfileData() {
        if (isUserLoggedIn()) {
            binding.tvName.text = "Hello,${getUserName()}!"
            binding.tvName.visible()
            binding.ivProfile.setImageFromUrl(getUserPicUrl())
        } else {
            binding.tvName.text = "Hello,User!"
            binding.tvName.visible()
            binding.btnUpgrade.visible()

        }
    }


    private fun setAds() {
        binding.adView.visible()
        binding.adView.loadBannerAd()
        binding.adView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.adView.loadBannerAd()
                }, 2000)
            }

            override fun onAdLoaded() {
                binding.adView.visible()
            }

            override fun onAdClosed() {
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.adView.loadBannerAd()
                }, 2000)

            }
        }
    }

    fun setSpinner() {
        val items =
            if (binding.tvSpinnerText.text == "This Week") arrayOf("This Month") else arrayOf("This Week")
        adapter = ArrayAdapter(requireActivity(), R.layout.layout_spinner, R.id.title, items)
        listPopupWindow = ListPopupWindow(requireActivity())
        listPopupWindow?.setAdapter(adapter)
        listPopupWindow?.anchorView = binding.tvSpinnerText
        listPopupWindow?.verticalOffset = 16
        listPopupWindow?.setBackgroundDrawable(resources.getDrawable(android.R.color.white))

        listPopupWindow?.setOnItemClickListener { parent, view, position, id ->
            val selectedTextView = view as TextView
            binding.tvSpinnerText.text = selectedTextView.text
            listPopupWindow?.dismiss()
        }
        listPopupWindow?.setOnDismissListener {
            if (binding.tvSpinnerText.text == "This Month") {
                viewModel.getMonthlyTask(
                    getUserEmail(),
                    deviceId(),
                    AppUtil.getStartAndEndOfMonthDates().first,
                    AppUtil.getStartAndEndOfMonthDates().second
                )
            } else {
                viewModel.getWeeklyTask(getUserEmail(), deviceId())
            }
        }
    }


    private fun initializeWaterIntakeGraph() {
        waterIntakeGraphAdapter.setItems(
            arrayListOf(
                BarData("Mon", 0.toFloat()),
                BarData("Tue", 0.toFloat()),
                BarData("Wed", 0.toFloat()),
                BarData("Thus", 0.toFloat()),
                BarData("Fri", 0.toFloat()),
                BarData("Sat", 0.toFloat()),
                BarData("Sun", 0.toFloat())
            )
        )
        binding.rvBarGraph.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvBarGraph.adapter = waterIntakeGraphAdapter
    }


    fun openProfileScreen() {
        val intent = Intent(activity, ProfileActivity::class.java)

        val options: ActivityOptions =
            ActivityOptions.makeSceneTransitionAnimation(
                requireActivity(),
                binding.ivProfile,
                "Robot"
            )
        startActivity(intent, options.toBundle())
    }

    fun openLoginScreen() {
        val options: ActivityOptions =
            ActivityOptions.makeSceneTransitionAnimation(
                requireActivity(),
                binding.ivProfile,
                "Robot"
            )
        startActivity(
            Intent(activity, LoginActivity::class.java).putExtra("view", "login"),
            options.toBundle()
        )
    }

    fun dialogDismiss() {
        Log.d("ProgressDialog", "dialogDismiss()->counter$progressCount HubFragment")
        if (progressCount >= 4) {
            dialog.close()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.i("Lifecycle", "HubFragment -> onPause()")
        dialog.close()
        removeObserver()
    }

    override fun onResume() {
        Log.i("Lifecycle", "HubFragment -> onResume()")
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        Log.i("Lifecycle", "HubFragment -> onStop()")
    }

    override fun onDestroy() {
        Log.i("Lifecycle", "HubFragment -> onDestroy()")
        super.onDestroy()
    }


}