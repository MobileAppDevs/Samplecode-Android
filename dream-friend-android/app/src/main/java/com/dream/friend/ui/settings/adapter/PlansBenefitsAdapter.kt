package com.dream.friend.ui.settings.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.dream.friend.R

class PlansBenefitsAdapter: RecyclerView.Adapter<PlansBenefitsAdapter.VH>() {

    val list = mutableListOf<String>()

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: MutableList<String>) {
        this.list.clear()
        this.list.addAll(list)
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.plan_benefit_item, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (list.size > 0) {
            holder.tvBenefit.text = list[position]
        }
    }

    override fun getItemCount() = list.size

    class VH(item: View): ViewHolder(item) {
        val tvBenefit: TextView = item.findViewById(R.id.tvBenefit)
    }
}