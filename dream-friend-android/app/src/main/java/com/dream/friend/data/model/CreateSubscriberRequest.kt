package com.dream.friend.data.model

data class CreateSubscriberRequest(
    val subscribedPlan: SubscribedPlan?=null,
    val userId: String?=null
)