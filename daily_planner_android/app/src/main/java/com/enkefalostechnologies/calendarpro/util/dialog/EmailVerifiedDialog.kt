package com.enkefalostechnologies.calendarpro.util.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.annotation.DrawableRes
import com.enkefalostechnologies.calendarpro.databinding.MsgDialog2Binding
import com.enkefalostechnologies.calendarpro.databinding.MsgDialogBinding
import com.enkefalostechnologies.calendarpro.util.Extension.gone
import com.enkefalostechnologies.calendarpro.util.Extension.visible
import com.enkefalostechnologies.calendarpro.util.MsgAlertDialogListener

fun Activity.messageAlertDialog(

    binding: MsgDialogBinding,
    btnText:String="",
    negativeBtnText:String="",
    title:String="Title",
    iconVisible:Boolean=false,
    msg: String = "",
    listener: MsgAlertDialogListener
): Dialog =
    Dialog(this).apply {
        window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        setContentView(binding.root)
        create()
        setCancelable(false)
        show()
        if(iconVisible){binding.icon.visible()}else{binding.icon.gone()}
        if(title.isNotBlank()){
            binding.tvTitle.visible()
            binding.tvTitle.text =title
        }else{binding.tvTitle.gone()}
        if(msg.isNotBlank()){
            binding.tvMsg.visible()
            binding.tvMsg.text = msg
        }else{binding.tvMsg.gone()}
        if(btnText.isNotBlank()){
            binding.btnDone.text = btnText
        }else{binding.btnDone.gone()}

        if(negativeBtnText.isNotBlank()){
            binding.btnCancel.text = negativeBtnText
        }else{binding.btnCancel.gone()}

        binding.btnDone.setOnClickListener {
            dismiss()
            listener.onDoneClicked()
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

fun Activity.messageAlertDialog2(
    binding: MsgDialog2Binding,
    btnText:String="",
    negativeBtnText:String="",
    @DrawableRes icon:Int?=null,
    iconVisible:Boolean=false,
    msg: String = "",
    listener: MsgAlertDialogListener
): Dialog =
    Dialog(this).apply {
        window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        setContentView(binding.root)
        create()
        setCancelable(false)
        show()
        if(icon!=null){
            binding.icon.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
        }
        if(iconVisible){binding.icon.visible()}else{binding.icon.gone()}
        if(msg.isNotBlank()){
            binding.tvMsg.visible()
            binding.tvMsg.text = msg
        }else{binding.tvMsg.gone()}
        if(btnText.isNotBlank()){
            binding.btnDone.text = btnText
        }else{binding.btnDone.gone()}

        if(negativeBtnText.isNotBlank()){
            binding.btnCancel.text = negativeBtnText
        }else{binding.btnCancel.gone()}

        binding.btnDone.setOnClickListener {
            dismiss()
            listener.onDoneClicked()
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }