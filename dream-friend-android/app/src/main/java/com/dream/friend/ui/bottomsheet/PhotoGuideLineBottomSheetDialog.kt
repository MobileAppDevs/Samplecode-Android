package com.dream.friend.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.dream.friend.R
import com.dream.friend.interfaces.PhotoGuideLineListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class PhotoGuideLineBottomSheetDialog(var listener:PhotoGuideLineListener) : BottomSheetDialogFragment(){
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.photo_guideline_bottomsheet_layout, container, false)
        view.setBackgroundResource(R.drawable.bottomsheet_bg)
        view.findViewById<MaterialButton>(R.id.btnUploadNow).setOnClickListener {
               dismiss()
            listener.onUploadBtnClicked()
        }
        view.findViewById<ImageView>(R.id.ivCross).setOnClickListener {
                dismiss()
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