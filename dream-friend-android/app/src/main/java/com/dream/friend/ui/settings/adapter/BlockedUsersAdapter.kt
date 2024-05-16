package com.dream.friend.ui.settings.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.dream.friend.R
import com.dream.friend.data.model.user.User
import com.dream.friend.interfaces.OnClickBlockedUSerListener

class BlockedUsersAdapter: RecyclerView.Adapter<BlockedUsersAdapter.VH>() {

    private lateinit var context: Context
    private var list = ArrayList<User>()

    private lateinit var listener: OnClickBlockedUSerListener

    fun listen(listener: OnClickBlockedUSerListener) {
        this.listener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: ArrayList<User>?) {
        this.list.clear()
        if (list!=null)
            this.list.addAll(list)
        this.notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val data = list[position]

        holder.tvName.text = data.userName
        if (data.images.size > 0)
            Glide.with(context)
                .load(data.images[0])
                .into(holder.ivProfile)

        holder.tvUnblock.setOnClickListener {
            listener.onClick(position)
        }
    }

    class VH(item: View): RecyclerView.ViewHolder(item) {
        val ivProfile: ShapeableImageView = item.findViewById(R.id.ivProfile)
        val tvName: TextView = item.findViewById(R.id.tvName)
        val tvUnblock: TextView = item.findViewById(R.id.tvUnblock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        context = parent.context
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.blocked_users_item, parent, false))
    }

    override fun getItemCount() = list.size
}