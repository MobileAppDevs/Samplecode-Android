package com.dream.friend.ui.bottomsheet.realtimeImage

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
import com.dream.friend.interfaces.PhotoDeleteListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DeleteAndDismissBottomSheet(var fileName:String,var listener: PhotoDeleteListener): BottomSheetDialogFragment(){
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottomsheet_photo_delete, container, false)
        view.setBackgroundResource(R.drawable.bottomsheet_bg)
        view.findViewById<TextView>(R.id.tvDeletePhoto).setOnClickListener{
            dismiss()
            listener.onDeleteClicked(fileName)
        }
        view.findViewById<TextView>(R.id.tvDismiss).setOnClickListener {
            dismiss()
        }
        return view
    }

}