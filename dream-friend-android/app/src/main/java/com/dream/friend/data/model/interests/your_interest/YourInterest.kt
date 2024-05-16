package com.dream.friend.data.model.interests.your_interest

data class YourInterest(
    val _id: String,
    val name: String,
    val hobbiesId: Int,
    val __v: Int,
    var isSelected: Boolean = false,
)