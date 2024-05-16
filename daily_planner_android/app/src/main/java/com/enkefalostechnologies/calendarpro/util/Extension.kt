package com.enkefalostechnologies.calendarpro.util

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.generated.model.ReminderEnum
import com.amplifyframework.datastore.generated.model.Tasks
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.util.AppUtil.showToast
import com.enkefalostechnologies.calendarpro.util.Extension.formatDateToDDMMYYHHMMSSA
import com.enkefalostechnologies.calendarpro.util.Extension.setImageFromUrl
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.math.abs

object Extension {
    fun View.invisible() {
        this.visibility = View.INVISIBLE
    }

    fun View.gone() {
        this.visibility = View.GONE
    }

    fun View.visible() {
        this.visibility = View.VISIBLE
    }

    fun ImageView.setImageFromUrl(
        url: String,
        @DrawableRes placeholder: Int = R.drawable.placeholder_img
    ) {
        val circularProgressDrawable = CircularProgressDrawable(this.context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 200f
        circularProgressDrawable.backgroundColor = R.color.color_bg
        circularProgressDrawable.start()

        Glide.with(this.context).load(url)
            .placeholder(circularProgressDrawable)
            .error(placeholder).into(this)
    }

    fun ImageView.setSvgImageUrl(
        url: String,
        @DrawableRes placeholder: Int = R.drawable.placeholder_img
    ) {
        val circularProgressDrawable = CircularProgressDrawable(this.context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 200f
        circularProgressDrawable.backgroundColor = R.color.color_bg
        circularProgressDrawable.start()

//        Glide.with(this.context).load(url)
//            .diskCacheStrategy( DiskCacheStrategy.ALL )
//            .placeholder(circularProgressDrawable)
//            .error(placeholder).into(this)
    }

    fun TextView.setSpannable(text: String) {
        val ss = SpannableStringBuilder(this.text.toString())

        val text1 = text
        ss.setSpan(
            null, ss.toString().indexOf(text1),
            ss.toString().indexOf(text1) + text1.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        ss.setSpan(
            CustomTypefaceSpan(text1, ResourcesCompat.getFont(this.context, R.font.inter_bold)),
            ss.toString().indexOf(text1),
            ss.toString().indexOf(text1) + text1.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        ss.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.color_25282B)),
            ss.toString().indexOf(text1),
            ss.toString().indexOf(text1) + text1.length,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )


        this.text = ss
        this.movementMethod = LinkMovementMethod.getInstance()
    }

    fun getCurrentIso8601Date(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val currentDateTime = Date(System.currentTimeMillis())
        return dateFormat.format(currentDateTime)
    }

    fun Date.dateToTemporalDate(): Temporal.Date {
        val date = this
        val offsetMillis = TimeZone.getDefault().getOffset(date.time).toLong()
        val offsetSeconds = TimeUnit.MILLISECONDS.toSeconds(offsetMillis).toInt()
        return Temporal.Date(date, offsetSeconds)
    }

    fun Date.dateToTemporalDateTime(): Temporal.DateTime {
        val date = this
        val offsetMillis = TimeZone.getDefault().getOffset(date.time).toLong()
        val offsetSeconds = TimeUnit.MILLISECONDS.toSeconds(offsetMillis).toInt()
        return Temporal.DateTime(date, offsetSeconds)
    }

    fun Date.getNextDayFromDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.time = this
        calendar.add(Calendar.DAY_OF_MONTH, 1) // Add one day to the current date
        return calendar.time
    }

    fun Date.getDayAfter(dayCount: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = this
        calendar.add(Calendar.DAY_OF_MONTH, dayCount) // Add one day to the current date
        return calendar.time
    }

    fun Date.getMonthAfter(monthCount: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = this
        calendar.add(Calendar.MONTH, monthCount) // Add one day to the current date
        return calendar.time
    }

    fun Date.getYearAfter(yearCount: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = this
        calendar.add(Calendar.YEAR, yearCount) // Add one day to the current date
        return calendar.time
    }


    fun Date.formatDateToDDMMYY(): String {
        val sdf = SimpleDateFormat("MM/dd/yy", Locale.getDefault())
        return sdf.format(this)
    }

    fun Date.formatDateToHHMMSS(): String {
        val sdf = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        val d = this
        d.seconds = 0
        return sdf.format(d)
    }

    fun Date.formatDateToDDMMYYHHMMSSA(): String {
        val sdf = SimpleDateFormat("MM/dd/yy hh:mm:ss a", Locale.getDefault())
        val d = this
        d.seconds = 0
        return sdf.format(d)
    }

    fun String.toCamelCase(): String {
        val words = this.split(" ").map { it.capitalize() }
        return words.joinToString("")
    }

    fun Dialog.visible() {
        Log.i("ProgressDialog", "visible() -> isShowing:${this.isShowing}")
        if (!this.isShowing) {
            this.show()
        }
    }

    fun Dialog.close() {
        Log.i("ProgressDialog", "close() -> isShowing:${this.isShowing}")
        if (this.isShowing)
            this.dismiss()
    }

    fun Tasks.calculateReminderTime(): Date {
        return when (this.reminder) {
            ReminderEnum.MIN05 -> {
                val calendar = Calendar.getInstance()
                calendar.time = this.time.toDate()
                calendar.add(Calendar.MINUTE, -5)
                calendar.time
            }

            ReminderEnum.MIN10 -> {
                val calendar = java.util.Calendar.getInstance()
                calendar.time = this.time.toDate()
                calendar.add(java.util.Calendar.MINUTE, -10)
                calendar.time
            }

            ReminderEnum.MIN30 -> {
                val calendar = Calendar.getInstance()
                calendar.time = this.time.toDate()
                calendar.add(Calendar.MINUTE, -30)
                calendar.time
            }

            ReminderEnum.MIN60 -> {
                val calendar = Calendar.getInstance()
                calendar.time = this.time.toDate()
                calendar.add(Calendar.HOUR, -1)
                calendar.time
            }

            else -> {
                this.time.toDate()
            }
        }
    }

    fun ImageView.enableHeart() {
        this.setImageDrawable(this.context.getDrawable(R.drawable.ic_health_selected))
    }

    fun ImageView.disableHeart() {
        this.setImageDrawable(this.context.getDrawable(R.drawable.ic_health))
    }

    fun ImageView.enableStar() {
        this.setImageDrawable(this.context.getDrawable(R.drawable.ic_start_rate_selected))
    }

    fun ImageView.disableStar() {
        this.setImageDrawable(this.context.getDrawable(R.drawable.ic_start_rate))
    }

    fun ImageView.enableHappyEmoji() {
        this.setImageDrawable(this.context.getDrawable(R.drawable.ic_happy_selected))
    }

    fun ImageView.enableSadEmoji() {
        this.setImageDrawable(this.context.getDrawable(R.drawable.ic_sad_selected))
    }

    fun ImageView.enableAngryEmoji() {
        this.setImageDrawable(this.context.getDrawable(R.drawable.ic_angry_selected))
    }

    fun ImageView.disableSadEmoji() {
        this.setImageDrawable(this.context.getDrawable(R.drawable.mood_sad_unselected_icon))
    }

    fun ImageView.disableHappyEmoji() {
        this.setImageDrawable(this.context.getDrawable(R.drawable.mood_happy_icon_unselected))
    }

    fun ImageView.disableAngryEmoji() {
        this.setImageDrawable(this.context.getDrawable(R.drawable.mood_angry_icon_unselected))
    }

    fun TextView.enableText() {
        this.setTextColor(ContextCompat.getColor(this.context, R.color.color_25282B))
    }

    fun TextView.disableText() {
        this.setTextColor(ContextCompat.getColor(this.context, R.color.color_9EA0A2))
    }

    fun ViewGroup.setOnSwipeListener(context: Context, onSwipe: (direction: Int) -> Unit) {
        val gestureDetector =
            GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                private val SWIPE_THRESHOLD = 10
                private val SWIPE_VELOCITY_THRESHOLD = 10
                override fun onFling(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {

                    val diffX = e2.x.minus(e1?.x ?: 0f)
                    if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        // Swipe detected, determine direction
                        if (diffX > 0) {
                            // Swipe right
                            onSwipe.invoke(GestureDirection.RIGHT)
                        } else {
                            // Swipe left
                            onSwipe.invoke(GestureDirection.LEFT)
                        }
                        return true
                    }
                    return super.onFling(e1, e2, velocityX, velocityY)
                }
            })

        setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            false
        }
    }

    object GestureDirection {
        const val LEFT = 0
        const val RIGHT = 1
    }
    fun String.toDate(format: String = "yyyy-MM-dd"): Date {
        val dateFormat = SimpleDateFormat(format)
        return dateFormat.parse(this)
    }
    fun String.removeLastChar(): String {
        if (this.isEmpty()) {
            return this
        }
        return this.substring(0, this.length - 1)
    }

    fun Context.getVersionName(): String? {
        return try {
            val packageInfo = this.packageManager.getPackageInfo(this.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    fun Context.openPlayStoreSubscriptionScreen(){
        val packageName = packageName
        val subscriptionUrl = "https://play.google.com/store/account/subscriptions?package=$packageName"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(subscriptionUrl))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        // Check if there's an activity available to handle this intent
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            showToast("Google Play store not installed.")
        }
    }


    fun Activity.openExactAlarmSettingPage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            startActivity(
                Intent(
                    Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                    Uri.parse("package:$packageName")
                )
            )
        }
    }
}