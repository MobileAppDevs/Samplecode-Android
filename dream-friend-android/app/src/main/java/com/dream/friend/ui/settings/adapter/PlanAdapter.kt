package com.dream.friend.ui.settings.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.dream.friend.R
import com.dream.friend.data.model.PlanModel
import com.dream.friend.interfaces.PlanSelectedListener

class PlanAdapter: RecyclerView.Adapter<PlanAdapter.VH>() {

    val list = mutableListOf<PlanModel>()
    private lateinit var listener: PlanSelectedListener

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: MutableList<PlanModel>) {
        this.list.clear()
        this.list.addAll(list)
        this.notifyDataSetChanged()
    }

    fun setListener(listener: PlanSelectedListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.plan_item, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (list.size > 0) {
            holder.tvTitle.text = list[position].title
            holder.tvTime.text = list[position].time
            holder.tvPrice.text = list[position].price
            if (list[position].isSelected) {
                holder.cl.setBackgroundResource(R.drawable.plan_bg_selected)
                holder.ivPlanSelected.setImageResource(R.drawable.plan_select)
            }
            else {
                holder.cl.setBackgroundResource(R.drawable.plan_bg_unselected)
                holder.ivPlanSelected.setImageResource(R.drawable.plan_unselect)
            }

            holder.cl.setOnClickListener {
                listener.onPlanSelected(list[position].position)
            }
        }
    }

    override fun getItemCount() = list.size

    class VH(item: View): ViewHolder(item) {
        val ivPlanSelected: ImageView = item.findViewById(R.id.ivPlanSelected)
        val cl: ConstraintLayout = item.findViewById(R.id.cl)
        val tvTitle: TextView = item.findViewById(R.id.tvTitle)
        val tvTime: TextView = item.findViewById(R.id.tvTime)
        val tvPrice: TextView = item.findViewById(R.id.tvPrice)
    }
}