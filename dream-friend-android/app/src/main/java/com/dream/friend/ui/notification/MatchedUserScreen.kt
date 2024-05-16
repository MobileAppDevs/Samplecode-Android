package com.dream.friend.ui.notification

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.dream.friend.R
import com.dream.friend.common.Utils.dialogLoading
import com.dream.friend.common.Utils.openLoginPage
import com.dream.friend.common.activityBindings
import com.dream.friend.common.startRotationAnimation
import com.dream.friend.data.model.MatchedUserDataString
import com.dream.friend.data.model.user.User
import com.dream.friend.data.network.Resource
import com.dream.friend.databinding.ActivityMatchedUserScreenBinding
import com.dream.friend.ui.home.activities.ChatActivity
import com.dream.friend.ui.viewModel.HomeScreenViewModel
import com.google.gson.Gson

class MatchedUserScreen : AppCompatActivity() {

    val binding: ActivityMatchedUserScreenBinding by activityBindings(R.layout.activity_matched_user_screen)
    private val viewModel: HomeScreenViewModel by viewModels()
    lateinit var dialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        dialog = dialogLoading()
        dialog.dismiss()
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
        val data = intent.getStringExtra("data")
        val matchedUserData = Gson().fromJson(data, MatchedUserDataString::class.java)
        binding.btnNotNow.setOnClickListener {
            finish()
        }
        binding.btnHello.setOnClickListener {
            viewModel.getUserDetail(matchedUserData.user1Id)
        }
        matchedUserData?.user1Name?.let {
            binding.tvName.text = it
        }
        if (matchedUserData?.user1Image?.isNotEmpty()==true) {
            Glide.with(binding.root.context).load(matchedUserData.user1Image[0])
                .placeholder(R.drawable.drawable_cr9dot5cffff8960c12ffff62a5)
                .error(R.drawable.drawable_bg_cr12cfff7f6).into(binding.ivUser1)
        }
        if (matchedUserData?.user2Image?.isNotEmpty()==true) {
            Glide.with(binding.root.context).load(matchedUserData.user2Image[0])
                .placeholder(R.drawable.drawable_cr9dot5cffff8960c12ffff62a5)
                .error(R.drawable.drawable_bg_cr12cfff7f6).into(binding.ivUser2)
        }
        addObserver()
    }

    private fun addObserver() {
        viewModel.getUserDetailResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    dialog.dismiss()
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.putExtra("chat-item", Gson().toJson(it.data?.user))
                    intent.putExtra("msg","Hello 👋")
                    startActivity(intent)
                    finish()
                }

                is Resource.Error -> {
                    dialog.dismiss()

                }

                is Resource.Loading -> {
                    dialog.show()
                    dialog.findViewById<ImageView>(R.id.imgAnimation).startRotationAnimation()
                }

                is Resource.TokenRenew -> {
                    dialog.dismiss()
                    if (it.isTokenExpire == true) {
                        openLoginPage()
                    }
                }
            }

        }
    }

    private fun removeObserver() {
        viewModel.getUserDetailResponse.removeObservers(this)
    }

    override fun onDestroy() {
        removeObserver()
        super.onDestroy()
    }
}