package com.enkefalostechnologies.calendarpro.router

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.media.Image
import android.util.Log
import android.widget.ImageView
import com.enkefalostechnologies.calendarpro.constant.Constants
import com.enkefalostechnologies.calendarpro.ui.GetStartedActivity
import com.enkefalostechnologies.calendarpro.ui.HomeActivity
import com.enkefalostechnologies.calendarpro.ui.LoginActivity
import com.enkefalostechnologies.calendarpro.ui.ProfileActivity
import com.enkefalostechnologies.calendarpro.ui.country_selection.CountrySelectionActivity
import com.enkefalostechnologies.calendarpro.ui.forgot_password.ForgotPasswordOtpVerificationScreen
import com.enkefalostechnologies.calendarpro.ui.forgot_password.ResetPasswordScreen
import com.enkefalostechnologies.calendarpro.ui.userconfirmation.UserConfirmationScreen
import com.enkefalostechnologies.calendarpro.ui.verifyemail.VerifyEmailOtpScreen
import com.enkefalostechnologies.calendarpro.ui.verifyemail.VerifyEmailScreen

object Router {
    fun Context.navigateToHome(view:ImageView?){
        Log.d("syncing","navigateToHome")
//        val options: ActivityOptions =
//            ActivityOptions.makeSceneTransitionAnimation(this as Activity, view, "Robot")
        val intent=Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
    fun Context.navigateToCountrySelectionScreen(){
        Log.d("syncing","navigateToHome")
        val intent=Intent(this, CountrySelectionActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
    fun Context.navigateToGetStartedContactPermissionScreen()= startActivity(Intent(this, GetStartedActivity::class.java)).also { (this as Activity).finishAffinity()}
    fun Context.navigateToGetStartedDisplayOverlayPermissionScreen()= startActivity(Intent(this, GetStartedActivity::class.java)).also { (this as Activity).finishAffinity()}
    fun Context.navigateToLoginScreen() {
        return startActivity(Intent(this, LoginActivity::class.java).apply {
                putExtra("view","login")
            })
    }

    fun Context.navigateToUserConfirmationScreen(email:String)=startActivity(Intent(this, UserConfirmationScreen::class.java).putExtra(Constants.INTENT_EMAIL,email))
    fun Context.navigateToUserConfirmationScreenForLogin(email:String,password:String)=startActivity(Intent(this, UserConfirmationScreen::class.java).putExtra(Constants.INTENT_EMAIL,email).putExtra(Constants.INTENT_PASSWORD,password))

    fun Context.navigateToForgotPasswordOtpVerificationScreen(email:String)=startActivity(Intent(this, ResetPasswordScreen::class.java).putExtra(Constants.INTENT_EMAIL,email))

    fun Context.navigateToResetPasswordScreen(code:String,email:String)=startActivity(Intent(this, ResetPasswordScreen::class.java).putExtra(Constants.INTENT_CODE,code).putExtra(Constants.INTENT_EMAIL,email))
    fun Context.navigateToVerifyEmailScreen(email:String)=startActivity(Intent(this, VerifyEmailScreen::class.java).putExtra(Constants.INTENT_EMAIL,email))
}