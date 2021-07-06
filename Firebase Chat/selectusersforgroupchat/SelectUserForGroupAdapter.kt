package com.example.firebasechatapp.example.chatmodule.selectusersforgroupchat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasechatapp.R
import com.example.firebasechatapp.example.chatmodule.users.User

import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.user_view.view.*

class SelectUserForGroupAdapter(val context: Context, private val userSelectCallBack: UserSelectCallBack) :
    RecyclerView.Adapter<SelectUserForGroupAdapter.UserHolder>() {

    var userList = ArrayList<User>()
    private val selectedUserList = ArrayList<User>()
    var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.user_view, parent, false)
        return UserHolder(view)
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val user = userList[position]

        val currentUser = mAuth.currentUser!!.uid

        if (user.user_id == currentUser) {
            holder.userName?.visibility = View.GONE
            holder.userEmail?.visibility = View.GONE
        } else {
            holder.userName?.text = user.name
            holder.userEmail?.text = user.email
        }

        holder.itemView.setOnClickListener {
            if (holder.selectorNotifier?.visibility == View.GONE) {
                holder.selectorNotifier?.visibility = View.VISIBLE
                val selectedUser =
                    User(
                        user.name,
                        user.email,
                        user.user_id
                    )
                selectedUserList.add(selectedUser)
                userSelectCallBack.selectedUser(selectedUserList)
            } else {
                holder.selectorNotifier?.visibility = View.GONE
                val unselectedUser =
                    User(
                        user.name,
                        user.email,
                        user.user_id
                    )
                selectedUserList.remove(unselectedUser)
                userSelectCallBack.selectedUser(selectedUserList)
            }

        }
    }

    override fun getItemCount(): Int {
        return if (userList.size == 0) 0 else userList.size
    }

    inner class UserHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userName: TextView? = null
        var userEmail: TextView? = null
        var selectorNotifier: RelativeLayout? = null

        init {
            userName = itemView.user
            userEmail = itemView.user_email
            selectorNotifier = itemView.selector_notifier
        }

    }
}