package com.dream.friend.ui.bottomsheet.block

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.dream.friend.R
import com.dream.friend.data.model.user.User
import com.dream.friend.interfaces.BlockBottomSheetListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class BlockBottomSheet(var user: User, var listener: BlockBottomSheetListener): BottomSheetDialogFragment(){
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottomsheet_block, container, false)
        view.setBackgroundResource(R.drawable.bottomsheet_bg)
        view.findViewById<TextView>(R.id.tvTitle).setText("Block ${user.userName}?")
        view.findViewById<MaterialButton>(R.id.btnBlock).setOnClickListener {
            dismiss()
            listener.onBlockBtnClicked(user)
        }
        view.findViewById<TextView>(R.id.btnReturnToProfile).setOnClickListener {
            dismiss()
        }

        return view
    }

}