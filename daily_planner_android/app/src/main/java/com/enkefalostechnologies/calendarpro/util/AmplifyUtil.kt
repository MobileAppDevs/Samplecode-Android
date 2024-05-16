package com.enkefalostechnologies.calendarpro.util

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import aws.smithy.kotlin.runtime.retries.TimedOutException
import com.amplifyframework.auth.AuthCodeDeliveryDetails
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthProvider
import com.amplifyframework.auth.AuthSession
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.exceptions.invalidstate.SignedInException
import com.amplifyframework.auth.cognito.exceptions.service.CodeExpiredException
import com.amplifyframework.auth.cognito.exceptions.service.CodeMismatchException
import com.amplifyframework.auth.cognito.exceptions.service.InvalidParameterException
import com.amplifyframework.auth.cognito.exceptions.service.InvalidPasswordException
import com.amplifyframework.auth.cognito.exceptions.service.LimitExceededException
import com.amplifyframework.auth.cognito.exceptions.service.PasswordResetRequiredException
import com.amplifyframework.auth.cognito.exceptions.service.UserCancelledException
import com.amplifyframework.auth.cognito.exceptions.service.UserNotConfirmedException
import com.amplifyframework.auth.cognito.exceptions.service.UserNotFoundException
import com.amplifyframework.auth.cognito.exceptions.service.UsernameExistsException
import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult
import com.amplifyframework.auth.exceptions.NotAuthorizedException
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.auth.result.AuthResetPasswordResult
import com.amplifyframework.auth.result.AuthSignUpResult
import com.amplifyframework.auth.result.AuthUpdateAttributeResult
import com.amplifyframework.core.Action
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.Amplify.Auth
import com.amplifyframework.core.Consumer
import com.enkefalostechnologies.calendarpro.constant.Constants
import com.enkefalostechnologies.calendarpro.exception.CustomException
import com.enkefalostechnologies.calendarpro.router.Router.navigateToHome
import com.enkefalostechnologies.calendarpro.util.AppUtil.showToast
import com.enkefalostechnologies.calendarpro.util.Extension.close


object AmplifyUtil {

    fun checkUserLoggedIn(
        onSuccess: Consumer<AuthSession>,
        onError: Consumer<AuthException>
    ) {
        Amplify.Auth.fetchAuthSession(
            onSuccess,
            onError
        )
    }

    fun updateUser(
        name: String,
        onSuccess: Consumer<Map<AuthUserAttributeKey, AuthUpdateAttributeResult>>,
        onError: Consumer<AuthException>
    ) {
        Amplify.Auth.updateUserAttributes(
            mutableListOf(
                AuthUserAttribute(AuthUserAttributeKey.name(), name),
                AuthUserAttribute(AuthUserAttributeKey.familyName(), name),
            ),
            onSuccess,
            onError
        )
    }

    fun signup(
        name: String,
        email: String,
        password: String,
        onSuccess: Consumer<AuthSignUpResult>,
        onError: Consumer<AuthException>
    ) {
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
        Amplify.Auth.signUp(
            email, password, options,
            onSuccess,
            onError
        )
    }

    fun Context.confirmUser(
        username: String,
        code: String,
        listener: AmplifyListener
    ) {
        Amplify.Auth.confirmSignUp(
            username, code,
            { result ->
                if (result.isSignUpComplete) {
                    listener.onSuccess()
                } else {
                    listener.onFailed()
                }
            },
            {
                this.handleExceptions( it)
            }
        )
    }

    fun Context.loginThroughEmail(
        username: String,
        password: String,
        listener: AmplifyListener
    ) {
        Amplify.Auth.signIn(username, password,
            { result ->
                if (result.isSignedIn) {
                    listener.onSuccess()
                } else {
                    listener.onFailed()
                }
            },
            { error ->
                when (error) {
                    is UserNotConfirmedException -> {
                        listener.loginWithConfirmationCode()
                    }

                    else -> this.handleExceptions(error)

                }
            }
        )
    }

    fun resendVerificationCode(
        email: String,
        onSuccess: Consumer<AuthCodeDeliveryDetails>,
        onError: Consumer<AuthException>
    ) {
        Auth.resendSignUpCode(email, onSuccess, onError)

    }

    fun signOut(listener: AmplifyListener) {
        Amplify.Auth.signOut { signOutResult ->
            when (signOutResult) {
                is AWSCognitoAuthSignOutResult.CompleteSignOut -> {
                    listener.onSuccess()
                }

                is AWSCognitoAuthSignOutResult.PartialSignOut -> {
                    listener.onFailed()
                }

                is AWSCognitoAuthSignOutResult.FailedSignOut -> {
                    listener.onFailed()
                }
            }
        }
    }

    fun resetPassword(
        email: String,
        onSuccess: Consumer<AuthResetPasswordResult>,
        onError: Consumer<AuthException>
    ) = Auth.resetPassword(email, onSuccess, onError)


    fun confirmResetPassword(
        email: String,
        password: String,
        confirmationCode: String,
        onSuccess: Action,
        onError: Consumer<AuthException>
    ) = Amplify.Auth.confirmResetPassword(email, password, confirmationCode, onSuccess, onError)

    fun Activity.googleLogin( listener: AmplifyListener) {
        Amplify.Auth.signInWithSocialWebUI(
            AuthProvider.google(),
            this,
            {
                it.nextStep.additionalInfo
                if (it.isSignedIn) {
                    listener.onSuccess()
                } else {
                    listener.onFailed()
                }
            },
            {
                when (it) {
                    is SignedInException -> listener.onSuccess()
                    else -> handleExceptions( it)
                }
            }
        )
    }

    fun Activity.facebookLogin(listener: AmplifyListener) {
        Amplify.Auth.signInWithSocialWebUI(
            AuthProvider.facebook(),
            this,
            {
                it.nextStep.additionalInfo
                if (it.isSignedIn) {
                    listener.onSuccess()
                } else {
                    listener.onFailed()
                }
            },
            { when (it) {
                is SignedInException -> listener.onSuccess()
                else -> handleExceptions( it)
            } }
        )

    }


    fun Context.handleExceptions( ex: Exception) {
//        dialog?.close()
        Handler(Looper.getMainLooper()).post(Runnable {
            when (ex) {
                is CustomException -> this.showToast(ex.message.toString())
                is NotAuthorizedException -> this.showToast(Constants.TOAST_INCORRECT_USERNAME_PASSWORD)
                is UsernameExistsException -> this.showToast(Constants.TOAST_USER_ALREADY_EXIST)
                is UserNotFoundException -> this.showToast(Constants.TOAST_EMAIL_NOT_REGISTERED)
                is CodeMismatchException -> this.showToast(Constants.TOAST_INCORRECT_CODE)
                is PasswordResetRequiredException -> this.showToast(Constants.TOAST_PASSWORD_RESET_REQUIRED)
                is InvalidParameterException -> this.showToast(Constants.TOAST_USER_NOT_REGISTERED)
                is LimitExceededException -> this.showToast(Constants.TOAST_LIMIT_EXCEEDED)
                is UserCancelledException -> this.showToast(Constants.TOAST_CANCELLED_SIGN_IN_ATTEMPT)
                is InvalidPasswordException -> this.showToast(Constants.ERROR_FORM_PASSWORD)
                is CodeExpiredException ->this.showToast(Constants.ERROR_CODE_EXPIRED)
                is TimedOutException ->this.showToast(Constants.TOAST_SOMETHING_WENT_WRONG)
                else -> this.showToast(Constants.TOAST_SOMETHING_WENT_WRONG)
            }
        })
    }


    interface AmplifyListener {
        fun loginWithConfirmationCode() {}
        fun onSuccess() {}
        fun onFailed() {}
        fun alreadySignedIn() {}
    }
}