package com.dream.friend.data.repository.dataSourceImpl

import android.content.Context
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
import com.dream.friend.data.repository.Repository
import com.dream.friend.data.repository.dataSource.UpdateUserDetailsDataSource
import okhttp3.MultipartBody
import retrofit2.Call

class UpdateUserDetailsDataSourceImpl(private val context: Context): UpdateUserDetailsDataSource {

    override suspend fun updateUserDetails(
        userId: String,
        updateUserDetailsReq: Any
    ): Call<PersonalDetailRes> =
        Repository(context).updateUserDetails(userId, updateUserDetailsReq)

    override suspend fun updateUserLocation(
        userId: String,
        locationReq: LocationReq
    ): Call<PersonalDetailRes> =
        Repository(context).updateUserLocation(userId, locationReq)
    override fun getSubscriptions(): Call<SubscriptionResponse> = Repository(context).getSubscriptions()

    override suspend fun getSexualOrientations(
        url: String
    ): Call<SexualOrientationRes> =
        Repository(context).getSexualOrientations(url)

    override suspend fun getYourInterests(
        url: String
    ): Call<YourInterestsRes> =
        Repository(context).getYourInterests(url)

    override suspend fun userUserProfileImages(
        userId: String,
        images: MultipartBody.Part
    ): Call<PersonalDetailRes> =
        Repository(context).userUserProfileImages(userId, images)

    override suspend fun realtimeImageUpload(
        userId: String,
        realtimeImage: MultipartBody.Part
    ): Call<RealtimeImageResponse> = Repository(context).realtimeImageUpload(userId, realtimeImage)


    override fun getTellUsAboutYouData(): Call<TellUsAboutYouModel> =
        Repository(context).getTellUsAboutYouData()


    override fun updateLifestyle(
        userId: String,
       lifestyleReq: LifestyleReq
    ): Call<PersonalDetailRes> =
        Repository(context).updateLifestyle(userId,lifestyleReq)

    override fun deleteUser(): Call<ErrorResponse> =
        Repository(context).deleteUser()

    override suspend fun getCities(url: String): Resource<Predictions> {
        val response = Repository(context).getCities(url)
        if(response.isSuccessful)
            response.body()?.let { result->
                return Resource.Success(result)
            }
        return Resource.Error(response.message())
    }
}