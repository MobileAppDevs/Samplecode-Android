package com.enkefalostechnologies.calendarpro.ui.bottomSheet

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.enkefalostechnologies.calendarpro.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DeleteOptionBottomSheet(val listener:OptionListener): BottomSheetDialogFragment(){
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.delete_option_bottomsheet, container, false)
        view.setBackgroundResource(R.drawable.bottomsheet_bg)
        view.findViewById<TextView>(R.id.tvOption1).setOnClickListener{
            dismiss()
            listener.onOption1Selected()
        }
        view.findViewById<TextView>(R.id.tvOption2).setOnClickListener {
            dismiss()
            listener.onOption2Selected()
        }
        return view
    }

}
class UpdateOptionBottomSheet(val listener:OptionListener): BottomSheetDialogFragment(){
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.update_option_bottomsheet, container, false)
        view.setBackgroundResource(R.drawable.bottomsheet_bg)
        view.findViewById<TextView>(R.id.tvOption1).setOnClickListener{
            dismiss()
            listener.onOption1Selected()
        }
        view.findViewById<TextView>(R.id.tvOption2).setOnClickListener {
            dismiss()
            listener.onOption2Selected()
        }
        return view
    }

}

interface OptionListener{
    fun onOption1Selected()
    fun onOption2Selected()
}