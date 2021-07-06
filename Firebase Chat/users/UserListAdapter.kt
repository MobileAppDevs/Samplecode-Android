package com.example.firebasechatapp.example.chatmodule.users

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasechatapp.R
import com.example.firebasechatapp.example.chatmodule.chat.ChatActivity
import com.example.firebasechatapp.example.utils.AppConstants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.user_view.view.*

class UserListAdapter(val context: Context) : RecyclerView.Adapter<UserListAdapter.UserHolder>() {

    var user_list = mutableListOf<User>()
    var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.user_view, parent, false)
        return UserHolder(view)
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val user = user_list[position]

        val cuurent_user = mAuth.currentUser!!.uid

        if (user.user_id.equals(cuurent_user)) {
            holder.user_name?.visibility = View.GONE
            holder.user_email?.visibility = View.GONE
        } else {
            holder.user_name?.text = user.name
            holder.user_email?.text = user.email
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(AppConstants.ID, user.user_id)
            intent.putExtra(AppConstants.NAME, user.name)
            context.startActivity(intent)

        }

    }

    override fun getItemCount(): Int {
        return if (user_list.size == 0) 0 else user_list.size

    }

   inner class UserHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var user_name: TextView? = null
        var user_email: TextView? = null

        init {
            user_name = itemView.user
            user_email = itemView.user_email
        }

    }
}