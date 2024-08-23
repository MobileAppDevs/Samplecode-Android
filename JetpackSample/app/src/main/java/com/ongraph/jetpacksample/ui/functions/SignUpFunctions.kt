package com.ongraph.jetpacksample.ui.functions

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.ongraph.jetpacksample.common.PreferenceHandler
import com.ongraph.jetpacksample.common.Utils.showToast
import com.ongraph.jetpacksample.remote.models.RegisterReq
import com.ongraph.jetpacksample.ui.HomeActivity
import com.ongraph.jetpacksample.ui.viewModel.SignUpViewModel

/**
 * sign up functions
 * to handle request and response
 * and redirections
 * */
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
                    //saving response
                    val handler = PreferenceHandler(context)
                    handler.writeBoolean(handler.hasLogin, true)

                    //open Home screen
                    Intent(context, HomeActivity::class.java).apply {
                        context.startActivity(this)
                    }
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