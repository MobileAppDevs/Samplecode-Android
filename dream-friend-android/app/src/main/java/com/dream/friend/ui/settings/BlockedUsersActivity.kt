package com.dream.friend.ui.settings

import android.app.Dialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.dream.friend.R
import com.dream.friend.common.Utils.createDialogWithFullWidth
import com.dream.friend.common.Utils.dialogLoading
import com.dream.friend.common.Utils.openLoginPage
import com.dream.friend.common.Utils.showToast
import com.dream.friend.common.activityBindings
import com.dream.friend.common.hide
import com.dream.friend.common.show
import com.dream.friend.common.startRotationAnimation
import com.dream.friend.data.model.user.User
import com.dream.friend.data.network.Resource
import com.dream.friend.databinding.ActivityBlockedUsersBinding
import com.dream.friend.interfaces.OnClickBlockedUSerListener
import com.dream.friend.ui.settings.adapter.BlockedUsersAdapter
import com.dream.friend.ui.viewModel.HomeScreenViewModel
import com.dream.friend.ui.viewModel.UserLoginViewModel

class BlockedUsersActivity : AppCompatActivity(), View.OnClickListener {

    private val binding: ActivityBlockedUsersBinding by activityBindings(R.layout.activity_blocked_users)
    lateinit var dialog: Dialog
    private val viewModelUserLogin: UserLoginViewModel by viewModels()
    private val viewModel: HomeScreenViewModel by viewModels()
    private lateinit var user: User
    private val adapter = BlockedUsersAdapter()
    private var users = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        dialog = dialogLoading()
        dialog.dismiss()

//        blockedUserView = BlockUserDialogBinding.inflate(LayoutInflater.from(this))
//        blockedUserDialog = createDialogWithFullWidth(blockedUserView.root)
//        blockedUserDialog.dismiss()

        user = viewModelUserLogin.getUser()!!

        setAdapter()
        addObserver()

        viewModel.getBlockedUsers()

        binding.ivBack.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0) {
            binding.ivBack -> finish()
        }
    }

    private fun setAdapter() {
        binding.rvBlockedUsers.adapter = adapter
        adapter.listen(object : OnClickBlockedUSerListener {
            override fun onClick(position: Int) {
                users[position].userId?.let { viewModel.blockUser(it,"0") }
            }
        })
    }

    private fun addObserver() {
        viewModel.getBlockedUsersResponse.observe(this) { response ->
            when (response) {
                is Resource.Error -> {
                    dialog.dismiss()
                    showToast(response.message.toString())
                }
                is Resource.Loading -> {
                    dialog.show()
                    dialog.findViewById<ImageView>(R.id.imgAnimation).startRotationAnimation()
                }
                is Resource.Success -> {
                    dialog.dismiss()
                    users.clear()
                    response.data?.users?.let { users.addAll(it) }
                    adapter.setList(response.data?.users)

                    if (response.data?.users == null || response.data.users.size == 0)
                        binding.tvNoDataFound.show()
                    else
                        binding.tvNoDataFound.hide()
                }
                is Resource.TokenRenew -> {
                    dialog.dismiss()
                    openLoginPage()
                }
            }
        }

        viewModel.blockUserResponse.removeObservers(this)
        viewModel.blockUserResponse.observe(this) { response ->
            when (response) {
                is Resource.Error -> {
                    dialog.dismiss()
                    showToast(response.message.toString())
                }
                is Resource.Loading -> {
                    dialog.show()
                    dialog.findViewById<ImageView>(R.id.imgAnimation).startRotationAnimation()
                }
                is Resource.Success -> {
                    dialog.dismiss()
                    viewModel.getBlockedUsers()
                }
                is Resource.TokenRenew -> {
                    dialog.dismiss()
                    openLoginPage()
                }
            }
        }
    }
}