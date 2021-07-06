package com.example.firebasechatapp.example.chatmodule.chat

import com.example.firebasechatapp.example.base.BasePresenter
import com.example.firebasechatapp.example.firebasework.DbPresenter
import java.lang.Exception

class ChatPresenter<T : ChatView> : BasePresenter<T>(), DbPresenter {

    override fun onAttach(view: T) {
        super.onAttach(view)
        if (isViewAttach()) {
            getBaseView()?.loadScreenView()
        }
    }

    fun getAnotherOnlineStatus(mChatUserId: String) {
        if (isViewAttach()) {
            if (getBaseView()?.isInternetAvailable()!!) {
                getBaseView()?.getDbInstance(this)?.getAnotherUserOnlineStatus(mChatUserId)
            } else {
                getBaseView()?.showInternetErrorMessage()
            }
        }
    }

    fun loadOnlineStatus(onlineStatus: Boolean) {
        if (isViewAttach()) {
            getBaseView()?.setAnotherUserOnlineStatus(onlineStatus)
        }
    }

    fun setCurrentUserOfflineStatus() {
        if (isViewAttach()) {
            if (getBaseView()?.isInternetAvailable()!!) {
                getBaseView()?.getDbInstance(this)?.setCurrentUserOfflineStatus()
            } else {
                getBaseView()?.showInternetErrorMessage()
            }
        }
    }

    fun setCurrentUserOnlineStatus() {
        if (isViewAttach()) {
            if (getBaseView()?.isInternetAvailable()!!) {
                getBaseView()?.getDbInstance(this)?.setCurrentUserOnlineStatus()
            } else {
                getBaseView()?.showInternetErrorMessage()
            }
        }
    }

    fun retrieveMessagesFromDb(
        mChatUserId: String,
        group: Boolean
    ) {
        if (isViewAttach()) {
            if (getBaseView()?.isInternetAvailable()!!) {
                getBaseView()?.getDbInstance(this)?.retrieveMessagesFromDb(mChatUserId, group)
            } else {
                getBaseView()?.showInternetErrorMessage()
            }
        }
    }

    fun loadAllMessages(userConversation: Message) {
        if (isViewAttach()) {
            getBaseView()?.loadUsersConversation(userConversation)
        }
    }

    fun setDeleteOnMessageFlag(
        checkIsDeleteOrNot: Message,
        mChatUserId: String,
        group: Boolean,
        position: Int
    ) {
        if (isViewAttach()) {
            if (getBaseView()?.isInternetAvailable()!!) {
                getBaseView()?.getDbInstance(this)
                    ?.setDeleteOnMessageFlag(checkIsDeleteOrNot, mChatUserId, group, position)
            } else {
                getBaseView()?.showInternetErrorMessage()
            }
        }
    }

    fun messageDeleteSuccess(position: Int) {
        if (isViewAttach())
            getBaseView()?.messageDeleteSuccess(position)
    }

    fun messageDeleteFailure(position: Int) {
        if (isViewAttach())
            getBaseView()?.messageDeleteFailure(position)
    }

    fun sendMessage(
        message: String,
        mChatUserId: String,
        mChatUserName: String,
        group: Boolean,
        type: String,
        currentTimeMillis: Long,
    ) {
        if (getBaseView()?.isInternetAvailable()!!) {
            getBaseView()?.getDbInstance(this)
                ?.sendMessage(message, mChatUserId, mChatUserName, group, type, currentTimeMillis)
        } else {
            getBaseView()?.showInternetErrorMessage()
        }
    }

    fun uploadImage(
        imageUri: Message
    ) {
        if (isViewAttach()) {
            if (getBaseView()?.isInternetAvailable()!!) {
                if (imageUri != null) {
                    getBaseView()?.getDbInstance(this)?.uploadImage(imageUri)
                }
            } else {
                getBaseView()?.showInternetErrorMessage()
            }
        }

    }

    fun uploadVideo(message: Message) {
        if (isViewAttach()) {
            if (getBaseView()?.isInternetAvailable()!!) {
                if (message != null) {
                    getBaseView()?.getDbInstance(this)?.uploadVideo(message)
                }
            } else {
                getBaseView()?.showInternetErrorMessage()
            }
        }
    }

    fun uploadImageSuccess(uploadimageurl: String, timestamp: Long) {
        if (isViewAttach()) {
            getBaseView()?.uploadImageSuccess(uploadimageurl, timestamp)
        }
    }

    fun uploadImageFailure(exception: Exception) {
        if (isViewAttach()) {
            getBaseView()?.uploadImageFailure(exception)
        }
    }


    fun uploadVideoSuccess(uploadVideoUrl: String, timestamp: Long) {
        if (isViewAttach()) {
            getBaseView()?.uploadVideoSuccess(uploadVideoUrl, timestamp)
        }
    }

    fun uploadVideoFailure(exception: Exception) {
        if (isViewAttach()) {
            getBaseView()?.uploadVideoFailure(exception)
        }
    }


}