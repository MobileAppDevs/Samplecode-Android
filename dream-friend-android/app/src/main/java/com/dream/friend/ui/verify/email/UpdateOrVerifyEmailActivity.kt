package com.dream.friend.ui.verify.email

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import com.dream.friend.R
import com.dream.friend.common.Utils.openLoginPage
import com.dream.friend.common.Utils.showToast
import com.dream.friend.common.activityBindings
import com.dream.friend.common.disable
import com.dream.friend.common.enable
import com.dream.friend.data.model.EmailRequest
import com.dream.friend.data.network.Resource
import com.dream.friend.databinding.ActivityNotificationSettingBinding
import com.dream.friend.databinding.ActivityUpdateOrVerifyEmailBinding
import com.dream.friend.ui.viewModel.HomeScreenViewModel
import com.dream.friend.ui.viewModel.UserLoginViewModel

class UpdateOrVerifyEmailActivity : AppCompatActivity(), View.OnClickListener {

    private val binding: ActivityUpdateOrVerifyEmailBinding by activityBindings(R.layout.activity_update_or_verify_email)
    private val viewModel: HomeScreenViewModel by viewModels()
    private val viewModelUserLogin: UserLoginViewModel by viewModels()

    val txtWatcher=object:TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if(binding.etEmail.text?.trim()?.isEmpty()==true){
                binding.btnSubmit.disable()
            }else{
                binding.btnSubmit.enable()
            }

        }

        override fun afterTextChanged(s: Editable?) {

        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
    }

   private fun init(){
       intent.getStringExtra("email")?.let {
           binding.etEmail.setText(it)
       }
       binding.etEmail.addTextChangedListener(txtWatcher)
        binding.ivBack.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
       addObserver()
    }


   private  fun addObserver(){
       viewModel.emailUpdateResponse.observe(this) { response ->
           when (response) {
               is Resource.Success -> {
                   if (response.data?.statusCode == 200) {
                       val intent=Intent(this,VerifyOtpForEmailActivity::class.java)
                       intent.putExtra("email",binding.etEmail.text?.trim().toString())
                       intent.putExtra("hashToken",response.data.hashToken)
                       startActivity(intent)
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
        viewModel.emailUpdateResponse.removeObservers(this)
    }


    override fun onDestroy() {
        super.onDestroy()
        removeObserver()
    }

    override fun onClick(v: View?) {
        when(v){
            binding.ivBack->{finish()}
            binding.btnSubmit->{
                if(binding.etEmail.text?.trim()!="") {
                    viewModel.verifyOrUpdateEmail(EmailRequest(email =binding.etEmail.text?.trim().toString()))
                }else{
                    showToast("Enter valid Email")
                }
            }
        }
    }


}