package com.example.firebasechatapp.example.chatmodule.creategroup

import androidx.recyclerview.widget.DiffUtil
import com.example.firebasechatapp.example.chatmodule.users.User

class   UserDiffCallBack: DiffUtil.ItemCallback<User>(){

    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
       return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
       return oldItem == newItem
    }

}