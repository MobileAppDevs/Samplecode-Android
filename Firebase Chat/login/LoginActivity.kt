package com.example.firebasechatapp.example.chatmodule.login

import android.content.Intent
import android.os.Bundle
import com.example.firebasechatapp.R
import com.example.firebasechatapp.example.base.BaseActivity
import com.example.firebasechatapp.example.chatmodule.recentchat.RecentChatActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity(), LoginView {

    lateinit var email: String
    lateinit var password: String
    lateinit var loginPresenter: LoginPresenter<LoginView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginPresenter = LoginPresenter()
        loginPresenter.onAttach(this)
    }

    override fun onDestroy() {
        loginPresenter.onDetach()
        super.onDestroy()
    }

    override fun loginFlowStart() {
        loginbutton.setOnClickListener {
            email = loginemail.text.toString().trim()
            password = loginpassword.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty() && password.length > 5) {
                loginPresenter.loginUser(email,password)
            } else {
                showToast("Either password is wrong or email")
            }
        }
    }

    override fun moveToHomeScreen() {
        val chatIntent = Intent(this, RecentChatActivity::class.java)
        chatIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(chatIntent)
        finish()
    }

    override fun loginSuccess() {
        hideProgressView()
        moveToHomeScreen()
        showToast("Login Register Successfully")
        showToast("Got some error in registration ")
    }

    override fun loginFailure(exception: String?) {
        showToast("Got some error in login $exception")
        hideProgressView()
    }
}