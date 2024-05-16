package com.dream.friend.data.model

import com.dream.friend.data.model.user.User

data class RealtimeImageResponse(
     var statusCode:Int,
     var message:String,
     var user: User
 )