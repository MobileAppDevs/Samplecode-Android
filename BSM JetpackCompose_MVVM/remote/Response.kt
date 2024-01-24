package com.ongraph.jetpackloginsample.remote

import com.ongraph.jetpackloginsample.remote.models.AuthData

data class Response(
    var error: Boolean? = null,
    var message: String? = null,
    var statusCode: Int? = null,
    var data: AuthData? = null,
)