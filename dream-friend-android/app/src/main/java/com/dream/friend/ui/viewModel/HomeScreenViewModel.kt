package com.dream.friend.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.dream.friend.R
import com.dream.friend.common.PreferenceHandler
import com.dream.friend.common.Utils.isNetworkAvailable
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
import com.dream.friend.data.model.chat.LastMessageReq
import com.dream.friend.data.model.error.ErrorResponse
import com.dream.friend.data.model.interests.sexual.SexualOrientationRes
import com.dream.friend.data.model.peronal_details.GetUsersRes
import com.dream.friend.data.model.peronal_details.PersonalDetailRes
import com.dream.friend.data.model.peronal_details.RtcResponse
import com.dream.friend.data.model.user.User
import com.dream.friend.data.network.Resource
import com.dream.friend.data.repository.dataSourceImpl.HomeScreenDataSourceImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    val app: Application,
): AndroidViewModel(app) {
    private var homeScreenDataSourceImpl = HomeScreenDataSourceImpl(app)
    private var preferenceHandler = PreferenceHandler(app)

    val getBlockedUsersResponse = MutableLiveData<Resource<GetUsersRes>>()
    val getUserDetailResponse = MutableLiveData<Resource<PersonalDetailRes>>()
    var blockUserResponse = MutableLiveData<Resource<GetUsersRes>>()
    var likeUserResponse = MutableLiveData<Resource<GetUsersRes>>()
    var superLikeResponse= MutableLiveData<Resource<ErrorResponse>>()
    var boostResponse= MutableLiveData<Resource<ErrorResponse>>()
    var rejectUserResponse = MutableLiveData<Resource<GetUsersRes>>()
    var dislikeUserResponse = MutableLiveData<Resource<GetUsersRes>>()
    var getAllUsersResponse = MutableLiveData<Resource<GetUsersRes>>()
    var getUsersResponse = MutableLiveData<Resource<GetUsersRes>>()
    var getWhomYouLikedUsersResponse = MutableLiveData<Resource<GetUsersRes>>()
    var getWhoLikeYouUsersResponse = MutableLiveData<Resource<GetUsersRes>>()
    var rtcResponse = MutableLiveData<Resource<RtcResponse>>()
    var logoutResponse = MutableLiveData<Resource<ErrorResponse>>()
    var lastMessageResponse= MutableLiveData<Resource<ErrorResponse>>()
    var markAsReadResponse= MutableLiveData<Resource<ErrorResponse>>()
    var basicFilterResponse= MutableLiveData<Resource<PersonalDetailRes>>()
    var incognitoModeResponse= MutableLiveData<Resource<PersonalDetailRes>>()
    var notificationResponse= MutableLiveData<Resource<PersonalDetailRes>>()
    var emailUpdateResponse= MutableLiveData<Resource<EmailRes>>()
    var emailOtpVerifyResponse= MutableLiveData<Resource<EmailRes>>()
    var educationResponse= MutableLiveData<Resource<EducationResponse>>()
    var sexualOrientationResponse= MutableLiveData<Resource<SexualOrientationRes>>()
    var interestsResponse= MutableLiveData<Resource<InterestsResponse>>()
    var religionResponse= MutableLiveData<Resource<ReligionResponse>>()
    var advanceFilterResponse= MutableLiveData<Resource<AdvanceFilterResponse>>()
    var clearAdvanceFilterResponse= MutableLiveData<Resource<ClearAdvanceFilterResponse>>()
    var createAdvanceFilterResponse= MutableLiveData<Resource<AdvanceFilterResponse>>()

    fun createAdvanceFilter(userId:String,body:CreateAdvanceFilterReq)=viewModelScope.launch(Dispatchers.IO) {
        try {
            createAdvanceFilterResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                homeScreenDataSourceImpl.createAdvanceFilter(userId,body)
                    .enqueue(object : Callback<AdvanceFilterResponse> {
                        override fun onResponse(
                            call: Call<AdvanceFilterResponse>,
                            response: Response<AdvanceFilterResponse>
                        ) {
                            response.body()?.let { createAdvanceFilterResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    createAdvanceFilterResponse.postValue(Resource.TokenRenew(true))
                                else
                                    createAdvanceFilterResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<AdvanceFilterResponse>, t: Throwable) {
                            createAdvanceFilterResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                createAdvanceFilterResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            createAdvanceFilterResponse.postValue(Resource.Error(e.message.toString()))
        }
    }
    fun getAdvanceFilterData(userId:String)=viewModelScope.launch(Dispatchers.IO) {
        try {
            advanceFilterResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                homeScreenDataSourceImpl.getAdvanceFilterData(userId)
                    .enqueue(object : Callback<AdvanceFilterResponse> {
                        override fun onResponse(
                            call: Call<AdvanceFilterResponse>,
                            response: Response<AdvanceFilterResponse>
                        ) {
                            response.body()?.let { advanceFilterResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    advanceFilterResponse.postValue(Resource.TokenRenew(true))
                                else
                                    advanceFilterResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<AdvanceFilterResponse>, t: Throwable) {
                            advanceFilterResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                advanceFilterResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            advanceFilterResponse.postValue(Resource.Error(e.message.toString()))
        }
    }
    fun clearAdvanceFilter(userId:String)=viewModelScope.launch(Dispatchers.IO) {
        try {
            clearAdvanceFilterResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                homeScreenDataSourceImpl.clearAdvanceFilter(userId)
                    .enqueue(object : Callback<ClearAdvanceFilterResponse> {
                        override fun onResponse(
                            call: Call<ClearAdvanceFilterResponse>,
                            response: Response<ClearAdvanceFilterResponse>
                        ) {
                            response.body()?.let { clearAdvanceFilterResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    clearAdvanceFilterResponse.postValue(Resource.TokenRenew(true))
                                else
                                    clearAdvanceFilterResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<ClearAdvanceFilterResponse>, t: Throwable) {
                            clearAdvanceFilterResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                clearAdvanceFilterResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            clearAdvanceFilterResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun getSexualOrientations(url:String)=viewModelScope.launch(Dispatchers.IO) {
        try {
            sexualOrientationResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                homeScreenDataSourceImpl.getSexualOrientations(url)
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
    fun getUserDetail(userId: String)=viewModelScope.launch(Dispatchers.IO) {
        try {
            getUserDetailResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                homeScreenDataSourceImpl.getUserDetail(userId =userId )
                    .enqueue(object : Callback<PersonalDetailRes> {
                        override fun onResponse(
                            call: Call<PersonalDetailRes>,
                            response: Response<PersonalDetailRes>
                        ) {
                            response.body()?.let {
                                getUserDetailResponse.postValue(Resource.Success(it))
                            }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    getUserDetailResponse.postValue(Resource.TokenRenew(true))
                                else
                                    sexualOrientationResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<PersonalDetailRes>, t: Throwable) {
                            getUserDetailResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                getUserDetailResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            getUserDetailResponse.postValue(Resource.Error(e.message.toString()))
        }
    }
    fun getEducations()=viewModelScope.launch(Dispatchers.IO) {
        try {
            educationResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                homeScreenDataSourceImpl.getEducations()
                    .enqueue(object : Callback<EducationResponse> {
                        override fun onResponse(
                            call: Call<EducationResponse>,
                            response: Response<EducationResponse>
                        ) {
                            response.body()?.let { educationResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    educationResponse.postValue(Resource.TokenRenew(true))
                                else
                                    educationResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<EducationResponse>, t: Throwable) {
                            educationResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                educationResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            educationResponse.postValue(Resource.Error(e.message.toString()))
        }
    }
    fun getReligions()=viewModelScope.launch(Dispatchers.IO) {
        try {
            religionResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                homeScreenDataSourceImpl.getReligions()
                    .enqueue(object : Callback<ReligionResponse> {
                        override fun onResponse(
                            call: Call<ReligionResponse>,
                            response: Response<ReligionResponse>
                        ) {
                            response.body()?.let { religionResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    religionResponse.postValue(Resource.TokenRenew(true))
                                else
                                    religionResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<ReligionResponse>, t: Throwable) {
                            religionResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                religionResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            religionResponse.postValue(Resource.Error(e.message.toString()))
        }
    }
    fun getInterests()=viewModelScope.launch(Dispatchers.IO) {
        try {
            interestsResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                homeScreenDataSourceImpl.getInterests()
                    .enqueue(object : Callback<InterestsResponse> {
                        override fun onResponse(
                            call: Call<InterestsResponse>,
                            response: Response<InterestsResponse>
                        ) {
                            response.body()?.let { interestsResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    interestsResponse.postValue(Resource.TokenRenew(true))
                                else
                                    interestsResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<InterestsResponse>, t: Throwable) {
                            interestsResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                interestsResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            interestsResponse.postValue(Resource.Error(e.message.toString()))
        }
    }
    fun incognitoMode(request:IncognitoModeRequest)=viewModelScope.launch(Dispatchers.IO) {
        try {
            incognitoModeResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                homeScreenDataSourceImpl.incognitoMode(request)
                    .enqueue(object : Callback<PersonalDetailRes> {
                        override fun onResponse(
                            call: Call<PersonalDetailRes>,
                            response: Response<PersonalDetailRes>
                        ) {
                            response.body()?.let { incognitoModeResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    incognitoModeResponse.postValue(Resource.TokenRenew(true))
                                else
                                    incognitoModeResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<PersonalDetailRes>, t: Throwable) {
                            incognitoModeResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                incognitoModeResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            incognitoModeResponse.postValue(Resource.Error(e.message.toString()))
        }
    }
    fun verifyOrUpdateEmail(request:EmailRequest)=viewModelScope.launch(Dispatchers.IO) {
        try {
            emailUpdateResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                homeScreenDataSourceImpl.verifyOrUpdateEmail(request)
                    .enqueue(object : Callback<EmailRes> {
                        override fun onResponse(
                            call: Call<EmailRes>,
                            response: Response<EmailRes>
                        ) {
                            response.body()?.let { emailUpdateResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    emailUpdateResponse.postValue(Resource.TokenRenew(true))
                                else
                                    emailUpdateResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<EmailRes>, t: Throwable) {
                            emailUpdateResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                emailUpdateResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            emailUpdateResponse.postValue(Resource.Error(e.message.toString()))
        }
    }
    fun emailOtpVerify(request:VerifyEmailOtpRequest)=viewModelScope.launch(Dispatchers.IO) {
        try {
            emailOtpVerifyResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                homeScreenDataSourceImpl.verifyEmailOtp(request)
                    .enqueue(object : Callback<EmailRes> {
                        override fun onResponse(
                            call: Call<EmailRes>,
                            response: Response<EmailRes>
                        ) {
                            response.body()?.let {  emailOtpVerifyResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    emailOtpVerifyResponse.postValue(Resource.TokenRenew(true))
                                else
                                    emailOtpVerifyResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<EmailRes>, t: Throwable) {
                            emailOtpVerifyResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                emailOtpVerifyResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            emailOtpVerifyResponse.postValue(Resource.Error(e.message.toString()))
        }
    }
    fun notificationEnableDisable(request:NotificationRequest)=viewModelScope.launch(Dispatchers.IO) {
        try {
            notificationResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                homeScreenDataSourceImpl.notificationEnableDisable(request)
                    .enqueue(object : Callback<PersonalDetailRes> {
                        override fun onResponse(
                            call: Call<PersonalDetailRes>,
                            response: Response<PersonalDetailRes>
                        ) {
                            response.body()?.let { notificationResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    notificationResponse.postValue(Resource.TokenRenew(true))
                                else
                                   notificationResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<PersonalDetailRes>, t: Throwable) {
                            notificationResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
               notificationResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
           notificationResponse.postValue(Resource.Error(e.message.toString()))
        }
    }
    fun applyBasicFilter(userId: String?, request:BasicFilterRequest)=viewModelScope.launch(Dispatchers.IO) {
        try {
            basicFilterResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                homeScreenDataSourceImpl.applyBasicFilter(userId.toString(),request)
                    .enqueue(object : Callback<PersonalDetailRes> {
                        override fun onResponse(
                            call: Call<PersonalDetailRes>,
                            response: Response<PersonalDetailRes>
                        ) {
                            response.body()?.let { basicFilterResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    basicFilterResponse.postValue(Resource.TokenRenew(true))
                                else
                                    basicFilterResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<PersonalDetailRes>, t: Throwable) {
                            basicFilterResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                basicFilterResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            basicFilterResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun getBlockedUsers() = viewModelScope.launch(Dispatchers.IO) {
        try {
            getBlockedUsersResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                homeScreenDataSourceImpl.blockedUsers()
                    .enqueue(object : Callback<GetUsersRes> {
                        override fun onResponse(
                            call: Call<GetUsersRes>,
                            response: Response<GetUsersRes>
                        ) {
                            response.body()?.let { getBlockedUsersResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    getBlockedUsersResponse.postValue(Resource.TokenRenew(true))
                                else
                                    getBlockedUsersResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<GetUsersRes>, t: Throwable) {
                            getBlockedUsersResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                getBlockedUsersResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            getBlockedUsersResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun blockUser(
        blockUserId: String,
        blockedUserStatus: String,
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            blockUserResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                homeScreenDataSourceImpl.blockUser(blockUserId, blockedUserStatus)
                    .enqueue(object : Callback<GetUsersRes> {
                        override fun onResponse(
                            call: Call<GetUsersRes>,
                            response: Response<GetUsersRes>
                        ) {
                            response.body()?.let { blockUserResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    blockUserResponse.postValue(Resource.TokenRenew(true))
                                else
                                    blockUserResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<GetUsersRes>, t: Throwable) {
                            blockUserResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                blockUserResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            blockUserResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun likeUser(
        likedUserId: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            likeUserResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                homeScreenDataSourceImpl.likeUser(likedUserId)
                    .enqueue(object : Callback<GetUsersRes> {
                        override fun onResponse(
                            call: Call<GetUsersRes>,
                            response: Response<GetUsersRes>
                        ) {
                            response.body()?.let { likeUserResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    likeUserResponse.postValue(Resource.TokenRenew(true))
                                else {
                                    likeUserResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                                }
                            }
                        }
                        override fun onFailure(call: Call<GetUsersRes>, t: Throwable) {
                            likeUserResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                likeUserResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            likeUserResponse.postValue(Resource.Error(e.message.toString()))
        }
    }
    fun superLike(
        likedUserId: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            superLikeResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                homeScreenDataSourceImpl.superLike(likedUserId)
                    .enqueue(object : Callback<ErrorResponse> {
                        override fun onResponse(
                            call: Call<ErrorResponse>,
                            response: Response<ErrorResponse>
                        ) {
                            response.body()?.let { superLikeResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    superLikeResponse.postValue(Resource.TokenRenew(true))
                                else {
                                    superLikeResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                                }
                            }
                        }
                        override fun onFailure(call: Call<ErrorResponse>, t: Throwable) {
                            superLikeResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                superLikeResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            superLikeResponse.postValue(Resource.Error(e.message.toString()))
        }
    }
    fun applyBoost() = viewModelScope.launch(Dispatchers.IO) {
        try {
            boostResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                homeScreenDataSourceImpl.applyBoost()
                    .enqueue(object : Callback<ErrorResponse> {
                        override fun onResponse(
                            call: Call<ErrorResponse>,
                            response: Response<ErrorResponse>
                        ) {
                            response.body()?.let {boostResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    boostResponse.postValue(Resource.TokenRenew(true))
                                else {
                                    boostResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                                }
                            }
                        }
                        override fun onFailure(call: Call<ErrorResponse>, t: Throwable) {
                            boostResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                boostResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            boostResponse.postValue(Resource.Error(e.message.toString()))
        }
    }
    fun rejectUser(
        likedUserId: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            rejectUserResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                homeScreenDataSourceImpl.rejectUser(likedUserId)
                    .enqueue(object : Callback<GetUsersRes> {
                        override fun onResponse(
                            call: Call<GetUsersRes>,
                            response: Response<GetUsersRes>
                        ) {
                            response.body()?.let { rejectUserResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    rejectUserResponse.postValue(Resource.TokenRenew(true))
                                else {
                                    rejectUserResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()?.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                                }
                            }
                        }
                        override fun onFailure(call: Call<GetUsersRes>, t: Throwable) {
                           rejectUserResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                rejectUserResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            rejectUserResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun dislikeUser(
        dislikedUserId: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            dislikeUserResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                homeScreenDataSourceImpl.dislikeUser(dislikedUserId)
                    .enqueue(object : Callback<GetUsersRes> {
                        override fun onResponse(
                            call: Call<GetUsersRes>,
                            response: Response<GetUsersRes>
                        ) {
                            response.body()?.let { dislikeUserResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    dislikeUserResponse.postValue(Resource.TokenRenew(true))
                                else
                                    dislikeUserResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<GetUsersRes>, t: Throwable) {
                            dislikeUserResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                dislikeUserResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            dislikeUserResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun getAllUsers(
        userId: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            getAllUsersResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                homeScreenDataSourceImpl.getAllUsers(userId)
                    .enqueue(object : Callback<GetUsersRes> {
                        override fun onResponse(
                            call: Call<GetUsersRes>,
                            response: Response<GetUsersRes>
                        ) {
                            response.body()?.let { getAllUsersResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    getAllUsersResponse.postValue(Resource.TokenRenew(true))
                                else
                                    getAllUsersResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<GetUsersRes>, t: Throwable) {
                            getAllUsersResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                getAllUsersResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            getAllUsersResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun getUsers(
        url: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            getUsersResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                homeScreenDataSourceImpl.getUsers(url)
                    .enqueue(object : Callback<GetUsersRes> {
                        override fun onResponse(
                            call: Call<GetUsersRes>,
                            response: Response<GetUsersRes>
                        ) {
                            response.body()?.let { getUsersResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    getUsersResponse.postValue(Resource.TokenRenew(true))
                                else
                                    getUsersResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<GetUsersRes>, t: Throwable) {
                            getUsersResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                getUsersResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            getUsersResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun getWhomYouLikedUsers(
        url: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            getWhomYouLikedUsersResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                homeScreenDataSourceImpl.getWhomYouLikedUsers(url)
                    .enqueue(object : Callback<GetUsersRes> {
                        override fun onResponse(
                            call: Call<GetUsersRes>,
                            response: Response<GetUsersRes>
                        ) {
                            response.body()?.let { getWhomYouLikedUsersResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    getWhomYouLikedUsersResponse.postValue(Resource.TokenRenew(true))
                                else
                                    getWhomYouLikedUsersResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<GetUsersRes>, t: Throwable) {
                            getWhomYouLikedUsersResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                getWhomYouLikedUsersResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            getWhomYouLikedUsersResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun getWhoLikedYouUsers(
        url: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            getWhoLikeYouUsersResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                homeScreenDataSourceImpl.getWhoLikedYouUsers(url)
                    .enqueue(object : Callback<GetUsersRes> {
                        override fun onResponse(
                            call: Call<GetUsersRes>,
                            response: Response<GetUsersRes>
                        ) {
                            response.body()?.let { getWhoLikeYouUsersResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    getWhoLikeYouUsersResponse.postValue(Resource.TokenRenew(true))
                                else
                                    getWhoLikeYouUsersResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<GetUsersRes>, t: Throwable) {
                            getWhoLikeYouUsersResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                getWhoLikeYouUsersResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            getWhoLikeYouUsersResponse.postValue(Resource.Error(e.message.toString()))
        }
    }
    fun rtcToken(
        channel:String,
        role:String
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            rtcResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                homeScreenDataSourceImpl.rtcToken(channel,role)
                    .enqueue(object : Callback<RtcResponse> {
                        override fun onResponse(
                            call: Call<RtcResponse>,
                            response: Response<RtcResponse>
                        ) {
                            response.body()?.let { rtcResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    rtcResponse.postValue(Resource.TokenRenew(true))
                                else
                                    rtcResponse.postValue(Resource.Error(response.errorBody().toString()))
                            }
                        }
                        override fun onFailure(call: Call<RtcResponse>, t: Throwable) {
                            rtcResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                rtcResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            rtcResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun logout() = viewModelScope.launch(Dispatchers.IO) {
        try {
            logoutResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                homeScreenDataSourceImpl.logout()
                    .enqueue(object : Callback<ErrorResponse> {
                        override fun onResponse(
                            call: Call<ErrorResponse>,
                            response: Response<ErrorResponse>
                        ) {
                            response.body()?.let { logoutResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    logoutResponse.postValue(Resource.TokenRenew(true))
                                else
                                    logoutResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<ErrorResponse>, t: Throwable) {
                            logoutResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                logoutResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            logoutResponse.postValue(Resource.Error(e.message.toString()))
        }
    }
    fun updateLastMessage(userId: String,lastMessageReq: LastMessageReq) = viewModelScope.launch(Dispatchers.IO) {
        try {
            lastMessageResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                homeScreenDataSourceImpl.lastMessage(userId =userId,
                    lastMessage = lastMessageReq.lastMessage,
                    type=lastMessageReq.type,
                    unreadCount = lastMessageReq.unreadCount,
                    channelName = lastMessageReq.channelName
                ).enqueue(object : Callback<ErrorResponse> {
                        override fun onResponse(
                            call: Call<ErrorResponse>,
                            response: Response<ErrorResponse>
                        ) {
                            response.body()?.let { lastMessageResponse.postValue(Resource.Success(it)) }
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    lastMessageResponse.postValue(Resource.TokenRenew(true))
                                else
                                    lastMessageResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        }
                        override fun onFailure(call: Call<ErrorResponse>, t: Throwable) {
                            lastMessageResponse.postValue(Resource.Error(t.message.toString()))
                        }
                    })
            } else
                lastMessageResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            lastMessageResponse.postValue(Resource.Error(e.message.toString()))
        }
    }
    fun markAsReadChat(chatId:String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            markAsReadResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                homeScreenDataSourceImpl.markAsRead(chatId).enqueue(object : Callback<ErrorResponse> {
                    override fun onResponse(
                        call: Call<ErrorResponse>,
                        response: Response<ErrorResponse>
                    ) {
                        response.body()?.let { markAsReadResponse.postValue(Resource.Success(it)) }
                        response.errorBody()?.let {
                            if (response.code() == 403)
                                markAsReadResponse.postValue(Resource.TokenRenew(true))
                            else
                                markAsReadResponse.postValue(Resource.Error(response.errorBody().toString()))
                        }
                    }
                    override fun onFailure(call: Call<ErrorResponse>, t: Throwable) {
                        markAsReadResponse.postValue(Resource.Error(t.message.toString()))
                    }
                })
            } else
                markAsReadResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            markAsReadResponse.postValue(Resource.Error(e.message.toString()))
        }



    }
}