package com.example.firebasechatapp.example.chatmodule.selectusersforgroupchat

import com.example.firebasechatapp.example.chatmodule.users.User

interface UserSelectCallBack {

    fun selectedUser(selected_user_list: ArrayList<User>)
}