package dev.pankaj.cleanarchitecture.extensions

import android.view.View

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
    this.alpha = 0.5f
}