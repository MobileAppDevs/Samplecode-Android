package com.ongraph.jetpackloginsample.remote.models

data class RegisterReq(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val fcmToken: String? = "token",
    val device: String = "android",
    val isNotificationEnable: Boolean = false,
    val phone: String? = null,
)