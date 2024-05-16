package com.enkefalostechnologies.calendarpro.database

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.amplifyframework.auth.AuthCodeDeliveryDetails
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthProvider
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.auth.result.AuthSignInResult
import com.amplifyframework.auth.result.AuthSignOutResult
import com.amplifyframework.auth.result.AuthSignUpResult
import com.amplifyframework.core.Amplify
import com.enkefalostechnologies.calendarpro.util.AmplifyUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

class AmplifyAuth {
    private  val TAG = "AmplifyAuth"
    val SignupValue                    : MutableLiveData<Boolean> = MutableLiveData()
    val ConfirmationCode               : MutableLiveData<Boolean> = MutableLiveData()
    val UserCreated                    : MutableLiveData<Boolean> = MutableLiveData()
    val UsernameEdited                 : MutableLiveData<Boolean> = MutableLiveData()
    val ProfileImageEdited             : MutableLiveData<Boolean> = MutableLiveData()
    val userAttributes                    : MutableLiveData<com.enkefalostechnologies.calendarpro.model.User> = MutableLiveData()
    val SignInResultConfirmation       : MutableLiveData<Boolean> = MutableLiveData()
    val SignOutResultConfirmation       : MutableLiveData<Boolean> = MutableLiveData()
    val isUserSignedIn                 : MutableLiveData<Boolean> = MutableLiveData()
    val isErrorOccurred                : MutableLiveData<Exception> = MutableLiveData()
    val isCodeSent                : MutableLiveData<Boolean> = MutableLiveData()



    fun checkSignIn(){
        Amplify.Auth.fetchAuthSession(
            { result ->
                run {
                    if (!result.isSignedIn){
                        Log.d("LoginActivity", "not signed in")

                        isUserSignedIn.postValue(false)

                    } else{

                        isUserSignedIn.postValue(true)
                    }

                }

            },
            { error ->
                Log.e("Main Activity", error.toString())
                isErrorOccurred.setValue(error)
            }
        )

    }

    fun SignIn(email: String, Password: String){

        Log.d(TAG, "email: $email, Password: $Password")
        try {
            val result = Amplify.Auth.signIn(email, Password,this::onSignInSucess, this::onSignInError)

        } catch (error: AuthException) {
            Log.e(TAG, "Sign in failed", error)
        }
    }
    fun SignUp(name:String,email:String,password:String){
        try {
            val options = AuthSignUpOptions.builder()
                .userAttributes(
                    mutableListOf(
                        AuthUserAttribute(AuthUserAttributeKey.email(), email),
                        AuthUserAttribute(AuthUserAttributeKey.name(), name),
                        AuthUserAttribute(AuthUserAttributeKey.familyName(), name),
                        AuthUserAttribute(
                            AuthUserAttributeKey.picture(),
                            "https://ui-avatars.com/api/?name=$name"
                        )
                    )
                )
                .build()
            val result = Amplify.Auth.signUp(email,password,options ,this::onSignUpSuccess, this::onSignUpError)

        } catch (error: AuthException) {
            Log.e(TAG, "Sign in failed", error)
        }
    }
    private fun onSignInSucess(authSignInResult: AuthSignInResult){
        Log.d(TAG, "SignIn Success")
        fetchUserAttributes()
        GlobalScope.launch(Dispatchers.Main) {
            if(authSignInResult.isSignedIn) {
                SignInResultConfirmation.setValue(true)
            }

        }
    }
    private fun onSignInError(e: AuthException){
        Log.d(TAG,"${e.message}")
        GlobalScope.launch(Dispatchers.Main) {
            isErrorOccurred.setValue(e)

        }
    }
    private fun onSignUpSuccess(authSignUpResult: AuthSignUpResult){
        Log.d(TAG, "SignIn Success")
        GlobalScope.launch(Dispatchers.Main) {
            SignupValue.setValue(true)
        }
    }
    private fun onUserConfirmationCodeSent(authSignUpResult: AuthCodeDeliveryDetails){
        Log.d(TAG, "SignIn Success")
        GlobalScope.launch(Dispatchers.Main) {
            isCodeSent.setValue(true)
        }
    }

    private fun onSignUpError(e: AuthException) {
        Log.e(TAG, "Error: ${e.message}")
        GlobalScope.launch(Dispatchers.Main) {
            isErrorOccurred.setValue(e)
        }


    }

     fun fetchUserAttributes(){
        Amplify.Auth.fetchUserAttributes({
            GlobalScope.launch(Dispatchers.Main) {
                var name= ""
                var email= ""
                var picUrl= ""
                var userId=""
                var isSocialLoggedIn= false
                var isEmailVerified= false
                it.map {
                    if (it.key == AuthUserAttributeKey.name()) {
                        name = it.value
                    }
                    if (it.key == AuthUserAttributeKey.picture()) {
                        picUrl = it.value
                    }
                    if (it.key == AuthUserAttributeKey.email()) {
                        email = it.value
                    }
                    if (it.key == AuthUserAttributeKey.email()) {
                        email = it.value
                    }
                    if (it.key == AuthUserAttributeKey.custom("sub")) {
                        userId=it.value
                    }
                    if (it.key == AuthUserAttributeKey.custom("identities")) {
                        isSocialLoggedIn =
                            it.value.contains("Google") || it.value.contains("Facebook")
                    }
                    if (it.key == AuthUserAttributeKey.emailVerified()) {
                        isEmailVerified = it.value.toBoolean()
                    }

                }
                userAttributes.setValue(com.enkefalostechnologies.calendarpro.model.User(
                    userId=userId,
                    name=name,
                email=email,
                picUrl=picUrl,
                isSocialLoggedIn=isSocialLoggedIn,
                isEmailVerified=isEmailVerified
                ))
            }
        },this::onSignInError)
    }

    fun SignOut(){
        Amplify.Auth.signOut(this::onSignOutSuccess)
    }

    private fun onSignOutSuccess(authSignUpResult: AuthSignOutResult){
        Log.d(TAG, "SignOut Success")
        GlobalScope.launch(Dispatchers.Main) {
            SignOutResultConfirmation.setValue(true)
        }
    }

    fun GoogleLogin(activity: Activity){
        Amplify.Auth.signInWithSocialWebUI(AuthProvider.google(), activity,this::onSignInSucess ,this::onSignInError)
    }
    fun FacebookLogin(activity: Activity){
        Amplify.Auth.signInWithSocialWebUI(AuthProvider.facebook(), activity,this::onSignInSucess ,this::onSignInError)
    }

    fun sendOtp(email:String){
        AmplifyUtil.resendVerificationCode(email = email, this::onUserConfirmationCodeSent, this::onSignInError)
    }
}