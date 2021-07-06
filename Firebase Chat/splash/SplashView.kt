package com.example.firebasechatapp.example.chatmodule.splash

import com.example.firebasechatapp.example.base.BaseView

interface SplashView : BaseView {

    fun loadScreenView()

    fun moveToRegistration()

    fun moveToChatActivity();
}