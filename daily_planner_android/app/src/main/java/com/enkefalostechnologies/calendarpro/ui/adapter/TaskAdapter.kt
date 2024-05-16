package com.enkefalostechnologies.calendarpro.ui.adapter

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.datastore.generated.model.ReminderEnum
import com.amplifyframework.datastore.generated.model.RepeatType
import com.amplifyframework.datastore.generated.model.TaskType
import com.amplifyframework.datastore.generated.model.Tasks
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.databinding.ItemTaskBinding
import com.enkefalostechnologies.calendarpro.util.Extension.gone
import com.enkefalostechnologies.calendarpro.util.Extension.visible
import com.enkefalostechnologies.calendarpro.util.TaskAdapterItemListener
import java.text.SimpleDateFormat
import java.util.Date


class TaskAdapter(filteredTask:List<Tasks>,var listener:TaskAdapterItemListener? = null)
    : RecyclerView.Adapter<TaskAdapter.ListViewHolder>() {
    private var checkDate: Date? = null
    private var list= listOf<Tasks>()
    private var headerFlag=false
    private var showDate=false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemTaskBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    fun setList(list:List<Tasks>,showDate:Boolean){
        this.headerFlag=false
        this.showDate=showDate
        this.list=list
        checkDate = null
//        notifyDataSetChanged()
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        with(holder){
            if (!list[position].isCompleted) {

                if(list[position].taskType==TaskType.HOLIDAY){
                    binding.layout.background=AppCompatResources.getDrawable(
                        binding.layout.context,
                        R.drawable.task_bg_holiday
                    )
                }else{
                    binding.layout.background=AppCompatResources.getDrawable(
                        binding.layout.context,
                        R.drawable.task_bg
                    )
                }
//                binding.tvCompleted.gone()
                binding.clPendingTasks.visible()
                binding.clCompletedTasks.gone()
                with(list[position]) {
                    binding.tvTitle.text = title
                    if (taskType == TaskType.CALL_EMAIL) {
                        binding.ivCheck.visible()
                        binding.tvTaskType.text = "Calls"
                        binding.tvTaskType.background = AppCompatResources.getDrawable(
                            binding.tvTaskType.context,
                            R.drawable.call_grey_bg
                        )
                        binding.tvTaskType.setTextColor(Color.BLACK)
                    }else if(taskType==TaskType.HOLIDAY) {
                        binding.ivCheck.gone()
                        binding.tvTitle.layoutParams.width=ViewGroup.LayoutParams.MATCH_PARENT
                        binding.tvTaskType.text = countryCode
                        binding.tvTaskType.isAllCaps=true
                        binding.tvTaskType.background = AppCompatResources.getDrawable(
                            binding.tvTaskType.context,
                            R.drawable.task_black_bg
                        )
                        binding.tvTaskType.setTextColor(Color.WHITE)
                    }else {
                        binding.ivCheck.visible()
                        binding.tvTaskType.text = "Task"
                        binding.tvTaskType.background = AppCompatResources.getDrawable(
                            binding.tvTaskType.context,
                            R.drawable.task_green_bg
                        )
                        binding.tvTaskType.setTextColor(Color.BLACK)
                    }
                    if (isImportant) {
                        binding.ivImportant.visible()
                        binding.ivImportant.setImageDrawable(
                            AppCompatResources.getDrawable(
                                binding.ivImportant.context,
                                R.drawable.ic_star
                            )
                        )
                    } else {
                            binding.ivImportant.visible()
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
                    if (reminder == ReminderEnum.NONE) {
                        binding.ivReminder.gone()
                    } else {
                        binding.ivReminder.visible()
                    }
                    if (repeatType!=RepeatType.NONE) {
                        binding.ivRepeat.visible()
                    } else {
                        binding.ivRepeat.gone()
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
            } else{
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
                    binding.tvTitle2.setOnClickListener {
                        listener?.onItemClicked(list[position])
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

            binding.ivReminder.setOnClickListener { listener?.onReminderClicked(list[position],position) }
            binding.ivRepeat.setOnClickListener { listener?.onRepeatClicked(list[position],position) }
//            val slideUp: Animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fall_down)
//            holder.itemView.startAnimation(slideUp)

        }
    }

    inner class ListViewHolder(val binding: ItemTaskBinding)
        :RecyclerView.ViewHolder(binding.root)

}
