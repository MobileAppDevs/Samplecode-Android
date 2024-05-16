package com.enkefalostechnologies.calendarpro.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.datastore.generated.model.RepeatType
import com.amplifyframework.datastore.generated.model.TaskType
import com.amplifyframework.datastore.generated.model.Tasks
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.databinding.ItemTaskBinding
import com.enkefalostechnologies.calendarpro.util.Extension.gone
import com.enkefalostechnologies.calendarpro.util.Extension.visible
import com.enkefalostechnologies.calendarpro.util.TaskAdapterItemListener

class TodayTaskAdapter(var list:List<Tasks>,var listener:TaskAdapterItemListener)
    : RecyclerView.Adapter<TodayTaskAdapter.ListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemTaskBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

//    fun setList(list:List<Tasks>,size:Int?=null){
//        if(size==null){
//            this.size=list.size
//        }else{
//            this.size=if(list.size<=3)list.size else size
//        }
//        this.list=list
//        notifyDataSetChanged()
//    }

    override fun getItemCount() = if(list.size>3) 3 else list.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        with(holder){
            with(list[position]) {
                binding.tvTitle.text=title
                if(taskType==TaskType.CALL_EMAIL) {
                    binding.tvTaskType.text="Calls"
                    binding.tvTaskType.background= AppCompatResources.getDrawable(binding.tvTaskType.context,R.drawable.call_grey_bg)
                }else {
                    binding.tvTaskType.text="Task"
                    binding.tvTaskType.background= AppCompatResources.getDrawable(binding.tvTaskType.context,R.drawable.task_green_bg)
                }
                if(isImportant){
                    binding.ivImportant.setImageDrawable( AppCompatResources.getDrawable(binding.ivImportant.context,R.drawable.ic_star))
                }else{
                    binding.ivImportant.setImageDrawable(AppCompatResources.getDrawable(binding.ivImportant.context,R.drawable.ic_star_unselected))
                }
                binding.tvTitle.setOnClickListener {
                    listener.onItemClicked(list[position])
                }
                binding.ivImportant.setOnClickListener {
                    listener.onItemClicked(list[position])
                }
                binding.tvTaskType.setOnClickListener {
                    listener.onItemClicked(list[position])
                }
                binding.ivCheck.setOnClickListener {
                    listener.onTaskCheck(list[position],position)
                }
                binding.ivReminder.setOnClickListener{
                    listener.onReminderClicked(list[position], position)
                }
                binding.ivRepeat.setOnClickListener{
                    listener.onRepeatClicked(list[position], position)
                }
                if(reminder==null){
                    binding.ivReminder.gone()
                }else{binding.ivReminder.visible()}
                if(repeatType==RepeatType.NONE){
                    binding.ivRepeat.gone()
                }else{binding.ivRepeat.visible()}
            }
//            val slideUp: Animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fall_down)
//            holder.itemView.startAnimation(slideUp)
        }
    }

    inner class ListViewHolder(val binding: ItemTaskBinding)
        :RecyclerView.ViewHolder(binding.root)

}
