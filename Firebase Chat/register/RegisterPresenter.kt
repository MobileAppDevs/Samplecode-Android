package com.example.firebasechatapp.example.chatmodule.register

import com.example.firebasechatapp.example.base.BasePresenter
import com.example.firebasechatapp.example.firebasework.AuthPresenter
import java.lang.Exception

class RegisterPresenter<T : RegisterView> : BasePresenter<T>(), AuthPresenter {

    override fun onAttach(view: T) {
        super.onAttach(view)
        if (isViewAttach())
            getBaseView()?.registerFlow()
    }

    fun registerUser(name: String, email: String, password: String) {
        if (isViewAttach()) {
            if (getBaseView()?.isInternetAvailable()!!) {
                getBaseView()?.initProgressBar(
                    "Registering User",
                    "Please wait while we creating your Account"
                )
                getBaseView()?.progressView()
                getBaseView()?.getAuthenticationInstance(this)?.registerUser(name, email, password)

            } else {
                getBaseView()?.showInternetErrorMessage()
            }
        }
    }

    fun registerSuccess() {
        if (isViewAttach()) {
            getBaseView()?.registerSuccess()
            getBaseView()?.hideProgressView()
        }
    }

    fun registerFailure(exception: Exception) {
        if (isViewAttach()) {
            getBaseView()?.registerFailure(exception)
            getBaseView()?.hideProgressView()
        }

    }
}