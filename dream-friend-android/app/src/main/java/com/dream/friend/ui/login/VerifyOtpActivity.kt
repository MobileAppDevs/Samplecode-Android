package com.dream.friend.ui.login


import com.dream.friend.R
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.BuildCompat
import androidx.lifecycle.Observer
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.dream.friend.common.*
import com.dream.friend.common.Utils.dialogLoading
import com.dream.friend.common.Utils.showToast
import com.dream.friend.data.model.error.ErrorResponse
import com.dream.friend.data.model.login.LoginWithMobileReq
import com.dream.friend.data.network.Resource
import com.dream.friend.databinding.ActivityVerifyOtpBinding
import com.dream.friend.ui.home.HomeActivity
import com.dream.friend.ui.onboarding.CreateProfileActivity
import com.dream.friend.ui.viewModel.UserLoginViewModel
import java.util.concurrent.TimeUnit

class VerifyOtpActivity : AppCompatActivity(), View.OnClickListener {

//    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var storedVerificationId: String? = null
    private lateinit var auth: FirebaseAuth
    lateinit var dialog: Dialog
    private val viewModel: UserLoginViewModel by viewModels()
    private val verifyOtpBinding: ActivityVerifyOtpBinding
            by activityBindings(R.layout.activity_verify_otp)
    private var code: String = ""
    private var phoneNumber: String = ""

    private var myCountDownTimer: MyCountDownTimer? = null

    private val verifyOtpObserver= Observer<Resource<ErrorResponse>>{ response->
        when(response) {
            is Resource.Success -> {
                response.data?.let {
                    dialog.dismiss()
//                    openNextScr()
                    viewModel.userLoginWithMobile(
                        LoginWithMobileReq(
                            phoneNumber.toLong(),
                            code.toInt()
                        )
                    )
                }
            }
            is Resource.Error -> {
                dialog.dismiss()
                response.message?.let { showToast(it) }
            }
            is Resource.Loading -> {
                dialog.show()
                dialog.findViewById<ImageView>(R.id.imgAnimation).startRotationAnimation()
            }
            is Resource.TokenRenew -> {}
        }
    }
    private val sendOtpObserver=Observer<Resource<ErrorResponse>>{ response->
        when(response) {
            is Resource.Success -> {
                response.data?.let {
                    dialog.dismiss()
//                    openNextScr()
                    showToast("OTP Has been re-sent successfully.")
                    startTimer()
                }
            }
            is Resource.Error -> {
                dialog.dismiss()
                response.message?.let { showToast(it) }
            }
            is Resource.Loading -> {
                dialog.show()
                dialog.findViewById<ImageView>(R.id.imgAnimation).startRotationAnimation()
            }
            is Resource.TokenRenew -> {}
        }
    }

//    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//            dialog.dismiss()
//            credential.smsCode?.apply {
//                verifyOtp(this)
//            }
//        }
//
//        override fun onVerificationFailed(e: FirebaseException) {
//            dialog.dismiss()
//            showToast(e.message.toString())
//        }
//
//        override fun onCodeSent(
//            verificationId: String,
//            token: PhoneAuthProvider.ForceResendingToken
//        ) {
//            // The SMS verification code has been sent to the provided phone number, we
//            // now need to ask the user to enter the code and then construct a credential
//            // by combining the code with a verification ID.
//            //       Log.d(TAG, "onCodeSent:$verificationId")
//
//            // Save verification ID and resending token so we can use them later
//
//            dialog.dismiss()
//            storedVerificationId = verificationId
//            resendToken = token
//            showToast("OTP Has been re-sent successfully.")
//            startTimer()
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(verifyOtpBinding.root)

        dialog = dialogLoading()
        dialog.dismiss()

        auth = Firebase.auth
        code = "${intent.getIntExtra("code", 0)}"
        phoneNumber = "${intent.getStringExtra("number")}"
//        storedVerificationId = "${intent.getStringExtra("verificationId")}"
//        resendToken = Gson().fromJson(
//            intent.getStringExtra("resendToken"),
//            PhoneAuthProvider.ForceResendingToken::class.java
//        )

        verifyOtpBinding.tvmDesc.text= "${getString(R.string.verify_des)} +${code}${phoneNumber}"
        val ss = SpannableStringBuilder(verifyOtpBinding.tvmDesc.text.toString())
        val font = ResourcesCompat.getFont(this, R.font.merriweather_sans_regular)
        val text1 = "+${code}${phoneNumber}"
        ss.setSpan(
            CustomTypefaceSpan("", font), ss.toString().indexOf(text1),
            ss.toString().indexOf(text1)+text1.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        ss.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.color_161616)),
            ss.toString().indexOf(text1),
            ss.toString().indexOf(text1)+text1.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        verifyOtpBinding.tvmDesc.text=ss
        verifyOtpBinding.btnVerify.setOnClickListener(this)
        verifyOtpBinding.toolbar.ivBack.setOnClickListener(this)
        verifyOtpBinding.toolbar.tvBack.setOnClickListener(this)
        verifyOtpBinding.tvSendAgain.setOnClickListener(this)

        startTimer()
        mobileLoginResponse()
        setObserver()
    }

    private fun mobileLoginResponse() {
        viewModel.userLoginWithMobileResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let {
                        dialog.dismiss()
                        viewModel.saveUser(it.user)
                        it.accessToken?.let { it1 -> viewModel.saveUserAccessToken(it1) }
                        if (it.user.isNew) openNextScr()
                        else openHomeScr()
                    }
                }
                is Resource.Error -> {
                    dialog.dismiss()
                    response.message?.let { showToast(it) }
                }
                is Resource.Loading -> {
                    dialog.show()
                    dialog.findViewById<ImageView>(R.id.imgAnimation).startRotationAnimation()
                }
                is Resource.TokenRenew -> {}
            }
        }
    }

    private fun openNextScr() {
        Intent(
            this,
            CreateProfileActivity::class.java
        ).also {
            startActivity(it)
            finishAffinity()
        }
    }

    private fun openHomeScr() {
        Intent(
            this,
            HomeActivity::class.java
        ).also {
            startActivity(it)
            finishAffinity()
        }
    }

    @androidx.annotation.OptIn(BuildCompat.PrereleaseSdkCheck::class)
    override fun onClick(id: View?) {
        when (id) {
            verifyOtpBinding.toolbar.ivBack,verifyOtpBinding.toolbar.tvBack -> {
                onBackPressedDispatcher.onBackPressed()
            }

            verifyOtpBinding.tvSendAgain -> {
                startTimer()
                dialog.show()
                dialog.findViewById<ImageView>(R.id.imgAnimation).startRotationAnimation()
//                resendVerificationCode(code, phoneNumber, resendToken)
                resendVerificationCode(code, phoneNumber)
            }

            verifyOtpBinding.btnVerify -> {
                if (verifyOtpBinding.etOtpNumber.text.isNullOrBlank() || verifyOtpBinding.etOtpNumber.text.toString().length != 6)
                    showToast("Enter OTP")
                else {
                    dialog.show()
                    dialog.findViewById<ImageView>(R.id.imgAnimation).startRotationAnimation()
                    verifyOtp(verifyOtpBinding.etOtpNumber.text.toString(),"+${code}${phoneNumber}")
                }
            }
        }
    }

    private fun verifyOtp(otp: String,mobileNumber:String) {
        viewModel.verifyOtp(otp,mobileNumber)
//        showToast(mobileNumber)
//        val credentials = storedVerificationId?.let { PhoneAuthProvider.getCredential(it, otp) }
//        signInWithPhoneAuthCredential(credentials)
    }

//    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential?) {
//        credential?.let {
//            auth.signInWithCredential(it)
//                .addOnCompleteListener(this) { task ->
//                    if (task.isSuccessful) {
////                        successFailureAlert(
////                            "OTP is verified",
////                            "Success",
////                            callback = { _: DialogInterface, i: Int ->
////                                if (i == DialogInterface.BUTTON_POSITIVE) {
//                                    viewModel.userLoginWithMobile(
//                                        LoginWithMobileReq(
//                                            phoneNumber.toLong(),
//                                            code.toInt()
//                                        )
//                                    )
////                                }
////                            }
////                        )
//                    } else {
//                        dialog.dismiss()
//                        showToast(task.exception?.message.toString())
//                    }
//                }
//        }
//    }

    private fun resendVerificationCode(code: String, phoneNumber: String)= viewModel.sendOtp("+${code}${phoneNumber}")

//    private fun resendVerificationCode(
//        code: String,
//        phoneNumber: String,
//        token: PhoneAuthProvider.ForceResendingToken?
//    ) {
//        val options = token?.let {
//            PhoneAuthOptions.newBuilder(Firebase.auth)
//                .setPhoneNumber("+$code$phoneNumber")                             // Phone number to verify
//                .setTimeout(1, TimeUnit.MINUTES)                           // Timeout and unit
//                .setActivity(this)                                                // Activity (for callback binding)
//                .setCallbacks(callbacks)                                          // OnVerificationStateChangedCallbacks
//                .setForceResendingToken(it)
//                .build()
//        }
//        if (options != null) {
//            PhoneAuthProvider.verifyPhoneNumber(options)
//        }
//    }

    private fun startTimer() {
        myCountDownTimer = MyCountDownTimer(30000, 1000)
        myCountDownTimer!!.start()
        verifyOtpBinding.OtpTimeRemainingLl.show()
        verifyOtpBinding.tvSendAgain.disable()
        verifyOtpBinding.tvSendAgain.setTextColor(ContextCompat.getColor(this, R.color.color_949494))
    }

    private fun stopTimer() {
        if (myCountDownTimer != null) {
            myCountDownTimer!!.cancel()
            verifyOtpBinding.OtpTimeRemainingLl.hide()
            verifyOtpBinding.tvSendAgain.enable()
            verifyOtpBinding.tvSendAgain.setTextColor(ContextCompat.getColor(this, R.color.color_161616))
        }
    }

    inner class MyCountDownTimer(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {
        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            verifyOtpBinding.tvSendAgain.text = getString(R.string.resend_code)
            verifyOtpBinding.tvSendAgain.setTextColor(ContextCompat.getColor(this@VerifyOtpActivity, R.color.greyText))
            verifyOtpBinding.tvOtpTimeRemaining.text =
                "00:${(millisUntilFinished / 1000)}"
        }

        override fun onFinish() {
            verifyOtpBinding.tvSendAgain.text = getString(R.string.resend_code)
            verifyOtpBinding.tvSendAgain.setTextColor(ContextCompat.getColor(this@VerifyOtpActivity, R.color.color_161616))
            stopTimer()
        }
    }

    fun setObserver(){
        viewModel.verifyOtpResponse.observe(this,verifyOtpObserver)
        viewModel.sendOtpResponse.observe(this,sendOtpObserver)
    }
    fun removeObserver(){
        viewModel.verifyOtpResponse.removeObserver(verifyOtpObserver)
        viewModel.sendOtpResponse.removeObserver(sendOtpObserver)
    }

    override fun onDestroy() {
        removeObserver()
        super.onDestroy()
    }
}