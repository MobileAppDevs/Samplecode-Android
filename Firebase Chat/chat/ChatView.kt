package com.example.firebasechatapp.example.chatmodule.chat

import com.example.firebasechatapp.example.base.BaseView
import java.lang.Exception

interface ChatView : BaseView {

    fun loadScreenView()

    fun setAnotherUserOnlineStatus(onlineStatus: Boolean)

    fun loadUsersConversation(user_messages: Message)

    fun messageDeleteSuccess(position: Int)

    fun messageDeleteFailure(position: Int)

    fun uploadImageSuccess(toString: String, timestamp: Long)

    fun uploadImageFailure(exception: Exception)

    fun uploadVideoSuccess(uploadVideoUrl: String, timestamp: Long)

    fun uploadVideoFailure(exception: Exception)

}