package com.dream.friend.data.model

import com.dream.friend.data.model.user.Location
import com.google.gson.annotations.SerializedName

data class EditProfileReq (
        val name: String?=null,
        val gender: String?=null,
        val birthDate: String?=null,
        val height:Int?=0,
        val aboutMe:String?=null,
        @SerializedName("sexualInterest") val interestIn: Int?=null,
        val sexualOrientation: ArrayList<Int>?=null,
        val education: ArrayList<Int>?=null,
        val religion: ArrayList<Int>?=null,
        val lifestyle: Lifestyle?=null,
        var city:String?=null,
        @SerializedName("interest") val yourInterest: ArrayList<Int>?=null,
        var location:Location?=null
)