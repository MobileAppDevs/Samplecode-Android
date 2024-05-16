package com.enkefalostechnologies.calendarpro.ui.bottomSheet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import com.amplifyframework.datastore.generated.model.ReminderEnum
import com.amplifyframework.datastore.generated.model.RepeatDays
import com.amplifyframework.datastore.generated.model.RepeatType
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.enkefalostechnologies.calendarpro.R
import java.util.Date


class NativeSharingBottomSheet():  BottomSheetDialogFragment()  {

    lateinit var radioGroup:RadioGroup
    lateinit var tvEmailPhone:TextView
    lateinit var tvReminderDone:TextView
    lateinit var tvRepeatAdd:TextView
    lateinit var etEmailPhone:EditText
    lateinit var ivImportant: ImageView
    lateinit var btnDone:CircularProgressButton

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottomsheet_native_sharing, container, false)
        view.findViewById<CircularProgressButton>(R.id.btnShare).setOnClickListener{
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Daily Planner: plan your daily activities and call easily.")
            val app_url = " https://play.google.com/store/apps/details?id=${requireActivity().packageName}"
            shareIntent.putExtra(Intent.EXTRA_TEXT, app_url)
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable=false
        }
    }
}