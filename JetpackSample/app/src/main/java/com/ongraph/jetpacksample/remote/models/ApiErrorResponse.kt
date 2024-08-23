package com.ongraph.jetpacksample.remote.models

/*
* api error response model
* */
data class ApiErrorResponse(
    val error: Boolean?,
    val message: String?,
    val statusCode: Int?
)
