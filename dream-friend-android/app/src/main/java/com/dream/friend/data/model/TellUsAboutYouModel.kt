package com.dream.friend.data.model

data class TellUsAboutYouModel(
    val education: List<Education>,
    val hobbies: List<Hobby>,
    val religion: List<Religion>,
    val sexOrientation: List<SexOrientation>,
    val status: Int
)