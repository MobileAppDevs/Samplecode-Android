package com.ongraph.qrscanning

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

object Utils {
    fun generateQR(str: String?, height: Int, width: Int): Bitmap? {
        return try {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(str, BarcodeFormat.QR_CODE, width, height)
            val w = bitMatrix.width
            val h = bitMatrix.height
            val pixels = IntArray(w * h)
            for (y in 0 until h) {
                for (x in 0 until w) {
                    pixels[y * w + x] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                }
            }
            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h)
            return bitmap
        }catch (e:Exception){
            null
        }
    }
}