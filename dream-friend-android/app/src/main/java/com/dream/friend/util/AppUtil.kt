package com.dream.friend.util

import android.app.Activity
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import java.util.Locale
import java.util.concurrent.TimeUnit

object AppUtil {
     fun getFormattedStopWatch(ms: Long): String {
        var milliseconds = ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        return "${if (hours < 10) "0" else ""}$hours:" +
                "${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (seconds < 10) "0" else ""}$seconds"
    }

  fun Activity.hideStatusBar(){
      window.setFlags(
          WindowManager.LayoutParams.FLAG_FULLSCREEN,
          WindowManager.LayoutParams.FLAG_FULLSCREEN);

  }
    fun Context.getCity(lat: Double, long: Double):String?{
        return try {
            Geocoder(this, Locale.getDefault()).getFromLocation(
                lat,
                long,
                1
            )?.get(0)?.locality
        }catch (e:Exception){
            null
        }
    }

}