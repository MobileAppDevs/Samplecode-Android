package com.dream.friend.data.model

data class PetModel(
    val name: String,
    val petId: Int,
    var isSelected: Boolean = false
)