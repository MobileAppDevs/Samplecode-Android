package com.dream.friend.data.model.peronal_details

import com.dream.friend.data.model.Lifestyle
import com.google.gson.annotations.SerializedName
import com.dream.friend.data.model.user.AgeRange
import com.dream.friend.data.model.user.Distance

data class UpdateUserDetailsReq(
    val name: String?=null,
    val birthDate: String?=null,
    val email:String?=null,
    val sexualOrientation: ArrayList<Int>?=null,
    @SerializedName("sexualInterest") val interestIn: Int?=null,
    @SerializedName("interest") val yourInterest: ArrayList<Int>?=null,
    val gender: String?=null,
    val university: String?=null,
    @SerializedName("maxDistance") val maxDistance: Distance?=null,
    @SerializedName("ageRange") val ageRange: AgeRange?=null,
    val aboutMe: String? = null,
    val zodiac: Int? = null,
    val city: String? = null,
    val height:Int?=0,
    val education: ArrayList<Int>?=null,
    val religion: ArrayList<Int>?=null,
    val lifestyle: Lifestyle?=null,
//    val workout: Int?=null,
    val preferNotToSay:Boolean?=false,
//    val smoking:Int?=null,
//    val drinking:Int?=null,
    val notificationOn: Boolean? = null,
)