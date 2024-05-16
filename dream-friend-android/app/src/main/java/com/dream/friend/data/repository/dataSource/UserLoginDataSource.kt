package com.dream.friend.data.repository.dataSource

import com.dream.friend.data.model.CallDeclineReq
import com.dream.friend.data.model.CreateSubscriberRequest
import com.dream.friend.data.model.SubscribeReq
import com.dream.friend.data.model.SubscriptionRes
import com.dream.friend.data.model.error.ErrorResponse
import com.dream.friend.data.network.Resource
import com.dream.friend.data.model.login.LoginRes
import com.dream.friend.data.model.login.LoginWithGoogleReq
import com.dream.friend.data.model.login.LoginWithMobileReq
import retrofit2.Call

interface UserLoginDataSource {
    suspend fun loginWithMobile(request: LoginWithMobileReq): Resource<LoginRes>
    suspend fun updatePhoneNumber(userId: String,request: LoginWithMobileReq): Resource<LoginRes>
    suspend fun loginWithGoogle(request: LoginWithGoogleReq): Resource<LoginRes>
    fun sendOTP(mobileNumber:String):Call<ErrorResponse>
    fun verifyOTP(verificationCode:String,mobileNumber:String):Call<ErrorResponse>
    fun fcmToken(userId: String, fcmToken: String): Call<ErrorResponse>
    fun deleteImage(userId: String, filename: String): Call<LoginRes>
    fun subscribe(request: CreateSubscriberRequest): Call<ErrorResponse>
    fun getMyPlanBenefits(userId: String): Call<SubscriptionRes>
    fun getAllBenefits(): Call<SubscriptionRes>
    fun callDecline(request: CallDeclineReq): Call<ErrorResponse>
}