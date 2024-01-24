package com.ongraph.jetpackloginsample.ui.functions

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.ongraph.jetpackloginsample.common.Utils.showToast
import com.ongraph.jetpackloginsample.remote.models.LoginReq
import com.ongraph.jetpackloginsample.ui.viewModel.LoginViewModel

object LoginFunctions {
    fun loginRequest(email: String, password: String, loginViewModel: LoginViewModel?) {
        loginViewModel?.loginUser(
            LoginReq(
                email,
                password,
                "token"
            )
        )
    }

    fun LifecycleOwner.responseLogin(loginViewModel: LoginViewModel?, context: Context) {
        loginViewModel?.loginResponse?.observe(this) {
            it.let {
                if (it.error == false) {
                    Log.d("login-response", it.toString())
                    //saving response
//                    val gson = Gson().toJson(it.data)
//                    val handler = PreferenceHandler(this)
//                    handler.writeString(handler.USER_LOGIN_DATA, gson)
//                    handler.writeBoolean(handler.USER_LOGIN_STATUS, true)
//                    handler.writeString(handler.AUTHORIZATION_TOKEN, it.data?.token!!)

                    //open Home screen
                }
                context.showToast("Success: ${it.message}")
            }
        }
        loginViewModel?.apiError?.removeObservers(this)
        loginViewModel?.apiError?.observe(this) {
            context.showToast("Error: ${it.message}")
        }
        loginViewModel?.onFailure?.removeObservers(this)
        loginViewModel?.onFailure?.observe(this) {
            context.showToast("Failure: ${it.message}")
        }
    }
}