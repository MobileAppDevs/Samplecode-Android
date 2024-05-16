package com.dream.friend.data.model.interests.your_interest

import com.google.gson.annotations.SerializedName

data class YourInterestsRes(
    val statusCode: Int? = null,
    @SerializedName("hobbies") val yourInterests: ArrayList<YourInterest> = arrayListOf()
)