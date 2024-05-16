package com.enkefalostechnologies.calendarpro.ui

import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import com.amplifyframework.auth.exceptions.NotAuthorizedException
import com.amplifyframework.core.Amplify
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.constant.Constants
import com.enkefalostechnologies.calendarpro.databinding.ActivityChangePasswordBinding
import com.enkefalostechnologies.calendarpro.ui.base.BaseActivity
import com.enkefalostechnologies.calendarpro.util.AmplifyUtil.handleExceptions
import com.enkefalostechnologies.calendarpro.util.AppUtil.showToast
import com.enkefalostechnologies.calendarpro.util.Extension.close
import com.enkefalostechnologies.calendarpro.util.Extension.visible

class ChangePassword :
    BaseActivity<ActivityChangePasswordBinding>(R.layout.activity_change_password) {
    private var isPasswordVisible = false
    private var isNewPasswordVisible = false
    override fun onViewBindingCreated() {
        binding.ibBack.setOnClickListener(this)
        binding.btnChangePassword.setOnClickListener(this)
        binding.ivVisibility.setOnClickListener(this)
        binding.ivVisibility2.setOnClickListener(this)
    }

    override fun addObserver() {

    }

    override fun removeObserver() {

    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.ibBack -> {
                finish()
            }

            binding.btnChangePassword -> {
                if (isChangePasswordFormValidated()) {
//                dialog.visible()
                    Amplify.Auth.updatePassword(binding.etOldPassword.text.toString(),
                        binding.etNewPassword.text.toString(),
                        {
                            runOnUiThread {
//                            dialog.close()
                                showToast(Constants.TOAST_PASSWORD_UPDATED_SUCCESSFULLY)
                                finish()
                            }
                        },
                        {
                            runOnUiThread {

                                when(it){
                                    is NotAuthorizedException -> this.showToast(Constants.TOAST_INVALID_OLD_PASSWORD)
                                    else->this.handleExceptions(it)
                                }

                            }
                        }
                    )
                }
            }

            binding.ivVisibility -> {
                if (!isPasswordVisible) {
                    binding.etOldPassword.transformationMethod =
                        HideReturnsTransformationMethod.getInstance();
                    binding.ivVisibility.setImageDrawable(resources.getDrawable(R.drawable.ic_pwd_visible));
                    isPasswordVisible = true;
                } else {
                    binding.etOldPassword.transformationMethod =
                        PasswordTransformationMethod.getInstance();
                    binding.ivVisibility.setImageDrawable(resources.getDrawable(R.drawable.ic_pwd_invisible));
                    isPasswordVisible = false;
                }
            }

            binding.ivVisibility2 -> {
                if (!isNewPasswordVisible) {
                    binding.etNewPassword.transformationMethod =
                        HideReturnsTransformationMethod.getInstance();
                    binding.ivVisibility2.setImageDrawable(resources.getDrawable(R.drawable.ic_pwd_visible));
                    isNewPasswordVisible = true;
                } else {
                    binding.etNewPassword.transformationMethod =
                        PasswordTransformationMethod.getInstance();
                    binding.ivVisibility2.setImageDrawable(resources.getDrawable(R.drawable.ic_pwd_invisible));
                    isNewPasswordVisible = false;
                }
            }
        }
    }


    private fun isChangePasswordFormValidated(): Boolean {
        if (binding.etOldPassword.text.toString().trim() == "") {
            showToast("Enter Old Password")
            return false
        }
        if (binding.etNewPassword.text.toString().trim() == "") {
            showToast("Enter New Password")
            return false
        }
        return true
    }

}