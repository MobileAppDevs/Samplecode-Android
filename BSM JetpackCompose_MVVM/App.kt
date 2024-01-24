package com.ongraph.jetpackloginsample

import android.app.Application
import android.content.Context

class App: Application(){

    companion object AppContext {
        lateinit var instance: Application

        fun getContext(): Context {
            return instance
        }
    }

    init {
        instance = this
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }
}