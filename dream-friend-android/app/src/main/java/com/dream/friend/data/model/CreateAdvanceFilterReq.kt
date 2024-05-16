package com.dream.friend.data.model

import com.dream.friend.data.model.user.Location

data class CreateAdvanceFilterReq(
    val education: List<Int>?=null,
    val height: Int?=null,
    val interest: List<Int>?=null,
    val isDrinking: Boolean?=null,
    val isEducation: Boolean?=null,
    val isInterest: Boolean?=null,
    val isReligion: Boolean?=null,
    val isSexualOrientation: Boolean?=null,
    val isSmoking: Boolean?=null,
    val isWorkout: Boolean?=null,
    val lifestyle: Lifestyle?=null,
    val location:Location?=null,
    val religion: List<Int>?=null,
    val sexualOrientation: List<Int>?=null,
    val verifiedProfileOnly: Boolean?=null
)