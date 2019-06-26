package com.ongraph.muslimhub.ui.activities

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.ongraph.muslimhub.R
import com.ongraph.muslimhub.ui.model.ApiInterface
import com.ongraph.muslimhub.ui.model.request.ForgotPasswordRequest
import com.ongraph.muslimhub.ui.model.request.SuperAdminLoginRequest
import com.ongraph.muslimhub.ui.model.response.GeneralResponse
import com.ongraph.muslimhub.ui.model.response.SuperAdminLoginResponse
import com.ongraph.muslimhub.ui.rest.ApiClient
import com.ongraph.muslimhub.ui.utils.AppGlobalApiCalls
import com.ongraph.muslimhub.ui.utils.AppUtils
import com.ongraph.muslimhub.ui.utils.AppUtils.showToast
import com.ongraph.muslimhub.ui.utils.SharedPrefsHelper
import kotlinx.android.synthetic.main.admin_login.*
import kotlinx.android.synthetic.main.dialog_forgot_password.*
import kotlinx.android.synthetic.main.layout_image_header.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SuperAdminLoginActivity : AppCompatActivity() {
    private var progressDialog: ProgressDialog? = null
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_login)

        init()
    }

    fun init() {
        tv_header.text = getString(R.string.admin_login)
        tv_header.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.login_admin, 0, 0, 0)
        iv_bck.visibility = View.GONE
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Please wait")
        progressDialog!!.setCancelable(false)

        /* containerView.iv_bck.visibility = View.GONE
         containerView.iv_search.visibility = View.VISIBLE*/

        btn_login.setOnClickListener {
            if (validate()) {
                AppUtils.hideKeyboard(this)
                callLoginApi()
            }
        }

        tv_reset.setOnClickListener {
            openForgotPasswordDialog()
        }
    }

    private fun validate(): Boolean {
        if (TextUtils.isEmpty(et_email.getText().toString().trim())
        /*!Patterns.EMAIL_ADDRESS.matcher(containerView.et_email.getText().toString().trim()).matches()*/) {
            et_email.setError(getString(R.string.valid_email))
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(et_email.getText().toString().trim()).matches()) {
            et_email.setError(getString(R.string.valid_email))
            return false
        }
        if (TextUtils.isEmpty(et_password.getText().toString().trim())) {
            et_password.setError(getString(R.string.valid_password))
            return false
        }
        return true
    }

    private fun callLoginApi() {
        progressDialog?.show()

        val mCustomLoginRequest = SuperAdminLoginRequest(et_email.getText().toString().trim(),
                et_password.getText().toString().trim())

        ApiClient.getClient(this)?.create(ApiInterface::class.java)?.superAdminLoginApi(mCustomLoginRequest)?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                progressDialog?.dismiss()
                try {
                    val gson = Gson()
                    if (response?.body() != null && response.isSuccessful) {
                        val mSuperAdminLoginResponse = gson.fromJson<SuperAdminLoginResponse>(response.body().string(),
                                SuperAdminLoginResponse::class.java)

                        /*SharedPrefsHelper.getInstance().save("is_superuser", mSuperAdminLoginResponse.getData()?.isSuperuser)
                        SharedPrefsHelper.getInstance().save("id", mSuperAdminLoginResponse.getData()?.id)
                        SharedPrefsHelper.getInstance().save("first_name", mSuperAdminLoginResponse.getData()?.firstName)
                        SharedPrefsHelper.getInstance().save("last_name", mSuperAdminLoginResponse.getData()?.lastName)
                        SharedPrefsHelper.getInstance().save("email", mSuperAdminLoginResponse.getData()?.email)
                        SharedPrefsHelper.getInstance().save("mobile_number", mSuperAdminLoginResponse.getData()?.mobileNumber)*/
                        SharedPrefsHelper.getInstance().save("Token", mSuperAdminLoginResponse.getData()?.token)
                        SharedPrefsHelper.getInstance().saveLoginData(mSuperAdminLoginResponse.getData())
                        showToast(findViewById<View>(android.R.id.content), "Login success")

                        if (mSuperAdminLoginResponse.getData()?.firstLogin!!) {
                            AppGlobalApiCalls.openChangePasswordDialog(this@SuperAdminLoginActivity)
                        } else {
                            val intent = Intent(this@SuperAdminLoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else if (response?.errorBody() != null) {
                        val error = gson.fromJson<GeneralResponse>(response.errorBody().string(), GeneralResponse::class.java)
                        if (error.message != null) {
                            showToast(findViewById<View>(android.R.id.content), error.message!!)
                        } else {
                            showToast(findViewById<View>(android.R.id.content), getString(R.string.somethingWrong))
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    AppUtils.showToast(findViewById(android.R.id.content), getResources().getString(R.string.somethingWrong))
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                progressDialog?.dismiss()
                showToast(findViewById<View>(android.R.id.content), getString(R.string.somethingWrong))
            }
        })
    }

    private fun openForgotPasswordDialog() {
        dialog = Dialog(this, R.style.FullScreenDialog)
        dialog.setContentView(R.layout.dialog_forgot_password)

        dialog.tv_header.text = getString(com.ongraph.muslimhub.R.string.forgot_pass)
        dialog.iv_bck.visibility = View.GONE

        dialog.btn_reset.setOnClickListener {
            if (validateForgetPassword()) {
                callForgotPasswordApi(dialog.et_email_id.text.toString().trim())
            }
        }

        dialog.show()
    }

    private fun validateForgetPassword(): Boolean {
        if (TextUtils.isEmpty(dialog.et_email_id.getText().toString().trim())) {
            dialog.et_email_id.error = getString(R.string.valid_email)
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(dialog.et_email_id.getText().toString().trim()).matches()) {
            dialog.et_email_id.error = getString(R.string.valid_email)
            return false
        }
        return true
    }

    private fun callForgotPasswordApi(email: String) {
        progressDialog?.show()
        val mForgotPasswordRequest = ForgotPasswordRequest(email)

        ApiClient.getClient(this)?.create(ApiInterface::class.java)?.forgotPasswordApi(mForgotPasswordRequest)?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                progressDialog?.dismiss()
                try {
                    val gson = Gson()
                    if (response?.body() != null && response.isSuccessful) {
                        val error = gson.fromJson<GeneralResponse>(response.body().string(),
                                GeneralResponse::class.java)
                        AppUtils.showToast(findViewById<View>(android.R.id.content), error.message!!)
                        dialog.dismiss()
                    } else if (response?.errorBody() != null) {
                        val error = gson.fromJson<GeneralResponse>(response.errorBody().string(), GeneralResponse::class.java)
                        if (error.message != null) {
                            AppUtils.showToast(dialog.window.decorView.findViewById<View>(android.R.id.content), error.message!!)
                        } else {
                            Toast.makeText(this@SuperAdminLoginActivity, getString(R.string.somethingWrong), Toast.LENGTH_SHORT).show()
                            AppUtils.showToast(dialog.window.decorView.findViewById<View>(android.R.id.content), getString(R.string.somethingWrong))
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    AppUtils.showToast(dialog.window.decorView.findViewById(android.R.id.content), getString(R.string.somethingWrong))
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                progressDialog?.dismiss()
                AppUtils.showToast(dialog.window.decorView.findViewById<View>(android.R.id.content), getString(R.string.somethingWrong))
            }
        })
    }

}