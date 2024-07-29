package com.nisha.myqrscanner.util

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator

/**
 * Setting up the intent integrator
 * */
fun Activity.setUpScanning() =
    IntentIntegrator(this).apply {
        setOrientationLocked(true)  //prevent screen orientation changes
        setPrompt("Place a barcode or QR code inside the view to scan a code.")  //display prompt message
        setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)  //desired barcode formats to scan
        setBarcodeImageEnabled(true)  //return the image path of the scanned barcode in the result intent
    }

/**
 * start scanning
 * */
fun IntentIntegrator.startScanning() = initiateScan()

/**
 * handle scan results
 * */
fun View.handleScanResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if(resultCode == Activity.RESULT_OK) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if(result != null) {
            Log.e("abcdefghijklmnopqrstuvwxyz", "handleScanResult: content: ${result.contents} imagePath: ${result.barcodeImagePath}")
            Snackbar.make(this, "Scanned: " + result.contents, Snackbar.LENGTH_LONG).show()
        } else {
            Snackbar.make(this, "Result is empty", Snackbar.LENGTH_SHORT).show()
        }
    } else {
        Snackbar.make(this, "Cancelled", Snackbar.LENGTH_SHORT).show()
    }
}