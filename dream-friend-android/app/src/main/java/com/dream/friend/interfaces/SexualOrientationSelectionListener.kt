package com.dream.friend.interfaces

import com.dream.friend.data.model.interests.sexual.SexualOrientation

interface SexualOrientationSelectionListener {
    fun onSelectionSexualOrientation(interest: SexualOrientation, isSelected: Boolean)
}