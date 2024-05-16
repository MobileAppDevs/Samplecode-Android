package com.dream.friend.data.repository.dataSourceImpl

import android.content.Context
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
import com.dream.friend.data.model.VerifyEmailOtpRequest
import com.dream.friend.data.model.error.ErrorResponse
import com.dream.friend.data.model.interests.sexual.SexualOrientationRes
import com.dream.friend.data.model.peronal_details.GetUsersRes
import com.dream.friend.data.model.peronal_details.PersonalDetailRes
import com.dream.friend.data.model.peronal_details.RtcResponse
import com.dream.friend.data.repository.Repository
import com.dream.friend.data.repository.dataSource.HomeScreenDataSource
import retrofit2.Call

class HomeScreenDataSourceImpl(private val context: Context) : HomeScreenDataSource {

    override fun getSexualOrientations(url:String): Call<SexualOrientationRes> = Repository(context).getSexualOrientations(url)
    override fun getUserDetail(userId: String): Call<PersonalDetailRes> =Repository(context).getUserDetail(userId)

    override fun getEducations(): Call<EducationResponse> = Repository(context).getEducations()
    override fun getReligions(): Call<ReligionResponse> = Repository(context).getReligions()
    override fun getInterests(): Call<InterestsResponse> = Repository(context).getInterests()
    override fun getAdvanceFilterData(userId: String): Call<AdvanceFilterResponse> = Repository(context).getAdvanceFilterData(userId)
    override fun clearAdvanceFilter(userId: String): Call<ClearAdvanceFilterResponse> = Repository(context).clearAdvanceFilter(userId)
    override fun createAdvanceFilter(userId: String,body:CreateAdvanceFilterReq): Call<AdvanceFilterResponse> = Repository(context).createAdvanceFilter(userId,body)
    override suspend fun blockedUsers(): Call<GetUsersRes> =
        Repository(context).blockedUsers()

    override fun applyBasicFilter(userId: String,request: BasicFilterRequest): Call<PersonalDetailRes> =Repository(context).applyBasicFilter(userId,request)
    override fun incognitoMode(request: IncognitoModeRequest): Call<PersonalDetailRes> =Repository(context).incognitoMode(request)
    override fun verifyOrUpdateEmail(request: EmailRequest): Call<EmailRes> =Repository(context).verifyOrUpdateEmail(request)
    override fun verifyEmailOtp(request:VerifyEmailOtpRequest): Call<EmailRes> =Repository(context).verifyEmailOtp(request)
    override fun notificationEnableDisable(request: NotificationRequest): Call<PersonalDetailRes> =Repository(context).notificationEnableDisable(request)

    override fun blockUser(
        blockedUserId: String,
        blockedUserStatus: String,
    ): Call<GetUsersRes> =
        Repository(context).blockUser(blockedUserId, blockedUserStatus)

    override fun likeUser(
        likeUserId: String
    ): Call<GetUsersRes> =
        Repository(context).likeUser(likeUserId)
    override fun superLike(
        likeUserId: String
    ): Call<ErrorResponse> =
        Repository(context).superLike(likeUserId)
    override fun applyBoost(): Call<ErrorResponse> =
        Repository(context).applyBoost()
    override fun rejectUser(
        likeUserId: String
    ): Call<GetUsersRes> =
        Repository(context).rejectUser(likeUserId)

    override fun dislikeUser(
        dislikeUserId: String
    ): Call<GetUsersRes> =
        Repository(context).dislikeUser(dislikeUserId)

    override fun getAllUsers(
        userId: String
    ): Call<GetUsersRes> =
        Repository(context).getAllUsers(userId)

    override fun getUsers(
        url: String
    ): Call<GetUsersRes> =
        Repository(context).getUsers(url)

    override fun getWhomYouLikedUsers(
        url: String
    ): Call<GetUsersRes> =
        Repository(context).getWhomYouLikedUsers(url)

    override fun rtcToken(channel: String, role: String): Call<RtcResponse> =Repository(context).rtcToken(channel,role)

    override fun logout(): Call<ErrorResponse> =Repository(context).logout()

    override fun lastMessage(
        userId:String,
        lastMessage: String,
        type: String,
        unreadCount: String,
        channelName: String
    ): Call<ErrorResponse> = Repository(context).lastMessage(userId,lastMessage,type,unreadCount,channelName)

    override fun markAsRead(chatId: String): Call<ErrorResponse> =Repository(context).markAsRead(chatId)

    override fun getWhoLikedYouUsers(
        url: String
    ): Call<GetUsersRes> =
        Repository(context).getWhoLikedYouUsers(url)
}