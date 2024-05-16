package com.dream.friend.data.model

import com.dream.friend.data.model.user.Location

data class AdvanceFilterData(
    val _id: String,
    val education: List<String>,
    val height: Int,
    val interest: List<String>,
    val isDrinking: Boolean,
    val isEducation: Boolean,
    val isInterest: Boolean,
    val isReligion: Boolean,
    val isSexualOrientation: Boolean,
    val isSmoking: Boolean,
    val isWorkout: Boolean,
    val lifestyle: Lifestyle,
    val location: Location,
    val religion: List<String>,
    val sexualOrientation: List<String>,
    val userId: String,
    val verifiedProfileOnly: Boolean
)