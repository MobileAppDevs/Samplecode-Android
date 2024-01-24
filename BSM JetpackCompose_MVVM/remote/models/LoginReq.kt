package com.ongraph.jetpackloginsample.remote.models

data class LoginReq(
    val email: String,
    val password: String,
    val fcmToken: String?,
    val device: String = "android"
)
