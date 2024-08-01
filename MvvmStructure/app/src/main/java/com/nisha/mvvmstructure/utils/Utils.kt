package com.nisha.mvvmstructure.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.google.android.material.snackbar.Snackbar
import com.nisha.mvvmstructure.R

object Utils {

    fun Context.progressDialog(): Dialog {
        val dialog = Dialog(this)
        val inflate = LayoutInflater.from(this).inflate(R.layout.progress_dialog, null, false)
        dialog.setContentView(inflate)
        dialog.window?.setLayout(250, 250)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        return dialog
    }

    /**
    * View Extensions
    * */

    fun View.hide() { this.visibility = View.GONE }

    fun View.show() { this.visibility = View.VISIBLE }

    fun View.invisible() { this.visibility = View.INVISIBLE }

    fun View.enable() {
        this.isEnabled = true
        this.alpha = 1.0f
    }

    fun View.disable() {
        this.isEnabled = false
        this.alpha = 0.5f
    }

    fun View.enabled() {
        this.isEnabled = true
        this.alpha = 0.5f
    }

    fun Context.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    fun View.snackBar(msg: String) = Snackbar.make(this, msg, Snackbar.LENGTH_SHORT).show()

    /**
     * Loading image with glide
     * */

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun loadImage(imageView: ImageView, imageUrl: String?) {
        Glide.with(imageView.context)
            .load(imageUrl)
            .placeholder(R.mipmap.ic_launcher)
            .signature(ObjectKey(imageUrl.toString()))
            .error(R.mipmap.ic_launcher)
            .into(imageView)
    }
}

