package com.ongraphtechnologies.mileequilizer

import android.app.*
import android.content.Intent
import com.ongraphtechnologies.mileequilizer.ForegroundService
import com.ongraphtechnologies.mileequilizer.MainActivity
import androidx.core.app.NotificationCompat
import com.ongraphtechnologies.mileequilizer.R
import android.os.Build
import android.os.IBinder
import android.util.Log

class ForegroundService : Service() {
    var CHANNEL_ID = "myChannel"
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.action == Constants.ACTION.STARTFOREGROUND_ACTION) {
            Log.i(LOG_TAG, "Received Start Foreground Intent ")
            val notificationIntent = Intent(this, MainActivity::class.java)
            notificationIntent.action = Constants.ACTION.MAIN_ACTION
            notificationIntent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK
                    or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            val pendingIntent = PendingIntent.getActivity(
                this, 0,
                notificationIntent, 0
            )
            val notification: Notification =
                NotificationCompat.Builder(this, CHANNEL_ID).setSmallIcon(R.drawable.eq_icon)
                    .setContentTitle("Equalizer")
                    .setContentText("Equilizer is enabled")
                    .setContentIntent(pendingIntent).setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setOngoing(true)
                    .build()
            startForeground(
                Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                notification
            )
            Log.i("WOW", "BUILDED NOTIFICATION ")
        } else if (intent.action ==
            Constants.ACTION.STOPFOREGROUND_ACTION
        ) {
            Log.i(LOG_TAG, "Received Stop Foreground Intent")
            stopForeground(true)
            stopSelf()
        }
        return START_REDELIVER_INTENT
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Equilizer"
            val description = "Equilizer is enabled"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(LOG_TAG, "In onDestroy")
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        private const val LOG_TAG = "ForegroundService"
    }
}