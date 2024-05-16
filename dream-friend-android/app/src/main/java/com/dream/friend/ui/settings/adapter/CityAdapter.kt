package com.dream.friend.ui.settings.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.dream.friend.R
import com.dream.friend.data.model.cititesModel.Prediction
import com.dream.friend.interfaces.SelectedInterestsCancelListener

class CityAdapter:RecyclerView.Adapter<CityAdapter.VH>() {

    private val list = mutableListOf<Prediction>()
    private lateinit var listener: SelectedInterestsCancelListener

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: MutableList<Prediction>) {
        this.list.clear()
        this.list.addAll(list)
        this.notifyDataSetChanged()
    }

    fun setListener(listener: SelectedInterestsCancelListener) {
        this.listener = listener
    }

    class VH(item: View): ViewHolder(item) {
        val tvCityName: TextView = item.findViewById(R.id.tvCityName)
        val ll: LinearLayoutCompat = item.findViewById(R.id.ll)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.city_item, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (list.size > 0) {
            //TODO(WE MUST USE DESCRIPTIONS INSTEAD OF MAIN TEXT)
//            holder.tvCityName.text = list[position].description
            holder.tvCityName.text = list[position].structured_formatting.main_text
            holder.ll.setOnClickListener {
//                listener.onSelectedInterestsCancelListener(list[position].description)
                listener.onSelectedInterestsCancelListener(list[position].structured_formatting.main_text)
            }
        }
    }

    override fun getItemCount() = list.size
}