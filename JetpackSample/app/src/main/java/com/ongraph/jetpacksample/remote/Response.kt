package com.ongraph.jetpacksample.remote

import com.ongraph.jetpacksample.remote.models.AuthData

/*
* response model for all api calls
* */
data class Response(
    var error: Boolean? = null,
    var message: String? = null,
    var statusCode: Int? = null,
    var data: AuthData? = null,
)