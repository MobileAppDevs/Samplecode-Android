package com.dream.friend.data.model

data class SubscriptionResponse(
    val message: String?=null,
    val statusCode: Int?=null,
    val subscriptionPlans: List<SubscriptionPlan>?= listOf()
)