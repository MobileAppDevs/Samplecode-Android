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
import com.dream.friend.interfaces.BlockAndReportThisPersonListener
import com.dream.friend.interfaces.BlockDialog1Listener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BlockAndReportThisPersonBottomSheet(var user: User,var listener: BlockAndReportThisPersonListener): BottomSheetDialogFragment(),View.OnClickListener{
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottomsheet_block_and_report_this_person, container, false)
        view.findViewById<LinearLayoutCompat>(R.id.bgSet).setBackgroundResource(R.drawable.bottomsheet_bg_yello)
       view.findViewById<TextView>(R.id.tvFakeProfile).setOnClickListener(this)
       view.findViewById<TextView>(R.id.tvIamjustNotInterested).setOnClickListener(this)
       view.findViewById<TextView>(R.id.tvInappropriateContent).setOnClickListener(this)
       view.findViewById<TextView>(R.id.tvScamCommercial).setOnClickListener(this)
       view.findViewById<TextView>(R.id.tvIdentityBasedHate).setOnClickListener(this)
       view.findViewById<TextView>(R.id.tvUnderage).setOnClickListener(this)
        return view
    }

    override fun onClick(v: View?) {
        dismiss()
        listener.onItemClicked(user)
    }

}