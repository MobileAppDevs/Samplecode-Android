package com.ongraph.jetpackloginsample.common

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

interface ILog {
    fun logI(message: String)
    fun logD(message: String)
    fun logE(message: String)
}

@Singleton
class Logger @Inject constructor() : ILog {

    companion object {
        const val TAG = "Logger : "
    }

    override fun logI(message: String) {
        Log.i(TAG, message)
    }

    override fun logD(message: String) {
        Log.d(TAG, message)
    }

    override fun logE(message: String) {
        Log.e(TAG, message)
    }
}