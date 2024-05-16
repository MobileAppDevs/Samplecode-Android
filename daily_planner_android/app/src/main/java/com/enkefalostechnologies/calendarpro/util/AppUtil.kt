package com.enkefalostechnologies.calendarpro.util

import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.format.DateFormat
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.generated.model.RepeatDays
import com.amplifyframework.datastore.generated.model.RepeatType
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.util.Extension.dateToTemporalDate
import com.enkefalostechnologies.calendarpro.util.Extension.getNextDayFromDate
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import java.lang.Math.abs
import java.util.Calendar
import java.util.Date
import java.util.Random
import java.util.TimeZone
import java.util.concurrent.TimeUnit


object AppUtil {
    fun Activity.isNetworkAvailable(): Boolean {
        val connectivityManager =
            this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!
            .isConnected
    }

    fun Activity.hideStatusBar() {
        /* window.setFlags(
             WindowManager.LayoutParams.FLAG_FULLSCREEN,
             WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.color_bg)

    }

    fun Activity.changeStatusBarColor(bgColorRes: Int) {
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, bgColorRes)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }

    }

    fun Context.showToast(msg: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(this, msg, duration).show()
    }

    fun String.isFromScreen(screenName: String): Boolean =
        this.equals(screenName, ignoreCase = true)

    fun getCurrentDay(): String {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // Days of the week are indexed from 1 (Sunday) to 7 (Saturday)
        val daysOfWeek =
            arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

        return daysOfWeek[dayOfWeek - 1] // Adjust for 0-based index
    }

    fun getCurrentTemporalDateTime(): Temporal.DateTime {
        val date = Date()
        val offsetMillis = TimeZone.getDefault().getOffset(date.time).toLong()
        val offsetSeconds = TimeUnit.MILLISECONDS.toSeconds(offsetMillis).toInt()
        return Temporal.DateTime(date, offsetSeconds)
    }

    fun getCurrentTemporalTime(): Temporal.Time {
        val date = Date()
        val offsetMillis = TimeZone.getDefault().getOffset(date.time).toLong()
        val offsetSeconds = TimeUnit.MILLISECONDS.toSeconds(offsetMillis).toInt()
        return Temporal.Time(date, offsetSeconds)
    }

    fun getTemporalDateTimeFromHrsAndMin(hour: Int, min: Int): Temporal.DateTime {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, min)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val date = calendar.time
        val offsetMillis = TimeZone.getDefault().getOffset(date.time).toLong()
        val offsetSeconds = TimeUnit.MILLISECONDS.toSeconds(offsetMillis).toInt()
        return Temporal.DateTime(date, offsetSeconds)
    }

    fun getCurrentTemporalDate(): Temporal.Date {
        val date = Date()
        val offsetMillis = TimeZone.getDefault().getOffset(date.time).toLong()
        val offsetSeconds = TimeUnit.MILLISECONDS.toSeconds(offsetMillis).toInt()
        return Temporal.Date(date, offsetSeconds)
    }

    fun getTemporalDateOfCurrentWeek(): MutableList<Temporal.Date> {
        val dateList = mutableListOf<Temporal.Date>()
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = calendar.firstDayOfWeek // Set Monday as the first day of the week
        calendar.time = Date()
        val startOfWeek = calendar.clone() as Calendar
        startOfWeek.add(
            Calendar.DATE,
            -startOfWeek.get(Calendar.DAY_OF_WEEK) + calendar.firstDayOfWeek
        )
        val monDate = startOfWeek.time//monday
        val tueDate = monDate.getNextDayFromDate()// tuesday
        val wedDate = tueDate.getNextDayFromDate()// wednesday
        val thursDate = wedDate.getNextDayFromDate()// thursday
        val friDate = thursDate.getNextDayFromDate()// friday
        val satDate = friDate.getNextDayFromDate()// saturday
        val sunDate = satDate.getNextDayFromDate()// sun
        dateList.add(monDate.dateToTemporalDate())
        dateList.add(tueDate.dateToTemporalDate())
        dateList.add(wedDate.dateToTemporalDate())
        dateList.add(thursDate.dateToTemporalDate())
        dateList.add(friDate.dateToTemporalDate())
        dateList.add(satDate.dateToTemporalDate())
        dateList.add(sunDate.dateToTemporalDate())
        return dateList
    }

    fun Activity.openUrl(url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    fun getStartAndEndOfMonthDates(): Pair<Date, Date> {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)

        val startOfMonth = Calendar.getInstance()
        startOfMonth.set(Calendar.YEAR, year)
        startOfMonth.set(Calendar.MONTH, month)
        startOfMonth.set(Calendar.DAY_OF_MONTH, 1)
        startOfMonth.set(Calendar.HOUR_OF_DAY, 0)
        startOfMonth.set(Calendar.MINUTE, 0)
        startOfMonth.set(Calendar.SECOND, 0)
        startOfMonth.set(Calendar.MILLISECOND, 0)

        val endOfMonth = Calendar.getInstance()
        endOfMonth.set(Calendar.YEAR, year)
        endOfMonth.set(Calendar.MONTH, month)
        endOfMonth.set(Calendar.DAY_OF_MONTH, endOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH))
        endOfMonth.set(Calendar.HOUR_OF_DAY, 23)
        endOfMonth.set(Calendar.MINUTE, 59)
        endOfMonth.set(Calendar.SECOND, 59)
        endOfMonth.set(Calendar.MILLISECOND, 999)

        val startDate = startOfMonth.time // Convert Calendar to Date
        val endDate = endOfMonth.time // Convert Calendar to Date

        return Pair(startDate, endDate)
    }

    fun Date.getDayFromDate() = DateFormat.format("EE", this).toString()
    fun Date.getDateStringFromDate() = DateFormat.format("dd", this).toString()
    fun Date.getMonthFromDate() = DateFormat.format("MMM", this).toString()
    fun Date.getMonthMMMMFromDate() = DateFormat.format("MMMM", this).toString()
    fun Date.getYearFromDate() = DateFormat.format("yy", this).toString()
    fun Date.getYearYYYYFromDate() = DateFormat.format("yyyy", this).toString()

    fun Context.showPushNotification(title: String, description: String) {

        val pendingIntent =
            PendingIntent.getActivity(this, 5, Intent(), PendingIntent.FLAG_IMMUTABLE)


        lateinit var notificationChannel: NotificationChannel
        lateinit var builder: Notification.Builder
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // checking if android version is greater than oreo(API 26) or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(title, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setShowWhen(false)
                .setContentTitle(title)
                .setContentText(description)
                .setContentIntent(pendingIntent)
        } else {

            builder = Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        this.resources,
                        R.mipmap.ic_launcher
                    )
                )
                .setContentTitle(title)
                .setContentText(description)
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(
            System.currentTimeMillis().toInt(),
            builder.build()
        )
    }

    fun AdView.loadBannerAd() {
        val adRequest = AdRequest.Builder().build()
        this.loadAd(adRequest)
    }

    fun generateRequestCode(): Int {
        val random = Random(System.currentTimeMillis())
        return 100_000 + random.nextInt(900_000) // Generates a random integer between 100000 and 999999
    }

    @SuppressLint("HardwareIds")
    fun Context.getAndroidId() =
        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

    fun getDatesBetween(
        startDate: Date,
        endDate: Date,
        repeatType: RepeatType,
        repeatCount: Int,
        repeatDays: List<RepeatDays>
    ): List<Date> {
        val dates = mutableListOf<Date>()
        when (repeatType) {
            RepeatType.DAY -> {
                val calendar = Calendar.getInstance()
                calendar.time = startDate

                while (calendar.time.before(endDate) || (calendar.time.date == endDate.date && calendar.time.month==endDate.month && calendar.time.year==endDate.year)) {
                    dates.add(calendar.time)
                    calendar.add(Calendar.DAY_OF_MONTH, repeatCount)
                }
            }

            RepeatType.MONTH -> {
                val calendar = Calendar.getInstance()
                calendar.time = startDate

                while (calendar.time.before(endDate) || (calendar.time.date == endDate.date && calendar.time.month==endDate.month && calendar.time.year==endDate.year)) {
                    dates.add(calendar.time)
                    calendar.add(Calendar.MONTH, repeatCount)
                }
            }

            RepeatType.YEAR -> {
                val calendar = Calendar.getInstance()
                calendar.time = startDate

                while (calendar.time.before(endDate) || (calendar.time.date == endDate.date && calendar.time.month==endDate.month && calendar.time.year==endDate.year)) {
                    dates.add(calendar.time)
                    calendar.add(Calendar.YEAR, repeatCount)
                }
            }

            RepeatType.WEEK -> {
                val weekStartDate = getDatesForCurrentWeek(startDate)[0]
                repeatDays.map {
                    dates.addAll(
                        getWeeklyDates(
                            getFirstDayDateWithinRange(weekStartDate, endDate, it)!!,
                            endDate,
                            repeatCount
                        )
                    )
                }
                if (!dates.any { it.date == startDate.date && it.month == startDate.month && it.year == startDate.year }) {
                    dates.add(startDate)
                }
                val filteredDates = dates.filter { it.after(startDate) || it.equals(startDate) }
                dates.clear()
                dates.addAll(filteredDates)
            }

            else -> {
                val calendar = Calendar.getInstance()
                calendar.time = startDate

                while (calendar.time.before(endDate) || (calendar.time.date == endDate.date && calendar.time.month==endDate.month && calendar.time.year==endDate.year)) {
                    dates.add(calendar.time)
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                }

            }


        }
//                 val calendar = Calendar.getInstance()
//                 calendar.time = startDate
//
//                 val endDateCalendar = Calendar.getInstance()
//                 endDateCalendar.time = endDate
//
//                 while (calendar.before(endDateCalendar) /*|| calendar == endDateCalendar*/) {
//                     dates.add(calendar.time)
//                     calendar.add(Calendar.DATE, 1)
//                 }
        return dates
    }

    fun Date.getRepeatDayFromDate() {
        when (this.getDayFromDate()) {
            "Mon" -> RepeatDays.MON
            "Tue" -> RepeatDays.TUE
            "Wed" -> RepeatDays.WED
            "Thu" -> RepeatDays.THU
            "Fri" -> RepeatDays.FRI
            "Sat" -> RepeatDays.SAT
            "Sun" -> RepeatDays.SUN
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun Context.isRuntimeNotificationGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }
    public fun  Context.isNotificationPermissionAvailable(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isRuntimeNotificationGranted()
        } else NotificationManagerCompat.from(this).areNotificationsEnabled()
    }

        fun Fragment.requestPermission(permission: String, requestCode: Int) {
        requestPermissions(arrayOf(permission), requestCode)
    }
//    fun isNotificationEnabled(context: Context): Boolean {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            return isNotificationChannelEnabled(context)
//        }
//        // For API levels 24 and 25, notifications are always enabled
//        return true
//    }

    fun Date.getDateAfter(days: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = this
        calendar.add(Calendar.DAY_OF_MONTH, days)
        return calendar.time
    }

    fun Date.getDateAfterMonths(months: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = this
        calendar.add(Calendar.MONTH, months)
        return calendar.time
    }

    fun Date.getDateAfterWeeks(weeks: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = this
        calendar.add(Calendar.WEEK_OF_YEAR, weeks)
        return calendar.time
    }

    fun Date.getDateAfterYears(years: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = this
        calendar.add(Calendar.YEAR, years)
        return calendar.time
    }

    fun Date.differenceInHoursAndMinutes(otherDate: Date): Pair<Long, Long> {
        val diffInMillis = abs(this.time - otherDate.time)

        // Calculate hours and minutes
        val hours = diffInMillis / (1000 * 60 * 60)
        val minutes = (diffInMillis % (1000 * 60 * 60)) / (1000 * 60)

        return Pair(hours, minutes)
    }

    fun Date.setTimeToZero(): Date {
        this.hours = 0
        this.minutes = 0
        this.seconds = 0
        return this
    }

    fun Int.isLeapYear(): Boolean {
        return this % 4 == 0 && (this % 100 != 0 || this % 400 == 0)
    }

    fun getWeeklyDates(startDate: Date, endDate: Date, repeatCount: Int): List<Date> {
        val datesList = mutableListOf<Date>()
        val calendar = Calendar.getInstance()
        calendar.time = startDate

        while (calendar.time.before(endDate) || (calendar.time.date == endDate.date && calendar.time.month==endDate.month && calendar.time.year==endDate.year)) {
            datesList.add(calendar.time)
            calendar.add(Calendar.WEEK_OF_YEAR, repeatCount)
        }

        return datesList
    }


    fun getFirstDayDateWithinRange(startDate: Date, endDate: Date, day: RepeatDays): Date? {
        val calendar = Calendar.getInstance()
        calendar.time = startDate

        while (calendar.time.before(endDate) || calendar.time == endDate) {
            if (calendar.get(Calendar.DAY_OF_WEEK) == day.getCalendarDay()) {
                return calendar.time
            }
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return calendar.time // No Monday found within the specified range
    }

    private fun RepeatDays.getCalendarDay(): Int {
        return when (this) {
            RepeatDays.MON -> Calendar.MONDAY
            RepeatDays.TUE -> Calendar.TUESDAY
            RepeatDays.WED -> Calendar.WEDNESDAY
            RepeatDays.THU -> Calendar.THURSDAY
            RepeatDays.FRI -> Calendar.FRIDAY
            RepeatDays.SAT -> Calendar.SATURDAY
            RepeatDays.SUN -> Calendar.SUNDAY
        }
    }

    fun getDatesForCurrentWeek(currentDate: Date): List<Date> {
        val datesList = mutableListOf<Date>()
        val calendar = Calendar.getInstance()

        // Set the calendar to the provided current date
        calendar.time = currentDate

        // Set the calendar to the beginning of the current week
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)

        // Iterate through the days of the week
        repeat(7) {
            datesList.add(calendar.time)
            calendar.add(Calendar.DAY_OF_WEEK, 1)
        }

        return datesList
    }

}