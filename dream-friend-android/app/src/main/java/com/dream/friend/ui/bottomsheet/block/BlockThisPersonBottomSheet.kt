package com.dream.friend.ui.bottomsheet.block

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.dream.friend.R
import com.dream.friend.data.model.user.User
import com.dream.friend.interfaces.BlockDialog1Listener
import com.dream.friend.interfaces.BlockThisPersonListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BlockThisPersonBottomSheet(var user: User, var listener: BlockThisPersonListener) : BottomSheetDialogFragment() {
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottomsheet_block_this_person, container, false)
        view.findViewById<LinearLayoutCompat>(R.id.bgSet)
            .setBackgroundResource(R.drawable.bottomsheet_bg_yello)
        view.findViewById<TextView>(R.id.tvBlock).setOnClickListener {
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