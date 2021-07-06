package com.example.firebasechatapp.example.chatmodule.register

import com.example.firebasechatapp.example.base.BaseView
import java.lang.Exception

interface RegisterView :BaseView{

    fun registerFlow()

    fun moveToHomeScreen()


    fun registerSuccess()

    fun registerFailure(exception: Exception)

}