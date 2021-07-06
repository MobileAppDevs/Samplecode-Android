package com.example.firebasechatapp.example.chatmodule.recentchat

import com.example.firebasechatapp.example.base.BaseView

interface RecentChatView : BaseView {

    fun loadScreenView()
    fun logOut()
    fun setUpRecyclerView()
    fun loadRecentChat(user_list: MutableList<RecentChatModel>)
    fun loadGroupRecentChat(groupList: MutableList<RecentChatModel>)

}