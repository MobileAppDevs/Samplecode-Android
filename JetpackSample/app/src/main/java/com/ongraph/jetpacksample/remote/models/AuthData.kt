package com.ongraph.jetpacksample.remote.models

/*
* authentication response data
* */
data class AuthData(
    var id: Long?,
    val phone: String,
    var firstname: String?,
    var lastname: String?,
    var email: String?,
    var token: String?,
    var refreshToken: String?,
    var fcmToken: String?,
    var isNotificationEnable: Boolean?,
    var isBabyPrivacyEnable: Boolean?
)