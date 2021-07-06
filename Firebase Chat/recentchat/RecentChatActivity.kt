package com.example.firebasechatapp.example.chatmodule.recentchat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.firebasechatapp.R
import com.example.firebasechatapp.example.base.BaseActivity
import com.example.firebasechatapp.example.chatmodule.register.RegisterActivity
import com.example.firebasechatapp.example.chatmodule.users.UserListActivity
import com.example.firebasechatapp.example.utils.TAG
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_recentchat.*

class RecentChatActivity : BaseActivity(), RecentChatView {

    lateinit var recentChatPresenter: RecentChatPresenter<RecentChatView>
    private lateinit var recentChatAdapter: RecentChatAdapter
    var recentUserList = mutableListOf<RecentChatModel>()
    private var groupRecentList = mutableListOf<RecentChatModel>()
    var mediatorList = mutableListOf<RecentChatModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recentchat)

        recentChatPresenter = RecentChatPresenter()

        recentChatPresenter.onAttach(this)

        setUpRecyclerView()
        logout.setOnClickListener {
            logOut()
        }
        fab.setOnClickListener {
            val userIntent = Intent(this, UserListActivity::class.java)
            startActivity(userIntent)
        }

    }

    override fun onDestroy() {
        recentChatPresenter.onDetach()
        super.onDestroy()
    }

    override fun loadScreenView() {
        Log.i(TAG, "loadScreenView: RecentChatActivity ")
        recentChatProgressBar.show()
        recentChatPresenter.loadAllRecentChats()
        recentChatPresenter.loadAllGroupInRecentChat()
    }

    override fun logOut() {
        FirebaseAuth.getInstance().signOut()
        sendToRegisterScreen()
    }

    override fun setUpRecyclerView() {
        recentChatAdapter = RecentChatAdapter(this)
        chated_userRecyclerview.adapter = recentChatAdapter
    }

    private fun sendToRegisterScreen() {
        val logout = Intent(this, RegisterActivity::class.java)
        startActivity(logout)
        finish()
    }

    override fun onRestart() {
        recentChatProgressBar.show()
        recentChatAdapter.recentChatList.clear()
        recentChatPresenter.loadAllRecentChats()
        recentChatPresenter.loadAllGroupInRecentChat()
        recentChatAdapter.notifyDataSetChanged()
        super.onRestart()
    }

    override fun loadRecentChat(user_list: MutableList<RecentChatModel>) {
        recentUserList = if (recentUserList.size == 0) {
            user_list
        } else {
            recentUserList.clear()
            user_list
        }
        setDataOnRecyclerView()
    }

    override fun loadGroupRecentChat(groupList: MutableList<RecentChatModel>) {
        groupRecentList = if (groupRecentList.size == 0) {
            groupList
        } else {
            groupRecentList.clear()
            groupList
        }
        setDataOnRecyclerView()
    }

    private fun setDataOnRecyclerView() {
        recentChatAdapter.recentChatList.clear()
        recentChatAdapter.notifyDataSetChanged()
        mediatorList.clear()
        mediatorList.addAll(recentUserList)
        mediatorList.addAll(groupRecentList)
        mediatorList.sortByDescending {
            it.last_message.timestamp
        }
        recentChatAdapter.recentChatList.addAll(mediatorList)
        recentChatAdapter.notifyDataSetChanged()
        recentChatProgressBar.hide()
    }

}