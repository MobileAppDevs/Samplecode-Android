package com.dream.friend.data.model

import com.dream.friend.data.model.user.AgeRange
import com.dream.friend.data.model.user.Distance

data class BasicFilterRequest(
    val ageRange: AgeRange?=null,
    val maxDistance: Distance?=null,
    val sexualInterest: Int?=null
)