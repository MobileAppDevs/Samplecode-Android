package com.nisha.myqrscanner.listeners

import com.google.zxing.integration.android.IntentResult

interface HandlerCallbackListener {
    fun onSuccess(data: IntentResult?)
}