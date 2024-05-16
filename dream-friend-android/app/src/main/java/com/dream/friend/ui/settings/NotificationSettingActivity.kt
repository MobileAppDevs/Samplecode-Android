package com.dream.friend.ui.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.SwitchCompat
import com.dream.friend.R
import com.dream.friend.common.Constants
import com.dream.friend.common.Utils.openLoginPage
import com.dream.friend.common.Utils.showToast
import com.dream.friend.common.activityBindings
import com.dream.friend.data.model.IncognitoModeRequest
import com.dream.friend.data.model.NotificationOn
import com.dream.friend.data.model.NotificationRequest
import com.dream.friend.data.network.Resource
import com.dream.friend.databinding.ActivityNotificationSettingBinding
import com.dream.friend.databinding.ActivitySettingBinding
import com.dream.friend.ui.viewModel.HomeScreenViewModel
import com.dream.friend.ui.viewModel.UserLoginViewModel
import okhttp3.internal.notifyAll

class NotificationSettingActivity : AppCompatActivity(), View.OnClickListener {

    private val binding: ActivityNotificationSettingBinding by activityBindings(R.layout.activity_notification_setting)
    private val viewModel: HomeScreenViewModel by viewModels()
    private val viewModelUserLogin: UserLoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.ivBack -> {
                finish()
            }
        }
    }

    private fun init() {
        binding.ivBack.setOnClickListener(this)
        val user = viewModelUserLogin.getUser()
        user?.notificationOn?.newMessage?.let {
            binding.switchNewMessage.isChecked = it
        }
        user?.notificationOn?.match?.let {
            binding.switchMatches.isChecked = it
        }
        user?.notificationOn?.profileVisitors?.let {
            binding.switchProfileVisitors.isChecked = it
        }
        user?.notificationOn?.otherNotification?.let {
            binding.switchOtherNotifications.isChecked = it
        }
        user?.notificationOn?.likedYou?.let {
            binding.switchLikedYou.isChecked = it
        }
        binding.switchNewMessage.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                viewModel.notificationEnableDisable(
                    NotificationRequest(
                        NotificationOn(
                            likedYou = binding.switchLikedYou.isChecked,
                            match = binding.switchMatches.isChecked,
                            newMessage = true,
                            otherNotification = binding.switchOtherNotifications.isChecked,
                            profileVisitors = binding.switchProfileVisitors.isChecked
                        )
                    )
                )
            } else {
                viewModel.notificationEnableDisable(
                    NotificationRequest(
                        NotificationOn(
                            likedYou = binding.switchLikedYou.isChecked,
                            match = binding.switchMatches.isChecked,
                            newMessage = false,
                            otherNotification = binding.switchOtherNotifications.isChecked,
                            profileVisitors = binding.switchProfileVisitors.isChecked
                        )
                    )
                )
            }
        }
        binding.switchMatches.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                viewModel.notificationEnableDisable(
                    NotificationRequest(
                        NotificationOn(
                            likedYou = binding.switchLikedYou.isChecked,
                            match = true,
                            newMessage = binding.switchNewMessage.isChecked,
                            otherNotification = binding.switchOtherNotifications.isChecked,
                            profileVisitors = binding.switchProfileVisitors.isChecked
                        )
                    )
                )
            } else {
                viewModel.notificationEnableDisable(
                    NotificationRequest(
                        NotificationOn(
                            likedYou = binding.switchLikedYou.isChecked,
                            match = false,
                            newMessage = binding.switchNewMessage.isChecked,
                            otherNotification = binding.switchOtherNotifications.isChecked,
                            profileVisitors = binding.switchProfileVisitors.isChecked
                        )
                    )
                )
            }
        }
        binding.switchLikedYou.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                viewModel.notificationEnableDisable(
                    NotificationRequest(
                        NotificationOn(
                            likedYou = true,
                            match = binding.switchMatches.isChecked,
                            newMessage = binding.switchNewMessage.isChecked,
                            otherNotification = binding.switchOtherNotifications.isChecked,
                            profileVisitors = binding.switchProfileVisitors.isChecked
                        )
                    )
                )
            } else {
                viewModel.notificationEnableDisable(
                    NotificationRequest(
                        NotificationOn(
                            likedYou = false,
                            match = binding.switchMatches.isChecked,
                            newMessage = binding.switchNewMessage.isChecked,
                            otherNotification = binding.switchOtherNotifications.isChecked,
                            profileVisitors = binding.switchProfileVisitors.isChecked
                        )
                    )
                )
            }
        }
        binding.switchProfileVisitors.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                viewModel.notificationEnableDisable(
                    NotificationRequest(
                        NotificationOn(
                            likedYou = binding.switchLikedYou.isChecked,
                            match = binding.switchMatches.isChecked,
                            newMessage = binding.switchNewMessage.isChecked,
                            otherNotification = binding.switchOtherNotifications.isChecked,
                            profileVisitors = true
                        )
                    )
                )
            } else {
                viewModel.notificationEnableDisable(
                    NotificationRequest(
                        NotificationOn(
                            likedYou = binding.switchLikedYou.isChecked,
                            match = binding.switchMatches.isChecked,
                            newMessage = binding.switchNewMessage.isChecked,
                            otherNotification = binding.switchOtherNotifications.isChecked,
                            profileVisitors = false
                        )
                    )
                )
            }
        }
        binding.switchOtherNotifications.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                viewModel.notificationEnableDisable(
                    NotificationRequest(
                        NotificationOn(
                            likedYou = binding.switchLikedYou.isChecked,
                            match = binding.switchMatches.isChecked,
                            newMessage = binding.switchNewMessage.isChecked,
                            otherNotification = true,
                            profileVisitors = binding.switchProfileVisitors.isChecked
                        )
                    )
                )
            } else {
                viewModel.notificationEnableDisable(
                    NotificationRequest(
                        NotificationOn(
                            likedYou = binding.switchLikedYou.isChecked,
                            match = binding.switchMatches.isChecked,
                            newMessage = binding.switchNewMessage.isChecked,
                            otherNotification = false,
                            profileVisitors = binding.switchProfileVisitors.isChecked
                        )
                    )
                )
            }
        }
        addObserver()
    }
  private fun  addObserver(){
      viewModel.notificationResponse.observe(this) { response ->
          when (response) {
              is Resource.Success -> {
                  if (response.data?.statusCode == 200) {
//                      val user= viewModelUserLogin.getUser()
//                      user?.notificationOn?.otherNotification=response.data.user.notificationOn?.otherNotification
//                      user?.notificationOn?.newMessage=response.data.user.notificationOn?.newMessage
//                      user?.notificationOn?.match=response.data.user.notificationOn?.match
//                      user?.notificationOn?.likedYou=response.data.user.notificationOn?.likedYou
//                      user?.notificationOn?.otherNotification=response.data.user.notificationOn?.otherNotification
                          viewModelUserLogin.saveUser(response.data.user)
                          showToast(Constants.SettingsSetUpSuccessMsg)

                  } else {
                      response.data?.message?.let { showToast(it) }
                  }
              }

              is Resource.Error, is Resource.Loading -> {}
              is Resource.TokenRenew -> {
                  if (response.isTokenExpire == true) {
                      openLoginPage()
                  }
              }
          }
      }
  }
}