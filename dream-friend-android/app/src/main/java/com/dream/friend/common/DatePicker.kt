package com.dream.friend.common

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.DatePicker
import android.widget.PopupMenu.OnDismissListener
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.dream.friend.R
import com.dream.friend.databinding.ActivityCreateProfileBinding
import com.dream.friend.databinding.ActivityEditProfileBinding
import com.dream.friend.ui.onboarding.CreateProfileActivity
import com.dream.friend.ui.settings.EditProfileActivity
import com.dream.friend.util.Extensions.visible
import java.util.*

class DatePicker(
    private val bind: ActivityCreateProfileBinding,
    private val bind2: ActivityEditProfileBinding? = null,
    private val listener:DatePickerListener?=null
): DialogFragment() {

    private lateinit var calendar: Calendar
    private val months = arrayListOf(
        "Jan",
        "Feb",
        "Mar",
        "Apr",
        "May",
        "Jun",
        "Jul",
        "Aug",
        "Sep",
        "Oct",
        "Nov",
        "Dec"
    )

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        calendar = Calendar.getInstance()
        val year = get(Calendar.YEAR)
        val month = get(Calendar.MONTH)
        val day = get(Calendar.DAY_OF_MONTH)

        val dateListener = DatePickerDialog.OnDateSetListener {da, YEAR, MONTH, DAY ->
            var d = "$DAY"
            if (DAY <= 9)
                d = "0$d"

            val mon = MONTH + 1
            CreateProfileActivity.dob = "$YEAR-${mon}-$d"
            EditProfileActivity?.dob = "$YEAR-${mon}-$d"
            bind.signup.tvDob.text = "$d/${months[MONTH]}/$YEAR"
            bind2?.tvDob?.text = "${months[MONTH]} $d,$YEAR"
           // bind2?.btnUpdate?.visible()
            listener?.onPositiveButtonClicked(da,YEAR,MONTH,DAY)
        }
        val datePickerDialog = DatePickerDialog(
            bind.root.context,
            R.style.DialogTheme,
            dateListener,
            year, month, day)
        calendar.add(Calendar.YEAR, -18)
        datePickerDialog.datePicker.maxDate = calendar.time.time
        datePickerDialog.show()
        val color = ContextCompat.getColor(bind.root.context, R.color.color_FFC629)
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(color)
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setOnClickListener {
            listener?.onNegativeButtonClicked()
            datePickerDialog.dismiss()
        }
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(color)
        return datePickerDialog
    }

    private fun get(i: Int): Int{
        return calendar.get(i)
    }
}

interface DatePickerListener{
   fun onPositiveButtonClicked(view: DatePicker, year:Int, month: Int, dayOfMonth:Int)
   fun onNegativeButtonClicked()
}