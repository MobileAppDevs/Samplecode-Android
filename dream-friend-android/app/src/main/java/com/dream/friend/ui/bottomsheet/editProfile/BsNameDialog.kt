package com.dream.friend.ui.bottomsheet.editProfile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
import com.dream.friend.interfaces.NameListener
import com.dream.friend.util.Extensions.gone
import com.dream.friend.util.Extensions.visible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.RangeSlider
//import kotlinx.android.synthetic.main.chat_items.view.tvName
import kotlin.math.roundToInt

class BsNameDialog(var title:String,var prevName: String?, var listener: NameListener) :
    BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottomsheet_sub_name, container, false)
        view.setBackgroundResource(R.drawable.bottomsheet_bg)
        view.findViewById<TextView>(R.id.tvTitle).text=title
        val etName=view.findViewById<EditText>(R.id.etName)
          etName.setText(prevName)
        view.findViewById<ImageView>(R.id.ivCross).setOnClickListener {
            dismiss()
        }
        view.findViewById<MaterialButton>(R.id.btnApplyFilters).setOnClickListener {
            dismiss()
            listener.onApplyBtnClicked(etName.text.toString().trim())
        }
        return view
    }

}