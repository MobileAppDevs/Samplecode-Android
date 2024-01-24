package com.ongraph.jetpackloginsample.remote.models

data class VerifyOtpReq(
    val email: String,
    val otp: String,
)