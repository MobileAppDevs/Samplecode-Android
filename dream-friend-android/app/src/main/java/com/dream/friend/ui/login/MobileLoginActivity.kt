package com.dream.friend.ui.login


import com.dream.friend.R
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BuildCompat
import androidx.lifecycle.Observer
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.dream.friend.common.*
import com.dream.friend.common.Utils.dialogLoading
import com.dream.friend.common.Utils.showToast
import com.dream.friend.data.model.CountryCode
import com.dream.friend.data.model.error.ErrorResponse
import com.dream.friend.data.network.Resource
import com.dream.friend.databinding.ActivityMobileLoginBinding
import com.dream.friend.interfaces.CountryCodeItemSelectedListener
import com.dream.friend.ui.login.adapter.CountryPickerAdapter
import com.dream.friend.ui.viewModel.UserLoginViewModel
import java.io.IOException
import java.util.concurrent.TimeUnit

class MobileLoginActivity : AppCompatActivity(), View.OnClickListener, TextWatcher {

    private var resendToken: ForceResendingToken? = null
    private var storedVerificationId: String = ""
    lateinit var dialog: Dialog
    private val viewModel: UserLoginViewModel by viewModels()
    private val mobileLoginBinding: ActivityMobileLoginBinding
            by activityBindings(R.layout.activity_mobile_login)
    private lateinit var countryCodeList: ArrayList<CountryCode>
    private var countryCode = CountryCode("+91", "India", 10, 10, "IN","\uD83C\uDDEE\uD83C\uDDF3")
    private lateinit var countryCodeAdapter: CountryPickerAdapter


    private val otpObserver=Observer<Resource<ErrorResponse>>{ response->
            when(response) {
                is Resource.Success -> {
                    response.data?.let {
                        dialog.dismiss()
                        openNextScr()
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
//        override fun onVerificationCompleted(credential: PhoneAuthCredential) {}
//        override fun onVerificationFailed(e: FirebaseException) {
//            dialog.dismiss()
//            showToast(e.message.toString())
//        }
//        override fun onCodeSent(
//            verificationId: String,
//            token: ForceResendingToken
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
////            successFailureAlert(
////                "OTP has been sent successfully.",
////                "Success",
////                callback = { _: DialogInterface, i: Int ->
////                    if (i == DialogInterface.BUTTON_POSITIVE) {
//                        openNextScr()
////                    }
////                }
////            )
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mobileLoginBinding.root)

        dialog = dialogLoading()
        dialog.dismiss()

        getCountryCodeDataList()

        mobileLoginBinding.btnContinue.setOnClickListener(this)
        mobileLoginBinding.toolbar.ivBack.setOnClickListener(this)
        mobileLoginBinding.toolbar.tvBack.setOnClickListener(this)
        mobileLoginBinding.tvCountryCode.setOnClickListener(this)
        mobileLoginBinding.ivBackFromCountryCode.setOnClickListener(this)
        mobileLoginBinding.etSearchYourCountry.addTextChangedListener(this)
        setObserver()
    }

    private fun getCountryCodeDataList() {
        try {
            val json = assets.open("country_codes.json")
                .bufferedReader()
                .use { it.readText() }
            val listCountryType = object : TypeToken<ArrayList<CountryCode>>() {}.type
            countryCodeList = Gson().fromJson(json, listCountryType)
            countryCodeAdapter = CountryPickerAdapter(object : CountryCodeItemSelectedListener {
                @SuppressLint("SetTextI18n")
                override fun setOnCountryCodeItemSelectedListener(item: CountryCode) {
                    countryCode = item
                    mobileLoginBinding.tvFlag.text=item.flag
                    mobileLoginBinding.tvCountryCode.text = item.dial_code
                    mobileLoginBinding.llMobileScr.show()
                    mobileLoginBinding.llCountryPicker.hide()
                }
            })
            countryCodeAdapter.setItems(countryCodeList)
            mobileLoginBinding.rvCountryPicker.adapter = countryCodeAdapter
        } catch (ioException: IOException) { ioException.printStackTrace() }
    }

    @androidx.annotation.OptIn(BuildCompat.PrereleaseSdkCheck::class)
    override fun onClick(id: View?) {
        when (id) {
            mobileLoginBinding.btnContinue -> {
                if (!mobileLoginBinding.etMobileNumber.text.isNullOrBlank() &&
                    mobileLoginBinding.etMobileNumber.text.toString().length >= countryCode.Min_NSN &&
                    mobileLoginBinding.etMobileNumber.text.toString().length <= countryCode.Max_NSN) {
                    dialog.show()
                    dialog.findViewById<ImageView>(R.id.imgAnimation).startRotationAnimation()
                    val cc=mobileLoginBinding.tvCountryCode.text.toString().replace("(","").replace(")","")
                    sentOtpToMobile("${cc}${mobileLoginBinding.etMobileNumber.text.toString()}")
//                    if (resendToken == null)
//                        sentOtpToMobile("")
//                    else
//                        resendVerificationCode()
                } else if (mobileLoginBinding.etMobileNumber.text.isNullOrBlank())
                    showToast("Enter Your Mobile Number")
                else if(countryCode.Min_NSN == countryCode.Max_NSN)
                   showToast("Please enter ${countryCode.Max_NSN} digits only")
                else if (mobileLoginBinding.etMobileNumber.text.toString().length > countryCode.Max_NSN)
                    showToast("Please enter digits between ${countryCode.Min_NSN} to ${countryCode.Max_NSN}")
                else
                    showToast("Enter Valid Mobile Number")
            }

            mobileLoginBinding.toolbar.ivBack -> {
                onBackPressedDispatcher.onBackPressed()
            }
            mobileLoginBinding.toolbar.tvBack -> {
                onBackPressedDispatcher.onBackPressed()
            }

            mobileLoginBinding.tvCountryCode -> {
                mobileLoginBinding.llMobileScr.hide()
                mobileLoginBinding.llCountryPicker.show()
                mobileLoginBinding.etSearchYourCountry.setText("")
            }

            mobileLoginBinding.ivBackFromCountryCode -> {
                mobileLoginBinding.llMobileScr.show()
                mobileLoginBinding.llCountryPicker.hide()
            }
        }
    }

    private fun sentOtpToMobile(phoneNumber:String)=viewModel.sendOtp(phoneNumber)

//    private fun sentOtpToMobile(phoneNumber:String) {
////        showToast(phoneNumber)
////        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
////            .setPhoneNumber(
////                mobileLoginBinding.tvCountryCode.text.toString().replace("(","").replace(")","") +
////                mobileLoginBinding.etMobileNumber.text.toString())      // Phone number to verify
////            .setTimeout(1, TimeUnit.MINUTES)                    // Timeout and unit
////            .setActivity(this)                                          // Activity (for callback binding)
////            .setCallbacks(callbacks)                                    // OnVerificationStateChangedCallbacks
////            .build()
////        PhoneAuthProvider.verifyPhoneNumber(options)
//
//
//
//    }

//    private fun resendVerificationCode() {
//        val options = resendToken?.let {
//            PhoneAuthOptions.newBuilder(Firebase.auth)
//                .setPhoneNumber(mobileLoginBinding.tvCountryCode.text.toString().replace("(","").replace(")","") +
//                        mobileLoginBinding.etMobileNumber.text.toString())         // Phone number to verify
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

    private fun openNextScr() {
        val code = mobileLoginBinding.tvCountryCode.text.toString()
            .replace("(","")
            .replace(")","")
            .replace("+","").toInt()
        Intent(
            this,
            VerifyOtpActivity::class.java
        ).also {
            it.putExtra("verificationId", storedVerificationId)
            it.putExtra("resendToken", Gson().toJson(resendToken))
            it.putExtra("number", mobileLoginBinding.etMobileNumber.text.toString())
            it.putExtra("code", code)
            startActivity(it)
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if(text.isNullOrBlank())
            countryCodeAdapter.setItems(countryCodeList)
        else{
            val list = arrayListOf<CountryCode>()
            for(l in countryCodeList)
                if(l.name.contains(text, true))
                    list.add(l)
            if (list.size > 0)
                countryCodeAdapter.setItems(list)
        }
    }

    override fun afterTextChanged(p0: Editable?) {}



    fun setObserver(){
        viewModel.sendOtpResponse.observe(this, otpObserver)
    }
    fun removeObserver(){
        viewModel.sendOtpResponse.removeObserver(otpObserver)
    }


    override fun onDestroy() {
        removeObserver()
        super.onDestroy()
    }

}