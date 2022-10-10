package com.ongraph.qrscanning

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_qrcode.*

class QRCodeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)

        generate.setOnClickListener {
            ivCode.isVisible = true
            tvCode.isVisible = false
            ivCode.setImageBitmap(
                Utils.generateQR("Hi there, This is my code.", 800, 500)
            )
        }

        scan.setOnClickListener {
            ivCode.isVisible = false
            tvCode.isVisible = true

            startActivityForResult(
                Intent(
                    this,
                    QRScanActivity::class.java
                ),
                101
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK)
            tvCode.text = "${data?.getStringExtra("code")}"
    }
}