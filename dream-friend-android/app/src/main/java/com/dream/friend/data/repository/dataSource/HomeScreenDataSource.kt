package com.dream.friend.data.repository.dataSource

import com.dream.friend.data.model.AdvanceFilterResponse
import com.dream.friend.data.model.BasicFilterRequest
import com.dream.friend.data.model.ClearAdvanceFilterResponse
import com.dream.friend.data.model.CreateAdvanceFilterReq
import com.dream.friend.data.model.EducationResponse
import com.dream.friend.data.model.EmailRequest
import com.dream.friend.data.model.EmailRes
import com.dream.friend.data.model.IncognitoModeRequest
import com.dream.friend.data.model.InterestsResponse
import com.dream.friend.data.model.NotificationRequest
import com.dream.friend.data.model.ReligionResponse
import com.dream.friend.data.model.SuperLikePlan
import com.dream.friend.data.model.VerifyEmailOtpRequest
import com.dream.friend.data.model.error.ErrorResponse
import com.dream.friend.data.model.interests.sexual.SexualOrientationRes
import com.dream.friend.data.model.peronal_details.GetUsersRes
import com.dream.friend.data.model.peronal_details.PersonalDetailRes
import com.dream.friend.data.model.peronal_details.RtcResponse
import retrofit2.Call

interface HomeScreenDataSource {
    suspend fun blockedUsers(): Call<GetUsersRes>
    fun applyBasicFilter (userId: String,request:BasicFilterRequest): Call<PersonalDetailRes>
    fun getEducations (): Call<EducationResponse>
    fun getAdvanceFilterData(userId: String): Call<AdvanceFilterResponse>
    fun clearAdvanceFilter(userId: String): Call<ClearAdvanceFilterResponse>
    fun createAdvanceFilter(userId: String,body:CreateAdvanceFilterReq): Call<AdvanceFilterResponse>
    fun getInterests(): Call<InterestsResponse>
    fun getReligions(): Call<ReligionResponse>
    fun getSexualOrientations (url:String): Call<SexualOrientationRes>
    fun getUserDetail (userId: String): Call<PersonalDetailRes>
    fun incognitoMode (request:IncognitoModeRequest): Call<PersonalDetailRes>
    fun verifyOrUpdateEmail (request:EmailRequest): Call<EmailRes>
    fun verifyEmailOtp (request:VerifyEmailOtpRequest): Call<EmailRes>
    fun  notificationEnableDisable (request:NotificationRequest): Call<PersonalDetailRes>

    fun blockUser (blockedUserId: String, blockedUserStatus: String,): Call<GetUsersRes>
    fun likeUser (likeUserId: String): Call<GetUsersRes>
    fun superLike (likeUserId: String): Call<ErrorResponse>
    fun applyBoost(): Call<ErrorResponse>
    fun rejectUser (likeUserId: String): Call<GetUsersRes>
    fun dislikeUser (dislikeUserId: String): Call<GetUsersRes>
    fun getAllUsers (userId: String): Call<GetUsersRes>
    fun getUsers(url: String): Call<GetUsersRes>
    fun getWhoLikedYouUsers(url: String): Call<GetUsersRes>
    fun getWhomYouLikedUsers(url: String): Call<GetUsersRes>
    fun rtcToken(channel:String,role:String):Call<RtcResponse>
    fun logout():Call<ErrorResponse>
    fun lastMessage(userId: String,lastMessage:String,type:String,unreadCount:String,channelName:String):Call<ErrorResponse>
    fun markAsRead(chatId:String):Call<ErrorResponse>
}