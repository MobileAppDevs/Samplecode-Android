package com.ongraph.jetpackloginsample.ui.functions

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.ongraph.jetpackloginsample.common.Utils.showToast
import com.ongraph.jetpackloginsample.remote.models.RegisterReq
import com.ongraph.jetpackloginsample.ui.viewModel.SignUpViewModel

object SignUpFunctions {
    fun signUpRequest(firstname: String, lastname: String, email: String, password: String, signUpViewModel: SignUpViewModel?) {
        signUpViewModel?.registerUser(
            RegisterReq(
                firstname,
                lastname,
                email,
                password
            )
        )
    }

    fun LifecycleOwner.responseSignUp(signUpViewModel: SignUpViewModel?, context: Context) {
        signUpViewModel?.registerResponse?.removeObservers(this)
        signUpViewModel?.registerResponse?.observe(this) {
            it.let {
                if (it.error == false) {
                    Log.d("register-response", it.toString())

                    /*val gson = Gson().toJson(it.data)
                    val handler = PreferenceHandler(this)
                    handler.writeString(handler.USER_LOGIN_DATA, gson)
                    handler.writeBoolean(handler.USER_LOGIN_STATUS, true)
                    handler.writeString(handler.AUTHORIZATION_TOKEN, it.data?.token!!)*/

                    //open Home screen
                    context.showToast("Success: ${it.message}")
                }
            }
        }
        signUpViewModel?.apiError?.removeObservers(this)
        signUpViewModel?.apiError?.observe(this) {
            context.showToast("ApiError: ${it.message}")
        }
        signUpViewModel?.onFailure?.removeObservers(this)
        signUpViewModel?.onFailure?.observe(this) {
            context.showToast("Failure: ${it.message}")
        }
    }
}