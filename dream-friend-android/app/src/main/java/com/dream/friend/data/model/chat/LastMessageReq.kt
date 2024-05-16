package com.dream.friend.data.model.chat

data class LastMessageReq(
    val channelName: String,
    val lastMessage: String,
    val type: String,
    val unreadCount: String
)