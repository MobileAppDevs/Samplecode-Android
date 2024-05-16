package com.dream.friend.ui.verify.phone

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
import com.dream.friend.R
import com.dream.friend.common.CustomTypefaceSpan
import com.dream.friend.common.Utils.dialogLoading
import com.dream.friend.common.Utils.showToast
import com.dream.friend.common.activityBindings
import com.dream.friend.common.disable
import com.dream.friend.common.enable
import com.dream.friend.common.hide
import com.dream.friend.common.show
import com.dream.friend.common.startRotationAnimation
import com.dream.friend.data.model.BasicFilterRequest
import com.dream.friend.data.model.error.ErrorResponse
import com.dream.friend.data.model.login.LoginWithMobileReq
import com.dream.friend.data.network.Resource
import com.dream.friend.databinding.ActivityUpdateOrVerifyMobileNumberBinding
import com.dream.friend.databinding.ActivityVerifyOtpForPhoneNumberBinding
import com.dream.friend.interfaces.BasicFilterListener
import com.dream.friend.interfaces.SuccessBottomSheetListener
import com.dream.friend.ui.bottomsheet.BasicFilterBottomSheetDialog
import com.dream.friend.ui.bottomsheet.SuccessfullyVerifiedBottomSheetDialog
import com.dream.friend.ui.bottomsheet.advanceFilter.AdvanceFilterBottomSheetDialog
import com.dream.friend.ui.home.HomeActivity
import com.dream.friend.ui.login.LoginActivity
import com.dream.friend.ui.login.VerifyOtpActivity
import com.dream.friend.ui.viewModel.HomeScreenViewModel
import com.dream.friend.ui.viewModel.UserLoginViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import java.util.concurrent.TimeUnit

class VerifyOtpForPhoneNumberActivity : AppCompatActivity(), View.OnClickListener {
    private var code: String = ""
    private var phoneNumber: String = ""
    private lateinit var auth: FirebaseAuth

    lateinit var successBottomSheetDialog: SuccessfullyVerifiedBottomSheetDialog

    private var myCountDownTimer: VerifyOtpForPhoneNumberActivity.MyCountDownTimer? = null
    private val binding: ActivityVerifyOtpForPhoneNumberBinding by activityBindings(R.layout.activity_verify_otp_for_phone_number)

    //    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
//    private var storedVerificationId: String? = null
    private val viewModel: UserLoginViewModel by viewModels()
    private val viewModelUserLogin: UserLoginViewModel by viewModels()
    lateinit var dialog: Dialog

    private val verifyOtpObserver = Observer<Resource<ErrorResponse>> { response ->
        when (response) {
            is Resource.Success -> {
                response.data?.let {
                    dialog.dismiss()
                    viewModelUserLogin.getUser()?.userId?.let { it1 ->
                        viewModel.updatePhoneNumber(
                            userId = it1,
                            LoginWithMobileReq(
                                phoneNumber.toLong(),
                                code.toInt()
                            )
                        )
                    }
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
    private val sendOtpObserver = Observer<Resource<ErrorResponse>> { response ->
        when (response) {
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
//           showToast("OTP Has been re-sent successfully.")
//            //startTimer()
//        }
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        setObserver()
    }

    @androidx.annotation.OptIn(BuildCompat.PrereleaseSdkCheck::class)
    override fun onClick(id: View?) {
        when (id) {
            binding.ivBack -> {
                finish()
            }

            binding.tvSendAgain -> {
                startTimer()
                dialog.show()
                dialog.findViewById<ImageView>(R.id.imgAnimation).startRotationAnimation()
                // resendVerificationCode(code, phoneNumber, resendToken)
                resendVerificationCode(code, phoneNumber)
            }

            binding.btnVerify -> {
                if (binding.etOtpNumber.text.isNullOrBlank() || binding.etOtpNumber.text.toString().length != 6)
                    showToast("Enter OTP")
                else {
                    dialog.show()
                    dialog.findViewById<ImageView>(R.id.imgAnimation).startRotationAnimation()
                    verifyOtp(binding.etOtpNumber.text.toString(), "+${code}${phoneNumber}")
                }
            }
        }
    }

    private fun init() {
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

        binding.tvmDesc.text = "${getString(R.string.verify_des)} +${code}${phoneNumber}"
        val ss = SpannableStringBuilder(binding.tvmDesc.text.toString())
        val font = ResourcesCompat.getFont(this, R.font.merriweather_sans_regular)
        val text1 = "+${code}${phoneNumber}"
        ss.setSpan(
            CustomTypefaceSpan("", font), ss.toString().indexOf(text1),
            ss.toString().indexOf(text1) + text1.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        ss.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.color_161616)),
            ss.toString().indexOf(text1),
            ss.toString().indexOf(text1) + text1.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvmDesc.text = ss
        binding.btnVerify.setOnClickListener(this)
        binding.ivBack.setOnClickListener(this)
        binding.tvSendAgain.setOnClickListener(this)

        startTimer()
        mobileLoginResponse()

    }

    private fun startTimer() {
        myCountDownTimer = MyCountDownTimer(30000, 1000)
        myCountDownTimer!!.start()
        binding.OtpTimeRemainingLl.show()
        binding.tvSendAgain.disable()
        binding.tvSendAgain.setTextColor(ContextCompat.getColor(this, R.color.color_949494))
    }

    private fun verifyOtp(otp: String, mobileNumber: String) {
        viewModel.verifyOtp(otp, mobileNumber)
    }
//    private fun verifyOtp(otp: String) {
//        val credentials = storedVerificationId?.let { PhoneAuthProvider.getCredential(it, otp) }
//        signInWithPhoneAuthCredential(credentials)
//    }


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
//                        viewModelUserLogin.getUser()?.userId?.let { it1 ->
//                            viewModel.updatePhoneNumber(
//                                userId = it1,
//                                LoginWithMobileReq(
//                                    phoneNumber.toLong(),
//                                    code.toInt()
//                                )
//                            )
//                        }
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

    
    private fun resendVerificationCode(code: String, phoneNumber: String) =
        viewModel.sendOtp("+${code}${phoneNumber}")

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
    private fun stopTimer() {
        if (myCountDownTimer != null) {
            myCountDownTimer!!.cancel()
            binding.OtpTimeRemainingLl.hide()
            binding.tvSendAgain.enable()
            binding.tvSendAgain.setTextColor(ContextCompat.getColor(this, R.color.color_161616))
        }
    }

    inner class MyCountDownTimer(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {
        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            binding.tvSendAgain.text = getString(R.string.resend_code)
            binding.tvSendAgain.setTextColor(
                ContextCompat.getColor(
                    this@VerifyOtpForPhoneNumberActivity,
                    R.color.greyText
                )
            )
            binding.tvOtpTimeRemaining.text =
                "00:${(millisUntilFinished / 1000)}"
        }

        override fun onFinish() {
            binding.tvSendAgain.text = getString(R.string.resend_code)
            binding.tvSendAgain.setTextColor(
                ContextCompat.getColor(
                    this@VerifyOtpForPhoneNumberActivity,
                    R.color.color_161616
                )
            )
            stopTimer()
        }
    }

    private fun mobileLoginResponse() {
        viewModel.updatePhoneNumberResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let {
                        dialog.dismiss()
                        viewModel.saveUser(it.user)
                        it.accessToken?.let { token ->
                            viewModel.saveUserAccessToken(token)
                        }
                        successBottomSheetDialog =
                            SuccessfullyVerifiedBottomSheetDialog("Phone number",
                                object : SuccessBottomSheetListener {
                                    override fun onGoHomeButtonClicked() {
                                        successBottomSheetDialog.dismiss()
                                        showToast("Mobile Number Updated successfully.")
                                        val intent = Intent(
                                            this@VerifyOtpForPhoneNumberActivity,
                                            HomeActivity::class.java
                                        )
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)
                                        finishAffinity()
                                    }
                                })
                        successBottomSheetDialog.show(supportFragmentManager, "ModalBottomSheet")

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

    fun setObserver() {
        viewModel.verifyOtpResponse.observe(this, verifyOtpObserver)
        viewModel.sendOtpResponse.observe(this, sendOtpObserver)
    }

    fun removeObserver() {
        viewModel.verifyOtpResponse.removeObserver(verifyOtpObserver)
        viewModel.sendOtpResponse.removeObserver(sendOtpObserver)
        viewModel.updatePhoneNumberResponse.removeObservers(this)
    }

    override fun onDestroy() {
        removeObserver()
        super.onDestroy()
    }
}