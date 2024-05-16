package com.dream.friend

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.dream.friend.App.Companion.set
import com.dream.friend.data.model.FindChatString
import com.dream.friend.data.model.MyNotificationModel
import com.dream.friend.data.model.NotificationModel
import com.dream.friend.data.model.UserDataString
import com.dream.friend.enums.NotificationType
import com.dream.friend.ui.home.HomeActivity
import com.dream.friend.ui.home.activities.CallingActivity
import com.dream.friend.ui.notification.MatchedUserScreen
import com.dream.myfirestorecharlibrary.MessageType
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import java.util.*


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private var tag = MyFirebaseMessagingService::class.java.simpleName
    private val channelId = "dream.friend.notifications"
    private val description = "dream friend notifications"
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(tag, "FirebaseMessagingService: Token $token")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(tag, "FirebaseMessagingService: Token ${remoteMessage.data}")
        val notificationModel =
            Gson().fromJson(Gson().toJson(remoteMessage.data), NotificationModel::class.java)
        when (notificationModel.notificationType) {
            NotificationType.WHO_LIKE_YOU_NOTIFICATION.value -> {
                whoLikeYouNotification(notificationModel, remoteMessage, this)
            }

            NotificationType.MATCH_NOTIFICATION.value -> {
                matchNotification(notificationModel, remoteMessage, this)
            }

            NotificationType.FIRST_TIME_LIKE_NOTIFICATION.value -> {
                firstTimeLikeNotification(notificationModel, remoteMessage, this)
            }

            NotificationType.FIRST_TIME_SUPER_LIKE_NOTIFICATION.value -> {
                firstTimeSuperLikeNotification(notificationModel, remoteMessage, this)
            }

            NotificationType.REAL_TIME_IMAGE_VERIFIED_NOTIFICATION.value -> {
                realtimeImageVerifiedNotification(notificationModel, remoteMessage,this)
            }

            NotificationType.REAL_TIME_IMAGE_PROCESSING_NOTIFICATION.value -> {
                realtimeImageProcessingNotification(notificationModel, remoteMessage,this)
            }

            NotificationType.REAL_TIME_IMAGE_REJECTED_NOTIFICATION.value -> {
                realtimeImageRejectedNotification(notificationModel, remoteMessage,this)
            }

            else -> {
                otherNotification(notificationModel, remoteMessage, this)
            }
        }

    }

    private fun whoLikeYouNotification(
        notificationModel: NotificationModel,
        remoteMessage: RemoteMessage,
        context: Context
    ) {

        val pendingIntent =
            NavDeepLinkBuilder(context)
                .setComponentName(HomeActivity::class.java)
                .setGraph(R.navigation.navigation)
                .setDestination(R.id.navigation_likes)
                .setArguments(Bundle())
                .createPendingIntent()


        lateinit var notificationChannel: NotificationChannel
        lateinit var builder: Notification.Builder
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val collapsedView = RemoteViews(packageName, R.layout.notification_first_time_like_layout)
       val msg="Some one liked you, click here to see.."

        // checking if android version is greater than oreo(API 26) or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, channelId)
                .setSmallIcon(R.drawable.first_time_like)
                .setShowWhen(false)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(collapsedView)
                .setContentIntent(pendingIntent)
        } else {

            builder = Notification.Builder(this)
                .setSmallIcon(R.drawable.first_time_like)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        this.resources,
                        R.drawable.first_time_like
                    )
                )
                .setContentTitle(msg)
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(
            NotificationType.WHO_LIKE_YOU_NOTIFICATION.value,
            builder.build()
        )
    }

    private fun matchNotification(
        notificationModel: NotificationModel,
        remoteMessage: RemoteMessage,
        context: Context
    ) {
        val i = Intent(context, MatchedUserScreen::class.java)
        i.apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("data",notificationModel.matchUserDataString)
            App.play()
        }
        context.startActivity(i)
    }

    private fun firstTimeLikeNotification(
        notificationModel: NotificationModel,
        remoteMessage: RemoteMessage, context: Context
    ) {
        val pendingIntent =
            NavDeepLinkBuilder(context)
                .setComponentName(HomeActivity::class.java)
                .setGraph(R.navigation.navigation)
                .setDestination(R.id.navigation_home)
                .setArguments(Bundle())
                .createPendingIntent()

        lateinit var notificationChannel: NotificationChannel
        lateinit var builder: Notification.Builder
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val collapsedView = RemoteViews(packageName, R.layout.notification_first_time_like_layout)
        val msg="\"Great job - we’ve got a sense of what you like. We’ll keep learning as you swipe!\""
        collapsedView.setTextViewText(R.id.text_view,msg)
        // checking if android version is greater than oreo(API 26) or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, channelId)
                .setSmallIcon(R.drawable.first_time_like)
                .setShowWhen(false)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(collapsedView)
                .setContentIntent(pendingIntent)
        } else {

            builder = Notification.Builder(this)
                .setSmallIcon(R.drawable.first_time_like)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        this.resources,
                        R.drawable.first_time_like
                    )
                )
                .setContentTitle(msg)
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(
            NotificationType.WHO_LIKE_YOU_NOTIFICATION.value,
            builder.build()
        )
    }

    private fun firstTimeSuperLikeNotification(
        notificationModel: NotificationModel,
        remoteMessage: RemoteMessage,
        context: Context
    ) {
        val collapsedView = RemoteViews(packageName, R.layout.notification_first_time_like_layout)
        collapsedView.setImageViewResource(R.id.image_view,R.drawable.first_time_like)

        val pendingIntent =
            NavDeepLinkBuilder(context)
                .setComponentName(HomeActivity::class.java)
                .setGraph(R.navigation.navigation)
                .setDestination(R.id.navigation_home)
                .setArguments(Bundle())
                .createPendingIntent()

        lateinit var notificationChannel: NotificationChannel
        lateinit var builder: Notification.Builder
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val msg="Awesome. You just sent Leslie a Crush."
        collapsedView.setTextViewText(R.id.text_view,msg)
        // checking if android version is greater than oreo(API 26) or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, channelId)
                .setSmallIcon(R.drawable.first_time_like)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(collapsedView)
                .setContentIntent(pendingIntent)
        } else {

            builder = Notification.Builder(this)
                .setSmallIcon(R.drawable.first_time_like)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        this.resources,
                        R.drawable.first_time_like
                    )
                )
                .setContentTitle(msg)
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(
            NotificationType.WHO_LIKE_YOU_NOTIFICATION.value,
            builder.build()
        )
    }

    private fun realtimeImageVerifiedNotification(
        notificationModel: NotificationModel,
        remoteMessage: RemoteMessage,
        context:Context
    ) {
        val collapsedView = RemoteViews(packageName, R.layout.notification_first_time_like_layout)
           collapsedView.setImageViewResource(R.id.image_view,R.drawable.ic_new_realtime_image_verified)

        val pendingIntent =
            NavDeepLinkBuilder(context)
                .setComponentName(HomeActivity::class.java)
                .setGraph(R.navigation.navigation)
                .setDestination(R.id.navigation_home)
                .setArguments(Bundle())
                .createPendingIntent()

        lateinit var notificationChannel: NotificationChannel
        lateinit var builder: Notification.Builder
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val msg="Congratulations Your photo has been successfully verified."
        collapsedView.setTextViewText(R.id.text_view,msg)
        // checking if android version is greater than oreo(API 26) or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_new_realtime_image_verified)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(collapsedView)
                .setContentIntent(pendingIntent)
        } else {

            builder = Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_new_realtime_image_verified)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        this.resources,
                        R.drawable.ic_new_realtime_image_verified
                    )
                )
                .setContentTitle(msg)
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(
            NotificationType.WHO_LIKE_YOU_NOTIFICATION.value,
            builder.build()
        )
    }

    private fun realtimeImageProcessingNotification(
        notificationModel: NotificationModel,
        remoteMessage: RemoteMessage,
        context:Context
    ) {
        val collapsedView = RemoteViews(packageName, R.layout.notification_first_time_like_layout)
        collapsedView.setImageViewResource(R.id.image_view,R.drawable.ic_new_realtime_image_processing)

        val pendingIntent =
            NavDeepLinkBuilder(context)
                .setComponentName(HomeActivity::class.java)
                .setGraph(R.navigation.navigation)
                .setDestination(R.id.navigation_home)
                .setArguments(Bundle())
                .createPendingIntent()

        lateinit var notificationChannel: NotificationChannel
        lateinit var builder: Notification.Builder
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val msg="We’ve got your photo, we’ll get back to you soon!"
        collapsedView.setTextViewText(R.id.text_view,msg)
        // checking if android version is greater than oreo(API 26) or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, channelId)
                .setSmallIcon( R.drawable.ic_new_realtime_image_processing)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(collapsedView)
                .setContentIntent(pendingIntent)
        } else {

            builder = Notification.Builder(this)
                .setSmallIcon( R.drawable.ic_new_realtime_image_processing)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        this.resources,
                        R.drawable.ic_new_realtime_image_processing
                    )
                )
                .setContentTitle(msg)
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(
            NotificationType.WHO_LIKE_YOU_NOTIFICATION.value,
            builder.build()
        )
    }

    private fun realtimeImageRejectedNotification(
        notificationModel: NotificationModel,
        remoteMessage: RemoteMessage,
        context: Context
    ) {
        val collapsedView = RemoteViews(packageName, R.layout.notification_first_time_like_layout)
        collapsedView.setImageViewResource(R.id.image_view,R.drawable.ic_realtime_image_rejected_notification)
        val pendingIntent =
            NavDeepLinkBuilder(context =context )
                .setComponentName(HomeActivity::class.java)
                .setGraph(R.navigation.navigation)
                .setDestination(R.id.navigation_home)
                .setArguments(Bundle())
                .createPendingIntent()

        lateinit var notificationChannel: NotificationChannel
        lateinit var builder: Notification.Builder
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val msg="Your photo wasn’t approved. Please submit a new photo to try again."
        collapsedView.setTextViewText(R.id.text_view,msg)
        // checking if android version is greater than oreo(API 26) or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_realtime_image_rejected_notification)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(collapsedView)
                .setContentIntent(pendingIntent)
        } else {

            builder = Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_realtime_image_rejected_notification)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        this.resources,
                        R.drawable.ic_realtime_image_rejected_notification
                    )
                )
                .setContentTitle(msg)
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(
            NotificationType.WHO_LIKE_YOU_NOTIFICATION.value,
            builder.build()
        )
    }


    private fun otherNotification(
        notificationModel: NotificationModel,
        remoteMessage: RemoteMessage,
        context: Context
    ) {
        val chatDetail =
            Gson().fromJson(notificationModel.findChatString, FindChatString::class.java)
        val userDetail =
            Gson().fromJson(notificationModel.userDataString, UserDataString::class.java)

        if (notificationModel.isDecline == true || notificationModel.isDecline == false) {
            App.stop()
            val notifyManager =
                context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notifyManager.cancelAll()
            App.userCallDeclineObserver.postValue(true)
        } else {
            App.userCallDeclineObserver.postValue(false)

            val myModel = MyNotificationModel(
                notificationModel.body,
                notificationModel.body,
                userDetail,
                chatDetail,
                notificationModel.clickAction,
                notificationModel.icon
            )

            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            val builder: NotificationCompat.Builder

            val bundle = Bundle().apply {
                putString("details", Gson().toJson(myModel))
            }
            val pendingIntent =
                NavDeepLinkBuilder(context)
                    .setComponentName(HomeActivity::class.java)
                    .setGraph(R.navigation.navigation)
                    .setDestination(R.id.navigation_home)
                    .setArguments(bundle)
                    .createPendingIntent()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(
                    "channelId",
                    "description",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationChannel.enableLights(true)
                notificationChannel.enableVibration(false)
                notificationManager.createNotificationChannel(notificationChannel)

                builder = NotificationCompat.Builder(context, "channelId")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setLargeIcon(
                        BitmapFactory.decodeResource(
                            context.resources,
                            R.drawable.ic_launcher_background
                        )
                    )
                    .setContentText(remoteMessage.data["body"])
                    .setAutoCancel(true)
                builder.setContentIntent(pendingIntent)

//                val resultIntent = Intent(this, MatchedUserScreen::class.java)
//                val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
//                    addNextIntentWithParentStack(resultIntent)
//                    getPendingIntent(
//                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//                    )
//                }
//                builder.setContentIntent(resultPendingIntent)


                if (chatDetail != null && (chatDetail.type.lowercase(Locale.getDefault()) == MessageType.TEXT.name.lowercase(
                        Locale.getDefault()
                    ) ||
                            chatDetail.type.lowercase(Locale.getDefault()) == MessageType.IMAGE.name.lowercase(
                        Locale.getDefault()
                    ) ||
                            chatDetail.type.lowercase(Locale.getDefault()) == MessageType.TEXTANDIMAGE.name.lowercase(
                        Locale.getDefault()
                    ))
                )
                    builder.setContentTitle(remoteMessage.data["title"])

                if (!App.IN_COMING_CALL)
                    builder.setContentIntent(pendingIntent)
            } else {

                builder = NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setLargeIcon(
                        BitmapFactory.decodeResource(
                            context.resources,
                            R.drawable.ic_launcher_background
                        )
                    )
                    .setContentText(remoteMessage.data["body"])
                    .setAutoCancel(true)
                builder.setContentIntent(pendingIntent)

                if (chatDetail.type.lowercase(Locale.getDefault()) == MessageType.TEXT.name.lowercase(
                        Locale.getDefault()
                    ) ||
                    chatDetail.type.lowercase(Locale.getDefault()) == MessageType.IMAGE.name.lowercase(
                        Locale.getDefault()
                    ) ||
                    chatDetail.type.lowercase(Locale.getDefault()) == MessageType.TEXTANDIMAGE.name.lowercase(
                        Locale.getDefault()
                    )
                )
                    builder.setContentTitle(remoteMessage.data["title"])

                if (!App.IN_COMING_CALL)
                    builder.setContentIntent(pendingIntent)
            }

            context.applicationContext.set()
            if (chatDetail != null)
                if (App.flag && !App.IN_COMING_CALL && (
                            chatDetail.type.lowercase(Locale.getDefault()) == MessageType.VIDEO.name.lowercase(
                                Locale.getDefault()
                            ) ||
                                    chatDetail.type.lowercase(Locale.getDefault()) == MessageType.AUDIO.name.lowercase(
                                Locale.getDefault()
                            )
                            )
                ) {
                    val i = Intent(context, CallingActivity::class.java)
                    i.apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        putExtras(bundle)
                        App.play()

                    }
                    context.startActivity(i)
                } else {
                    if (!App.IN_COMING_CALL)
                        if (chatDetail.type.lowercase(Locale.getDefault()) == MessageType.VIDEO.name.lowercase(
                                Locale.getDefault()
                            ) ||
                            chatDetail.type.lowercase(Locale.getDefault()) == MessageType.AUDIO.name.lowercase(
                                Locale.getDefault()
                            )
                        ) {
                            App.play()
                        }
                    notificationManager.notify(1234, builder.build())
                }
        }
    }
}