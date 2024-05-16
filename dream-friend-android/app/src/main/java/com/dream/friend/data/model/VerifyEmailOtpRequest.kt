package com.dream.friend.data.model

data class VerifyEmailOtpRequest(
    val email: String,
    val hashToken: String,
    val otp: String
)