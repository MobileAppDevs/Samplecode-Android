package com.enkefalostechnologies.calendarpro.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.generated.model.ListGroup
import com.amplifyframework.datastore.generated.model.Tasks
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.databinding.ItemListBinding
import com.enkefalostechnologies.calendarpro.databinding.ItemWaterIntakeDateBinding
import com.enkefalostechnologies.calendarpro.util.AppUtil
import com.enkefalostechnologies.calendarpro.util.AppUtil.getDayFromDate
import com.enkefalostechnologies.calendarpro.util.Extension.dateToTemporalDate
import com.enkefalostechnologies.calendarpro.util.ListAdapterListener
import com.enkefalostechnologies.calendarpro.util.WaterIntakeDateAdapterListener
import java.time.temporal.Temporal
import java.util.Date

class WaterInTakeDateAdapter(var selectedDate:com.amplifyframework.core.model.temporal.Temporal.Date, var listener:WaterIntakeDateAdapterListener)
    : RecyclerView.Adapter<WaterInTakeDateAdapter.ListViewHolder>() {

     var list:List<com.amplifyframework.core.model.temporal.Temporal.Date> = listOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemWaterIntakeDateBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }
   fun setItems(newList:List<com.amplifyframework.core.model.temporal.Temporal.Date>){
       this.list=newList
   }

    override fun getItemCount() = list.size

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        with(holder){
            with(list[position]) {
                if(selectedDate==list[position]) {
                    binding.tvDate.setBackgroundResource(R.drawable.bg_selected_date)
                }else{
                    binding.tvDate.background=null
                }
                if(list[position] <= AppUtil.getCurrentTemporalDate()){
                    binding.tvDay.setTextColor(ContextCompat.getColor(binding.tvDay.context, R.color.color_25282B))
                    binding.tvDate.setTextColor(ContextCompat.getColor(binding.tvDate.context, R.color.color_25282B))
                }else{
                    binding.tvDay.setTextColor(ContextCompat.getColor(binding.tvDay.context, R.color.color_9EA0A2))
                    binding.tvDate.setTextColor(ContextCompat.getColor(binding.tvDate.context, R.color.color_9EA0A2))
                }
                binding.tvDate.text = String.format("%02d", list[position].toDate().date)
                binding.tvDay.text =list[position].toDate().getDayFromDate()
                if(list[position]<=AppUtil.getCurrentTemporalDate()) {
                    holder.itemView.setOnClickListener {
                        selectedDate = list[position]
                        listener.onItemClicked(selectedDate)
                        //   notifyDataSetChanged()
                    }
                }
            }
        }
    }

    inner class ListViewHolder(val binding: ItemWaterIntakeDateBinding)
        :RecyclerView.ViewHolder(binding.root)

}
