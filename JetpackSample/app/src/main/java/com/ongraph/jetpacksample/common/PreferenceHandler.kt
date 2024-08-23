package com.ongraph.jetpacksample.common

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class PreferenceHandler @Inject constructor(private val context: Context) {

    private val prefName = "APPLICATION_ID"
    private val mode = Context.MODE_PRIVATE
    val hasLogin = "HAS_LOGGED_IN"

    fun writeBoolean(key: String, value: Boolean) {
        getEditor(context).putBoolean(key, value).commit()
    }

    fun readBoolean(key: String, defValue: Boolean): Boolean {
        return getPreferences(context).getBoolean(key, defValue)
    }

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(prefName, mode)
    }

    private fun getEditor(context: Context): SharedPreferences.Editor {
        return getPreferences(context).edit()
    }
}
