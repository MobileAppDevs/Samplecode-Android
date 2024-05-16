package com.dream.friend.data.model.login

import com.dream.friend.data.model.user.AccessToken
import com.dream.friend.data.model.user.User

data class LoginRes(
    val statusCode: Int? = null,
    val message: String? = null,
    val accessToken: AccessToken?=null,
    val user: User,
)