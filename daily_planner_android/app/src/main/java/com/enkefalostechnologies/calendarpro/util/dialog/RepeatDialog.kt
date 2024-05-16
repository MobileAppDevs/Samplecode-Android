package com.enkefalostechnologies.calendarpro.util.dialog

import android.R
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.amplifyframework.datastore.generated.model.RepeatDays
import com.amplifyframework.datastore.generated.model.RepeatType
import com.enkefalostechnologies.calendarpro.databinding.DatePickerBinding
import com.enkefalostechnologies.calendarpro.databinding.RepeatDialogBinding
import com.enkefalostechnologies.calendarpro.ui.picker.datePickerDialog
import com.enkefalostechnologies.calendarpro.util.AppUtil.getDateAfter
import com.enkefalostechnologies.calendarpro.util.AppUtil.getDateAfterMonths
import com.enkefalostechnologies.calendarpro.util.AppUtil.getDateAfterWeeks
import com.enkefalostechnologies.calendarpro.util.AppUtil.getDateAfterYears
import com.enkefalostechnologies.calendarpro.util.AppUtil.getDateStringFromDate
import com.enkefalostechnologies.calendarpro.util.AppUtil.getMonthFromDate
import com.enkefalostechnologies.calendarpro.util.AppUtil.getYearFromDate
import com.enkefalostechnologies.calendarpro.util.AppUtil.showToast
import com.enkefalostechnologies.calendarpro.util.DatePickerListener
import com.enkefalostechnologies.calendarpro.util.DialogUtil.dateAndTimePicker
import com.enkefalostechnologies.calendarpro.util.Extension.getDayAfter
import com.enkefalostechnologies.calendarpro.util.Extension.getMonthAfter
import com.enkefalostechnologies.calendarpro.util.Extension.getNextDayFromDate
import com.enkefalostechnologies.calendarpro.util.Extension.getYearAfter
import com.enkefalostechnologies.calendarpro.util.Extension.gone
import com.enkefalostechnologies.calendarpro.util.Extension.visible
import com.enkefalostechnologies.calendarpro.util.repeatDialogListener
import org.joda.time.LocalDateTime
import java.util.Date

fun Activity.repeatDialog(
    repeatTypes: RepeatType=RepeatType.DAY,
    repeatDay: MutableList<RepeatDays> = mutableListOf(),
    taskDate: Date?,
    endDate:Date?,
    binding: RepeatDialogBinding,
    listener: repeatDialogListener,
    repeatCount:Int
): Dialog =
    Dialog(this).apply {
        var repeatType: RepeatType = repeatTypes
        var endDates = endDate
        var repeatDays = repeatDay
        setContentView(binding.root)
        create()
        show()
        window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        val list = arrayOf("Days", "Weeks", "Months", "Years")
        val adapter = ArrayAdapter(
            binding.root.context,
            R.layout.simple_spinner_item,
            list
        ) // where array_name consists of the items to show in Spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        binding.spinner.adapter = adapter
        binding.spinner.setSelection(when(repeatType){
            RepeatType.DAY->0
            RepeatType.WEEK->1
            RepeatType.MONTH->2
            RepeatType.YEAR->3
            else->0
        })
        binding.tvCount.setText("1")
        binding.spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {
                    repeatType = when (position) {
                        0 -> RepeatType.DAY
                        1 -> RepeatType.WEEK
                        2 -> RepeatType.MONTH
                        3 -> RepeatType.YEAR
                        else -> RepeatType.DAY
                    }
                    if (repeatType != RepeatType.WEEK) {
                        binding.llWeeks.gone()
                    } else {
                        binding.llWeeks.visible()
                    }
//                    val count = if (binding.tvCount.text.toString().trim() == "") 1 else binding.tvCount.text.toString().trim().toInt()
//                    endDates=when(repeatType){
//                        RepeatType.DAY->taskDate?.getDateAfter(count)
//                        RepeatType.WEEK->taskDate?.getDateAfterWeeks(count)
//                        RepeatType.MONTH->taskDate?.getDateAfterMonths(count)
//                        RepeatType.YEAR->taskDate?.getDateAfterYears(count)
//                        RepeatType.NONE->taskDate?.getDateAfter(count)
//                    }
//                    binding.tvEndDate.text = "Ends on ${endDates?.getDateStringFromDate()} ${endDates?.getMonthFromDate()} ${endDates?.getYearFromDate()}"

                }


                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }
        binding.tvEndDate.setOnClickListener { v ->
            datePickerDialog(
                taskDate,
                DatePickerBinding.inflate(LayoutInflater.from(v.context)),
                object : DatePickerListener {
                    override fun onDateSelected(date: Date) {
                        endDates=date
                        binding.tvEndDate.text =
                            "Ends on ${date.getDateStringFromDate()} ${date.getMonthFromDate()} ${date.getYearFromDate()}"
                    }

                })
        }
        binding.llWeeks.gone()
        binding.tvEndDate.text =if(endDates==null) "Ends on DD/MM/YYYY" else
            "Ends on ${endDates?.getDateStringFromDate()}/${endDates?.getMonthFromDate()}/${endDates?.getYearFromDate()}"
//        binding.tvCount.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//                p0?.let {data->
//                    Handler(Looper.getMainLooper()).post {
//                        val count = if (data.toString().trim() == ""
//                        ) 1 else data.toString().trim().toInt()
////                        if (repeatType == RepeatType.DAY && count > 31) {
////                            binding.spinner.setSelection(1)
////                            binding.tvCount.setText("1")
//////                    endDate=endDate.getDayAfter(count)
////                        } else if (repeatType == RepeatType.WEEK && count > 4) {
////                            binding.spinner.setSelection(2)
////                            binding.tvCount.setText("1")
//////                    endDate=endDate.getDayAfter(count*7)
////                        } else if (repeatType == RepeatType.MONTH && count > 12) {
////                            binding.spinner.setSelection(3)
////                            binding.tvCount.setText("1")
//////                    endDate=endDate.getMonthAfter(count)
////                        }
//                        if (count!=0){
//                            endDates=when(repeatType){
//                                RepeatType.DAY->taskDate?.getDateAfter(count)
//                                RepeatType.WEEK->taskDate?.getDateAfterWeeks(count)
//                                RepeatType.MONTH->taskDate?.getDateAfterMonths(count)
//                                RepeatType.YEAR->taskDate?.getDateAfterYears(count)
//                                RepeatType.NONE->taskDate?.getDateAfter(count)
//                            }
//                            binding.tvEndDate.text = "Ends on ${endDates?.getDateStringFromDate()} ${endDates?.getMonthFromDate()} ${endDates?.getYearFromDate()}"
//                        }
//                    }
//                }
//
////                binding.tvEndDate.text = "Ends on ${
////                    endDate.getYearAfter(count).getDateStringFromDate()
////                } ${
////                    endDate.getYearAfter(count).getMonthFromDate()
////                } ${endDate.getYearAfter(count).getYearFromDate()}"
//            }
//
//        })
        if(repeatDays.contains(RepeatDays.MON)){
            binding.tvMon.backgroundTintList =
                ContextCompat.getColorStateList(
                    binding.tvMon.context,
                    com.enkefalostechnologies.calendarpro.R.color.color_D3F26A
                )
        }
        if(repeatDays.contains(RepeatDays.TUE)){
            binding.tvTue.backgroundTintList =
                ContextCompat.getColorStateList(
                    binding.tvTue.context,
                    com.enkefalostechnologies.calendarpro.R.color.color_D3F26A
                )
        }
        if(repeatDays.contains(RepeatDays.WED)){
            binding.tvWed.backgroundTintList =
                ContextCompat.getColorStateList(
                    binding.tvWed.context,
                    com.enkefalostechnologies.calendarpro.R.color.color_D3F26A
                )
        }
        if(repeatDays.contains(RepeatDays.THU)){
            binding.tvThu.backgroundTintList =
                ContextCompat.getColorStateList(
                    binding.tvThu.context,
                    com.enkefalostechnologies.calendarpro.R.color.color_D3F26A
                )
        }
        if(repeatDays.contains(RepeatDays.FRI)){
            binding.tvFri.backgroundTintList =
                ContextCompat.getColorStateList(
                    binding.tvFri.context,
                    com.enkefalostechnologies.calendarpro.R.color.color_D3F26A
                )
        }
        if(repeatDays.contains(RepeatDays.SAT)){
            binding.tvSat.backgroundTintList =
                ContextCompat.getColorStateList(
                    binding.tvSat.context,
                    com.enkefalostechnologies.calendarpro.R.color.color_D3F26A
                )
        }
        if(repeatDays.contains(RepeatDays.SUN)){
            binding.tvSun.backgroundTintList =
                ContextCompat.getColorStateList(
                    binding.tvSun.context,
                    com.enkefalostechnologies.calendarpro.R.color.color_D3F26A
                )
        }
        binding.tvMon.setOnClickListener { view ->
            if (repeatDays.contains(RepeatDays.MON)) {
                repeatDays.remove(RepeatDays.MON)
                binding.tvMon.backgroundTintList =
                    ContextCompat.getColorStateList(
                        view.context,
                        com.enkefalostechnologies.calendarpro.R.color.white
                    )
            } else {
                repeatDays.add(RepeatDays.MON)
                binding.tvMon.backgroundTintList =
                    ContextCompat.getColorStateList(
                        view.context,
                        com.enkefalostechnologies.calendarpro.R.color.color_D3F26A
                    )
            }
        }
        binding.tvTue.setOnClickListener { view ->
            if (repeatDays.contains(RepeatDays.TUE)) {
                repeatDays.remove(RepeatDays.TUE)
                binding.tvTue.backgroundTintList =
                    ContextCompat.getColorStateList(
                        view.context,
                        com.enkefalostechnologies.calendarpro.R.color.white
                    )
            } else {
                repeatDays.add(RepeatDays.TUE)
                binding.tvTue.backgroundTintList =
                    ContextCompat.getColorStateList(
                        view.context,
                        com.enkefalostechnologies.calendarpro.R.color.color_D3F26A
                    )
            }
        }
        binding.tvWed.setOnClickListener { view ->
            if (repeatDays.contains(RepeatDays.WED)) {
                repeatDays.remove(RepeatDays.WED)
                binding.tvWed.backgroundTintList =
                    ContextCompat.getColorStateList(
                        view.context,
                        com.enkefalostechnologies.calendarpro.R.color.white
                    )
            } else {
                repeatDays.add(RepeatDays.WED)
                binding.tvWed.backgroundTintList =
                    ContextCompat.getColorStateList(
                        view.context,
                        com.enkefalostechnologies.calendarpro.R.color.color_D3F26A
                    )
            }
        }
        binding.tvThu.setOnClickListener { view ->
            if (repeatDays.contains(RepeatDays.THU)) {
                repeatDays.remove(RepeatDays.THU)
                binding.tvThu.backgroundTintList =
                    ContextCompat.getColorStateList(
                        view.context,
                        com.enkefalostechnologies.calendarpro.R.color.white
                    )
            } else {
                repeatDays.add(RepeatDays.THU)
                binding.tvThu.backgroundTintList =
                    ContextCompat.getColorStateList(
                        view.context,
                        com.enkefalostechnologies.calendarpro.R.color.color_D3F26A
                    )
            }
        }
        binding.tvFri.setOnClickListener { view ->
            if (repeatDays.contains(RepeatDays.FRI)) {
                repeatDays.remove(RepeatDays.FRI)
                binding.tvFri.backgroundTintList =
                    ContextCompat.getColorStateList(
                        view.context,
                        com.enkefalostechnologies.calendarpro.R.color.white
                    )
            } else {
                repeatDays.add(RepeatDays.FRI)
                binding.tvFri.backgroundTintList =
                    ContextCompat.getColorStateList(
                        view.context,
                        com.enkefalostechnologies.calendarpro.R.color.color_D3F26A
                    )
            }
        }
        binding.tvSat.setOnClickListener { view ->
            if (repeatDays.contains(RepeatDays.SAT)) {
                repeatDays.remove(RepeatDays.SAT)
                binding.tvSat.backgroundTintList =
                    ContextCompat.getColorStateList(
                        view.context,
                        com.enkefalostechnologies.calendarpro.R.color.white
                    )
            } else {
                repeatDays.add(RepeatDays.SAT)
                binding.tvSat.backgroundTintList =
                    ContextCompat.getColorStateList(
                        view.context,
                        com.enkefalostechnologies.calendarpro.R.color.color_D3F26A
                    )
            }
        }
        binding.tvSun.setOnClickListener { view ->
            if (repeatDays.contains(RepeatDays.SUN)) {
                repeatDays.remove(RepeatDays.SUN)
                binding.tvSun.backgroundTintList =
                    ContextCompat.getColorStateList(
                        view.context,
                        com.enkefalostechnologies.calendarpro.R.color.white
                    )
            } else {
                repeatDays.add(RepeatDays.SUN)
                binding.tvSun.backgroundTintList =
                    ContextCompat.getColorStateList(
                        view.context,
                        com.enkefalostechnologies.calendarpro.R.color.color_D3F26A
                    )
            }
        }
        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnCreate.setOnClickListener {
            if(repeatType ==RepeatType.WEEK && repeatDays.isEmpty()){
                showToast("Please select week days...")
                return@setOnClickListener
            }
            if(repeatType!=RepeatType.WEEK){
                repeatDays.clear()
            }
            if(endDates==null){
                showToast("Select End Date...")
                return@setOnClickListener
            }
            val count = if (binding.tvCount.text.toString().trim() == "") 1 else binding.tvCount.text.toString().trim().toInt()
            listener.onDoneClicked(repeatType, repeatDays, endDates?:taskDate?.getNextDayFromDate(),count,this)
            dismiss()
        }
        binding.tvCount.setText(repeatCount.toString())
}


fun Activity.repeatDialogDisabled(
    repeatTypes: RepeatType=RepeatType.DAY,
    repeatDay: MutableList<RepeatDays> = mutableListOf(),
    taskDate: Date?,
    endDate:Date?,
    binding: RepeatDialogBinding,
    listener: repeatDialogListener,
    repeatCount:Int
): Dialog =
    Dialog(this).apply {
        var repeatType: RepeatType = repeatTypes
        var endDates = endDate
        var repeatDays = repeatDay
        setContentView(binding.root)
        create()
        show()
        window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        val list = arrayOf("Days", "Weeks", "Months", "Years")
        val adapter = ArrayAdapter(
            binding.root.context,
            R.layout.simple_spinner_item,
            list
        ) // where array_name consists of the items to show in Spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        binding.spinner.adapter = adapter
        binding.spinner.setSelection(when(repeatType){
            RepeatType.DAY->0
            RepeatType.WEEK->1
            RepeatType.MONTH->2
            RepeatType.YEAR->3
            else->0
        })
        binding.tvCount.setText("1")
        binding.tvCount.isEnabled=false
        binding.spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {
                    repeatType = when (position) {
                        0 -> RepeatType.DAY
                        1 -> RepeatType.WEEK
                        2 -> RepeatType.MONTH
                        3 -> RepeatType.YEAR
                        else -> RepeatType.DAY
                    }
                    if (repeatType != RepeatType.WEEK) {
                        binding.llWeeks.gone()
                    } else {
                        binding.llWeeks.visible()
                    }
//                    val count = if (binding.tvCount.text.toString().trim() == "") 1 else binding.tvCount.text.toString().trim().toInt()
//                    endDates=when(repeatType){
//                        RepeatType.DAY->taskDate?.getDateAfter(count)
//                        RepeatType.WEEK->taskDate?.getDateAfterWeeks(count)
//                        RepeatType.MONTH->taskDate?.getDateAfterMonths(count)
//                        RepeatType.YEAR->taskDate?.getDateAfterYears(count)
//                        RepeatType.NONE->taskDate?.getDateAfter(count)
//                    }
//                    binding.tvEndDate.text = "Ends on ${endDates?.getDateStringFromDate()} ${endDates?.getMonthFromDate()} ${endDates?.getYearFromDate()}"

                }


                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }
        binding.spinner.isClickable=false
        binding.spinner.isEnabled=false
        binding.llWeeks.gone()
        binding.tvEndDate.text =if(endDates==null) "Ends on DD/MM/YYYY" else
            "Ends on ${endDates?.getDateStringFromDate()} ${endDates?.getMonthFromDate()} ${endDates?.getYearFromDate()}"
        if(repeatDays.contains(RepeatDays.MON)){
            binding.tvMon.backgroundTintList =
                ContextCompat.getColorStateList(
                    binding.tvMon.context,
                    com.enkefalostechnologies.calendarpro.R.color.color_D3F26A
                )
        }
        if(repeatDays.contains(RepeatDays.TUE)){
            binding.tvTue.backgroundTintList =
                ContextCompat.getColorStateList(
                    binding.tvTue.context,
                    com.enkefalostechnologies.calendarpro.R.color.color_D3F26A
                )
        }
        if(repeatDays.contains(RepeatDays.WED)){
            binding.tvWed.backgroundTintList =
                ContextCompat.getColorStateList(
                    binding.tvWed.context,
                    com.enkefalostechnologies.calendarpro.R.color.color_D3F26A
                )
        }
        if(repeatDays.contains(RepeatDays.THU)){
            binding.tvThu.backgroundTintList =
                ContextCompat.getColorStateList(
                    binding.tvThu.context,
                    com.enkefalostechnologies.calendarpro.R.color.color_D3F26A
                )
        }
        if(repeatDays.contains(RepeatDays.FRI)){
            binding.tvFri.backgroundTintList =
                ContextCompat.getColorStateList(
                    binding.tvFri.context,
                    com.enkefalostechnologies.calendarpro.R.color.color_D3F26A
                )
        }
        if(repeatDays.contains(RepeatDays.SAT)){
            binding.tvSat.backgroundTintList =
                ContextCompat.getColorStateList(
                    binding.tvSat.context,
                    com.enkefalostechnologies.calendarpro.R.color.color_D3F26A
                )
        }
        if(repeatDays.contains(RepeatDays.SUN)){
            binding.tvSun.backgroundTintList =
                ContextCompat.getColorStateList(
                    binding.tvSun.context,
                    com.enkefalostechnologies.calendarpro.R.color.color_D3F26A
                )
        }
        binding.btnCancel.text="Close"
        binding.btnCreate.text="Edit"
        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnCreate.setOnClickListener {

            if(repeatType ==RepeatType.WEEK && repeatDays.isEmpty()){
                showToast("Please select week days...")
                return@setOnClickListener
            }
//            binding.btnCreate.startAnimation()
            if(repeatType!=RepeatType.WEEK){
                repeatDays.clear()
            }
            if(endDates==null){
                showToast("Select End Date...")
                return@setOnClickListener
            }
            val count = if (binding.tvCount.text.toString().trim() == "") 1 else binding.tvCount.text.toString().trim().toInt()
            listener.onDoneClicked(repeatType, repeatDays, endDates?:taskDate?.getNextDayFromDate(),count,this)
//            dismiss()
        }
        binding.tvCount.setText(repeatCount.toString())
    }