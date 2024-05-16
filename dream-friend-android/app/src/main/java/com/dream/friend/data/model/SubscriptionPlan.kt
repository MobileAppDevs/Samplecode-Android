package com.dream.friend.data.model

data class SubscriptionPlan(
    val __v: Int?=null,
    val _id: String?=null,
    val active: Boolean?=null,
    val createdAt: String?=null,
    val planFeatures: List<PlanFeature>?= listOf(),
    val planId: String?=null,
    val planName: String?=null,
    val planSchedule: List<PlanSchedule>?= listOf(),
    val updatedAt: String?=null
)