package com.enkefalostechnologies.calendarpro.api.model

data class Error(
    val code: Int,
    val errors: List<ErrorX>,
    val message: String
)