package com.ongraph.jetpackloginsample.common

import android.content.Context
import android.content.SharedPreferences
import com.ongraph.jetpackloginsample.BuildConfig
import javax.inject.Inject

class PreferenceHandler @Inject constructor(private val context: Context) {

    private val prefName = BuildConfig.APPLICATION_ID
    private val mode = Context.MODE_PRIVATE

    fun writeBoolean(key: String, value: Boolean) {
        getEditor(context).putBoolean(key, value).commit()
    }

    fun readBoolean(key: String, defValue: Boolean): Boolean {
        return getPreferences(context).getBoolean(key, defValue)
    }

    fun readBooleanWithNull(key: String, defValue: Boolean?): Boolean? {
        return if (defValue != null)
            getPreferences(context).getBoolean(key, defValue)
        else
            null
    }

    fun writeInteger(key: String, value: Int) {
        getEditor(context).putInt(key, value).commit()
    }

    fun readInteger(key: String, defValue: Int): Int {
        return getPreferences(context).getInt(key, defValue)
    }

    fun writeString(key: String, value: String) {
        getEditor(context).putString(key, value).commit()
    }

    fun writeStringWithNull(key: String, value: String?) {
        getEditor(context).putString(key, value).commit()
    }

    fun readString(key: String, defValue: String): String {
        return getPreferences(context).getString(key, defValue)!!
    }

    fun readStringWithNull(key: String, defValue: String?): String? {
        return getPreferences(context).getString(key, defValue)
    }

    fun writeFloat(key: String, value: Float) {
        getEditor(context).putFloat(key, value).commit()
    }

    fun readFloat(key: String, defValue: Float): Float {
        return getPreferences(context).getFloat(key, defValue)
    }

    fun writeLong(key: String, value: Long) {
        getEditor(context).putLong(key, value).commit()
    }

    fun readLong(key: String, defValue: Long): Long {
        return getPreferences(context).getLong(key, defValue)
    }

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(prefName, mode)
    }

    private fun getEditor(context: Context): SharedPreferences.Editor {
        return getPreferences(context).edit()
    }

    fun clearSharePreferences(context: Context) {
        val preferences = context.getSharedPreferences(prefName, mode)
        preferences.edit().clear().apply()
    }
}
