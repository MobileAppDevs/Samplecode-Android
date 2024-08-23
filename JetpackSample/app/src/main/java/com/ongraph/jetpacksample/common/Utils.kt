package com.ongraph.jetpacksample.common

import android.content.Context
import android.widget.Toast

object Utils {
    fun Context.showToast(message: String?) {
        Toast.makeText(
            this,
            "$message",
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Helper function to check if the email and password fields are not empty or blank.
     *
     * @param email The email input from the user.
     * @param password The password input from the user.
     * @return true if both fields are non-null and not blank; false otherwise.
     */
    fun checkFields(email: String?, password: String?): Boolean {
        return !email.isNullOrBlank() && !password.isNullOrBlank()
    }

    /**
     * Validates the input fields for user registration or login.
     *
     * @param firstname The first name of the user, which must not be null or blank.
     * @param lastname The last name of the user, which must not be null or blank.
     * @param email The email address of the user, which must not be null or blank.
     * @param password The password provided by the user, which must not be null or blank.
     * @param confirmPassword The password confirmation field, which must match the password and be at least 8 characters long.
     * @return True if all fields are valid; false otherwise. This includes:
     *         - Non-null and non-blank first name, last name, email, and password
     *         - Password and confirmPassword must match
     *         - Password must be at least 8 characters long
     */
    fun checkFields(firstname: String?, lastname: String?, email: String?, password: String?, confirmPassword: String?): Boolean {
        return !firstname.isNullOrBlank() && !lastname.isNullOrBlank() && !email.isNullOrBlank() && !password.isNullOrBlank() && password == confirmPassword && password.length >= 8
    }
}