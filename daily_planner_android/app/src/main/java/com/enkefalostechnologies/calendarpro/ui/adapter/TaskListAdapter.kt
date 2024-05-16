package com.enkefalostechnologies.calendarpro.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.amplifyframework.datastore.generated.model.TaskType
import com.amplifyframework.datastore.generated.model.Tasks
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.databinding.ItemTaskBinding
import com.enkefalostechnologies.calendarpro.util.Extension.gone
import com.enkefalostechnologies.calendarpro.util.Extension.visible
import com.enkefalostechnologies.calendarpro.util.TaskAdapterItemListener
import java.text.SimpleDateFormat
import java.util.Date


class TaskListAdapter(var listener:TaskAdapterItemListener? = null) : BaseAdapter(){
    private var checkDate: Date? = null
    private var list= listOf<Tasks>()
    private var headerFlag=false
    private var showDate=false

    fun setList(list:List<Tasks>,showDate:Boolean){
        this.headerFlag=false
        this.showDate=showDate
        this.list=list
        checkDate = null
//        notifyDataSetChanged()
    }

    override fun getCount()=list.size

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, contentView: View?, parent: ViewGroup?): View {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        if (!list[position].isCompleted) {
//                binding.tvCompleted.gone()
            binding.clPendingTasks.visible()
            binding.clCompletedTasks.gone()
            with(list[position]) {
                binding.tvTitle.text = title
                if (taskType == TaskType.CALL_EMAIL) {
                    binding.tvTaskType.text = "Calls"
                    binding.tvTaskType.background = AppCompatResources.getDrawable(
                        binding.tvTaskType.context,
                        R.drawable.call_grey_bg
                    )
                } else {
                    binding.tvTaskType.text = "Task"
                    binding.tvTaskType.background = AppCompatResources.getDrawable(
                        binding.tvTaskType.context,
                        R.drawable.task_green_bg
                    )
                }
                if (isImportant) {
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
                binding.tvTitle.setOnClickListener {
                    listener?.onItemClicked(list[position])
                }
                binding.ivImportant.setOnClickListener {
                    listener?.onItemClicked(list[position])
                }
                binding.tvTaskType.setOnClickListener {
                    listener?.onItemClicked(list[position])
                }
                binding.ivCheck.setOnClickListener {
                    listener?.onTaskCheck(list[position],position)
                }
                if (reminder == null) {
                    binding.ivReminder.gone()
                } else {
                    binding.ivReminder.visible()
                }
                if (repeatDays.isEmpty()) {
                    binding.ivRepeat.gone()
                } else {
                    binding.ivRepeat.visible()
                }
                if(showDate) {
                    if (checkDate != date.toDate()) {
                        checkDate = date.toDate()
                        binding.clTime.visible()
                        var formatter = SimpleDateFormat("EEE, ")
                        binding.tvToday.text = formatter.format(date.toDate())
                        formatter = SimpleDateFormat("dd MMM")
                        binding.tvDate.text = formatter.format(date.toDate())
                    } else binding.clTime.gone()
                }else binding.clTime.gone()
            }
            listener?.setEvents(list[position])
        } else {
//                if(!headerFlag){
//                    binding.tvCompleted.visible()
//                    headerFlag=true
//                }else{binding.tvCompleted.gone()}
            binding.clPendingTasks.gone()
            binding.clCompletedTasks.visible()
            with(list[position]) {
                binding.tvTitle2.paintFlags = binding.tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.tvTitle2.text=title
                if(taskType==TaskType.CALL_EMAIL) {
                    binding.tvTaskType2.text="Calls"
                    binding.tvTaskType2.background= AppCompatResources.getDrawable(binding.tvTaskType.context,R.drawable.call_grey_bg)
                    binding.tvTaskType2.backgroundTintList= ContextCompat.getColorStateList(binding.tvTaskType.context, R.color.white)
                }else {

                    binding.tvTaskType2.text="Task"
                    binding.tvTaskType2.background= AppCompatResources.getDrawable(binding.tvTaskType.context,R.drawable.task_green_bg)
                    binding.tvTaskType2.backgroundTintList= ContextCompat.getColorStateList(binding.tvTaskType.context, R.color.color_D3F26A)
                }
                if(showDate) {
                    if (checkDate != date.toDate()) {
                        checkDate = date.toDate()
                        binding.clTime2.visible()
                        var formatter = SimpleDateFormat("EEE, ")
                        binding.tvToday2.text = formatter.format(date.toDate())
                        formatter = SimpleDateFormat("dd MMM")
                        binding.tvDate2.text = formatter.format(date.toDate())
                    } else binding.clTime2.gone()
                }else binding.clTime2.gone()
                binding.ivCheck2.setOnClickListener {
                    listener?.onTaskCheck(list[position],position)
                }
            }
        }
        return binding.root
    }


}
