package com.nisha.mvvmstructure.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nisha.mvvmstructure.data.model.ListItem
import com.nisha.mvvmstructure.databinding.DetailsItemLayoutBinding

class MyAdapter: RecyclerView.Adapter<MyAdapter.CustomViewHolder>() {

    // List to hold the movie items
    private var list: List<ListItem> = mutableListOf()

    // Called when RecyclerView needs a new ViewHolder of the given type to represent an item
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomViewHolder {
        // Inflate the layout for the movie item and create a new ViewHolder
        return CustomViewHolder(

            /**
             * todo: replace with your layout
             * */

            DetailsItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    // Called by RecyclerView to display the data at the specified position
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        // Get the item from the list at the given position and bind it to the ViewHolder
        val item = list[position]
        holder.bind(item)
    }

    // Returns the total number of items in the data set held by the adapter
    override fun getItemCount(): Int = list.size

    // ViewHolder class to hold the view for each movie item
    class CustomViewHolder(private val detailBinding: DetailsItemLayoutBinding) :
        RecyclerView.ViewHolder(detailBinding.root) {
        // Bind the movie item data to the views
        fun bind(item: ListItem) {
            // Set the item
            this.detailBinding.tvTitle.text = item.listItem1
        }
    }

    // Function to update the data in the adapter
    @SuppressLint("NotifyDataSetChanged")
    fun setData(mList: List<ListItem>) {
        // Set the new list of movies
        this.list = mList
        // Notify the adapter that the data set has changed so it can update the views
        notifyDataSetChanged()
    }
}
