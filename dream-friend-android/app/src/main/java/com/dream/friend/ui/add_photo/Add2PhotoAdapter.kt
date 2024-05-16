package com.dream.friend.ui.add_photo

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dream.friend.R
import com.dream.friend.interfaces.AddImageClickListener
import com.dream.friend.common.hide
import com.dream.friend.common.show

class Add2PhotoAdapter : RecyclerView.Adapter<Add2PhotoAdapter.ViewHolder>(){

    private lateinit var context: Context
    private lateinit var listener: AddImageClickListener
    private var images = arrayListOf<String>()

    @SuppressLint("NotifyDataSetChanged")
    fun setImages(images: ArrayList<String>) {
        this.images = images
        notifyDataSetChanged()
    }

    fun setOnAddImageListener(listener: AddImageClickListener) {
        this.listener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (images.isNotEmpty() && images.size > position) {
            holder.ivProfileImage.setImageDrawable (null)
            Glide
                .with(context)
                .load(images[position])
                .placeholder(R.drawable.drawable_cr9dot5cffff8960c12ffff62a5)
                .error(R.drawable.drawable_bg_cr12cfff7f6)
                .into(holder.ivProfileImage)

            holder.ivProfileImage.scaleType = ImageView.ScaleType.CENTER_CROP
            holder.ivProfileImageAdd.hide()
            holder.ivDelete.show()
//            holder.ivBg.show()
            if (images.size <= 2)
                holder.ivDelete.hide()
        } else {
            holder.ivProfileImage.setImageResource(R.drawable.add_photo_bg)
            holder.ivProfileImageAdd.show()
            holder.ivDelete.hide()
//            holder.ivBg.hide()
        }

        holder.ivProfileImage.setOnClickListener {
            if (!holder.ivProfileImageAdd.isVisible)
                listener.setOnClickListener(position)
        }

        holder.ivProfileImageAdd.setOnClickListener {
            listener.setOnAddImageItemListener()
        }

        holder.ivDelete.setOnClickListener {
            listener.setOnImageItemDeleteListener(images[position])
        }
    }

    override fun getItemCount(): Int {
        return 6
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context

        return ViewHolder(
            LayoutInflater
                .from(context)
                .inflate(
                    R.layout.add_photo_items,
                    parent,
                    false
                )
        )
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProfileImageAdd: ImageView = itemView.findViewById(R.id.ivProfileImageAdd)
        val ivProfileImage: ImageView = itemView.findViewById(R.id.ivProfileImage)
        val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
//        val ivBg: ImageView = itemView.findViewById(R.id.ivBg)
    }
}