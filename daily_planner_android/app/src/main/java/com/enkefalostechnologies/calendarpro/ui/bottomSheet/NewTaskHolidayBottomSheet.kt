package com.enkefalostechnologies.calendarpro.ui.bottomSheet

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.generated.model.ReminderEnum
import com.amplifyframework.datastore.generated.model.RepeatDays
import com.amplifyframework.datastore.generated.model.RepeatType
import com.amplifyframework.datastore.generated.model.TaskType
import com.amplifyframework.datastore.generated.model.Tasks
import com.amplifyframework.datastore.generated.model.User
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.databinding.DateAndTimePickerBinding
import com.enkefalostechnologies.calendarpro.databinding.ReminderDialogBinding
import com.enkefalostechnologies.calendarpro.databinding.RepeatDialogBinding
import com.enkefalostechnologies.calendarpro.model.DateTimePickerModel
import com.enkefalostechnologies.calendarpro.ui.PhoneBookActivity
import com.enkefalostechnologies.calendarpro.util.AmplifyDataModelUtil
import com.enkefalostechnologies.calendarpro.util.AppUtil
import com.enkefalostechnologies.calendarpro.util.AppUtil.differenceInHoursAndMinutes
import com.enkefalostechnologies.calendarpro.util.AppUtil.getDateAfter
import com.enkefalostechnologies.calendarpro.util.AppUtil.getDateAfterMonths
import com.enkefalostechnologies.calendarpro.util.AppUtil.getDateAfterWeeks
import com.enkefalostechnologies.calendarpro.util.AppUtil.getDateAfterYears
import com.enkefalostechnologies.calendarpro.util.AppUtil.getDateStringFromDate
import com.enkefalostechnologies.calendarpro.util.AppUtil.getDayFromDate
import com.enkefalostechnologies.calendarpro.util.AppUtil.getMonthFromDate
import com.enkefalostechnologies.calendarpro.util.AppUtil.getYearYYYYFromDate
import com.enkefalostechnologies.calendarpro.util.AppUtil.requestPermission
import com.enkefalostechnologies.calendarpro.util.AppUtil.showToast
import com.enkefalostechnologies.calendarpro.util.DateTimeHelper.minuteBefore
import com.enkefalostechnologies.calendarpro.util.DateTimePickerListener
import com.enkefalostechnologies.calendarpro.util.DialogUtil.dateAndTimePicker
import com.enkefalostechnologies.calendarpro.util.DialogUtil.reminderDialog
import com.enkefalostechnologies.calendarpro.util.DialogUtil.timePicker
import com.enkefalostechnologies.calendarpro.util.Extension.formatDateToDDMMYY
import com.enkefalostechnologies.calendarpro.util.Extension.formatDateToDDMMYYHHMMSSA
import com.enkefalostechnologies.calendarpro.util.Extension.formatDateToHHMMSS
import com.enkefalostechnologies.calendarpro.util.Extension.getNextDayFromDate
import com.enkefalostechnologies.calendarpro.util.NewTaskBottomSheetListener
import com.enkefalostechnologies.calendarpro.util.ReminderDialogListener
import java.util.Calendar
import java.util.Date

class NewTaskHolidayBottomSheet(var task:Tasks, var listener: NewTaskBottomSheetListener) : BottomSheetDialogFragment() {

    lateinit var radioGroup: RadioGroup
    lateinit var tvCountryCode: TextView
    lateinit var tvReminderDone: TextView
    lateinit var ivImportant: ImageView
    lateinit var btnDone: CircularProgressButton
    lateinit var mView:View
    var isMarkedImportant = false
    var reminderEnum: ReminderEnum? = ReminderEnum.NONE
    var repeatTypes: RepeatType = RepeatType.NONE
    var repeatDayss = listOf<RepeatDays>()
    var selectedDate: Date? = Date()
    var repeatCount:Int?=1
    var endDate: Date? = null
    var isTimeSelected:Boolean=false
    var customDateTime:Date?= null


    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.holiday_bottom_sheet, container, false)
        mView=view
        radioGroup = view.findViewById(R.id.radioGroup)
        tvCountryCode = view.findViewById(R.id.tvCountryCode)
        btnDone = view.findViewById(R.id.btnDone)
        tvReminderDone = view.findViewById(R.id.tvReminderDone)
        ivImportant = view.findViewById(R.id.ivImportant)
         tvReminderDone.setOnClickListener {
             if(reminderEnum==ReminderEnum.NONE && !isTimeSelected) {
                 requireActivity().showToast("Please select time first...")
                 return@setOnClickListener
             }
            requireActivity().reminderDialog(
                customTime=selectedDate!!,
                reminder=reminderEnum?:ReminderEnum.NONE,
                view=ReminderDialogBinding.inflate(LayoutInflater.from(requireActivity())).root,
                listener=object : ReminderDialogListener {
                    override fun onDoneClicked(
                        dialog: Dialog,
                        reminder: ReminderEnum?,
                        reminderTime:Date
                    ) {
                        reminderEnum = reminder
                        customDateTime=when(reminderEnum){
                            ReminderEnum.MIN05 -> selectedDate?.minuteBefore(5)
                            ReminderEnum.MIN10 -> selectedDate?.minuteBefore(10)
                            ReminderEnum.MIN30 -> selectedDate?.minuteBefore(30)
                            ReminderEnum.MIN60 -> selectedDate?.minuteBefore(60)
                            ReminderEnum.CUSTOM-> reminderTime
                            else->selectedDate
                        }
                        tvReminderDone.text = customDateTime?.formatDateToDDMMYYHHMMSSA()
                        /** reset repeat when user changes reminder**/
                        repeatTypes=RepeatType.NONE
                        repeatDayss= mutableListOf()
                        repeatCount=1
                        endDate=selectedDate?.getNextDayFromDate()
                        dialog.dismiss()
                    }

                }
            ).show()
        }

        ivImportant.setOnClickListener {
            if (!isMarkedImportant) {
                ivImportant.setImageDrawable(ivImportant.context.getDrawable(R.drawable.ic_star))
                isMarkedImportant = true
            } else {
                ivImportant.setImageDrawable(ivImportant.context.getDrawable(R.drawable.ic_star_unselected))
                isMarkedImportant = false
            }
        }


        view.findViewById<TextView>(R.id.tvTime).setOnClickListener {
            val calender = Calendar.getInstance()
            calender.set(selectedDate?.year!!, selectedDate?.month!!, selectedDate?.date!!)
            calender.set(Calendar.HOUR_OF_DAY,selectedDate?.hours!!)
            calender.set(Calendar.MINUTE,selectedDate?.minutes!!)
            requireActivity().timePicker(
                DateTimePickerModel(dateTime =calender.time, isDateSelected = false, isTimeSelected = true),
                DateAndTimePickerBinding.inflate(
                    LayoutInflater.from(requireActivity())
                ).root,
                object : DateTimePickerListener {
                    override fun onDateTimeSelected(dateTimePickerModel: DateTimePickerModel) {
                          selectedDate?.hours=dateTimePickerModel.dateTime.hours
                          selectedDate?.minutes=dateTimePickerModel.dateTime.minutes
//                        if(dateTimePickerModel.isDateSelected==true) {
//                            view.findViewById<TextView>(R.id.tvDate).text =
//                                dateTimePickerModel.dateTime.formatDateToDDMMYY()
//                        }
                        if(dateTimePickerModel.isTimeSelected==true){
                            view.findViewById<TextView>(R.id.tvTime).text =
                                selectedDate!!.formatDateToHHMMSS()
                            isTimeSelected=true
                        }
                        /** reset reminder when user changes date time **/
                        reminderEnum= ReminderEnum.NONE
                        customDateTime =  selectedDate
                        tvReminderDone.text ="Add"
                        //tvReminderDone.text  = customDateTime?.formatDateToDDMMYYHHMMSSA()
                        /** reset repeat when user changes reminder**/
                        repeatTypes=RepeatType.NONE
                        repeatDayss= mutableListOf()
                        repeatCount=1
                        endDate=selectedDate?.getNextDayFromDate()
                    }

                }
            )
        }

        view.findViewById<CircularProgressButton>(R.id.btnDone).setOnClickListener {
            if (isFormValidated()) {
                listener.onDoneClicked(
                    this,
                    title = view.findViewById<EditText>(R.id.etTitle).text.toString().trim(),
                    emailPhone ="",
                    taskType =TaskType.HOLIDAY ,
                    description ="",
                    date = selectedDate!!,
                    hours = selectedDate!!.hours,
                    minute = selectedDate!!.minutes,
                    reminder = reminderEnum,
                    isTimeSelected = isTimeSelected,
                    repeatType = repeatTypes,
                    repeatDays = repeatDayss,
                    isImportant = isMarkedImportant,
                    customDate = customDateTime!!,
                    endDate = endDate,
                    rc = repeatCount?:0,
                )
            }
        }
        setData(task)
        return view
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                    dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = true
        }
    }

    private fun isFormValidated(): Boolean {

        if (selectedDate == null) {
            requireActivity().showToast("Select date...")
            return false
        }
        if((reminderEnum!=ReminderEnum.NONE && !isTimeSelected) || (repeatTypes!=RepeatType.NONE && !isTimeSelected)){
            requireActivity().showToast("Please select time first...")
            return false
        }
        return true
    }
    private fun setData(task:Tasks) {
        endDate = task?.endDate?.toDate()
        isTimeSelected=task?.isTimeSelected!!
        repeatCount = task?.repeatCount
        mView?.findViewById<EditText>(R.id.etTitle)?.setText(task?.title)
        repeatTypes = task?.repeatType!!
        repeatDayss = task?.repeatDays!!
        reminderEnum = task?.reminder
        mView?.findViewById<EditText>(R.id.etTitle)?.setText(task.title)
        mView?.findViewById<TextView>(R.id.tvCountryCode)?.setText(task.countryCode)
        if(task?.reminder!=ReminderEnum.NONE) {
            mView?.findViewById<TextView>(R.id.tvReminderDone)?.text = task.customTime?.toDate()?.formatDateToDDMMYYHHMMSSA()
        }
        mView?.findViewById<TextView>(R.id.tvDate)?.text = task.date?.toDate()?.formatDateToDDMMYY()
        if(isTimeSelected) {
            mView?.findViewById<TextView>(R.id.tvTime)?.text = task?.time?.toDate()?.formatDateToHHMMSS()
        }
        selectedDate = task?.time?.toDate()
        customDateTime = task?.customTime?.toDate()
        if (task?.isImportant == true) {
            mView?.findViewById<ImageView>(R.id.ivImportant)?.setImageDrawable(
                AppCompatResources.getDrawable(
                    mView?.findViewById<ImageView>(R.id.ivImportant)!!.context,
                    R.drawable.ic_star
                )
            )
            isMarkedImportant=true
        } else {
            mView?.findViewById<ImageView>(R.id.ivImportant)?.setImageDrawable(
                AppCompatResources.getDrawable(
                    mView?.findViewById<ImageView>(R.id.ivImportant)!!.context,
                    R.drawable.ic_star_unselected
                )
            )
            isMarkedImportant=false
        }
    }

}