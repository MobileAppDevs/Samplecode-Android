package com.dream.friend.common

import android.content.res.Resources
import android.graphics.Color
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.dream.friend.R

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.enable() {
    this.isEnabled = true
    this.alpha = 1.0f
}

fun View.disable() {
    this.isEnabled = false
    this.alpha = 1.0f
}

fun View.enabled() {
    this.isEnabled = true
    this.alpha = 0.5f
}

fun View.disabled() {
    this.isEnabled = false
    this.alpha = 0.5f
}

fun View.transparent() {
    this.setBackgroundColor(Color.TRANSPARENT)
}

fun TextView.blackText() {
    this.setTextColor(Color.BLACK)
}

fun TextView.whiteText() {
    this.setTextColor(Color.WHITE)
}

fun TextView.textColor(@ColorRes id: Int) {
    this.setTextColor(resources.getColor(id, null))
}

fun TextView.fontFamily(@FontRes id: Int) {
    this.typeface = ResourcesCompat.getFont(this.context, id)
}

fun TextView.setBackground(@DrawableRes id: Int) {
    this.background = ContextCompat.getDrawable(this.context, id)
}

fun View.startRotationAnimation() {
    val animation: Animation = AnimationUtils.loadAnimation(context, R.anim.rotation)
    startAnimation(animation)
}

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()