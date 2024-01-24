package com.ongraph.jetpackloginsample.remote.models

data class ApiErrorResponse(
    val error: Boolean?,
    val message: String?,
    val statusCode: Int?
)
