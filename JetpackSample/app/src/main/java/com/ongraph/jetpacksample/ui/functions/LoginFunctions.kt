package com.ongraph.jetpacksample.ui.functions

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.ongraph.jetpacksample.common.PreferenceHandler
import com.ongraph.jetpacksample.common.Utils.showToast
import com.ongraph.jetpacksample.remote.models.LoginReq
import com.ongraph.jetpacksample.ui.HomeActivity
import com.ongraph.jetpacksample.ui.viewModel.LoginViewModel

/**
 * sign in functions
 * to handle request and response
 * and redirections
 * */
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
                    val handler = PreferenceHandler(context)
                    handler.writeBoolean(handler.hasLogin, true)

                    //open Home screen
                    Intent(context, HomeActivity::class.java).apply {
                        context.startActivity(this)
                    }
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