package com.example.firebasechatapp.example.chatmodule.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.firebasechatapp.R
import com.example.firebasechatapp.example.base.BaseActivity
import com.example.firebasechatapp.example.chatmodule.recentchat.RecentChatActivity
import com.example.firebasechatapp.example.chatmodule.register.RegisterActivity
import com.google.firebase.auth.FirebaseUser

class SplashActivity : BaseActivity(), SplashView {

    lateinit var splashPresenter: SplashPresenter<SplashView>

    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        setContentView(R.layout.activity_main)

        //instances
        splashPresenter = SplashPresenter()

        splashPresenter.onAttach(this)
        firebaseUser = firebaseAuth.currentUser

    }

    override fun onDestroy() {
        splashPresenter.onDetach()
        super.onDestroy()
    }

    override fun loadScreenView() {
        val handler = Handler(Looper.myLooper()!!)
        handler.postDelayed({
            if (firebaseUser == null) {
                moveToRegistration()
            }
            else{
                moveToChatActivity()
            }
        }, 2000)

    }

    override fun moveToRegistration() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun moveToChatActivity() {
        val intent = Intent(this, RecentChatActivity::class.java)
        startActivity(intent)
        finish()
    }


}