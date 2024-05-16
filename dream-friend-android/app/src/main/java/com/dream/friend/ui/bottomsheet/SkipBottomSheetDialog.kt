package com.dream.friend.ui.bottomsheet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.dream.friend.R
import com.dream.friend.ui.add_photo.AddPhotoActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class SkipBottomSheetDialog : BottomSheetDialogFragment(){
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.skip_bottomsheet_layout, container, false)
        view.setBackgroundResource(R.drawable.bottomsheet_bg)
        view.findViewById<MaterialButton>(R.id.btnCompleteNow).setOnClickListener {
               dismiss()
        }
        view.findViewById<TextView>(R.id.tvSkipNow).setOnClickListener {
                Intent(requireActivity(), AddPhotoActivity::class.java).also {
                    startActivity(it)
                    requireActivity().finishAffinity()
                }
        }
        return view
    }

}