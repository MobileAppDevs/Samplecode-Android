package com.dream.friend.data.model.cititesModel

import com.google.gson.annotations.SerializedName

data class Predictions(
    @SerializedName("predictions")
    val predictions: List<Prediction>,
    @SerializedName("status")
    val status: String
)