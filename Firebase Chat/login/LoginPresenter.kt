package com.example.firebasechatapp.example.chatmodule.login

import com.example.firebasechatapp.example.base.BasePresenter
import com.example.firebasechatapp.example.firebasework.AuthPresenter

class LoginPresenter<T : LoginView> : BasePresenter<T>(), AuthPresenter {

    override fun onAttach(view: T) {
        super.onAttach(view)
        if (isViewAttach()) {
            getBaseView()?.loginFlowStart()
        }
    }

    fun loginUser(email: String, password: String) {
        if (isViewAttach()) {
            if (getBaseView()?.isInternetAvailable()!!) {
                getBaseView()?.initProgressBar(
                    "Logging In",
                    "Please wait while we check your credentials."
                )
                getBaseView()?.progressView()
                getBaseView()?.getAuthenticationInstance(this)?.loginUser(email, password)
            } else {
                getBaseView()?.showInternetErrorMessage()
            }
        }
    }

    fun loginSuccess() {
        if (isViewAttach()) {
            getBaseView()?.loginSuccess()
            getBaseView()?.hideProgressView()
        }

    }

    fun loginFailure(exception: String?) {
        if (isViewAttach()) {
            getBaseView()?.loginFailure(exception)
            getBaseView()?.hideProgressView()
        }

    }


}