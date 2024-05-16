package com.dream.friend.interfaces

import com.dream.friend.data.model.user.User

interface LikeOrDislikeOrBlockProfileListener {
    fun setOnLikeOrDislikeOrBlockProfileListener(
        isLike: Boolean = false,
        isDislike: Boolean = false,
        isReject: Boolean = false,
        isSuperLike:Boolean=false,
        position: Int,
        user: User
    )
    fun onBlockClicked(position: Int)

    fun onClickExtendedProfile(user: User, position: Int)
}