package com.nisha.myqrscanner

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.nisha.myqrscanner.databinding.ActivityMainBinding
import com.nisha.myqrscanner.util.handleScanResult
import com.nisha.myqrscanner.util.setUpScanning
import com.nisha.myqrscanner.util.startScanning

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val scanner: IntentIntegrator by lazy { setUpScanning() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.btnScanCode.setOnClickListener {
            scanner.startScanning()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        binding.root.handleScanResult(requestCode, resultCode, data)
    }
}