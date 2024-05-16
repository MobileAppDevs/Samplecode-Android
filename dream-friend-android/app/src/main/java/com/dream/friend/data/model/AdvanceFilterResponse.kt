package com.dream.friend.data.model

data class AdvanceFilterResponse(
    val data: List<AdvanceFilterData>,
    val message: String,
    val status: Int
)