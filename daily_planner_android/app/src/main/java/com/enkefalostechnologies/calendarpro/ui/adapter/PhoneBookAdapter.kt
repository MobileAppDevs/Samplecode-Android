package com.enkefalostechnologies.calendarpro.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.databinding.PhoneBookItemBinding
import com.enkefalostechnologies.calendarpro.model.LocalContact
import com.enkefalostechnologies.calendarpro.util.PhoneBookAdapterItemListener

class PhoneBookAdapter(val listener: PhoneBookAdapterItemListener): RecyclerView.Adapter<PhoneBookAdapter.ListViewHolder>() {

    private val list = mutableListOf<LocalContact>()
    private lateinit var context: Context

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: MutableList<LocalContact>?) {
        this.list.clear()
        list?.let { this.list.addAll(it) }
        this.notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        with(list[position]) {
            holder.binding.tvName.text = name
            holder.binding.tvPhone.text = mobileNumber
            Glide.with(context)
                .load(imagePath)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .into(holder.binding.ivProfile)

            holder.itemView.setOnClickListener {
                listener.onClick(this)
            }
        }
    }

    override fun getItemCount(): Int = list.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        context = parent.context
        return ListViewHolder(PhoneBookItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    inner class ListViewHolder (val binding: PhoneBookItemBinding)
        :RecyclerView.ViewHolder(binding.root)
}