package com.dream.friend.ui.bottomsheet.block

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.dream.friend.R
import com.dream.friend.data.model.user.User
import com.dream.friend.interfaces.BasicFilterListener
import com.dream.friend.interfaces.BlockDialog1Listener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BlockAndReportBottomSheet(var user: User, var listener: BlockDialog1Listener): BottomSheetDialogFragment(){
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottomsheet_block_and_report, container, false)
        view.setBackgroundResource(R.drawable.bottomsheet_bg)
        view.findViewById<TextView>(R.id.tvBlock).setOnClickListener{
            dismiss()
            listener.onBlockClicked(user)
        }
        view.findViewById<TextView>(R.id.tvBlockReport).setOnClickListener {
            dismiss()
            listener.onBlockAndReportClicked(user)
        }
        return view
    }

}