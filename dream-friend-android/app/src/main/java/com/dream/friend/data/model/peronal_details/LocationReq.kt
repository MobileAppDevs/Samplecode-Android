package com.dream.friend.data.model.peronal_details

import com.dream.friend.data.model.user.Location

data class LocationReq(
    val location: Location?=null,
    val city:String?=null
)