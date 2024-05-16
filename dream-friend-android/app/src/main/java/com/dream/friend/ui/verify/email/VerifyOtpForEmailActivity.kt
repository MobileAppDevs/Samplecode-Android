package com.dream.friend.ui.verify.email

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dream.friend.R
import com.dream.friend.common.Utils.dialogLoading
import com.dream.friend.common.Utils.openLoginPage
import com.dream.friend.common.Utils.showToast
import com.dream.friend.common.activityBindings
import com.dream.friend.common.disable
import com.dream.friend.common.enable
import com.dream.friend.common.hide
import com.dream.friend.common.show
import com.dream.friend.data.model.EmailRequest
import com.dream.friend.data.model.VerifyEmailOtpRequest
import com.dream.friend.data.network.Resource
import com.dream.friend.databinding.ActivityVerifyOtpForEmailBinding
import com.dream.friend.interfaces.SuccessBottomSheetListener
import com.dream.friend.ui.bottomsheet.SuccessfullyVerifiedBottomSheetDialog
import com.dream.friend.ui.home.HomeActivity
import com.dream.friend.ui.viewModel.HomeScreenViewModel
import com.dream.friend.ui.viewModel.UserLoginViewModel

class VerifyOtpForEmailActivity : AppCompatActivity() ,View.OnClickListener{
    private val binding: ActivityVerifyOtpForEmailBinding by activityBindings(R.layout.activity_verify_otp_for_email)
    private val viewModel: HomeScreenViewModel by viewModels()
    private val viewModelUserLogin: UserLoginViewModel by viewModels()
    private var myCountDownTimer: VerifyOtpForEmailActivity.MyCountDownTimer? = null
    lateinit var  successBottomSheetDialog:SuccessfullyVerifiedBottomSheetDialog
    private lateinit var email:String
    lateinit var dialog: Dialog
    private lateinit var hashToken:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
    }

    private fun init(){
        dialog=dialogLoading()
        dialog.dismiss()
        email=intent.getStringExtra("email")!!
        hashToken=intent.getStringExtra("hashToken")!!
        binding.ivBack.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
        binding.tvSendAgain.setOnClickListener(this)
        startTimer()
        addObserver()
    }

    private  fun addObserver(){
        viewModel.emailOtpVerifyResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    if (response.data?.statusCode == 200) {
                            stopTimer()
//                            viewModelUserLogin.saveUser(response.data)
                            successBottomSheetDialog= SuccessfullyVerifiedBottomSheetDialog("email address",object: SuccessBottomSheetListener {
                                override fun onGoHomeButtonClicked() {
                                    successBottomSheetDialog.dismiss()
                                    showToast("Email updated successfully")
                                    val intent = Intent(this@VerifyOtpForEmailActivity, HomeActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finishAffinity()
                                }
                            })
                            successBottomSheetDialog.show(supportFragmentManager, "ModalBottomSheet")

                    } else {
                        response.data?.message?.let { showToast(it) }
                    }
                }

                is Resource.Error, is Resource.Loading -> {}
                is Resource.TokenRenew -> {
                    if (response.isTokenExpire == true) {
                        openLoginPage()
                    }
                }
            }
        }
        viewModel.emailUpdateResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    if (response.data?.statusCode == 200) {
                        showToast("Otp resent successfully.")
                        hashToken=response.data.hashToken
                        stopTimer()
                        startTimer()
                    } else {
                        response.data?.message?.let { showToast(it) }
                    }
                }

                is Resource.Error, is Resource.Loading -> {}
                is Resource.TokenRenew -> {
                    if (response.isTokenExpire == true) {
                        openLoginPage()
                    }
                }
            }
        }

    }
    private fun  removeObserver(){
        viewModel.emailOtpVerifyResponse.removeObservers(this)
        viewModel.emailUpdateResponse.removeObservers(this)
    }


    override fun onDestroy() {
        super.onDestroy()
        removeObserver()
    }
    override fun onClick(v: View?) {

        when(v){
            binding.btnSubmit->{
                if (binding.etOtpNumbers.text.isNullOrBlank() || binding.etOtpNumbers.text?.trim().toString().length != 6)
                    showToast("Enter OTP")
                else {
                    viewModel.emailOtpVerify(VerifyEmailOtpRequest(
                        email=email,
                        hashToken =hashToken,
                        otp=binding.etOtpNumbers.text?.trim().toString()
                    ))
                }

            }
            binding.ivBack->{finish()}
            binding.tvSendAgain->{
                    viewModel.verifyOrUpdateEmail(EmailRequest(email =email))
            }
        }

    }

    private fun startTimer() {
        myCountDownTimer = MyCountDownTimer(30000, 1000)
        myCountDownTimer!!.start()
        binding.OtpTimeRemainingLl.show()
        binding.tvSendAgain.disable()
        binding.tvSendAgain.setTextColor(ContextCompat.getColor(this, R.color.color_949494))
    }

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
            binding.tvSendAgain.setTextColor(ContextCompat.getColor(this@VerifyOtpForEmailActivity, R.color.greyText))
           binding.tvOtpTimeRemaining.text =
                "00:${(millisUntilFinished / 1000)}"
        }

        override fun onFinish() {
            binding.tvSendAgain.text = getString(R.string.resend_code)
            binding.tvSendAgain.setTextColor(ContextCompat.getColor(this@VerifyOtpForEmailActivity, R.color.color_161616))
            stopTimer()
        }
    }
}