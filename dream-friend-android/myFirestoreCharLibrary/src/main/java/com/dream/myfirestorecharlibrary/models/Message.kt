package com.dream.myfirestorecharlibrary.models

import com.dream.myfirestorecharlibrary.Constants.DATE_FORMAT
import com.dream.myfirestorecharlibrary.Constants.TIME_FORMAT
import com.dream.myfirestorecharlibrary.MessageType
import java.text.SimpleDateFormat

data class Message(
    val message: String? = null,
    val messageBy: String? = null,
    val time: String? = SimpleDateFormat(TIME_FORMAT).format(System.currentTimeMillis()).toString(),
    val date: String? = SimpleDateFormat(DATE_FORMAT).format(System.currentTimeMillis()).toString(),
    var messageType: String = MessageType.TEXT.name,
    var path: String? = null
)