package com.example.firebasechatapp.example.chatmodule.users

import com.example.firebasechatapp.example.base.BaseView

interface UserListView :BaseView{

    fun loadScreenView()
    fun moveToCreateGroupActivity()
    fun loadAllUsers(user_list: ArrayList<User>)
}