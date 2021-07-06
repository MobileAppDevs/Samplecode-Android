package com.example.firebasechatapp.example.chatmodule.recentchat

import android.util.Log
import com.example.firebasechatapp.example.base.BasePresenter
import com.example.firebasechatapp.example.firebasework.DbPresenter
import com.example.firebasechatapp.example.utils.TAG

class RecentChatPresenter<T : RecentChatView> : BasePresenter<T>(), DbPresenter {

    override fun onAttach(view: T) {
        super.onAttach(view)
        if (isViewAttach()) {
            getBaseView()?.loadScreenView()
        }
    }

    fun loadAllRecentChats() {
        Log.i(TAG, "loadAllRecentChats: RecentChatPresenter")
        //Log.i(TAG, "loadScreenView: RecentChatActivity ")
        if (isViewAttach()) {
            if (getBaseView()?.isInternetAvailable()!!) {
                getBaseView()?.getDbInstance(this)?.getAllRecentChat()
            } else {
                getBaseView()?.showInternetErrorMessage()
            }
        }
    }

    fun loadRecentChat(recent_list: MutableList<RecentChatModel>) {
        if (isViewAttach()) {
            if (getBaseView()?.isInternetAvailable()!!)
                getBaseView()?.loadRecentChat(recent_list)
            else
                getBaseView()?.showInternetErrorMessage()
        }
    }

    fun loadAllGroupInRecentChat() {
        if (isViewAttach()) {
            if (getBaseView()?.isInternetAvailable()!!) {
                getBaseView()?.getDbInstance(this)?.getGroupRecentChat()
            } else {
                getBaseView()?.showInternetErrorMessage()
            }
        }
    }

    fun loadAllRecentGroups(group_list: MutableList<RecentChatModel>){
        if (isViewAttach()) {
            if (getBaseView()?.isInternetAvailable()!!)
                getBaseView()?.loadGroupRecentChat(group_list)
            else
                getBaseView()?.showInternetErrorMessage()
        }
    }

}