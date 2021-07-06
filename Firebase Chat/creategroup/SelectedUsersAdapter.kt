package com.example.firebasechatapp.example.chatmodule.creategroup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasechatapp.R
import com.example.firebasechatapp.example.chatmodule.users.User
import kotlinx.android.synthetic.main.selected_user_view.view.*

class SelectedUsersAdapter : ListAdapter<User, SelectedUsersAdapter.Holder>(UserDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(parent.context).inflate(
                R.layout.selected_user_view, parent, false
            )
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView? = null

        init { name = itemView.selected_user_name }

        fun bind(user: User?){ name?.text = user?.name}
    }

}


