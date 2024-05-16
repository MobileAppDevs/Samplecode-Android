package com.dream.friend.data.model

data class SubscribedPlan(
    val boostPlan: List<BoostPlan>?=null,
    val premiumPlan: List<NormalPlan>?=null,
    val plusPlan: List<NormalPlan>?=null,
    val superLikePlan: List<SuperLikePlan>?=null
)