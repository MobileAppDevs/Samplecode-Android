package com.dream.friend.ui.verify.phone

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.dream.friend.R
import com.dream.friend.common.Utils.dialogLoading
import com.dream.friend.common.Utils.showToast
import com.dream.friend.common.activityBindings
import com.dream.friend.common.disable
import com.dream.friend.common.enable
import com.dream.friend.common.hide
import com.dream.friend.common.show
import com.dream.friend.common.startRotationAnimation
import com.dream.friend.data.model.CountryCode
import com.dream.friend.data.model.error.ErrorResponse
import com.dream.friend.data.network.Resource
import com.dream.friend.databinding.ActivityUpdateOrVerifyEmailBinding
import com.dream.friend.databinding.ActivityUpdateOrVerifyMobileNumberBinding
import com.dream.friend.interfaces.CountryCodeItemSelectedListener
import com.dream.friend.ui.login.VerifyOtpActivity
import com.dream.friend.ui.login.adapter.CountryPickerAdapter
import com.dream.friend.ui.viewModel.HomeScreenViewModel
import com.dream.friend.ui.viewModel.UserLoginViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.util.concurrent.TimeUnit

class UpdateOrVerifyMobileNumberActivity : AppCompatActivity(), View.OnClickListener, TextWatcher {

    private val binding: ActivityUpdateOrVerifyMobileNumberBinding by activityBindings(R.layout.activity_update_or_verify_mobile_number)
    private val viewModel: HomeScreenViewModel by viewModels()
    private val viewModelUserLogin: UserLoginViewModel by viewModels()
    lateinit var dialog: Dialog
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var storedVerificationId: String = ""
    private lateinit var countryCodeList: ArrayList<CountryCode>
    private var countryCode = CountryCode("+91", "India", 10, 10, "IN","\uD83C\uDDEE\uD83C\uDDF3")
    private lateinit var countryCodeAdapter: CountryPickerAdapter
     val edtTxtWatcher=object:TextWatcher{
         override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

         }

         override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
             if (!binding.etMobileNumber.text.isNullOrBlank() &&
                 binding.etMobileNumber.text.toString().length >= countryCode.Min_NSN &&
                 binding.etMobileNumber.text.toString().length <= countryCode.Max_NSN) {
                 binding.btnSubmit.enable()
             }else{
                 binding.btnSubmit.disable()
             }

         }

         override fun afterTextChanged(s: Editable?) {

         }

     }

    private val otpObserver= Observer<Resource<ErrorResponse>>{ response->
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
//            openNextScr()
////            successFailureAlert(
////                "OTP has been sent successfully.",
////                "Success",
////                callback = { _: DialogInterface, i: Int ->
////                    if (i == DialogInterface.BUTTON_POSITIVE) {
////                        openNextScr()
////                    }
////                }
////            )
//        }
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
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
                    binding.tvFlag.text=item.flag
                    binding.tvCountryCode.text = item.dial_code
                    binding.llMobileScr.show()
                   binding.llCountryPicker.hide()
                }
            })
            countryCodeAdapter.setItems(countryCodeList)
            binding.rvCountryPicker.adapter = countryCodeAdapter
        } catch (ioException: IOException) { ioException.printStackTrace() }
    }
    private fun init(){
        intent.getStringExtra("phone")?.let {
            if(it.length>=10) {
                binding.etMobileNumber.setText(it)
            }
        }
        dialog=dialogLoading()
        dialog.dismiss()
        getCountryCodeDataList()
        binding.etMobileNumber.addTextChangedListener(edtTxtWatcher)
        binding.ivBack.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
        binding.tvCountryCode.setOnClickListener(this)
        binding.ivBackFromCountryCode.setOnClickListener(this)
        binding.etSearchYourCountry.addTextChangedListener(this)
    }
    private fun openNextScr() {
        val code = binding.tvCountryCode.text.toString()
            .replace("(","")
            .replace(")","")
            .replace("+","").toInt()
        Intent(
            this,
            VerifyOtpForPhoneNumberActivity::class.java
        ).also {
            it.putExtra("verificationId", storedVerificationId)
            it.putExtra("resendToken", Gson().toJson(resendToken))
            it.putExtra("number", binding.etMobileNumber.text.toString())
            it.putExtra("code", code)
            startActivity(it)
        }
    }
    override fun onClick(v: View?) {

        when(v){
            binding.ivBack->{finish()}
            binding.btnSubmit->{
                if (!binding.etMobileNumber.text.isNullOrBlank() &&
                    binding.etMobileNumber.text.toString().length >= countryCode.Min_NSN &&
                    binding.etMobileNumber.text.toString().length <= countryCode.Max_NSN) {
                    dialog.show()
                    dialog.findViewById<ImageView>(R.id.imgAnimation).startRotationAnimation()
//                    if (resendToken == null)
//                        sentOtpToMobile()
//                    else
//                        resendVerificationCode()
                    sentOtpToMobile()
                } else if (binding.etMobileNumber.text.isNullOrBlank())
                   showToast("Enter Your Mobile Number")
                else if(countryCode.Min_NSN == countryCode.Max_NSN)
                   showToast("Please enter ${countryCode.Max_NSN} digits only")
                else if (binding.etMobileNumber.text.toString().length > countryCode.Max_NSN)
                   showToast("Please enter digits between ${countryCode.Min_NSN} to ${countryCode.Max_NSN}")
                else
                   showToast("Enter Valid Mobile Number")
            }
            binding.tvCountryCode -> {
                binding.llMobileScr.hide()
                binding.llCountryPicker.show()
                binding.etSearchYourCountry.setText("")
            }

            binding.ivBackFromCountryCode -> {
               binding.llMobileScr.show()
                binding.llCountryPicker.hide()
            }

        }

    }

    private fun sentOtpToMobile(){viewModelUserLogin.sendOtp(binding.tvCountryCode.text.toString().replace("(","").replace(")","") +binding.etMobileNumber.text.toString())}
//    private fun sentOtpToMobile() {
//        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
//            .setPhoneNumber(
//                binding.tvCountryCode.text.toString().replace("(","").replace(")","") +
//                        binding.etMobileNumber.text.toString())      // Phone number to verify
//            .setTimeout(1, TimeUnit.MINUTES)                    // Timeout and unit
//            .setActivity(this)                                          // Activity (for callback binding)
//            .setCallbacks(callbacks)                                    // OnVerificationStateChangedCallbacks
//            .build()
//        PhoneAuthProvider.verifyPhoneNumber(options)
//    }

//    private fun resendVerificationCode() {
//        val options = resendToken?.let {
//            PhoneAuthOptions.newBuilder(Firebase.auth)
//                .setPhoneNumber(binding.tvCountryCode.text.toString().replace("(","").replace(")","") +
//                        binding.etMobileNumber.text.toString())         // Phone number to verify
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
        viewModelUserLogin.sendOtpResponse.observe(this, otpObserver)
    }
    fun removeObserver(){
        viewModelUserLogin.sendOtpResponse.removeObserver(otpObserver)
    }


    override fun onDestroy() {
        removeObserver()
        super.onDestroy()
    }


}