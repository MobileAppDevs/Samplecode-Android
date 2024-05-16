package com.dream.friend.data.model

data class NotificationOn(
    var likedYou: Boolean?=true,
    var match: Boolean?=true,
    var newMessage: Boolean?=true,
    var otherNotification: Boolean?=true,
    val profileVisitors: Boolean?=true
)