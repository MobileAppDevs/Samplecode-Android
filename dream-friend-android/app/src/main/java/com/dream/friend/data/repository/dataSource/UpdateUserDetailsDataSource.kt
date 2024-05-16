package com.dream.friend.data.repository.dataSource

import com.dream.friend.data.model.Lifestyle
import com.dream.friend.data.model.LifestyleReq
import com.dream.friend.data.model.RealtimeImageResponse
import com.dream.friend.data.model.SubscriptionResponse
import com.dream.friend.data.model.TellUsAboutYouModel
import com.dream.friend.data.model.cititesModel.Predictions
import com.dream.friend.data.model.error.ErrorResponse
import com.dream.friend.data.model.interests.sexual.SexualOrientationRes
import com.dream.friend.data.model.interests.your_interest.YourInterestsRes
import com.dream.friend.data.model.peronal_details.GetUsersRes
import com.dream.friend.data.model.peronal_details.LocationReq
import com.dream.friend.data.model.peronal_details.PersonalDetailRes
import com.dream.friend.data.model.peronal_details.UpdateUserDetailsReq
import com.dream.friend.data.network.Resource
import okhttp3.MultipartBody
import retrofit2.Call

interface UpdateUserDetailsDataSource {
    suspend fun updateUserDetails(userId: String, updateUserDetailsReq: Any): Call<PersonalDetailRes>
    suspend fun updateUserLocation(userId: String, locationReq: LocationReq): Call<PersonalDetailRes>
    suspend fun getSexualOrientations(url: String): Call<SexualOrientationRes>
    suspend fun getYourInterests(url: String): Call<YourInterestsRes>
    suspend fun userUserProfileImages(userId: String, images: MultipartBody.Part): Call<PersonalDetailRes>
    suspend fun realtimeImageUpload(userId: String, realtimeImage: MultipartBody.Part): Call<RealtimeImageResponse>
    fun getSubscriptions(): Call<SubscriptionResponse>
    fun getTellUsAboutYouData(): Call<TellUsAboutYouModel>
    fun updateLifestyle(userId: String,lifestyleReq:LifestyleReq): Call<PersonalDetailRes>
    fun deleteUser(): Call<ErrorResponse>
    suspend fun getCities(url: String): Resource<Predictions>
}