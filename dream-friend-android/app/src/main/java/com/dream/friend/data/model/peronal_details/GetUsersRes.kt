package com.dream.friend.data.model.peronal_details

import com.dream.friend.data.model.PetModel
import com.dream.friend.data.model.user.User

data class GetUsersRes(
    val statusCode: Int? = null,
    val message: String? = null,
    val total: Int? = null,
    val likeCount:Int?=0,
    val users: ArrayList<User> = arrayListOf(),
    val subscriber:Subscriber?=null,
    val realtimeImage: ArrayList<String> = arrayListOf(),
    val pets: ArrayList<PetModel> = arrayListOf(),
)
data class Subscriber(
    val isBoostSubscriber:Boolean?=false,
    var isPlusSubscriber:Boolean?= false,
    var isPremiumSubscriber:Boolean?= false,
    var isSuperLikeSubscriber:Boolean?= false
)