package com.dream.friend.interfaces

interface AddImageClickListener {
    fun setOnAddImageItemListener()
    fun setOnImageItemDeleteListener(item: String)
    fun setOnClickListener(position: Int)
}

interface PhotoGuideLineListener{
    fun onUploadBtnClicked()
}