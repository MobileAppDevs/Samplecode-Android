package com.dream.friend.ui.bottomsheet.advanceFilter

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import com.dream.friend.R
import com.dream.friend.common.Utils.showToast
import com.dream.friend.common.fontFamily
import com.dream.friend.data.model.NotificationOn
import com.dream.friend.data.model.NotificationRequest
import com.dream.friend.interfaces.SubFilterListener
import com.dream.friend.ui.settings.EditProfileActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class AskForVerificationBottomSheetDialog(var title:String) : BottomSheetDialogFragment(){

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottomsheet_ask_for_verification, container, false)
        view.setBackgroundResource(R.drawable.bottomsheet_bg)
        view.findViewById<TextView>(R.id.tvTitle).text=title
        view.findViewById<ImageView>(R.id.ivCross).setOnClickListener { dismiss() }
        view.findViewById<MaterialButton>(R.id.btnApplyFilters).setOnClickListener {
            dismiss()
            startActivity(Intent(requireActivity(),EditProfileActivity::class.java))
        }

        return view
    }

}