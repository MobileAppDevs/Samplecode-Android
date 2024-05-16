package com.dream.friend.ui.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.dream.friend.R
import com.dream.friend.common.PreferenceHandler
import com.dream.friend.common.Utils.isNetworkAvailable
import com.dream.friend.data.model.CallDeclineReq
import com.dream.friend.data.model.CreateSubscriberRequest
import com.dream.friend.data.model.SubscribeReq
import com.dream.friend.data.model.SubscriptionRes
import com.dream.friend.data.model.error.ErrorResponse
import com.dream.friend.data.model.login.LoginRes
import com.dream.friend.data.model.login.LoginWithGoogleReq
import com.dream.friend.data.model.login.LoginWithMobileReq
import com.dream.friend.data.model.user.AccessToken
import com.dream.friend.data.model.user.User
import com.dream.friend.data.network.Resource
import com.dream.friend.data.repository.dataSourceImpl.UserLoginDataSourceImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class UserLoginViewModel @Inject constructor(
    val app: Application
): AndroidViewModel(app) {

    private var userLoginDataSource = UserLoginDataSourceImpl(app)
    private var preferenceHandler = PreferenceHandler(app)

    var userLoginWithMobileResponse = MutableLiveData<Resource<LoginRes>>()
    var updatePhoneNumberResponse = MutableLiveData<Resource<LoginRes>>()
    var userLoginWithGoogleResponse = MutableLiveData<Resource<LoginRes>>()
    private var fcmTokenResponse= MutableLiveData<Resource<ErrorResponse>>()
     var sendOtpResponse= MutableLiveData<Resource<ErrorResponse>>()
     var verifyOtpResponse= MutableLiveData<Resource<ErrorResponse>>()
    var deleteImageResponse= MutableLiveData<Resource<LoginRes>>()
    var subscribeResponse= MutableLiveData<Resource<ErrorResponse>>()
    var planBenefitsResponse= MutableLiveData<Resource<SubscriptionRes>>()
    var allBenefitsResponse= MutableLiveData<Resource<SubscriptionRes>>()
    var callDeclineResponse= MutableLiveData<Resource<ErrorResponse>>()

    fun userLoginWithMobile(
        request: LoginWithMobileReq
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            userLoginWithMobileResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                val result = userLoginDataSource.loginWithMobile(request)
                userLoginWithMobileResponse.postValue(result)
            } else
                userLoginWithMobileResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception){
            userLoginWithMobileResponse.postValue(Resource.Error(e.message.toString()))
        }
    }
    fun updatePhoneNumber(
        userId: String,
        request: LoginWithMobileReq
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            updatePhoneNumberResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                val result = userLoginDataSource.updatePhoneNumber(userId = userId,request)
                updatePhoneNumberResponse.postValue(result)
            } else
                updatePhoneNumberResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception){
            updatePhoneNumberResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun userLoginWithGoogle(
        request: LoginWithGoogleReq
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            userLoginWithGoogleResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                val result = userLoginDataSource.loginWithGoogle(request)
                userLoginWithGoogleResponse.postValue(result)
            } else
                userLoginWithGoogleResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception){
            userLoginWithGoogleResponse.postValue(Resource.Error(e.message.toString()))
        }
    }


    fun sendOtp(mobileNumber:String)=viewModelScope.launch(Dispatchers.IO) {
        try {
            sendOtpResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                userLoginDataSource.sendOTP(mobileNumber).enqueue(object :
                    Callback<ErrorResponse> {
                    override fun onResponse(
                        call: Call<ErrorResponse>,
                        response: Response<ErrorResponse>
                    ) {
                        response.body()?.let { sendOtpResponse.postValue(Resource.Success(it)) }
                        response.errorBody()?.let {
                            if (response.code() == 403)
                                sendOtpResponse.postValue(Resource.TokenRenew(true))
                            else
                                sendOtpResponse.postValue(Resource.Error(
                                    Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                ))
                        }
                    }
                    override fun onFailure(call: Call<ErrorResponse>, t: Throwable) {
                        sendOtpResponse.postValue(Resource.Error(t.message.toString()))
                    }
                })
            } else
                sendOtpResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
           sendOtpResponse.postValue(Resource.Error(e.message.toString()))
        }
    }
    fun verifyOtp(verificationCode:String,mobileNumber:String)=viewModelScope.launch(Dispatchers.IO) {
        try {
            verifyOtpResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                userLoginDataSource.verifyOTP(verificationCode,mobileNumber).enqueue(object :
                    Callback<ErrorResponse> {
                    override fun onResponse(
                        call: Call<ErrorResponse>,
                        response: Response<ErrorResponse>
                    ) {
                        response.body()?.let { verifyOtpResponse.postValue(Resource.Success(it)) }
                        response.errorBody()?.let {
                            if (response.code() == 403)
                                verifyOtpResponse.postValue(Resource.TokenRenew(true))
                            else
                                verifyOtpResponse.postValue(Resource.Error(
                                    Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                ))
                        }
                    }
                    override fun onFailure(call: Call<ErrorResponse>, t: Throwable) {
                        verifyOtpResponse.postValue(Resource.Error(t.message.toString()))
                    }
                })
            } else
                verifyOtpResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            verifyOtpResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun fcmToken(userId: String, fcmToken: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            fcmTokenResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                userLoginDataSource.fcmToken(userId, fcmToken).enqueue(object :
                    Callback<ErrorResponse> {
                    override fun onResponse(
                        call: Call<ErrorResponse>,
                        response: Response<ErrorResponse>
                    ) {
                        response.body()?.let { fcmTokenResponse.postValue(Resource.Success(it)) }
                        response.errorBody()?.let {
                            if (response.code() == 403)
                                fcmTokenResponse.postValue(Resource.TokenRenew(true))
                            else
                                fcmTokenResponse.postValue(Resource.Error(
                                    Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                ))
                        }
                    }
                    override fun onFailure(call: Call<ErrorResponse>, t: Throwable) {
                        fcmTokenResponse.postValue(Resource.Error(t.message.toString()))
                    }
                })
            } else
                fcmTokenResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            fcmTokenResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun deleteImage(userId: String, filename: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            deleteImageResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                userLoginDataSource.deleteImage(userId, filename).enqueue(object :
                    Callback<LoginRes> {
                    override fun onResponse(
                        call: Call<LoginRes>,
                        response: Response<LoginRes>
                    ) {
                        response.body()?.let { deleteImageResponse.postValue(Resource.Success(it)) }
                        response.errorBody()?.let {
                            if (response.code() == 403)
                                deleteImageResponse.postValue(Resource.TokenRenew(true))
                            else
                                deleteImageResponse.postValue(Resource.Error(
                                    Gson().fromJson(response.errorBody()!!.charStream(), LoginRes::class.java).message.toString()
                                ))
                        }
                    }
                    override fun onFailure(call: Call<LoginRes>, t: Throwable) {
                        deleteImageResponse.postValue(Resource.Error(t.message.toString()))
                    }
                })
            } else
                deleteImageResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            deleteImageResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun subscribe(request: CreateSubscriberRequest) = viewModelScope.launch(Dispatchers.IO) {
        try {
            subscribeResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                userLoginDataSource.subscribe(request).enqueue(object :
                    Callback<ErrorResponse> {
                    override fun onResponse(
                        call: Call<ErrorResponse>,
                        response: Response<ErrorResponse>
                    ) {
                        response.body()?.let { subscribeResponse.postValue(Resource.Success(it)) }
                        response.errorBody()?.let {
                            if (response.code() == 403)
                                subscribeResponse.postValue(Resource.TokenRenew(true))
                            else
                                subscribeResponse.postValue(Resource.Error(
                                    Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                ))
                        }
                    }
                    override fun onFailure(call: Call<ErrorResponse>, t: Throwable) {
                        subscribeResponse.postValue(Resource.Error(t.message.toString()))
                    }
                })
            } else
                subscribeResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            subscribeResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun getMyPlanBenefits(userId: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            planBenefitsResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                userLoginDataSource.getMyPlanBenefits(userId).enqueue(object :
                    Callback<SubscriptionRes> {
                    override fun onResponse(
                        call: Call<SubscriptionRes>,
                        response: Response<SubscriptionRes>
                    ) {
                        response.body()?.let { planBenefitsResponse.postValue(Resource.Success(it)) }
                        try {
                            response.errorBody()?.let {
                                if (response.code() == 403)
                                    planBenefitsResponse.postValue(Resource.TokenRenew(true))
                                else
                                    planBenefitsResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                    ))
                            }
                        } catch (e: Exception) {
                            planBenefitsResponse.postValue(Resource.Error(e.message.toString()))
                        }
                    }
                    override fun onFailure(call: Call<SubscriptionRes>, t: Throwable) {
                        planBenefitsResponse.postValue(Resource.Error(t.message.toString()))
                    }
                })
            } else
                planBenefitsResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            planBenefitsResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun getAllBenefits() = viewModelScope.launch(Dispatchers.IO) {
        try {
            allBenefitsResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                userLoginDataSource.getAllBenefits().enqueue(object :
                    Callback<SubscriptionRes> {
                    override fun onResponse(
                        call: Call<SubscriptionRes>,
                        response: Response<SubscriptionRes>
                    ) {
                        response.body()?.let { allBenefitsResponse.postValue(Resource.Success(it)) }
                        response.errorBody()?.let {
                            if (response.code() == 403)
                                allBenefitsResponse.postValue(Resource.TokenRenew(true))
                            else
                                allBenefitsResponse.postValue(Resource.Error(
                                    Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                ))
                        }
                    }
                    override fun onFailure(call: Call<SubscriptionRes>, t: Throwable) {
                        allBenefitsResponse.postValue(Resource.Error(t.message.toString()))
                    }
                })
            } else
                allBenefitsResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            allBenefitsResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun callDecline(request: CallDeclineReq) = viewModelScope.launch(Dispatchers.IO) {
        try {
            callDeclineResponse.postValue(Resource.Loading())
            if (app.isNetworkAvailable()) {
                userLoginDataSource.callDecline(request).enqueue(object :
                    Callback<ErrorResponse> {
                    override fun onResponse(
                        call: Call<ErrorResponse>,
                        response: Response<ErrorResponse>
                    ) {
                        response.body()?.let { callDeclineResponse.postValue(Resource.Success(it)) }
                        response.errorBody()?.let {
                            if (response.code() == 403)
                                callDeclineResponse.postValue(Resource.TokenRenew(true))
                            else
                                callDeclineResponse.postValue(Resource.Error(
                                    Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).message.toString()
                                ))
                        }
                    }
                    override fun onFailure(call: Call<ErrorResponse>, t: Throwable) {
                        callDeclineResponse.postValue(Resource.Error(t.message.toString()))
                    }
                })
            } else
                callDeclineResponse.postValue(Resource.Error(app.getString(R.string.noInternet)))
        } catch (e: Exception) {
            callDeclineResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun saveUserAccessToken(accessToken: AccessToken) =
        preferenceHandler.writeString(
            preferenceHandler.authorizationToken,
            accessToken.access_token
        )

    fun saveUser(user: User) =
        preferenceHandler.writeString(
            preferenceHandler.userData,
            Gson().toJson(user)
        )

    fun getUser(): User? =
        Gson().fromJson(
            preferenceHandler.readStringWithNull(
                preferenceHandler.userData,
                null
            ),
            User::class.java
        )
    /**
     * returns 0 for whoLikeYouTab
     * returns 1 for whomYouLikeTab
     */
    fun getTab():Int= preferenceHandler.readInteger("LikeTab",0)
    /**
     * save value 0 for whoLikeYouTab
     * save value 1 for whomYouLikeTab
     */

    fun saveTab(tabNo:Int)=preferenceHandler.writeInteger("LikeTab",tabNo)



    fun clearPreference(context: Context) = preferenceHandler.clearSharePreferences(context)

}