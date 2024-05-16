package com.dream.friend.ui.bottomsheet

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.dream.friend.R
import com.dream.friend.interfaces.BasicFilterListener
import com.dream.friend.interfaces.SuccessBottomSheetListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class SuccessfullyVerifiedBottomSheetDialog(var forString:String,var listener: SuccessBottomSheetListener) :
    BottomSheetDialogFragment() {
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottomsheet_successfully_verified, container, false)
        view.setBackgroundResource(R.drawable.bottomsheet_bg)
        view.findViewById<TextView>(R.id.desc).text = "Yahoo! You have successfully verified your $forString."
        view.findViewById<MaterialButton>(R.id.btnGoHome).setOnClickListener {
            listener.onGoHomeButtonClicked()
        }
        return view
    }

}