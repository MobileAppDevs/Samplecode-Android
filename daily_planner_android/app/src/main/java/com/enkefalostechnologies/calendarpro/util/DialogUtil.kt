package com.enkefalostechnologies.calendarpro.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.DatePicker.OnDateChangedListener
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import android.widget.TimePicker.OnTimeChangedListener
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.generated.model.KnowYourDay
import com.amplifyframework.datastore.generated.model.ReminderEnum
import com.amplifyframework.datastore.generated.model.RepeatDays
import com.amplifyframework.datastore.generated.model.RepeatType
import com.amplifyframework.datastore.generated.model.WaterInTake
import com.enkefalostechnologies.calendarpro.App
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.constant.Constants
import com.enkefalostechnologies.calendarpro.constant.StorageConstant
import com.enkefalostechnologies.calendarpro.databinding.DateAndTimePickerBinding
import com.enkefalostechnologies.calendarpro.databinding.DialogProgressBinding
import com.enkefalostechnologies.calendarpro.model.DateTimePickerModel
import com.enkefalostechnologies.calendarpro.ui.adapter.RepeatAdapter
import com.enkefalostechnologies.calendarpro.ui.adapter.WaterInTakeDateAdapter
import com.enkefalostechnologies.calendarpro.util.AppUtil.showToast
import com.enkefalostechnologies.calendarpro.util.DateTimeHelper.minuteBefore
import com.enkefalostechnologies.calendarpro.util.DialogUtil.dateAndTimePicker
import com.enkefalostechnologies.calendarpro.util.Extension.dateToTemporalDate
import com.enkefalostechnologies.calendarpro.util.Extension.formatDateToDDMMYY
import com.enkefalostechnologies.calendarpro.util.Extension.gone
import com.enkefalostechnologies.calendarpro.util.Extension.invisible
import com.enkefalostechnologies.calendarpro.util.Extension.visible
import java.util.Calendar
import java.util.Date


object DialogUtil {

    fun Context.waterIntakeUtil(
        isLoggedIn:Boolean,
        userId: String,
        deviceId:String,
        view: View,
        listener: WaterIntakeDialogListener
    ): Dialog =
        Dialog(this).apply {
            val tvGlass = view.findViewById<TextView>(R.id.tvGlass)
            val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
            var selectedDate = AppUtil.getCurrentTemporalDate()
            var innerCount = 0
            if(isLoggedIn) {
                Amplify.DataStore.query(
                    WaterInTake::class.java, Where.matches(
                        WaterInTake.USER_ID.eq(
                            userId
                        ).and(WaterInTake.DATE.eq(selectedDate))
                    ), { data ->
                        innerCount = if (data.hasNext()) {
                            data.next().count
                        } else 0
                        view.findViewById<TextView>(R.id.tvCount).text = innerCount.toString()
                    }, {
                        innerCount = 0
                        view.findViewById<TextView>(R.id.tvCount).text = innerCount.toString()
                    })
            }else{
                Amplify.DataStore.query(
                    WaterInTake::class.java, Where.matches(
                        WaterInTake.DEVICE_ID.eq(
                            deviceId
                        ).and(WaterInTake.DATE.eq(selectedDate))
                    ), { data ->
                        innerCount = if (data.hasNext()) {
                            data.next().count
                        } else 0
                        view.findViewById<TextView>(R.id.tvCount).text = innerCount.toString()
                    }, {
                        innerCount = 0
                        view.findViewById<TextView>(R.id.tvCount).text = innerCount.toString()
                    })
            }
            val rvDate = view.findViewById<RecyclerView>(R.id.rvDate)
            val adapter =
                WaterInTakeDateAdapter(selectedDate, object : WaterIntakeDateAdapterListener {
                    override fun onItemClicked(date: Temporal.Date) {
                        selectedDate = date
                        rvDate.adapter?.notifyDataSetChanged()
                        if(isLoggedIn) {
                            Amplify.DataStore.query(
                                WaterInTake::class.java, Where.matches(
                                    WaterInTake.USER_ID.eq(
                                        userId
                                    ).and(WaterInTake.DATE.eq(selectedDate))
                                ), { data ->
                                    innerCount = if (data.hasNext()) {
                                        data.next().count
                                    } else 0
                                    view.findViewById<TextView>(R.id.tvCount).text =
                                        innerCount.toString()
                                    setProgress(innerCount, progressBar, tvGlass)
                                }, {
                                    innerCount = 0
                                    view.findViewById<TextView>(R.id.tvCount).text =
                                        innerCount.toString()
                                    setProgress(innerCount, progressBar, tvGlass)
                                })
                        }else{
                            Amplify.DataStore.query(
                                WaterInTake::class.java, Where.matches(
                                    WaterInTake.DEVICE_ID.eq(
                                        deviceId
                                    ).and(WaterInTake.DATE.eq(selectedDate))
                                ), { data ->
                                    innerCount = if (data.hasNext()) {
                                        data.next().count
                                    } else 0
                                    view.findViewById<TextView>(R.id.tvCount).text =
                                        innerCount.toString()
                                    setProgress(innerCount, progressBar, tvGlass)
                                }, {
                                    innerCount = 0
                                    view.findViewById<TextView>(R.id.tvCount).text =
                                        innerCount.toString()
                                    setProgress(innerCount, progressBar, tvGlass)
                                })
                        }

                    }
                })
            rvDate.adapter = adapter
            adapter.setItems(AppUtil.getTemporalDateOfCurrentWeek())

            setContentView(view)
            create()
            show()
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            window?.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
            setProgress(innerCount, progressBar, tvGlass)
            view.findViewById<ImageView>(R.id.ibBack).setOnClickListener { dismiss() }
            view.findViewById<ImageView>(R.id.ibIncrement).setOnClickListener {
                if(innerCount<12) {
                    innerCount++
                    setProgress(innerCount, progressBar, tvGlass)
                }
                view.findViewById<TextView>(R.id.tvCount).text = innerCount.toString()
            }
            view.findViewById<ImageView>(R.id.ibDecrement).setOnClickListener {
                if (innerCount != 0) {
                    innerCount--
                    setProgress(innerCount, progressBar, tvGlass)
                }
                view.findViewById<TextView>(R.id.tvCount).text = innerCount.toString()
            }
            view.findViewById<CircularProgressButton>(R.id.btnDone).setOnClickListener {
                dismiss()
                listener.onDone(selectedDate, innerCount)
            }

        }


    @SuppressLint("SetTextI18n")
    private fun Context.setProgress(innerCount: Int, progressBar: ProgressBar, tvGlass: TextView) {
        if (innerCount <= 12) {
            progressBar.progressDrawable =
                ContextCompat.getDrawable(
                    this,
                    if (innerCount <= 4) R.drawable.circular_progress_bar1_4
                    else if (innerCount <= 8) R.drawable.circular_progress_bar5_8
                    else R.drawable.circular_progress_bar9_12
                )
            progressBar.progress = innerCount
        }
        tvGlass.text = "${String.format("%02d", innerCount)}\nGlass"
    }


    fun Activity.dateAndTimePicker(
        dateTimePickerModel: DateTimePickerModel,
        view: View,
        listener: DateTimePickerListener,
       /** for
                                           case true:calender will be visible
                                           case false : time Picker will be visible **/
    ): Dialog =
        Dialog(this).apply {
            setContentView(view)
            create()
            show()
            window?.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
            view.findViewById<TimePicker>(R.id.timePicker).gone()
            view.findViewById<DatePicker>(R.id.datePicker).gone()
                view.findViewById<TextView>(R.id.tvTime).setOnClickListener {
                    view.findViewById<TimePicker>(R.id.timePicker).visible()
                    view.findViewById<View>(R.id.ViewDateLine).invisible()
                    view.findViewById<View>(R.id.ViewTimeLine).visible()
                    view.findViewById<DatePicker>(R.id.datePicker).gone()
                    dateTimePickerModel.isTimeSelected=true

                }
                view.findViewById<TextView>(R.id.tvDate).setOnClickListener {
                    view.findViewById<DatePicker>(R.id.datePicker).visible()
                    view.findViewById<View>(R.id.ViewDateLine).visible()
                    view.findViewById<View>(R.id.ViewTimeLine).invisible()
                    view.findViewById<TimePicker>(R.id.timePicker).gone()
                    dateTimePickerModel.isDateSelected=true
                }


            val calendar = Calendar.getInstance()
            calendar.time =dateTimePickerModel.dateTime
//            calendar.timeInMillis = System.currentTimeMillis()
            view.findViewById<DatePicker>(R.id.datePicker).init(calendar.get(Calendar.YEAR)-1900,
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                OnDateChangedListener { datePicker, year, month, dayOfMonth ->
                    if(dateTimePickerModel.isDateSelected==true) {
                        val selectedDate = Date(year - 1900, month, dayOfMonth)
                        dateTimePickerModel.dateTime.date = selectedDate.date
                        dateTimePickerModel.dateTime.month = selectedDate.month
                        dateTimePickerModel.dateTime.year = selectedDate.year
                    }
                })
            view.findViewById<DatePicker>(R.id.datePicker). setMinDate(System. currentTimeMillis() - 1000);
            view.findViewById<DatePicker>(R.id.datePicker). updateDate(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))
            val timePickerView = view.findViewById<TimePicker>(R.id.timePicker)
            timePickerView.setHour( dateTimePickerModel.dateTime.hours);
            timePickerView.setMinute(dateTimePickerModel.dateTime.minutes);
            timePickerView.setOnTimeChangedListener { p0, hrs, min ->
                dateTimePickerModel.dateTime.hours = hrs
                dateTimePickerModel.dateTime.minutes = min
            }
               if(dateTimePickerModel.isDateSelected==true) {
                   view.findViewById<TextView>(R.id.tvDate).performClick()
               }
            if(dateTimePickerModel.isTimeSelected==true){
                view.findViewById<TextView>(R.id.tvTime).performClick()
            }
            view.findViewById<Button>(R.id.negativeButton).setOnClickListener { dismiss() }
            view.findViewById<Button>(R.id.positiveButton).setOnClickListener {
                dismiss()
                listener.onDateTimeSelected(dateTimePickerModel)
            }

            hideKeyboardInputInTimePicker(resources.configuration.orientation, timePickerView)
        }

    fun Activity.timePicker(
        dateTimePickerModel: DateTimePickerModel,
        view: View,
        listener: DateTimePickerListener,
        /** for
        case true:calender will be visible
        case false : time Picker will be visible **/
    ): Dialog =
        Dialog(this).apply {
            setContentView(view)
            create()
            show()
            window?.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
            view.findViewById<TimePicker>(R.id.timePicker).gone()
            view.findViewById<LinearLayoutCompat>(R.id.llDate).gone()
            view.findViewById<DatePicker>(R.id.datePicker).gone()
            view.findViewById<TextView>(R.id.tvTime).setOnClickListener {
                view.findViewById<TimePicker>(R.id.timePicker).visible()
                view.findViewById<View>(R.id.ViewDateLine).invisible()
                view.findViewById<View>(R.id.ViewTimeLine).visible()
                view.findViewById<DatePicker>(R.id.datePicker).gone()
                dateTimePickerModel.isTimeSelected=true

            }
            val calendar = Calendar.getInstance()
            calendar.time =dateTimePickerModel.dateTime
            calendar.timeInMillis = System.currentTimeMillis()
            view.findViewById<DatePicker>(R.id.datePicker).init(calendar.get(Calendar.YEAR)-1900,
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                OnDateChangedListener { datePicker, year, month, dayOfMonth ->
                    if(dateTimePickerModel.isDateSelected==true) {
                        val selectedDate = Date(year - 1900, month, dayOfMonth)
                        dateTimePickerModel.dateTime.date = selectedDate.date
                        dateTimePickerModel.dateTime.month = selectedDate.month
                        dateTimePickerModel.dateTime.year = selectedDate.year
                    }
                })
            view.findViewById<DatePicker>(R.id.datePicker). setMinDate(System. currentTimeMillis() - 1000);
            view.findViewById<DatePicker>(R.id.datePicker). updateDate(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))
            val timePickerView = view.findViewById<TimePicker>(R.id.timePicker)
            timePickerView.setHour( dateTimePickerModel.dateTime.hours);
            timePickerView.setMinute(dateTimePickerModel.dateTime.minutes);
            timePickerView.setOnTimeChangedListener { p0, hrs, min ->
                dateTimePickerModel.dateTime.hours = hrs
                dateTimePickerModel.dateTime.minutes = min
            }
            if(dateTimePickerModel.isDateSelected==true) {
                view.findViewById<TextView>(R.id.tvDate).performClick()
            }
            if(dateTimePickerModel.isTimeSelected==true){
                view.findViewById<TextView>(R.id.tvTime).performClick()
            }
            view.findViewById<Button>(R.id.negativeButton).setOnClickListener { dismiss() }
            view.findViewById<Button>(R.id.positiveButton).setOnClickListener {
                dismiss()
                listener.onDateTimeSelected(dateTimePickerModel)
            }

            hideKeyboardInputInTimePicker(resources.configuration.orientation, timePickerView)
        }

    fun hideKeyboardInputInTimePicker(orientation: Int, timePicker: TimePicker)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            try
            {
                if (orientation == Configuration.ORIENTATION_PORTRAIT)
                {
                    ((timePicker.getChildAt(0) as LinearLayout).getChildAt(4) as LinearLayout).getChildAt(0).visibility = View.GONE
                }
                else
                {
                    (((timePicker.getChildAt(0) as LinearLayout).getChildAt(2) as LinearLayout).getChildAt(2) as LinearLayout).getChildAt(0).visibility = View.GONE
                }
            }
            catch (ex: Exception)
            {
            }

        }
    }


    fun Activity.createNewListDialog(
        view: View,
        listener: NewListCreateListener
    ): Dialog =
        Dialog(this).apply {
            setContentView(view)
            create()
            show()
            window?.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
            view.findViewById<EditText>(R.id.etListName).addTextChangedListener(object:TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                   if(p0.toString().trim().isBlank()){
                        view.findViewById<TextView>(R.id.tvlistError).text="List Name is required."
                        view.findViewById<TextView>(R.id.tvlistError).visible()
                    }else {
                        view.findViewById<TextView>(R.id.tvlistError).gone()
                    }
                }

            })
            view.findViewById<CircularProgressButton>(R.id.btnCancel)
                .setOnClickListener { dismiss() }
            view.findViewById<CircularProgressButton>(R.id.btnCreate).setOnClickListener {
                val listName=view.findViewById<EditText>(R.id.etListName).text.toString().trim()
                if (listName.trim().isBlank()) {
                    view.findViewById<TextView>(R.id.tvlistError).text="List Name is required."
                    view.findViewById<TextView>(R.id.tvlistError).visible()
                }else {
                    view.findViewById<TextView>(R.id.tvlistError).gone()
                    listener.onCreateClicked(this,listName)
                }
            }

        }

    fun Activity.renameListDialog(
        listName: String,
        view: View,
        listener: NewListCreateListener
    ): Dialog =
        Dialog(this).apply {
            setContentView(view)
            create()
            show()
            window?.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
            view.findViewById<TextView>(R.id.tvTitle).text = "Edit Name"
            view.findViewById<EditText>(R.id.etListName).addTextChangedListener(object:TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                    if(p0.toString().trim().isBlank()){
                        view.findViewById<TextView>(R.id.tvlistError).text="List Name is required."
                        view.findViewById<TextView>(R.id.tvlistError).visible()
                    }else {
                        view.findViewById<TextView>(R.id.tvlistError).gone()
                    }
                }

            })
            view.findViewById<CircularProgressButton>(R.id.btnCancel)
                .setOnClickListener { dismiss() }
            view.findViewById<CircularProgressButton>(R.id.btnCreate).text = "Update"
            view.findViewById<EditText>(R.id.etListName).hint = listName
            view.findViewById<CircularProgressButton>(R.id.btnCreate).setOnClickListener {
                // view.findViewById<CircularProgressButton>(R.id.btnCreate).startAnimation()
                val listName=view.findViewById<EditText>(R.id.etListName).text.toString().trim()
                if (listName.trim().isBlank()) {
                    view.findViewById<TextView>(R.id.tvlistError).text="List Name is required."
                    view.findViewById<TextView>(R.id.tvlistError).visible()
                }else {
                    view.findViewById<TextView>(R.id.tvlistError).gone()
                    listener.onCreateClicked(this,listName)
                }
            }

        }

    fun Activity.reminderDialog(
        customTime:Date,
        reminder: ReminderEnum=ReminderEnum.MIN05,
        view: View,
        listener: ReminderDialogListener
    ): Dialog =
        Dialog(this).apply {
            val context = this@reminderDialog
            val dialog = this
            var reminderEnum: ReminderEnum? = reminder
            setContentView(view)
            create()
            show()
            var reminderTime=customTime
            window?.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
            val list = arrayOf("5 Min", "10 Min", "30 Min", "60 Min", "Custom")
            val adapter = ArrayAdapter(
                view.context,
                android.R.layout.simple_spinner_item,
                list
            ) // where array_name consists of the items to show in Spinner
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            view.findViewById<Spinner>(R.id.spinner).adapter = adapter
            view.findViewById<Spinner>(R.id.spinner).onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        p0: AdapterView<*>?,
                        p1: View?,
                        position: Int,
                        p3: Long
                    ) {
                        reminderEnum = when (position) {
                            0 -> ReminderEnum.MIN05
                            1 -> ReminderEnum.MIN10
                            2 -> ReminderEnum.MIN30
                            3 -> ReminderEnum.MIN60
                            4 -> ReminderEnum.CUSTOM
                            else -> null
                        }
                        if (reminderEnum == ReminderEnum.CUSTOM) {
                            val day = customTime
                            val calender = Calendar.getInstance()
                            calender.set(day.year+1900, day.month, day.date)
                            val tempDate =calender.time
                            context.dateAndTimePicker(
                                dateTimePickerModel = DateTimePickerModel(dateTime = tempDate, isDateSelected = true, isTimeSelected = false),
                                view=DateAndTimePickerBinding.inflate(
                                    LayoutInflater.from(context)
                                ).root, listener = object : DateTimePickerListener {
                                    override fun onDateTimeSelected(dateTimePickerModel: DateTimePickerModel) {
                                         reminderTime=dateTimePickerModel.dateTime

                                    }

                                }
                            )
                        }

                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }

                }
            view.findViewById<Spinner>(R.id.spinner).setSelection(when(reminderEnum){
                ReminderEnum.MIN05 -> 0
                ReminderEnum.MIN10 -> 1
                ReminderEnum.MIN30 -> 2
                ReminderEnum.MIN60 -> 3
                ReminderEnum.CUSTOM ->4
                else->0
            })
            view.findViewById<CircularProgressButton>(R.id.btnCancel)
                .setOnClickListener { dismiss() }
            view.findViewById<CircularProgressButton>(R.id.btnCreate).setOnClickListener {
                view.findViewById<CircularProgressButton>(R.id.btnCreate).startAnimation()
                listener.onDoneClicked(this, reminderEnum,reminderTime)
            }

        }

    fun Activity.reminderDialogDisabled(
        customTime:Date,
        reminder: ReminderEnum=ReminderEnum.MIN05,
        view: View,
        listener: ReminderDialogListener
    ): Dialog =
        Dialog(this).apply {
            val context = this@reminderDialogDisabled
            val dialog = this
            var reminderEnum: ReminderEnum? = reminder
            setContentView(view)
            create()
            show()
            var reminderTime=customTime
            window?.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
            val list = arrayOf("5 Min", "10 Min", "30 Min", "60 Min", "Custom")
            val adapter = ArrayAdapter(
                view.context,
                android.R.layout.simple_spinner_item,
                list
            ) // where array_name consists of the items to show in Spinner
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            view.findViewById<Spinner>(R.id.spinner).adapter = adapter
            view.findViewById<Spinner>(R.id.spinner).onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        p0: AdapterView<*>?,
                        p1: View?,
                        position: Int,
                        p3: Long
                    ) {
                        reminderEnum = when (position) {
                            0 -> ReminderEnum.MIN05
                            1 -> ReminderEnum.MIN10
                            2 -> ReminderEnum.MIN30
                            3 -> ReminderEnum.MIN60
                            4 -> ReminderEnum.CUSTOM
                            else -> null
                        }
                        if (reminderEnum == ReminderEnum.CUSTOM) {
                            val day = customTime
                            val calender = Calendar.getInstance()
                            calender.set(day.year, day.month, day.day)
                            val tempDate =calender.time
//                            context.dateAndTimePicker(
//                                dateTimePickerModel = DateTimePickerModel(dateTime = tempDate, isDateSelected = true, isTimeSelected = false),
//                                view=DateAndTimePickerBinding.inflate(
//                                    LayoutInflater.from(context)
//                                ).root, listener = object : DateTimePickerListener {
//                                    override fun onDateTimeSelected(dateTimePickerModel: DateTimePickerModel) {
//                                        reminderTime=dateTimePickerModel.dateTime
//
//                                    }
//
//                                }
//                            )
                        }

                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }

                }
            view.findViewById<Spinner>(R.id.spinner).setSelection(when(reminderEnum){
                ReminderEnum.MIN05 -> 0
                ReminderEnum.MIN10 -> 1
                ReminderEnum.MIN30 -> 2
                ReminderEnum.MIN60 -> 3
                ReminderEnum.CUSTOM ->4
                else->0
            })
            view.findViewById<Spinner>(R.id.spinner).isClickable=false
            view.findViewById<Spinner>(R.id.spinner).isEnabled=false
            view.findViewById<CircularProgressButton>(R.id.btnCancel).text="Close"
            view.findViewById<CircularProgressButton>(R.id.btnCreate).text="Edit"
            view.findViewById<CircularProgressButton>(R.id.btnCancel)
                .setOnClickListener { dismiss() }
            view.findViewById<CircularProgressButton>(R.id.btnCreate).setOnClickListener {
//                view.findViewById<CircularProgressButton>(R.id.btnCreate).startAnimation()
                listener.onDoneClicked(this, reminderEnum,reminderTime)
            }

        }
    fun Activity.dialogLoading(): Dialog =
        createDialogWithFullWidthAndHeight(DialogProgressBinding.inflate(LayoutInflater.from(this)).root).apply {
            setCancelable(false)
            create()
            show()
        }
    fun Context.createDialogWithFullWidthAndHeight(view: View): Dialog =
        Dialog(this,R.style.Base_Theme_DailyPlanner).apply {
            setContentView(view)
            create()
            show()
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT)
            window?.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT))
        }

}