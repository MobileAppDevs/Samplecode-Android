package com.dream.friend.data.model.interests.sexual

import com.google.gson.annotations.SerializedName

data class SexualOrientationRes(
    val statusCode: Int? = null,
    @SerializedName("sexOrientation") val sexualOrientations: ArrayList<SexualOrientation> = arrayListOf()
)