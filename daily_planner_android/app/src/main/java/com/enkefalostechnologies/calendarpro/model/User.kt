package com.enkefalostechnologies.calendarpro.model

import android.devicelock.DeviceId
import com.amplifyframework.datastore.generated.model.SubscriptionType

data class User(
    var userId: String,
    var name: String,
    var email: String,
    var picUrl: String,
    var deviceId: String="",
    var isSocialLoggedIn: Boolean = false,
    var isSubscriptionPurchased: Boolean = false,
    var isEmailVerified: Boolean = false,
    var isNotificationEnabled: Boolean = true,
    var isReminderEnabled: Boolean = true,
    var subscriptionType: SubscriptionType = SubscriptionType.NONE
)