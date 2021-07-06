package com.example.firebasechatapp.example.chatmodule.login

import com.example.firebasechatapp.example.base.BaseView

interface LoginView : BaseView {

    fun loginFlowStart()
    fun moveToHomeScreen()
    fun loginSuccess()
    fun loginFailure(exception: String?)

}