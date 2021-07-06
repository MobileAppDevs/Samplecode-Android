package com.example.firebasechatapp.example.chatmodule.selectusersforgroupchat

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebasechatapp.R
import com.example.firebasechatapp.example.base.BaseActivity
import com.example.firebasechatapp.example.chatmodule.creategroup.CreateGroupActivity
import com.example.firebasechatapp.example.chatmodule.users.User
import kotlinx.android.synthetic.main.activity_create_group.*

class SelectUserForGroupActivity : BaseActivity(), SelectUserForGroupView,
    UserSelectCallBack {

    lateinit var createGroupPresenter: SelectUserForGroupPresenter<SelectUserForGroupView>
    lateinit var createGroupAdapter: SelectUserForGroupAdapter
    private var listUser = ArrayList<User>()
    private var finalSelectedUsers = ArrayList<User>()
    lateinit var listForSelection: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        listForSelection = intent.getSerializableExtra("listdata") as ArrayList<User>

        createGroupPresenter = SelectUserForGroupPresenter()
        createGroupPresenter.onAttach(this)

        move_to_creategroup.setOnClickListener {
            if (finalSelectedUsers.size > 0)
                moveToCreateGroupActivity()
            else
                showToast("At least 1 contact must be selected")
        }

    }

    override fun loadScreenView() {
        setUpUserRecyclerView()
        createGroupAdapter.userList = listForSelection
        createGroupAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        createGroupPresenter.onDetach()
        super.onDestroy()
    }

    private fun moveToCreateGroupActivity() {
        val createGroupIntent = Intent(this, CreateGroupActivity::class.java)
        createGroupIntent.putExtra("listdata",finalSelectedUsers)
        startActivity(createGroupIntent)
    }

    private fun setUpUserRecyclerView() {
        createGroupAdapter = SelectUserForGroupAdapter(this, this)
        create_group_recyclerview.adapter = createGroupAdapter
        create_group_recyclerview.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
        createGroupAdapter.userList = listUser
    }

    override fun selectedUser(selected_user_list:  ArrayList<User>) {
        finalSelectedUsers = selected_user_list
    }
}


