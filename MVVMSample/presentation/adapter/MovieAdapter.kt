package com.ongraph.mvvmcode.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ongraph.mvvmcode.R
import com.ongraph.mvvmcode.data.model.MovieListItem
import com.ongraph.mvvmcode.databinding.MovieItemLayoutBinding

class MovieAdapter(): RecyclerView.Adapter<MovieAdapter.CustomViewHolder>() {

    private var list: List<MovieListItem> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val bookBinding: MovieItemLayoutBinding = DataBindingUtil.inflate(layoutInflater, R.layout.movie_item_layout, parent, false)
        return CustomViewHolder(bookBinding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = list.size

    class CustomViewHolder(private val movie: MovieItemLayoutBinding) :
        RecyclerView.ViewHolder(movie.root) {
        fun bind(item: MovieListItem) {
            this.movie.datamodel = item
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(mList: List<MovieListItem>){
        this.list = mList
        notifyDataSetChanged()
    }
}