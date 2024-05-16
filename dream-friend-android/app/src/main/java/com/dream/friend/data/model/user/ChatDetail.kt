package com.dream.friend.data.model.user

data class ChatDetail(
var chatId: String?=null,
var sentBy: String?=null,
var lastMessage:String?=null,
var type:String?=null,
var unreadCount: Int=0,
var dateTime: String?=null,
var channelName: String?=null,
)
