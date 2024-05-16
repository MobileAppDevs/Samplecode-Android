package com.dream.friend.ui.login.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.dream.friend.R
import com.dream.friend.data.model.CountryCode
import com.dream.friend.interfaces.CountryCodeItemSelectedListener

class CountryPickerAdapter(private val countryCodeItemSelectedListener: CountryCodeItemSelectedListener) : RecyclerView.Adapter<CountryPickerAdapter.MyViewHolder>() {

    private var myList = arrayListOf<CountryCode>()

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(list: ArrayList<CountryCode>) {
        if (myList.isNotEmpty())
            myList.clear()
        myList.addAll(list)
        this.notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = myList[position]
        holder.tvCountryCode.text = "${data.dial_code}"
        holder.tvCountryName.text = data.name

        holder.llItem.setOnClickListener {
            countryCodeItemSelectedListener.setOnCountryCodeItemSelectedListener(data)
        }
    }

    override fun getItemCount(): Int = myList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
        MyViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.country_picker_item,
                    parent,
                    false
                )
        )

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCountryCode: TextView = itemView.findViewById(R.id.tvCountryCode)
        val tvCountryName: TextView = itemView.findViewById(R.id.tvCountryName)
        val llItem: LinearLayoutCompat = itemView.findViewById(R.id.llItem)
    }
}