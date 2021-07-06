package com.example.firebasechatapp.example.chatmodule.selectusersforgroupchat

import com.example.firebasechatapp.example.base.BasePresenter
import com.example.firebasechatapp.example.firebasework.DbPresenter

class SelectUserForGroupPresenter<T: SelectUserForGroupView> : BasePresenter<T>() ,DbPresenter{

    override fun onAttach(view: T) {
        super.onAttach(view)
        if(isViewAttach()){
            getBaseView()?.loadScreenView()
        }
    }

}