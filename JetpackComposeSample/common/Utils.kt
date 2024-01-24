package com.ongraph.whatsappclone.common

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.ongraph.whatsappclone.common.Utils.fromJson
import com.ongraph.whatsappclone.model.ChatOrGroupModel

object Utils {
    fun Context.showToast(message: String?) {
        Toast.makeText(
            this,
            "$message",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun toJson(data:Any): String? {
       return Gson().toJson(data)
    }
    fun fromJson(jsonString:String,dataClass:Class<Any>): Class<Any>{
        return Gson().fromJson(jsonString,dataClass::class.java)
    }
}