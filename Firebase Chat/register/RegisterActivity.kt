package com.example.firebasechatapp.example.chatmodule.register

import android.content.Intent
import android.os.Bundle
import com.example.firebasechatapp.R
import com.example.firebasechatapp.example.base.BaseActivity
import com.example.firebasechatapp.example.chatmodule.recentchat.RecentChatActivity
import com.example.firebasechatapp.example.chatmodule.login.LoginActivity
import kotlinx.android.synthetic.main.activity_register.*
import java.lang.Exception

class RegisterActivity : BaseActivity(), RegisterView {

    lateinit var registerPresenter: RegisterPresenter<RegisterView>
    lateinit var name: String
    lateinit var email: String
    lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        registerPresenter = RegisterPresenter()
        registerPresenter.onAttach(this)

        movetologin.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

    }

    override fun registerFlow() {
        registerButton.setOnClickListener {
            name = registerName.text.toString().trim()
            email = registerEmail.text.toString().trim()
            password = registerPassword.text.toString().trim()
            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && password.length > 5
            ) {
                registerPresenter.registerUser(name, email, password)

            } else {
                showToast("Please Fill All the Detail")
            }

        }
    }

    override fun moveToHomeScreen() {
        val chatIntent = Intent(this, RecentChatActivity::class.java)
        chatIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(chatIntent)
        finish()
    }

    override fun registerSuccess() {
        hideProgressView()
        moveToHomeScreen()
        showToast("User register successfully")
    }

    override fun registerFailure(exception: Exception) {
        showToast("" + exception.localizedMessage)
        hideProgressView()
    }

}