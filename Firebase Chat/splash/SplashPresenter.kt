package com.example.firebasechatapp.example.chatmodule.splash

import com.example.firebasechatapp.example.base.BasePresenter

class SplashPresenter<T : SplashView> : BasePresenter<T>() {

    override fun onAttach(view: T) {
        super.onAttach(view)
        if(isViewAttach()){
            getBaseView()?.loadScreenView()
        }
    }
}