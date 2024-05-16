package com.enkefalostechnologies.calendarpro.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

object StorageUtil {
    fun openPermissionSettings(con: Context): Intent {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:${con.packageName}")
        )
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        return intent
    }
}