package com.dream.friend.ui.bottomsheet.editProfile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSeekBar
import com.dream.friend.R
import com.dream.friend.data.model.BasicFilterRequest
import com.dream.friend.data.model.user.AgeRange
import com.dream.friend.data.model.user.Distance
import com.dream.friend.data.model.user.User
import com.dream.friend.interfaces.BasicFilterListener
import com.dream.friend.interfaces.HeightListener
import com.dream.friend.util.Extensions.gone
import com.dream.friend.util.Extensions.visible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.RangeSlider
import kotlin.math.roundToInt

class BsHeightDialog(var title:String,var prevHeight:Int?, var listener: HeightListener) :
    BottomSheetDialogFragment() {
    var height = 0

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottomsheet_height, container, false)
        view.setBackgroundResource(R.drawable.bottomsheet_bg)
        view.findViewById<TextView>(R.id.tvTitle).text=title
        val txtHeight = view.findViewById<TextView>(R.id.txtHeight)

        val heightSeekbar = view.findViewById<AppCompatSeekBar>(R.id.heightSeekbar)
        prevHeight?.let {
            heightSeekbar.progress=it
            txtHeight.text = "${it} CM"
        }
        heightSeekbar?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seek: SeekBar,
                progress: Int, fromUser: Boolean
            ) {

                if (progress > 0) {
                    txtHeight.text = "${progress} CM"
                    height = progress
                    txtHeight.visible()
                } else {
                    txtHeight.gone()
                }

            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {

            }
        })

        view.findViewById<ImageView>(R.id.ivCross).setOnClickListener {
            dismiss()
        }
        view.findViewById<MaterialButton>(R.id.btnApplyFilters).setOnClickListener {
            dismiss()
            listener.onApplyBtnClicked(height)
        }

        return view
    }

}