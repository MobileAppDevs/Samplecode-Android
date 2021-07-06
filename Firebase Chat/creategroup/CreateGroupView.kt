package com.example.firebasechatapp.example.chatmodule.creategroup

import com.example.firebasechatapp.example.base.BaseView

interface CreateGroupView : BaseView{

    fun loadScreenView()
    fun setUpRecyclerView()
    fun groupCreateSuccess()
    fun groupCreateFailure()
}