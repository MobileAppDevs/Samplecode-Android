package com.dream.friend.data.repository.dataSourceImpl

import android.content.Context
import com.dream.friend.data.model.CallDeclineReq
import com.dream.friend.data.model.CreateSubscriberRequest
import com.dream.friend.data.model.SubscribeReq
import com.dream.friend.data.model.SubscriptionRes
import com.dream.friend.data.model.error.ErrorResponse
import com.dream.friend.data.model.login.LoginRes
import com.dream.friend.data.model.login.LoginWithGoogleReq
import com.dream.friend.data.model.login.LoginWithMobileReq
import com.dream.friend.data.network.Resource
import com.dream.friend.data.repository.Repository
import com.dream.friend.data.repository.dataSource.UserLoginDataSource
import retrofit2.Call

class UserLoginDataSourceImpl(private val context: Context): UserLoginDataSource {

    override suspend fun loginWithMobile(request: LoginWithMobileReq): Resource<LoginRes> {
        val response = Repository(context).loginWithMobile(request)
        if(response.isSuccessful)
            response.body()?.let { result->
                return Resource.Success(result)
            }
        return Resource.Error(response.message())
    }

    override suspend fun updatePhoneNumber(userId: String,request: LoginWithMobileReq): Resource<LoginRes> {
        val response = Repository(context).updatePhoneNumber(userId,request)
        if(response.isSuccessful)
            response.body()?.let { result->
                return Resource.Success(result)
            }
        return Resource.Error(response.message())
    }

    override suspend fun loginWithGoogle(request: LoginWithGoogleReq): Resource<LoginRes> {
        val response = Repository(context).loginWithGoogle(request)
        if(response.isSuccessful)
            response.body()?.let { result->
                return Resource.Success(result)
            }
        return Resource.Error(response.message())
    }

    override fun sendOTP(mobileNumber: String): Call<ErrorResponse> =Repository(context).sendOtp(mobileNumber)
    override fun verifyOTP(verificationCode:String,mobileNumber: String): Call<ErrorResponse> =Repository(context).verifyOtp(verificationCode,mobileNumber)

    override fun fcmToken(userId: String, fcmToken: String): Call<ErrorResponse> =
        Repository(context).fcmToken(userId, fcmToken)

    override fun deleteImage(userId: String, filename: String): Call<LoginRes> =
        Repository(context).deleteImage(userId, filename)

    override fun subscribe(request: CreateSubscriberRequest  ): Call<ErrorResponse> =
        Repository(context).subscribe(request)

    override fun getMyPlanBenefits(userId: String): Call<SubscriptionRes> =
        Repository(context).getMyPlanBenefits(userId)

    override fun getAllBenefits(): Call<SubscriptionRes> =
        Repository(context).getAllBenefits()

    override fun callDecline(request: CallDeclineReq): Call<ErrorResponse> =
        Repository(context).callDecline(request)
}