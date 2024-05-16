package com.dream.friend.interfaces

import com.dream.friend.data.model.interests.your_interest.YourInterest

interface InterestsSelectionListener {
    fun onSelectionInterests(interest: YourInterest, isSelected: Boolean)
}