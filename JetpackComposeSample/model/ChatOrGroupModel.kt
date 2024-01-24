package com.ongraph.whatsappclone.model

import java.time.Instant
import java.time.LocalDateTime


data class ChatOrGroupModel(
    var title:String,
    var lastMessage:String,
    var isGroup:Boolean?=false,
    var dpUrl:String="",
    var time:String=""
)
