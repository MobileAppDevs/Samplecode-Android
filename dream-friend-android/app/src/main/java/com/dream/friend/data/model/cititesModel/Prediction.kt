package com.dream.friend.data.model.cititesModel

import com.google.gson.annotations.SerializedName

data class Prediction(
    @SerializedName("description")
    val description: String,
    @SerializedName("structured_formatting")
    val structured_formatting: StructuredFormatting
)