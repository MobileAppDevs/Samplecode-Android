package com.dream.friend.data.model.peronal_details

import com.dream.friend.data.model.user.User

data class PersonalDetailRes(
    val statusCode: Int? = null,
    val message: String? = null,
    val user: User,
    val realtimeImage: ArrayList<String> = arrayListOf()
)