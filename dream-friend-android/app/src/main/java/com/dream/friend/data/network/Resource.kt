package com.dream.friend.data.network

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
    val isTokenExpire: Boolean? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class TokenRenew<T>(isTokenExpire: Boolean): Resource<T>(isTokenExpire = isTokenExpire)
}