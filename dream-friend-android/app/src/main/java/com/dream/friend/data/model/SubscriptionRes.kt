package com.dream.friend.data.model

data class SubscriptionRes (
    val data: AdvanceFilterData,
    val status: Int,
    val message: String
)

data class Data(
    val goldSubscribe: Subscription? = null,
    val monthlySubscribe: Subscription? = null,
    val popularSubscribe: Subscription? = null,
)

data class Subscription(
    val _id: String? = null,
    val like: Int? = null,
    val perks: ArrayList<String>? = null,
    val planId: String? = null,
    val planName: String? = null,
    val planPrice: String? = null,
    val planValidity: String? = null,
)