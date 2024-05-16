package com.dream.friend.data.network

import com.dream.friend.common.Constants.ALL_BENEFITS
import com.dream.friend.common.Constants.APPLY_BOOST
import com.dream.friend.common.Constants.BlockUser
import com.dream.friend.common.Constants.CALL_DECLINE
import com.dream.friend.common.Constants.CREATE_UPDATE_SUBSCRIBERS
import com.dream.friend.common.Constants.DELETE_ADVANCE_FILTER_DATA
import com.dream.friend.common.Constants.DislikeUser
import com.dream.friend.common.Constants.FCMToken
import com.dream.friend.common.Constants.GET_ADVANCE_FILTER_DATA
import com.dream.friend.common.Constants.GET_EDUCATIONS
import com.dream.friend.common.Constants.GET_INTERESTS
import com.dream.friend.common.Constants.GET_RELIGION
import com.dream.friend.common.Constants.GET_USER_DETAIL
import com.dream.friend.common.Constants.GetAllPets
import com.dream.friend.common.Constants.GetAllUsers
import com.dream.friend.common.Constants.GetBlockedUsers
import com.dream.friend.common.Constants.INCOGNITO_MODE
import com.dream.friend.common.Constants.LikeUser
import com.dream.friend.common.Constants.LoginUserWithGoogle
import com.dream.friend.common.Constants.LoginUserWithMobile
import com.dream.friend.common.Constants.RtcToken
import com.dream.friend.common.Constants.UpdateLifestyle
import com.dream.friend.common.Constants.UpdateUser
import com.dream.friend.common.Constants.UserLocation
import com.dream.friend.common.Constants.UserProfileImages
import com.dream.friend.common.Constants.deleteImage
import com.dream.friend.common.Constants.deleteUser
import com.dream.friend.common.Constants.lastMessage
import com.dream.friend.common.Constants.logout
import com.dream.friend.common.Constants.markAsRead
import com.dream.friend.common.Constants.MY_PLAN_BENEFITS
import com.dream.friend.common.Constants.NOTIFICATION_ENABLE_DISABLE
import com.dream.friend.common.Constants.POST_CREATE_ADVANCE_FILTER_DATA
import com.dream.friend.common.Constants.RealtimeImageUpload
import com.dream.friend.common.Constants.RejectedUser
import com.dream.friend.common.Constants.SUBSCRIBE
import com.dream.friend.common.Constants.SUPER_LIKE
import com.dream.friend.common.Constants.TwilioSendOtp
import com.dream.friend.common.Constants.TwilioVerifyOtp
import com.dream.friend.common.Constants.VERIFY_EMAIL_OTP
import com.dream.friend.common.Constants.VERIFY_OR_UPDATE_EMAIL
import com.dream.friend.common.Constants.getCombineDataForTellUsAboutYou
import com.dream.friend.common.Constants.subscription
import com.dream.friend.data.model.*
import com.dream.friend.data.model.chat.LastMessageReq
import com.dream.friend.data.model.error.ErrorResponse
import com.dream.friend.data.model.interests.sexual.SexualOrientationRes
import com.dream.friend.data.model.interests.your_interest.YourInterestsRes
import com.dream.friend.data.model.login.*
import com.dream.friend.data.model.peronal_details.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Url

interface WebService {

    @POST(LoginUserWithMobile)
    suspend fun loginWithMobile(@Body loginWithMobileReq: LoginWithMobileReq): Response<LoginRes>
    @PATCH("$UpdateUser{userId}")
    suspend fun updatePhoneNumber(
        @Path("userId") userId: String,
        @Body loginWithMobileReq: LoginWithMobileReq): Response<LoginRes>
    @POST(LoginUserWithGoogle)
    suspend fun loginWithGoogle(@Body loginWithGoogleReq: LoginWithGoogleReq): Response<LoginRes>

    @POST("$RtcToken{channel}/{role}")
    fun rtcToken(
        @Path("channel") channel: String,
        @Path("role") role:String
    ): Call<RtcResponse>

    @PATCH("$UpdateUser{userId}")
    fun userLocationDetails(
        @Path("userId") userId: String,
        @Body updateUserDetailsReq: Any,
    ): Call<PersonalDetailRes>


    @POST(TwilioSendOtp)
    fun sendOtp(
        @Body request: Any,
    ): Call<ErrorResponse>

    @POST(TwilioVerifyOtp)
    fun verifyOtp(
        @Body request: Any,
    ): Call<ErrorResponse>

    @PATCH(logout)
    fun logout(): Call<ErrorResponse>

    @POST("$lastMessage{userId}")
    fun lastMessage(
        @Path("userId") userId: String,
        @Body lastMessageReq: LastMessageReq,
    ): Call<ErrorResponse>

    @PATCH("$markAsRead{chatId}")
    fun markAsRead(
        @Path("chatId") chatId: String
    ): Call<ErrorResponse>

    @PATCH("$UserLocation{userId}")
    fun userLocationDetails(
        @Path("userId") userId: String,
        @Body locationReq: LocationReq,
    ): Call<PersonalDetailRes>
    @GET(subscription)
    fun getSubscriptions(): Call<SubscriptionResponse>

    @PATCH("$UserProfileImages{userId}")
    @Multipart
    fun userUserProfileImages(
        @Path("userId") userId: String,
        @Part images: MultipartBody.Part,
    ): Call<PersonalDetailRes>
    @PATCH("$RealtimeImageUpload{userId}")
    @Multipart
    fun uploadRealtimeImage(
        @Path("userId") userId: String,
        @Part realtimeImage: MultipartBody.Part,
    ):Call<RealtimeImageResponse>
    @GET
    fun getSexualOrientations(
        @Url url: String
    ): Call<SexualOrientationRes>

    @GET
    fun getYourInterests(
        @Url url: String
    ): Call<YourInterestsRes>

    @GET(GetBlockedUsers)
    fun blockedUsers(): Call<GetUsersRes>

    @PATCH("$BlockUser{blockedUserId}/{blockedUserStatus}")
    fun blockUser(
        @Path("blockedUserId") blockedUserId: String,
        @Path("blockedUserStatus") blockedUserStatus: String,
    ): Call<GetUsersRes>

    @PATCH("$LikeUser{likeUserId}")
    fun likeUser(
        @Path("likeUserId") likeUserId: String
    ): Call<GetUsersRes>

    @PATCH("$RejectedUser{userId}")
    fun rejectUser(
        @Path("userId") userId: String
    ): Call<GetUsersRes>

    @PATCH("$UpdateUser{userId}")
    fun basicFilter(
        @Path("userId") userId: String,
        @Body request: BasicFilterRequest
    ): Call<PersonalDetailRes>
    @POST("$INCOGNITO_MODE")
    fun incognitoMode(
        @Body request:IncognitoModeRequest
    ): Call<PersonalDetailRes>

    @POST("$VERIFY_OR_UPDATE_EMAIL")
    fun verifyOrUpdateEmail(
        @Body request:EmailRequest
    ): Call<EmailRes>
    @POST("$VERIFY_EMAIL_OTP")
    fun verifyEmailOtp(
        @Body request:VerifyEmailOtpRequest
    ): Call<EmailRes>

    @POST("$NOTIFICATION_ENABLE_DISABLE")
    fun notificationEnableDisable(
        @Body request:NotificationRequest
    ): Call<PersonalDetailRes>

    @PATCH("$DislikeUser{dislikeUserId}")
    fun dislikeUser(
        @Path("dislikeUserId") dislikeUserId: String
    ): Call<GetUsersRes>

    @GET("$GetAllUsers{userId}")
    fun getAllUsers(
        @Path("userId") userId: String
    ): Call<GetUsersRes>

    @GET(GET_EDUCATIONS)
    fun getEducations(): Call<EducationResponse>
    @GET(GET_RELIGION)
    fun getReligions(): Call<ReligionResponse>
    @GET(GET_INTERESTS)
    fun getInterests(): Call<InterestsResponse>
    @GET("$GET_ADVANCE_FILTER_DATA{userId}")
    fun getAdvanceFilterData(
        @Path("userId") userId: String
    ): Call<AdvanceFilterResponse>

    @POST("$POST_CREATE_ADVANCE_FILTER_DATA{userId}")
    fun createAdvanceFilterData(
        @Path("userId") userId: String,
        @Body request:CreateAdvanceFilterReq
    ): Call<AdvanceFilterResponse>

    @DELETE("$DELETE_ADVANCE_FILTER_DATA{userId}")
    fun clearAdvanceFilterData(
        @Path("userId") userId: String
    ): Call<ClearAdvanceFilterResponse>
    @GET
    fun getUsers(
        @Url url: String
    ): Call<GetUsersRes>

    @GET(GET_USER_DETAIL)
    fun getUserDetail(
        @Path("userId") userId: String,
    ): Call<PersonalDetailRes>
    @GET
    fun getWhomYouLikedUsers(
        @Url url: String
    ): Call<GetUsersRes>

    @GET
    fun getWhoLikedYouUsers(
        @Url url: String
    ): Call<GetUsersRes>



    @PATCH("$UpdateLifestyle{userId}")
    fun updateLifestyle(
        @Path("userId") userId: String,
        @Body request: LifestyleReq
    ): Call<PersonalDetailRes>
    @PATCH("$SUPER_LIKE/{userId}")
    fun superLike(
        @Path("userId") userId: String,
    ): Call<ErrorResponse>
    @PATCH(APPLY_BOOST)
    fun applyBoost(): Call<ErrorResponse>

    @DELETE(deleteUser)
    fun deleteUser(): Call<ErrorResponse>

    @POST(FCMToken)
    fun fcmToken(
        @Body request: FCMTokenReq
    ): Call<ErrorResponse>

    @DELETE("$deleteImage{userId}/{filename}")
    fun deleteImage(
        @Path("userId") userId: String,
        @Path("filename") filename: String,
    ): Call<LoginRes>

    @POST(SUBSCRIBE)
    fun subscribe(
        @Body  request: CreateSubscriberRequest
    ): Call<ErrorResponse>

    @POST(MY_PLAN_BENEFITS)
    fun getMyPlanBenefits(
        @Body request: MyBenefitsReq
    ): Call<SubscriptionRes>

    @POST(ALL_BENEFITS)
    fun getAllBenefits(
        @Body request: AllBenefitsReq
    ): Call<SubscriptionRes>

    @POST(CALL_DECLINE)
    fun callDecline(
        @Body request: CallDeclineReq
    ): Call<ErrorResponse>

//    @POST(CREATE_UPDATE_SUBSCRIBERS)
//    fun createUpdateSubscriber(
//        @Body request: CreateSubscriberRequest
//    ): Call<ErrorResponse>

    // additional added api end points
    @GET(getCombineDataForTellUsAboutYou)
    fun getTellUsAboutYouData(): Call<TellUsAboutYouModel>



}