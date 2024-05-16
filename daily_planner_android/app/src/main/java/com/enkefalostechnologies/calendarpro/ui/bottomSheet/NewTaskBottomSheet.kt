package com.enkefalostechnologies.calendarpro.ui.bottomSheet

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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
import androidx.core.app.ActivityCompat
import com.amplifyframework.datastore.generated.model.ReminderEnum
import com.amplifyframework.datastore.generated.model.RepeatDays
import com.amplifyframework.datastore.generated.model.RepeatType
import com.amplifyframework.datastore.generated.model.TaskType
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.constant.Constants
import com.enkefalostechnologies.calendarpro.databinding.DateAndTimePickerBinding
import com.enkefalostechnologies.calendarpro.databinding.MsgDialogBinding
import com.enkefalostechnologies.calendarpro.databinding.ReminderDialogBinding
import com.enkefalostechnologies.calendarpro.databinding.RepeatDialogBinding
import com.enkefalostechnologies.calendarpro.model.DateTimePickerModel
import com.enkefalostechnologies.calendarpro.ui.PhoneBookActivity
import com.enkefalostechnologies.calendarpro.ui.userconfirmation.UserConfirmationScreen
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
import com.enkefalostechnologies.calendarpro.util.Extension.formatDateToDDMMYY
import com.enkefalostechnologies.calendarpro.util.Extension.formatDateToDDMMYYHHMMSSA
import com.enkefalostechnologies.calendarpro.util.Extension.formatDateToHHMMSS
import com.enkefalostechnologies.calendarpro.util.Extension.getNextDayFromDate
import com.enkefalostechnologies.calendarpro.util.Extension.gone
import com.enkefalostechnologies.calendarpro.util.Extension.visible
import com.enkefalostechnologies.calendarpro.util.MsgAlertDialogListener
import com.enkefalostechnologies.calendarpro.util.NewTaskBottomSheetListener
import com.enkefalostechnologies.calendarpro.util.ReminderDialogListener
import com.enkefalostechnologies.calendarpro.util.dialog.messageAlertDialog
import com.enkefalostechnologies.calendarpro.util.dialog.repeatDialog
import com.enkefalostechnologies.calendarpro.util.repeatDialogListener
import java.util.Date

class NewTaskBottomSheet(var sD: Date,var listener: NewTaskBottomSheetListener) : BottomSheetDialogFragment() {

    lateinit var radioGroup: RadioGroup
    lateinit var tvEmailPhone: TextView
    lateinit var tvReminderDone: TextView
    lateinit var tvRepeatAdd: TextView
    lateinit var etEmailPhone: EditText
    lateinit var ivImportant: ImageView
    lateinit var btnDone: CircularProgressButton
    lateinit var ivEmailPhone: ImageView
    var isNormalTask: Boolean = true
    var isMarkedImportant = false
    var reminderEnum: ReminderEnum? = ReminderEnum.NONE
    var repeatTypes: RepeatType = RepeatType.NONE
    var repeatDayss = listOf<RepeatDays>()
    var selectedDate: Date? = sD
    var repeatCount:Int?=1
    var endDate: Date? = null
    var isTimeSelected:Boolean=false
    var customDateTime:Date?= null

    init {
        sD.hours=0
        sD.minutes=0
        sD.seconds=0
        selectedDate = sD
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.new_task_bottom_sheet, container, false)
        radioGroup = view.findViewById(R.id.radioGroup)
        tvEmailPhone = view.findViewById(R.id.tvEmailPhone)
        etEmailPhone = view.findViewById(R.id.etEmailPhone)
        btnDone = view.findViewById(R.id.btnDone)
        tvReminderDone = view.findViewById(R.id.tvReminderDone)
        tvRepeatAdd = view.findViewById(R.id.tvRepeatAdd)
        ivImportant = view.findViewById(R.id.ivImportant)
        ivEmailPhone = view.findViewById(R.id.ivEmailPhone)
        reminderEnum=ReminderEnum.NONE
        customDateTime=selectedDate
        endDate=selectedDate?.getNextDayFromDate()
        view.findViewById<TextView>(R.id.tvDate).text = selectedDate?.formatDateToDDMMYY()
       // tvReminderDone.text = customDateTime?.formatDateToDDMMYYHHMMSSA()
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
                        tvRepeatAdd.text= "Add"
                        dialog.dismiss()
                    }

                }
            ).show()
        }
        tvRepeatAdd.setOnClickListener {
            if(repeatTypes==RepeatType.NONE && !isTimeSelected) {
                requireActivity().showToast("Please select time first...")
                return@setOnClickListener
            }
            requireActivity().repeatDialog(
                repeatTypes=repeatTypes,
                repeatDay=repeatDayss.toMutableList(),
                taskDate = selectedDate!!,
                endDate = null,
                binding = RepeatDialogBinding.inflate(LayoutInflater.from(requireActivity())),
                listener = object : repeatDialogListener {
                    override fun onDoneClicked(
                        repeatType: RepeatType,
                        repeatDays: List<RepeatDays>,
                        eDate: Date?,
                        rc:Int,
                        d: Dialog
                    ) {
                        d.dismiss()
                        endDate = eDate
                        repeatDayss = repeatDays
                        repeatTypes = repeatType
                        repeatCount=rc
                        var txt = if (repeatDays.isEmpty()) "Add" else ""
                        if (repeatDays.isNotEmpty()) {
                            repeatDays.map {
                                txt += when (it) {
                                    RepeatDays.MON -> "Mon"
                                    RepeatDays.TUE -> "Tue"
                                    RepeatDays.WED -> "Wed"
                                    RepeatDays.THU -> "Thu"
                                    RepeatDays.FRI -> "Fri"
                                    RepeatDays.SAT -> "Sat"
                                    RepeatDays.SUN -> "Sun"
                                    else -> ""
                                }
                                txt += ", "
                            }
                            tvRepeatAdd.text = txt.dropLast(2)
                            tvRepeatAdd.isSingleLine = true
                            tvRepeatAdd.ellipsize = TextUtils.TruncateAt.END
                        } else {
                            tvRepeatAdd.text = repeatType.name.capitalize()
                        }
                    }

                },
                repeatCount = repeatCount?:1).show()
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


        when (radioGroup.checkedRadioButtonId) {
            R.id.rbTodo -> {
                ivEmailPhone.gone()
                tvEmailPhone.gone()
                etEmailPhone.gone()
                isNormalTask = true
            }

            R.id.rbCallsEmails -> {
                ivEmailPhone.visible()
                tvEmailPhone.visible()
                etEmailPhone.visible()
                isNormalTask = false
            }
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbTodo -> {
                    group.check(R.id.rbTodo)
                    ivEmailPhone.gone()
                    tvEmailPhone.gone()
                    etEmailPhone.gone()
                    isNormalTask = true
                }

                R.id.rbCallsEmails -> {
                    if(!hasPermissions(requireContext(),Manifest.permission.READ_CONTACTS)){
                        if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_CONTACTS)) {
                            requireActivity().messageAlertDialog(
                                MsgDialogBinding.inflate(LayoutInflater.from(requireActivity())),
                                title = "Permission",
                                msg = "Calendar Pro app will fetch contacts for your convenience.",
                                btnText = "Give Permission",
                                negativeBtnText = "Dismiss",
                                listener = object : MsgAlertDialogListener {
                                    override fun onDoneClicked() {
                                        dialog?.visible()
                                        val intent = Intent()

                                        // For API 29 (Android 10) and above, use the general settings screen
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                            intent.action = Settings.ACTION_APPLICATION_SETTINGS
                                        } else {
                                            // For API 28 (Android 9) and below, use the specific app details screen
                                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                            val uri = Uri.fromParts("package",requireActivity().application.packageName, null)
                                            intent.data = uri
                                        }
                                        startActivity(intent)
                                    }
                                }).show()
                        }else {
                            requestPermission(
                                Manifest.permission.READ_CONTACTS,
                                1358
                            )
                        }
                        ivEmailPhone.gone()
                        tvEmailPhone.gone()
                        etEmailPhone.gone()
                        isNormalTask = true
                        group.check(R.id.rbTodo)
                        return@setOnCheckedChangeListener
                    }
                    ivEmailPhone.visible()
                    tvEmailPhone.visible()
                    etEmailPhone.visible()
                    isNormalTask = false
                }
            }


        }

        view.findViewById<TextView>(R.id.tvDate).setOnClickListener {
            requireActivity().dateAndTimePicker(
                DateTimePickerModel(dateTime = selectedDate!!, isDateSelected = true, isTimeSelected = false),
                DateAndTimePickerBinding.inflate(
                    LayoutInflater.from(requireActivity())
                ).root,
                object : DateTimePickerListener {
                    override fun onDateTimeSelected(dateTimePickerModel: DateTimePickerModel) {
                        selectedDate = dateTimePickerModel.dateTime
                        if(dateTimePickerModel.isDateSelected==true) {
                            view.findViewById<TextView>(R.id.tvDate).text =
                                dateTimePickerModel.dateTime.formatDateToDDMMYY()
                        }
                        if(dateTimePickerModel.isTimeSelected==true){
                            view.findViewById<TextView>(R.id.tvTime).text =
                                dateTimePickerModel.dateTime.formatDateToHHMMSS()
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
                        tvRepeatAdd.text= "Add"
                    }

                }
            )
        }
        view.findViewById<TextView>(R.id.tvTime).setOnClickListener {
            requireActivity().dateAndTimePicker(
                DateTimePickerModel(dateTime = selectedDate!!, isDateSelected = false, isTimeSelected = true),
                DateAndTimePickerBinding.inflate(
                    LayoutInflater.from(requireActivity())
                ).root,
                object : DateTimePickerListener {
                    override fun onDateTimeSelected(dateTimePickerModel: DateTimePickerModel) {
                        selectedDate = dateTimePickerModel.dateTime
                        if(dateTimePickerModel.isDateSelected==true) {
                            view.findViewById<TextView>(R.id.tvDate).text =
                                dateTimePickerModel.dateTime.formatDateToDDMMYY()
                        }
                        if(dateTimePickerModel.isTimeSelected==true){
                            view.findViewById<TextView>(R.id.tvTime).text =
                                dateTimePickerModel.dateTime.formatDateToHHMMSS()
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
                        tvRepeatAdd.text= "Add"
                    }

                }
            )
        }

        view.findViewById<CircularProgressButton>(R.id.btnDone).setOnClickListener {
            if (isFormValidated()) {
                listener.onDoneClicked(
                    this,
                    title = view.findViewById<EditText>(R.id.etTitle).text.toString().trim(),
                    emailPhone = view.findViewById<EditText>(R.id.etEmailPhone).text.toString()
                        .trim(),
                    taskType = if (isNormalTask) TaskType.TODO else TaskType.CALL_EMAIL,
                    description = view.findViewById<EditText>(R.id.etDescription).text.toString()
                        .trim(),
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
                    rc = repeatCount?:0
                )
            }
        }

        ivEmailPhone.setOnClickListener {
            resultLauncherContacts.launch(Intent(requireContext(), PhoneBookActivity::class.java))
        }
        return view
    }

    private var resultLauncherContacts =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                etEmailPhone.setText(result.data?.getStringExtra("Contact").toString())
            }
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
        if (view?.findViewById<EditText>(R.id.etTitle)?.text.toString().trim().isBlank()) {
            requireActivity().showToast("Enter valid title...")
            return false
        }
        if (selectedDate == null) {
            requireActivity().showToast("Select date...")
            return false
        }
        if((reminderEnum!=ReminderEnum.NONE && !isTimeSelected) || (repeatTypes!=RepeatType.NONE && !isTimeSelected)){
            requireActivity().showToast("Please select time first...")
            return false
        }
        if(repeatTypes ==RepeatType.WEEK && repeatDayss.isEmpty()){
            requireActivity().showToast("Please select week days...")
            return false
        }
//        when(repeatTypes){
//            RepeatType.NONE->{
//                endDate=selectedDate?.getNextDayFromDate()
//            }
//            RepeatType.DAY->{
//                val eDate=selectedDate?.getDateAfter(repeatCount?:1)
//                if(endDate?.compareTo(eDate)!! != 0){
//                    requireActivity().showToast("End date should be equal to ${eDate?.getDateStringFromDate()} ${eDate?.getMonthFromDate()} ${eDate?.getYearYYYYFromDate()}")
//                    return false
//                }
//                endDate=eDate
//            }
//            RepeatType.WEEK->{
//                val eDate=selectedDate?.getDateAfterWeeks(repeatCount?:1)
//                if(endDate?.compareTo(eDate)!! != 0){
//                    requireActivity().showToast("End date should be equal to ${eDate?.getDateStringFromDate()} ${eDate?.getMonthFromDate()} ${eDate?.getYearYYYYFromDate()}")
//                    return false
//                }
//                endDate=eDate
//            }
//            RepeatType.MONTH->{
//                val eDate=selectedDate?.getDateAfterMonths(repeatCount?:1)
//                if(endDate?.compareTo(eDate)!! != 0){
//                    requireActivity().showToast("End date should be equal to ${eDate?.getDateStringFromDate()} ${eDate?.getMonthFromDate()} ${eDate?.getYearYYYYFromDate()}")
//                    return false
//                }
//                endDate=eDate
//            }
//            RepeatType.YEAR->{
//                val eDate=selectedDate?.getDateAfterYears(repeatCount?:1)
//                if(endDate?.compareTo(eDate)!! != 0){
//                    requireActivity().showToast("End date should be equal to ${eDate?.getDateStringFromDate()} ${eDate?.getMonthFromDate()} ${eDate?.getYearYYYYFromDate()}")
//                    return false
//                }
//                endDate=eDate
//            }
//        }
        return true
    }
    private fun hasPermissions(context: Context, vararg permissions: String): Boolean {

        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }
}