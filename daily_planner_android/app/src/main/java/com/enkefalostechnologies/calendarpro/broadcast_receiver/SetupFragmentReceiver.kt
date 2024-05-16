package com.enkefalostechnologies.calendarpro.broadcast_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.calldorado.Calldorado.setAftercallCustomView
import com.enkefalostechnologies.calendarpro.ui.calldorado.AfterCallCustomView


class SetupFragmentReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.PHONE_STATE") {
            setAftercallCustomView(context,AfterCallCustomView(context))
        }
    }
}