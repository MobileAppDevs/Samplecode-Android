package com.dream.friend.enums

import androidx.annotation.DrawableRes
import com.dream.friend.R
import com.dream.friend.ui.bottomsheet.plan.PlanModel

enum class Plan(
    var planName: String,
    var features: ArrayList<PlanModel>,
    @DrawableRes var bgResource: Int
) {
    DREAM_FRIEND_PLUS(
        planName = "Dream Friend Plus", features = arrayListOf(
            PlanModel(
                imageDrawable = R.drawable.ic_plan_unlimited_likes,
                description = "Get unlimited likes and maximize your chances of finding a match.",
                title = "Unlimited likes"
            ),
            PlanModel(
                imageDrawable = R.drawable.ic_plan_advance_filter,
                description = "Save time by focusing on exactly what you're looking for.",
                title = "Advance filter"
            ),
            PlanModel(
                imageDrawable = R.drawable.ic_plan_unlimited_rewind,
                description = "Skip someone accidentally? Rewind and see the last person again.",
                title = "Unlimited rewinds"
            ),
            PlanModel(
                imageDrawable = R.drawable.ic_plan_no_ads,
                description = "Use the app without interruptions.",
                title = "Browse without ads"
            ),
            PlanModel(
                imageDrawable = R.drawable.ic_plan_swipe,
                description = "Change your location to match with people in any city or country.",
                title = "Swipe around the world"
            ),
        ),
        bgResource = R.drawable.bg_plan_plus
    ),
    DREAM_FRIEND_PREMIUM(
        planName = "Dream Friend Premium", features = arrayListOf(
            PlanModel(
                imageDrawable = R.drawable.ic_plan_see_who_like_you,
                description = "See people who already Like you & match with them instantly!",
                title = "See who like you"
            ),
            PlanModel(
                imageDrawable = R.drawable.ic_daily_super_like,
                description = "Really like someone? Use super likes to let them know and be shown to them first!",
                title = "1 Super like per day"
            ),
            PlanModel(
                imageDrawable = R.drawable.ic_plan_free_boost,
                description = "Be shown ahead of everyone else for 1 hour.",
                title = "1 Free boost per week"
            ),
            PlanModel(
                imageDrawable = R.drawable.ic_plan_incognito,
                description = "Only be shown to people you've liked.",
                title = "Incognito mode"
            ),
            PlanModel(
                imageDrawable = R.drawable.ic_plan_other_premium,
                description = "Unlimited likes, Advance filter and many more!",
                title = "All other premium features"
            ),
        ),
        bgResource = R.drawable.bg_plan_premium
    ),
    SUPER_LIKES(
        planName = "Super Likes", features = arrayListOf(
            PlanModel(
                imageDrawable = R.drawable.ic_daily_super_like,
                description = "Really like someone? Send them a Superlike and you're 9x more likely to match!",
                title = "1 Super like per day"
            ),
        ),
        bgResource = R.drawable.bg_plan_super_likes
    ),
    BOOSTS(
        planName = "Boosts", features = arrayListOf(
            PlanModel(
                imageDrawable = R.drawable.ic_plan_free_boost,
                description = "Be shown ahead of everyone else for 1 hour.",
                title = "1 Free boost per week"
            ),
        ),
        bgResource = R.drawable.bg_plan_plus
    )
}