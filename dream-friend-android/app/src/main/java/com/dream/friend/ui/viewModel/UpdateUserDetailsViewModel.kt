package com.dream.friend.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.dream.friend.R
import com.dream.friend.common.Utils.isNetworkAvailable
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
import com.dream.friend.data.network.Resource
import com.dream.friend.data.repository.dataSourceImpl.UpdateUserDetailsDataSourceImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class UpdateUserDetailsViewModel @Inject constructor(
    val app: Application,
): AndroidViewModel(app) {

    private var updateUserDetailsDataSource = UpdateUserDetailsDataSourceImpl(app)

    var updateUserDetailsResponse = MutableLiveData<Resource<PersonalDetailRes>>()
    var updateUserLocationResponse = MutableLiveData<Resource<PersonalDetailRes>>()
    var sexualOrientationResponse = MutableLiveData<Resource<SexualOrientationRes>>()
    var yourInterestsResponse = MutableLiveData<Resource<YourInterestsRes>>()
    var updateUserProfileImagesResponse = MutableLiveData<Resource<PersonalDetailRes>>()
    var realtimeImageResponse = MutableLiveData<Resource<RealtimeImageResponse>>()

    val getTellUsAboutYouData = MutableLiveData<Resource<TellUsAboutYouModel>>()
    val updateLifestyleResponse = MutableLiveData<Resource<PersonalDetailRes>>()
    val deleteUserResponse = MutableLiveData<Resource<ErrorResponse>>()
    val subscriptionResponse = MutableLiveData<Resource<SubscriptionResponse>>()
    val getCitiesResponse = MutableLiveData<Resource<Predictions>>()

    fun uploadRealtimeImage(
        userId: String,
        realtimeImage:MultipartBody.Part
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            realtimeImageResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                updateUserDetailsDataSource.realtimeImageUpload(userId, realtimeImage)
                    .enqueue(object : Callback<RealtimeImageResponse> {
                        override fun onResponse(
                            call: Call<RealtimeImageResponse>,
                            response: Response<RealtimeImageResponse>
                        ) {
                            response.body()?.let { realtimeImageResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    realtimeImageResponse.postValue(Resource.TokenRenew(true))
                                else
                                    realtimeImageResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<RealtimeImageResponse>, t: Throwable) {
                            realtimeImageResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                realtimeImageResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            realtimeImageResponse.postValue(Resource.Error(e.message.toString()))
        }
    }
    fun updateUserDetails(
        userId: String,
        updateUserDetailsReq: Any
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            updateUserDetailsResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                updateUserDetailsDataSource.updateUserDetails(userId, updateUserDetailsReq)
                    .enqueue(object : Callback<PersonalDetailRes> {
                        override fun onResponse(
                            call: Call<PersonalDetailRes>,
                            response: Response<PersonalDetailRes>
                        ) {
                            response.body()?.let { updateUserDetailsResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    updateUserDetailsResponse.postValue(Resource.TokenRenew(true))
                                else
                                    updateUserDetailsResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<PersonalDetailRes>, t: Throwable) {
                            updateUserDetailsResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                updateUserDetailsResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            updateUserDetailsResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun updateUserLocation(
        userId: String,
        locationReq: LocationReq
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            updateUserLocationResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                updateUserDetailsDataSource.updateUserLocation(userId, locationReq)
                    .enqueue(object : Callback<PersonalDetailRes> {
                        override fun onResponse(
                            call: Call<PersonalDetailRes>,
                            response: Response<PersonalDetailRes>
                        ) {
                            response.body()?.let { updateUserLocationResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    updateUserLocationResponse.postValue(Resource.TokenRenew(true))
                                else
                                    updateUserLocationResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<PersonalDetailRes>, t: Throwable) {
                            updateUserLocationResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                updateUserLocationResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            updateUserLocationResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun getSubscriptions() = viewModelScope.launch(Dispatchers.IO) {
        try {
            subscriptionResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                updateUserDetailsDataSource.getSubscriptions()
                    .enqueue(object : Callback<SubscriptionResponse> {
                        override fun onResponse(
                            call: Call<SubscriptionResponse>,
                            response: Response<SubscriptionResponse>
                        ) {
                            response.body()?.let { subscriptionResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    subscriptionResponse.postValue(Resource.TokenRenew(true))
                                else
                                    subscriptionResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<SubscriptionResponse>, t: Throwable) {

                            subscriptionResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                subscriptionResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            subscriptionResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun getSexualOrientations(
        url: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        sexualOrientationResponse.postValue(Resource.Loading())
        try {
            sexualOrientationResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                updateUserDetailsDataSource.getSexualOrientations(url)
                    .enqueue(object : Callback<SexualOrientationRes> {
                        override fun onResponse(
                            call: Call<SexualOrientationRes>,
                            response: Response<SexualOrientationRes>
                        ) {
                            response.body()?.let { sexualOrientationResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    sexualOrientationResponse.postValue(Resource.TokenRenew(true))
                                else
                                    sexualOrientationResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<SexualOrientationRes>, t: Throwable) {
                            sexualOrientationResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                sexualOrientationResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            sexualOrientationResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun getYourInterests(
        url: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        yourInterestsResponse.postValue(Resource.Loading())
        try {
            yourInterestsResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                updateUserDetailsDataSource.getYourInterests(url)
                    .enqueue(object : Callback<YourInterestsRes> {
                        override fun onResponse(
                            call: Call<YourInterestsRes>,
                            response: Response<YourInterestsRes>
                        ) {
                            response.body()?.let { yourInterestsResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    yourInterestsResponse.postValue(Resource.TokenRenew(true))
                                else
                                    yourInterestsResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<YourInterestsRes>, t: Throwable) {
                            yourInterestsResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                yourInterestsResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            yourInterestsResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun userUserProfileImages(
        userId: String,
        images: MultipartBody.Part
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            updateUserProfileImagesResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                updateUserDetailsDataSource.userUserProfileImages(userId, images)
                    .enqueue(object : Callback<PersonalDetailRes> {
                        override fun onResponse(
                            call: Call<PersonalDetailRes>,
                            response: Response<PersonalDetailRes>
                        ) {
                            response.body()?.let { updateUserProfileImagesResponse.postValue(Resource.Success(it)) }
                            try {
                                response.errorBody()?.let {
                                    if (response.code() == 403)
                                        updateUserProfileImagesResponse.postValue(Resource.TokenRenew(true))
                                    else
                                        updateUserProfileImagesResponse.postValue(Resource.Error(
                                            Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                        ))
                                }
                            } catch (e: Exception) {
                                updateUserProfileImagesResponse.postValue(Resource.Error(e.message.toString()))
                            }
                        }
                        override fun onFailure(call: Call<PersonalDetailRes>, t: Throwable) {
                            updateUserProfileImagesResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                updateUserProfileImagesResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            updateUserProfileImagesResponse.postValue(Resource.Error(e.message.toString()))
        }
    }


    fun getTellUsAboutYouData() = viewModelScope.launch(Dispatchers.IO) {
        try {
            getTellUsAboutYouData.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                updateUserDetailsDataSource.getTellUsAboutYouData()
                    .enqueue(object : Callback<TellUsAboutYouModel> {
                        override fun onResponse(
                            call: Call<TellUsAboutYouModel>,
                            response: Response<TellUsAboutYouModel>
                        ) {
                            response.body()?.let { getTellUsAboutYouData.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    getTellUsAboutYouData.postValue(Resource.TokenRenew(true))
                                else
                                    getTellUsAboutYouData.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<TellUsAboutYouModel>, t: Throwable) {
                            getTellUsAboutYouData.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                getTellUsAboutYouData.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            getTellUsAboutYouData.postValue(Resource.Error(e.message.toString()))
        }
    }


    fun updateLifestyle(
        userId: String,
        lifestyleReq: LifestyleReq
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            updateLifestyleResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                updateUserDetailsDataSource.updateLifestyle(userId,lifestyleReq)
                    .enqueue(object : Callback<PersonalDetailRes> {
                        override fun onResponse(
                            call: Call<PersonalDetailRes>,
                            response: Response<PersonalDetailRes>
                        ) {
                            response.body()?.let { updateLifestyleResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    updateLifestyleResponse.postValue(Resource.TokenRenew(true))
                                else
                                    updateLifestyleResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<PersonalDetailRes>, t: Throwable) {
                            updateLifestyleResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                updateLifestyleResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            updateLifestyleResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun deleteUser() = viewModelScope.launch(Dispatchers.IO) {
        try {
            deleteUserResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                updateUserDetailsDataSource.deleteUser()
                    .enqueue(object : Callback<ErrorResponse> {
                        override fun onResponse(
                            call: Call<ErrorResponse>,
                            response: Response<ErrorResponse>
                        ) {
                            response.body()?.let { deleteUserResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    deleteUserResponse.postValue(Resource.TokenRenew(true))
                                else
                                    deleteUserResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<ErrorResponse>, t: Throwable) {
                            deleteUserResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                deleteUserResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            deleteUserResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun getCities(
        url: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            getCitiesResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                val result = updateUserDetailsDataSource.getCities(url)
                getCitiesResponse.postValue(result)
            } else
                getCitiesResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception){
            getCitiesResponse.postValue(Resource.Error(e.message.toString()))
        }
    }
}