package com.nisha.mvvmstructure.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nisha.mvvmstructure.data.model.MovieListItem
import com.nisha.mvvmstructure.databinding.MovieItemLayoutBinding
import com.nisha.mvvmstructure.utils.Utils

class MovieAdapter: RecyclerView.Adapter<MovieAdapter.CustomViewHolder>() {

    // List to hold the movie items
    private var list: List<MovieListItem> = mutableListOf()

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

            MovieItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    class CustomViewHolder(private val movie: MovieItemLayoutBinding) :
        RecyclerView.ViewHolder(movie.root) {
        // Bind the movie item data to the views
        fun bind(item: MovieListItem) {
            // Load the image using a utility function
            Utils.loadImage(this.movie.ivImage, "https://image.tmdb.org/t/p/w500" + item.posterPath)
            // Set the description text
            this.movie.tvDescription.text = item.overview
            // Set the title text
            this.movie.tvTitle.text = item.title
        }
    }

    // Function to update the data in the adapter
    @SuppressLint("NotifyDataSetChanged")
    fun setData(mList: List<MovieListItem>) {
        // Set the new list of movies
        this.list = mList
        // Notify the adapter that the data set has changed so it can update the views
        notifyDataSetChanged()
    }
}
