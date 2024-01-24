package com.ongraph.whatsappclone.remote.model

data class Chat (
    val message: String? = null,
    val from: Int? = null,
    var isSelected: Boolean = false,
)