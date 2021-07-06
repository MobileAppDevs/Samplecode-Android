package com.example.firebasechatapp.example.chatmodule.creategroup

import com.example.firebasechatapp.example.base.BasePresenter
import com.example.firebasechatapp.example.firebasework.DbPresenter
import com.example.firebasechatapp.example.chatmodule.users.User

class CreateGroupPresenter<T : CreateGroupView> : BasePresenter<T>(), DbPresenter {

    override fun onAttach(view: T) {
        super.onAttach(view)
        if (isViewAttach()) {
            getBaseView()?.loadScreenView()
        }
    }

    fun createGroup(uuid: String, groupName: String, selectedUserList: MutableList<User>) {
        if (isViewAttach()) {
            if (getBaseView()?.isInternetAvailable()!!) {
                getBaseView()?.initProgressBar(
                    "Group Creating",
                    "Please wait while group is creating"
                )
                getBaseView()?.progressView()
                getBaseView()?.getDbInstance(this)?.createGroup(uuid, groupName, selectedUserList)
            } else {
                getBaseView()?.showInternetErrorMessage()
            }
        }
    }

    fun groupCreateSuccess() {
        if (isViewAttach()) {
            getBaseView()?.groupCreateSuccess()
            getBaseView()?.hideProgressView()

        }
    }

    fun groupCreateFailure() {
        if (isViewAttach()) {
            getBaseView()?.groupCreateFailure()
            getBaseView()?.hideProgressView()
        }
    }


}