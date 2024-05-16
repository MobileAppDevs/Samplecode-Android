package com.dream.friend.data.model

data class CallDeclineReq(
    val whomeCall: String,
    val whoCall: String,
    val callCutByOther: Boolean = false,
    val callCutBySelf: Boolean = false,
    val callingStatus: String = "0",
)
