package com.enkefalostechnologies.calendarpro.broadcast_receiver
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.google.firebase.analytics.FirebaseAnalytics

class FirebaseEventBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val action = intent.action
        if (action != null && action == "custom_firebase_event") {
            val mFirebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)
            val eventName = intent.getStringExtra("eventName")
            val imageName = intent.getStringExtra("imageName")
            val fullText = intent.getStringExtra("fullText")
            val eventType = intent.getStringExtra("eventType");
            if (!eventName.isNullOrEmpty() && TextUtils.equals("firebase",eventType)) {
                val params = intent.getBundleExtra("eventParams") ?: Bundle()
                imageName?.let {
                    params.putString("image_name", it)
                }
                fullText?.let {
                    params.putString("full_text", it)
                }
                mFirebaseAnalytics.logEvent(eventName, params)
            }
        }
    }
}