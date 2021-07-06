package com.example.firebasechatapp.example.chatmodule.users

import com.example.firebasechatapp.example.base.BasePresenter
import com.example.firebasechatapp.example.firebasework.DbPresenter

class UserListPresenter<T : UserListView> :BasePresenter<T>() ,DbPresenter{

    override fun onAttach(view: T) {
        super.onAttach(view)
        if(isViewAttach()){
            getBaseView()?.loadScreenView()
        }
    }

    fun loadAllUserList() {
        if(isViewAttach()){
            if (getBaseView()?.isInternetAvailable()!!){
                getBaseView()?.getDbInstance(this)?.getUserList()
            }
            else{
                getBaseView()?.showInternetErrorMessage()
            }
        }
    }
    fun loadAllUsers(user_list: ArrayList<User>){
        if(isViewAttach()){
            if(getBaseView()?.isInternetAvailable()!!){
                getBaseView()?.loadAllUsers(user_list)
            }
            else{
                getBaseView()?.showInternetErrorMessage()
            }
        }
    }
}