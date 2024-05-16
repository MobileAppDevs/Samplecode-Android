package com.enkefalostechnologies.calendarpro.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.databinding.ItemDateBinding
import com.enkefalostechnologies.calendarpro.util.AppUtil.getDateStringFromDate
import com.enkefalostechnologies.calendarpro.util.AppUtil.getDayFromDate
import com.enkefalostechnologies.calendarpro.util.CalenderListener
import com.enkefalostechnologies.calendarpro.util.CalenderUtil.isCurrentDate
import com.enkefalostechnologies.calendarpro.util.CalenderUtil.isSelectedDate
import com.enkefalostechnologies.calendarpro.util.Extension.gone
import com.enkefalostechnologies.calendarpro.util.Extension.invisible
import com.enkefalostechnologies.calendarpro.util.Extension.visible
import java.util.Date


class CalenderLinearViewAdapter(var selectedDate: Date? = Date(), var listener: CalenderListener) :
    RecyclerView.Adapter<CalenderLinearViewAdapter.ListViewHolder>() {

    var list: List<Date> = listOf()
    var eventList = mutableListOf<Date>()
    var headerList = mutableListOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    var currentMonthDateList: List<Date> = listOf()

    //    var date1:Date?=null
//    var date2:Date?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemDateBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    fun setItems(newList: List<Date>, currentMonthDateList: List<Date>) {
        this.currentMonthDateList = currentMonthDateList
        this.list = newList
        notifyDataSetChanged()
    }

    fun setEvent(dateList:List<Date>) {
        this.eventList=dateList.toMutableList()
        notifyDataSetChanged()
    }

    fun removeEvent() {
        this.eventList.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount() = 7

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {

        with(holder) {
            binding.tvHeader.visible()
            binding.tvDate.gone()
            binding.ivEvent.gone()
            binding.tvHeader.text = list[position].getDayFromDate()
            binding.tvDate.text = list[position].getDateStringFromDate()
            if (currentMonthDateList.contains(list[position])) {
                binding.tvDate.setTextColor(binding.tvDate.context.resources.getColor(R.color.white))
            } else {
                binding.tvDate.setTextColor(binding.tvDate.context.resources.getColor(R.color.color_9EA0A2))
            }
            if (list[position].isCurrentDate()) {
                binding.tvDate.setBackground(
                    ContextCompat.getDrawable(
                        binding.tvDate.context,
                        R.drawable.bg_circle
                    )
                )
                binding.tvDate.backgroundTintList =
                    ContextCompat.getColorStateList(binding.tvDate.context, R.color.color_D3F26A);
                binding.tvDate.setTextColor(binding.tvDate.context.resources.getColor(R.color.color_25282B))
            } else if (selectedDate!=null && list[position].isSelectedDate(selectedDate!!)) {
                binding.tvDate.setBackground(
                    ContextCompat.getDrawable(
                        binding.tvDate.context,
                        R.drawable.bg_circle
                    )
                )
                binding.tvDate.setTextColor(binding.tvDate.context.resources.getColor(R.color.color_25282B))
            } else {
                binding.tvDate.background = null
            }

            binding.tvDate.visible()
//                    if(list[position-7].isBetween(date1,date2)){
//                        holder.itemView.setBackgroundColor(ContextCompat.getColor(binding.ll.context, R.color.white))
//                        binding.tvDate.setTextColor(binding.tvDate.context.resources.getColor(R.color.color_25282B))
//                    }else{
//                        holder.itemView.background=null
//                    }
            if (eventList.contains(list[position])) {
                binding.ivEvent.visible()
            } else {
                if (eventList.isEmpty())
                    binding.ivEvent.gone()
                else
                    binding.ivEvent.invisible()
            }
            if (currentMonthDateList.contains(list[position])) {
                holder.itemView.setOnClickListener {
                    selectedDate = if(selectedDate!=null){
                        if(list[position]!=selectedDate){
                            list[position]
                        }else{
                            null
                        }
                    }else{
                        list[position]
                    }
                    notifyDataSetChanged()
                    listener.onDateSelected(selectedDate)
                }
            }
        }

    }

    inner class ListViewHolder(val binding: ItemDateBinding) : RecyclerView.ViewHolder(binding.root)

}
