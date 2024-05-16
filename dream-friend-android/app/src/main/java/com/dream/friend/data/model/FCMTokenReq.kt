package com.dream.friend.data.model

data class FCMTokenReq (
    val userId: String,
    val fcmToken: String,
    val deviceType: String = "android",
)