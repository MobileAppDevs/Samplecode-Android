package com.nisha.myqrscanner.viewModel

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.nisha.myqrscanner.listeners.HandlerCallbackListener
import com.nisha.myqrscanner.util.handleScanResult
import com.nisha.myqrscanner.util.setUpScanning
import com.nisha.myqrscanner.util.startScanning
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class MainViewModel: ViewModel() {
    private var _responseIntentResult = MutableStateFlow<IntentResult?>(null)
    val responseIntentResult: StateFlow<IntentResult?> = _responseIntentResult

    private var _responseIntentIntegrator = MutableStateFlow<IntentIntegrator?>(null)
    val responseIntentIntegrator: StateFlow<IntentIntegrator?> = _responseIntentIntegrator

    fun invokeScanSetup(activity: Activity) {
        viewModelScope.launch {
            _responseIntentIntegrator.value = activity.setUpScanning()
        }
    }

    fun startScanning(scanner: IntentIntegrator?) {
        viewModelScope.launch {
            scanner?.startScanning()
        }
    }

    fun handleScanResult(view: View, requestCode: Int, resultCode: Int, data: Intent?) {
        viewModelScope.launch {
            view.handleScanResult(requestCode, resultCode, data, object : HandlerCallbackListener {
                override fun onSuccess(data: IntentResult?) {
                    _responseIntentResult.value = data
                }
            })
        }
    }
}