package com.dream.friend.data.model

import com.google.gson.annotations.SerializedName

data class NotificationModel (
    @SerializedName("body") val body: String,
    @SerializedName("title") val title: String,
    @SerializedName("userDataString") val userDataString: String,
    @SerializedName("findChatString") val findChatString: String,
    @SerializedName("notificationType") val notificationType: Int?=8,// refer enum NotificationType class for values
    @SerializedName("matchUserData") val matchUserDataString: String,
    @SerializedName("clickAction") val clickAction: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("isDecline") val isDecline: Boolean? = null,
)

data class MyNotificationModel (
    @SerializedName("body") val body: String,
    @SerializedName("title") val title: String,
    @SerializedName("userDataString") val userDataString: UserDataString?,
    @SerializedName("findChatString") val findChatString: FindChatString?,
    @SerializedName("clickAction") val clickAction: String,
    @SerializedName("icon") val icon: String,
)

data class UserDataString(
    @SerializedName("name") val name: String,
    @SerializedName("images") val images: ArrayList<String>
//    @SerializedName("images") val images: ArrayList<Image>
)

data class MatchedUserDataString(
    @SerializedName("user1Id") val user1Id: String,
    @SerializedName("user2Id") val user2Id: String,
    @SerializedName("user2name") val user2Name: String,
    @SerializedName("user1name") val user1Name: String,
    @SerializedName("user1image") val user1Image: ArrayList<String>,
    @SerializedName("user2image") val user2Image: ArrayList<String>
//    @SerializedName("images") val images: ArrayList<Image>
)

data class Image(
    val fieldname: String,
    val originalname: String,
    val encoding: String,
    val mimetype: String,
    val destination: String,
    val filename: String,
    val path: String,
    val size: String,
)

data class FindChatString(
    @SerializedName("_id") val _id: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("chatWith") val chatWith: String,
    @SerializedName("lastMessage") val lastMessage: String,
    @SerializedName("type") val type: String,
    @SerializedName("unreadCount") val unreadCount: Int,
    @SerializedName("dateTime") val dateTime: String,
    @SerializedName("__v") val __v: String,
    @SerializedName("channelName") val channelName: String
)