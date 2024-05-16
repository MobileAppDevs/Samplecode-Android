package com.enkefalostechnologies.calendarpro.ui.calldorado

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import com.amplifyframework.datastore.generated.model.Tasks
import com.calldorado.ui.views.custom.CalldoradoCustomView
import com.enkefalostechnologies.calendarpro.constant.StorageConstant
import com.enkefalostechnologies.calendarpro.databinding.AftercallNativeLayoutBinding
import com.enkefalostechnologies.calendarpro.ui.GetStartedActivity
import com.enkefalostechnologies.calendarpro.ui.HomeActivity
import com.enkefalostechnologies.calendarpro.ui.adapter.TaskAdapter
import com.enkefalostechnologies.calendarpro.ui.adapter.TodayTaskAdapter
import com.enkefalostechnologies.calendarpro.util.AmplifyDataModelUtil
import com.enkefalostechnologies.calendarpro.util.AppUtil.getAndroidId
import com.enkefalostechnologies.calendarpro.util.AppUtil.getDateStringFromDate
import com.enkefalostechnologies.calendarpro.util.AppUtil.getMonthFromDate
import com.enkefalostechnologies.calendarpro.util.AppUtil.getYearYYYYFromDate
import com.enkefalostechnologies.calendarpro.util.AppUtil.showToast
import com.enkefalostechnologies.calendarpro.util.Extension.gone
import com.enkefalostechnologies.calendarpro.util.Extension.visible
import com.enkefalostechnologies.calendarpro.util.PreferenceHandler
import com.enkefalostechnologies.calendarpro.util.TaskAdapterItemListener
import java.util.Date


class AfterCallCustomView(context: Context?) : CalldoradoCustomView(context) {
    lateinit var preferenceManager: PreferenceHandler
    val binding by lazy { AftercallNativeLayoutBinding.inflate(LayoutInflater.from(context)) }
    var todaySTaskList = mutableListOf<Tasks>()
    init {
        this.context = context
    }
    val todaySTaskAdapter = TaskAdapter(todaySTaskList,object : TaskAdapterItemListener {
        override fun onItemClicked(task: Tasks) {
        }

        override fun onTaskCheck(task: Tasks, position: Int) {
            if(task.isCompleted) {
                AmplifyDataModelUtil(context!!).markTaskAsUnDone(
                    taskId = task.id,
                    email = getUserEmail(),
                    deviceId = deviceId(),
                    {
                          fetchData()
                    }, {
                        Handler(Looper.getMainLooper()).post(Runnable {
                            context.showToast(it.localizedMessage)
                        })
                    })
            }else {
                AmplifyDataModelUtil(context!!).markTaskDone(
                    taskId = task.id,
                    email = getUserEmail(),
                    deviceId = deviceId(),
                    {
                            fetchData()
                    }, {
                        Handler(Looper.getMainLooper()).post(Runnable {
                            context.showToast(it.localizedMessage)
                        })
                    })
            }
        }

        override fun onReminderClicked(task: Tasks, position: Int) {

        }

        override fun onRepeatClicked(task: Tasks, position: Int) {

        }

    })

    fun notifyAdapter(){
        todaySTaskAdapter.setList(todaySTaskList,false)
        todaySTaskAdapter.notifyDataSetChanged()
    }
    @SuppressLint("SetTextI18n")
    override fun getRootView(): View {
        preferenceManager=PreferenceHandler(context)
        binding.rvTasks.adapter=todaySTaskAdapter
        adjustLayoutsForData()
        binding.tvTodayDate.text = "${Date().getMonthFromDate()} ${Date().getDateStringFromDate()},${Date().getYearYYYYFromDate()}"
        fetchData()
        binding.ibAdd.setOnClickListener {
            val i= Intent(it.context,HomeActivity::class.java)
            i.putExtra("open","createTask")
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
           context.startActivity(i)
        }
        binding.ibRight.setOnClickListener {
            val i= Intent(it.context,HomeActivity::class.java)
           i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)
        }
        return binding.root
    }
fun fetchData(){
    AmplifyDataModelUtil(context).getTodaySTaskAndEvents(email = getUserEmail(),deviceId(),{ taskList->
        todaySTaskList.clear()
        while(taskList.hasNext()){
               todaySTaskList.add(taskList.next())
        }
        Handler(Looper.getMainLooper()).post(Runnable {
            adjustLayoutsForData()
        })
    },{
        Handler(Looper.getMainLooper()).post(Runnable {
            todaySTaskList.clear()
            adjustLayoutsForData()
        })
    })
}
    fun adjustLayoutsForData(){
        notifyAdapter()
        if(todaySTaskList.isEmpty()){
            binding.rvTasks.gone()
            binding.tvNoTasks.visible()
        }else{
            binding.rvTasks.visible()
            binding.tvNoTasks.gone()
        }
    }


    fun deviceId(): String =context.getAndroidId()


    fun getUserEmail():String =preferenceManager.readString(StorageConstant.USER_EMAIL,"")



}