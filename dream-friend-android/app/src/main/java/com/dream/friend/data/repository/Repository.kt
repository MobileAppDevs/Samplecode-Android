package com.dream.friend.data.repository

import android.content.Context
import com.dream.friend.data.model.*
import com.dream.friend.data.model.chat.LastMessageReq
import com.dream.friend.data.model.cititesModel.Predictions
import com.dream.friend.data.model.error.ErrorResponse
import com.dream.friend.data.model.interests.sexual.SexualOrientationRes
import com.dream.friend.data.model.interests.your_interest.YourInterestsRes
import com.dream.friend.data.model.login.LoginRes
import com.dream.friend.data.model.login.LoginWithGoogleReq
import com.dream.friend.data.model.login.LoginWithMobileReq
import com.dream.friend.data.model.otp.SendOtpReq
import com.dream.friend.data.model.otp.VerifyOtpReq
import com.dream.friend.data.model.peronal_details.*
import com.dream.friend.data.network.ApiHelper
import com.dream.friend.data.network.PlaceAutoComplete
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response

class Repository(context: Context) {
    private val webServiceWithoutToken = ApiHelper(context).createService()
    private val webServiceWithToken = ApiHelper(context).createAppService()

    suspend fun loginWithMobile (
        request: LoginWithMobileReq
    ): Response<LoginRes>
    = webServiceWithoutToken.loginWithMobile(request)
    suspend fun updatePhoneNumber(
        userId: String,
        request: LoginWithMobileReq
    ): Response<LoginRes>
            = webServiceWithToken.updatePhoneNumber(userId = userId,request)

    suspend fun loginWithGoogle (
        request: LoginWithGoogleReq
    ): Response<LoginRes>
    = webServiceWithoutToken.loginWithGoogle(request)

    fun sendOtp (
        mobileNumber:String
    ): Call<ErrorResponse> =
        webServiceWithoutToken
            .sendOtp(
                request =SendOtpReq(mobNo = mobileNumber)
            )
    fun verifyOtp (
        verificationCode:String,
        mobileNumber:String
    ): Call<ErrorResponse> =
        webServiceWithoutToken
            .verifyOtp(
                request =VerifyOtpReq(
                    code=verificationCode,
                    mobNo = mobileNumber
                )
            )

    fun updateUserDetails (
        userId: String,
        updateUserDetailsReq:Any
    ): Call<PersonalDetailRes> =
        webServiceWithToken
            .userLocationDetails(
                userId = userId,
                updateUserDetailsReq = updateUserDetailsReq
            )

    fun updateUserLocation (
        userId: String,
        locationReq: LocationReq,
    ): Call<PersonalDetailRes> =
        webServiceWithToken
            .userLocationDetails(
                userId = userId,
                locationReq = locationReq
            )
    fun getSubscriptions (): Call<SubscriptionResponse> =
        webServiceWithToken.getSubscriptions()

    fun userUserProfileImages (
        userId: String,
        images: MultipartBody.Part,
    ): Call<PersonalDetailRes> =
        webServiceWithToken
            .userUserProfileImages(
                userId = userId,
                images = images
            )
    fun realtimeImageUpload (
        userId: String,
        realtimeImage: MultipartBody.Part,
    ): Call<RealtimeImageResponse> =
        webServiceWithToken
            .uploadRealtimeImage(
                userId = userId,
                realtimeImage = realtimeImage)

    fun getSexualOrientations (
        url: String
    ): Call<SexualOrientationRes> =
        webServiceWithoutToken
            .getSexualOrientations(
                url
            )

    fun getYourInterests (
        url: String
    ): Call<YourInterestsRes> =
        webServiceWithoutToken
            .getYourInterests(
                url
            )

    fun blockedUsers (): Call<GetUsersRes> =
        webServiceWithToken
            .blockedUsers()
    fun applyBasicFilter (userId: String,request: BasicFilterRequest): Call<PersonalDetailRes> =
        webServiceWithToken
            .basicFilter(userId,request)
    fun incognitoMode (request: IncognitoModeRequest): Call<PersonalDetailRes> =
        webServiceWithToken
            .incognitoMode(request)
    fun verifyOrUpdateEmail (request: EmailRequest): Call<EmailRes> =
        webServiceWithToken
            .verifyOrUpdateEmail(request)
    fun verifyEmailOtp (request: VerifyEmailOtpRequest): Call<EmailRes> =
        webServiceWithToken
            .verifyEmailOtp(request)
    fun notificationEnableDisable (request: NotificationRequest): Call<PersonalDetailRes> =
        webServiceWithToken
            .notificationEnableDisable(request)
    fun blockUser (
        blockedUserId: String,
        blockedUserStatus: String,
    ): Call<GetUsersRes> =
        webServiceWithToken
            .blockUser(blockedUserId, blockedUserStatus)

    fun likeUser (
        likeUserId: String
    ): Call<GetUsersRes> =
        webServiceWithToken
            .likeUser(likeUserId)
    fun superLike(
        likeUserId: String
    ): Call<ErrorResponse> =
        webServiceWithToken
            .superLike(likeUserId)
    fun applyBoost(): Call<ErrorResponse> =
        webServiceWithToken
            .applyBoost()

    fun rejectUser (
        userId: String
    ): Call<GetUsersRes> =
        webServiceWithToken
            .rejectUser(userId)

    fun dislikeUser (
        dislikeUserId: String
    ): Call<GetUsersRes> =
        webServiceWithToken
            .dislikeUser(dislikeUserId)

    fun getEducations (): Call<EducationResponse> =
        webServiceWithToken.getEducations()

    fun getReligions (): Call<ReligionResponse> =
        webServiceWithToken.getReligions()

    fun getInterests (): Call<InterestsResponse> =
        webServiceWithToken.getInterests()
    fun getAdvanceFilterData (userId:String): Call<AdvanceFilterResponse> =
        webServiceWithToken.getAdvanceFilterData(userId)
    fun createAdvanceFilter (userId:String,body:CreateAdvanceFilterReq): Call<AdvanceFilterResponse> =
        webServiceWithToken.createAdvanceFilterData(userId,body)
    fun clearAdvanceFilter (userId:String): Call<ClearAdvanceFilterResponse> =
        webServiceWithToken.clearAdvanceFilterData(userId)
    fun getAllUsers (
        userId: String
    ): Call<GetUsersRes> =
        webServiceWithToken
            .getAllUsers(userId)

    fun getUsers (
        url: String
    ): Call<GetUsersRes> =
        webServiceWithToken
            .getUsers(url)

    fun getUserDetail (userId:String): Call<PersonalDetailRes> =
        webServiceWithToken.getUserDetail(userId = userId)

    fun getWhomYouLikedUsers (
        url: String
    ): Call<GetUsersRes> =
        webServiceWithToken
            .getWhomYouLikedUsers(url)

    fun getWhoLikedYouUsers (
        url: String
    ): Call<GetUsersRes> =
        webServiceWithToken
            .getWhoLikedYouUsers(url)



    fun updateLifestyle (
        userId: String,
       lifestyleReq: LifestyleReq
    ): Call<PersonalDetailRes> =
        webServiceWithToken
            .updateLifestyle(userId,lifestyleReq)


    fun rtcToken(
        channel: String,
        role:String
    ): Call<RtcResponse> =
        webServiceWithToken
            .rtcToken(
                channel=channel,
                role=role
            )

    fun logout(): Call<ErrorResponse> =
        webServiceWithToken.logout()

    fun lastMessage(
        userId: String,
        lastMessage: String,
        type: String,
        unreadCount: String,
        channelName: String): Call<ErrorResponse> =
        webServiceWithToken.lastMessage(userId=userId, lastMessageReq = LastMessageReq(channelName,lastMessage,type,unreadCount))

    fun markAsRead(
        chatId:String
    ):Call<ErrorResponse> =
        webServiceWithToken.markAsRead(chatId)

    fun deleteUser(): Call<ErrorResponse> =
        webServiceWithToken.deleteUser()

    suspend fun getCities(url: String): Response<Predictions> {
        return PlaceAutoComplete().getInstance().getCities(url)
    }

    fun fcmToken(userId: String, fcmToken: String): Call<ErrorResponse> =
        webServiceWithToken.fcmToken(FCMTokenReq(userId, fcmToken))

    fun deleteImage(userId: String, filename: String): Call<LoginRes> =
        webServiceWithToken.deleteImage(userId, filename)

    fun subscribe(request: CreateSubscriberRequest): Call<ErrorResponse> =
        webServiceWithToken.subscribe(request)

    fun getMyPlanBenefits(userId: String): Call<SubscriptionRes> =
        webServiceWithToken.getMyPlanBenefits(MyBenefitsReq(userId))

    fun getAllBenefits(): Call<SubscriptionRes> =
        webServiceWithToken.getAllBenefits(AllBenefitsReq())

    fun callDecline(request: CallDeclineReq): Call<ErrorResponse> =
        webServiceWithToken.callDecline(request)


    //additional apis

    fun getTellUsAboutYouData (): Call<TellUsAboutYouModel> =
        webServiceWithToken
            .getTellUsAboutYouData()
}
