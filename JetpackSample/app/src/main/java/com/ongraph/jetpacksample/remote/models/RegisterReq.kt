package com.ongraph.jetpacksample.remote.models

/*
* Signup api request body
* */
data class RegisterReq(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val fcmToken: String? = "fc-token",
    val device: String = "android",
    val isNotificationEnable: Boolean = false,
    val phone: String? = null,
)