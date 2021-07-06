package com.example.firebasechatapp.example.chatmodule.users

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebasechatapp.R
import com.example.firebasechatapp.example.base.BaseActivity
import com.example.firebasechatapp.example.chatmodule.selectusersforgroupchat.SelectUserForGroupActivity
import kotlinx.android.synthetic.main.activity_users.*

class UserListActivity : BaseActivity() , UserListView {

    lateinit var userPresenter: UserListPresenter<UserListView>
    lateinit var userAdapter: UserListAdapter
    private var listUser = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        userPresenter = UserListPresenter()
        userPresenter.onAttach(this)

        new_group_fab.setOnClickListener {
            moveToCreateGroupActivity()
        }
    }

    override fun loadScreenView() {
        userPresenter.loadAllUserList()
    }

    override fun onDestroy() {
        userPresenter.onDetach()
        super.onDestroy()
    }

    private fun setUpUserListRecyclerView() {
        userAdapter = UserListAdapter(this)
        usersRecyclerview.adapter = userAdapter
        usersRecyclerview.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
        userAdapter.user_list = listUser
    }

    override fun moveToCreateGroupActivity() {
        val createGroupIntent = Intent(this, SelectUserForGroupActivity::class.java)
        createGroupIntent.putExtra("listdata",listUser)
        startActivity(createGroupIntent)
    }

    override fun loadAllUsers(user_list: ArrayList<User>) {
        setUpUserListRecyclerView()
        userAdapter.user_list = user_list
        listUser = user_list
        userAdapter.notifyDataSetChanged()
    }
}