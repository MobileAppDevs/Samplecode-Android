package com.dream.friend.data.model

import androidx.annotation.DrawableRes
import com.dream.friend.R

data class MyInterests(
    val interest: String,
    var position: Int = 0,
    @DrawableRes var id: Int = R.drawable.drawable_interest_photography_e94057,
    @DrawableRes var idSelected: Int = R.drawable.drawable_interest_photography_ffffff,
    var isSelected: Boolean = false,
)