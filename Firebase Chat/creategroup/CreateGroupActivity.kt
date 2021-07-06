package com.example.firebasechatapp.example.chatmodule.creategroup


import android.content.Intent
import android.os.Bundle
import com.example.firebasechatapp.R
import com.example.firebasechatapp.example.chatmodule.recentchat.RecentChatActivity
import com.example.firebasechatapp.example.base.BaseActivity
import com.example.firebasechatapp.example.chatmodule.users.User
import kotlinx.android.synthetic.main.activity_create_group2.*
import java.util.*
import kotlin.collections.ArrayList

class CreateGroupActivity : BaseActivity(), CreateGroupView {

    lateinit var createGroupPresenter: CreateGroupPresenter<CreateGroupView>
    lateinit var selectedUsersAdapter: SelectedUsersAdapter
    lateinit var list: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group2)

        list = intent.getSerializableExtra("listdata") as ArrayList<User>

        createGroupPresenter = CreateGroupPresenter()
        createGroupPresenter.onAttach(this)

        create_gp.setOnClickListener {
            if (groupname.text.isEmpty()) {
                showToast("please provide group Name")
            } else {
                val uuid = UUID.randomUUID().toString()
                createGroupPresenter.createGroup(uuid, groupname.text.toString(), list)
            }
        }
    }
    override fun onDestroy() {
        createGroupPresenter.onDetach()
        super.onDestroy()
    }

    override fun loadScreenView() {
        setUpRecyclerView()
    }

    override fun setUpRecyclerView() {
        selectedUsersAdapter = SelectedUsersAdapter()
        seleted_user_recyclerview.adapter = selectedUsersAdapter
        participantcount.text = "participate: ${list.size} and you"
        selectedUsersAdapter.submitList(list)
    }

     private fun moveToRecentChat() {
        val chatIntent = Intent(this, RecentChatActivity::class.java)
        startActivity(chatIntent)
        finishAffinity()
    }

    override fun groupCreateSuccess() {
        moveToRecentChat()
    }

    override fun groupCreateFailure() {
       showToast("Group did not create due to some error")
    }

}