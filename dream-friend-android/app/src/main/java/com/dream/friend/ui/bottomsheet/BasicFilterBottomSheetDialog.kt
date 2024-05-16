package com.dream.friend.ui.bottomsheet

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.fragment.app.viewModels
import com.dream.friend.R
import com.dream.friend.data.model.BasicFilterRequest
import com.dream.friend.data.model.user.AgeRange
import com.dream.friend.data.model.user.Distance
import com.dream.friend.data.model.user.User
import com.dream.friend.interfaces.BasicFilterListener
import com.dream.friend.ui.viewModel.UserLoginViewModel
import com.dream.friend.util.Extensions.gone
import com.dream.friend.util.Extensions.visible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.RangeSlider
import kotlinx.android.synthetic.main.basic_filters_bottomsheet_layout.ageRangeSlider
import kotlin.math.roundToInt

class BasicFilterBottomSheetDialog(var user: User?, var listener: BasicFilterListener) :
    BottomSheetDialogFragment() {

    private val viewModelUserLogin: UserLoginViewModel by viewModels()
    var minAgeRange: Int? = null
    var maxAgeRange: Int? = null
    var sexualInterest: Int? = null
    var distance: Int? = null

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.basic_filters_bottomsheet_layout, container, false)
        view.setBackgroundResource(R.drawable.bottomsheet_bg)
//        sexualInterest =
//            when (view.findViewById<RadioGroup>(R.id.radioGroup).checkedRadioButtonId) {
//                R.id.rbMan -> 1
//                R.id.rbWoman -> 2
//                R.id.rbOther -> 3
//                else -> null
//            }
        view.findViewById<RadioGroup>(R.id.radioGroup).check(
            when (viewModelUserLogin.getUser()?.interestIn) {
                1 -> R.id.rbMan
                2 -> R.id.rbWoman
                3 -> R.id.rbOther
                else -> R.id.rbMan
            }
        ).also {
            when (view.findViewById<RadioGroup>(R.id.radioGroup).checkedRadioButtonId) {
                R.id.rbMan -> {
                    view.findViewById<View>(R.id.v1).gone()
                    view.findViewById<View>(R.id.v2).visible()
                    sexualInterest = 1
                }

                R.id.rbWoman -> {
                    view.findViewById<View>(R.id.v1).gone()
                    view.findViewById<View>(R.id.v2).gone()
                    sexualInterest = 2
                }

                R.id.rbOther -> {
                    view.findViewById<View>(R.id.v1).visible()
                    view.findViewById<View>(R.id.v2).gone()
                    sexualInterest = 3
                }

                else -> {
                    view.findViewById<View>(R.id.v1).visible()
                    view.findViewById<View>(R.id.v2).visible()
                    sexualInterest = 0
                }
            }
        }
        sexualInterest=viewModelUserLogin.getUser()?.interestIn
        view.findViewById<RadioGroup>(R.id.radioGroup)
            .setOnCheckedChangeListener { group, checkedId ->
                when (view.findViewById<RadioGroup>(R.id.radioGroup).checkedRadioButtonId) {
                    R.id.rbMan -> {
                        view.findViewById<View>(R.id.v1).gone()
                        view.findViewById<View>(R.id.v2).visible()
                        sexualInterest = 1
                    }

                    R.id.rbWoman -> {
                        view.findViewById<View>(R.id.v1).gone()
                        view.findViewById<View>(R.id.v2).gone()
                        sexualInterest = 2
                    }

                    R.id.rbOther -> {
                        view.findViewById<View>(R.id.v1).visible()
                        view.findViewById<View>(R.id.v2).gone()
                        sexualInterest = 3
                    }

                    else -> {
                        view.findViewById<View>(R.id.v1).visible()
                        view.findViewById<View>(R.id.v2).visible()
                        sexualInterest = 0
                    }
                }
            }
        val rangeSlider = view.findViewById<RangeSlider>(R.id.ageRangeSlider)
        val txtAge = view.findViewById<TextView>(R.id.txtAge)
        if(viewModelUserLogin.getUser()?.ageRange?.minAge==null && viewModelUserLogin.getUser()?.ageRange?.maxAge==null) {
            rangeSlider.setValues(18f,32f)

        }else{
            rangeSlider.setValues(
                viewModelUserLogin.getUser()?.ageRange?.minAge?.toString()?.toFloat(), viewModelUserLogin.getUser()?.ageRange?.maxAge.toString()?.toFloat()
            )
        }
        if(viewModelUserLogin.getUser()?.ageRange?.minAge==null &&  viewModelUserLogin.getUser()?.ageRange?.maxAge==null) {
            txtAge.text = "18-32"
        }else{
            txtAge.text="${viewModelUserLogin.getUser()?.ageRange?.minAge}-${viewModelUserLogin.getUser()?.ageRange?.maxAge}"
        }
        txtAge.visible()
        rangeSlider.addOnChangeListener { slider, value, fromUser ->
            if (fromUser) {
                minAgeRange = rangeSlider.values[0].roundToInt()
                maxAgeRange = rangeSlider.values[1].roundToInt()
                if (minAgeRange == maxAgeRange) {
                    txtAge.gone()
                } else {
                    txtAge.text = "$minAgeRange-$maxAgeRange"
                    txtAge.visible()
                }
            }
        }

        val distanceSeekbar = view.findViewById<AppCompatSeekBar>(R.id.distanceSeekbar)
        val txtDistance = view.findViewById<TextView>(R.id.txtDistance)
        viewModelUserLogin.getUser()?.maxDistance?.distance?.let {
            if(it==200){
                distanceSeekbar.progress = it
                txtDistance.text = "Whole Country"
                txtDistance.visible()
            }else {
                distanceSeekbar.progress = it
                txtDistance.text = "0-${it}KM"
                txtDistance.visible()
            }
            distance=it
        }
        distanceSeekbar?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seek: SeekBar,
                progress: Int, fromUser: Boolean
            ) {

                if (progress > 0) {
                    if(progress==200){
                        txtDistance.text = "Whole Country"
                    }else {
                        txtDistance.text = "0-${progress}KM"
                    }
                    distance = progress
                    txtDistance.visible()
                } else {
                    txtDistance.gone()
                }

            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {

            }
        })
        view.findViewById<TextView>(R.id.tvAdvanceFilter).setOnClickListener {
            listener.onAdvanceFilterClicked()
        }
        view.findViewById<ImageView>(R.id.ivCross).setOnClickListener {
            dismiss()
        }
        view.findViewById<MaterialButton>(R.id.btnApplyFilters).setOnClickListener {
            listener.onBasicFilterApplyBtnClicked(
                BasicFilterRequest(
                    ageRange = if (maxAgeRange != null && minAgeRange != null) AgeRange(
                        maxAge = maxAgeRange,
                        minAge = minAgeRange
                    ) else null,
                    maxDistance = if (distance != null) Distance(distance = distance) else null,
                    sexualInterest = if (sexualInterest != null) sexualInterest else null
                )
            )
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
        }
    }
}