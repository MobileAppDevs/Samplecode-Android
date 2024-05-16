package com.dream.friend.data.model

data class PlanModel(
    val position: Int,
    val title: String,
    val time: String,
    var price: String,
    var productId: String,
    var isSelected: Boolean = false
)