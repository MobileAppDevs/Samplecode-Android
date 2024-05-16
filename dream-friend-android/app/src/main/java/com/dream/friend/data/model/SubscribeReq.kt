package com.dream.friend.data.model

data class SubscribeReq (
    val userId: String,
    val planName: String,
    val planId: String,
    val planStartDate: String,
    val planExpiryDate: String,
)

data class AllBenefitsReq(val active: Boolean = true)

data class MyBenefitsReq(val userId: String)