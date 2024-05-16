package com.enkefalostechnologies.calendarpro.util

import com.enkefalostechnologies.calendarpro.constant.Constants
import java.util.regex.Pattern

object ValidationUtil {
    fun String.validateEmail()= Pattern.compile(Constants.EMAIL_PATTERN).matcher(this).matches()

    fun String.validateField()=this.isNotBlank()
    fun String.validatePassword()=this.isNotBlank() && Pattern.compile(Constants.PASSWORD_PATTERN).matcher(this).matches()

    fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false
        if (password.filter { it.isDigit() }.firstOrNull() == null) return false
        if (password.filter { it.isLetter() }.filter { it.isUpperCase() }.firstOrNull() == null) return false
        if (password.filter { it.isLetter() }.filter { it.isLowerCase() }.firstOrNull() == null) return false
        if (password.filter { !it.isLetterOrDigit() }.firstOrNull() == null) return false

        return true
    }
}