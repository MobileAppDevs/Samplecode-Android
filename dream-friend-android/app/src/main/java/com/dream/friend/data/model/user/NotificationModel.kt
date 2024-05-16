package com.dream.friend.data.model.user

data class NotificationModel(
    val newMessage:Boolean?=false,
    val match:Boolean?=false,
    val likedYou:Boolean?=false,
    val profileVisitors:Boolean?=false,
    val otherNotification:Boolean?=false,
)