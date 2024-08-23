package com.ongraph.jetpacksample.remote.models

/*
* Login api request body
* */
data class LoginReq(
    val email: String,
    val password: String,
    val fcmToken: String?,
    val device: String = "android"
)
