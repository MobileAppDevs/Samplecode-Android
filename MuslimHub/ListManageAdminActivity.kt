package com.ongraph.muslimhub.ui.activities

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import com.google.gson.Gson
import com.ongraph.muslimhub.R
import com.ongraph.muslimhub.ui.adapters.AdminAdapter
import com.ongraph.muslimhub.ui.callbacks.AppCallBackListner
import com.ongraph.muslimhub.ui.model.ApiInterface
import com.ongraph.muslimhub.ui.model.request.AddAdminRequest
import com.ongraph.muslimhub.ui.model.response.AdminListResponse
import com.ongraph.muslimhub.ui.model.response.GeneralResponse
import com.ongraph.muslimhub.ui.rest.ApiClient
import com.ongraph.muslimhub.ui.utils.AppDialogs
import com.ongraph.muslimhub.ui.utils.AppUtils
import kotlinx.android.synthetic.main.activity_manage_admin.*
import kotlinx.android.synthetic.main.layout_add_admin.*
import kotlinx.android.synthetic.main.layout_image_header.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListManageAdminActivity : AppCompatActivity() {

    private var mAdminList: ArrayList<AdminListResponse.User> = ArrayList()
    private var progressDialog: ProgressDialog? = null
    private var mAdmin: AdminListResponse.User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_admin)
        init()
    }

    private fun init() {
        tv_header.setText(getString(R.string.manage_admin))
        iv_bck.setImageResource(R.mipmap.search_move)
        iv_add.visibility = View.VISIBLE

        iv_bck.setOnClickListener(View.OnClickListener {
            onBackPressed()
            finish()
        })

        iv_bck!!.visibility = View.VISIBLE
        ll_header!!.visibility = View.VISIBLE

        if (getIntent().extras.get("org_id") != null) {
            val org_id = getIntent().extras.get("org_id")
            if (AppUtils.isConnected(this)) {
                adminLisApi(org_id as Int)
            } else {
                AppUtils.showToast(findViewById<View>(android.R.id.content), getString(R.string.chk_network))
            }
        }

        iv_add.setOnClickListener(View.OnClickListener {
            openAddAminDialog()
        })

        rv_admin.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val mOrgsAdapter = AdminAdapter(mAdminList, this, object : AppCallBackListner.DeleteCallback {
            override fun onResult(mObject: Any?, id: Int?) {
                if (mObject != null) {
                    mAdmin = mObject as AdminListResponse.User
                    editAdminDialog(mAdmin!!.id)
                } else if (id != null) {
                    AppDialogs.showAlertDialog(this@ListManageAdminActivity, "", resources.getString(R.string.delete_admin),
                            true, object : AppCallBackListner.DialogCallback {
                        override fun onClickPositiveButton() {
                            if (AppUtils.isConnected(this@ListManageAdminActivity)) {
                                deleteAdminApi(id)
                            } else {
                                AppUtils.showToast(findViewById<View>(android.R.id.content), getString(R.string.chk_network))
                            }
                        }

                        override fun onClickNegativeButton() {
                        }
                    })
                }
            }
        })
        rv_admin.adapter = mOrgsAdapter
    }

    private fun openAddAminDialog() {
        val dialog = Dialog(this, R.style.FullScreenDialog);
        dialog.setContentView(R.layout.layout_add_admin);

        dialog.tv_header.setText(getString(R.string.add_admin))
        dialog.iv_bck.setImageResource(R.mipmap.search_move)
        dialog.iv_bck!!.setOnClickListener {
            dialog.dismiss()
        }

        dialog.btn_save.setOnClickListener {
            if (validate(dialog))
                if (getIntent().extras.get("org_id") != null) {
                    val org_id = getIntent().extras.get("org_id")
                    if (AppUtils.isConnected(this)) {
                        addAdminApi(org_id as Int, dialog)
                    } else {
                        AppUtils.showToast(findViewById<View>(android.R.id.content), getString(R.string.chk_network))
                    }
                }
        }

        dialog.btn_cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show();
    }

    private fun editAdminDialog(id: Int?) {
        val dialog = Dialog(this, R.style.FullScreenDialog);
        dialog.setContentView(R.layout.layout_add_admin);

        dialog.tv_header.setText(getString(R.string.edit_admin))
        dialog.iv_bck.visibility = View.GONE

        if (mAdmin != null) {
            if (mAdmin!!.mobileNumber!! != null && mAdmin!!.mobileNumber!!.contains("-")) {
                val countryCode: String = mAdmin!!.mobileNumber!!.substring(0, mAdmin!!.mobileNumber!!.indexOf(("-")))
                val mobileNumber: String = mAdmin!!.mobileNumber!!.substring(mAdmin!!.mobileNumber!!.indexOf("-") + 1
                        , mAdmin!!.mobileNumber!!.length)

                dialog.et_admin_phone.setText(mobileNumber)
                dialog.ccp_admin.setCountryForPhoneCode(countryCode.toInt());
            } else {
                val countryCode: String = mAdmin!!.mobileNumber!!.substring(0, 2)
                val mobileNumber: String = mAdmin!!.mobileNumber!!.substring(3, mAdmin!!.mobileNumber!!.length)

                dialog.et_admin_phone.setText(mobileNumber)
                dialog.ccp_admin.setCountryForPhoneCode(countryCode.toInt());
            }

            dialog.et_admin_fname.setText(mAdmin!!.firstName)
            dialog.et_admin_lname.setText(mAdmin!!.lastName)
            dialog.et_admin_email.setText(mAdmin!!.email)

            if (mAdmin!!.privileges!!.orgPerm.equals("Iqama Only", true)) {
                dialog.rb_iqama.isChecked = true
            } else if (mAdmin!!.privileges!!.orgPerm.equals("All", true)) {
                dialog.rb_all.isChecked = true
            }
        }

        dialog.btn_save.setOnClickListener {
            if (validate(dialog))
                if (AppUtils.isConnected(this)) {
                    editAdminApi(id as Int, dialog)
                } else {
                    AppUtils.showToast(findViewById<View>(android.R.id.content), getString(R.string.chk_network))
                }
        }

        dialog.btn_cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show();
    }

    private fun validate(dialog: Dialog): Boolean {
        if (TextUtils.isEmpty(dialog.et_admin_fname.getText().toString().trim())) {
            dialog.et_admin_fname.setError(getString(R.string.valid_fname))
            return false
        }
        if (TextUtils.isEmpty(dialog.et_admin_lname.getText().toString().trim())) {
            dialog.et_admin_lname.setError(getString(R.string.valid_email))
            return false
        }
        if (TextUtils.isEmpty(dialog.et_admin_email.getText().toString().trim())) {
            dialog.et_admin_email.setError(getString(R.string.valid_email))
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(dialog.et_admin_email.getText().toString().trim()).matches()) {
            dialog.et_admin_email.setError(getString(R.string.valid_email))
            return false
        }
        if (TextUtils.isEmpty(dialog.et_admin_phone.getText().toString().trim())) {
            dialog.et_admin_phone.setError(getString(R.string.valid_phone))
            return false
        }
        return true
    }

    fun adminLisApi(org_id: Int) {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Please wait")
        progressDialog!!.setCancelable(false)
        progressDialog?.show()

        ApiClient.getClient(this)?.create(ApiInterface::class.java)?.adminListApi(org_id)?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                progressDialog?.dismiss()
                try {
                    val gson = Gson()
                    if (response?.body() != null && response.isSuccessful()) {
                        val mAdminListResponse = gson.fromJson<AdminListResponse>(response.body().string(), AdminListResponse::class.java!!)
                        mAdminList.clear()
                        mAdminList.addAll(mAdminListResponse.getUsers() as ArrayList<AdminListResponse.User>)
                        rv_admin.adapter?.notifyDataSetChanged()

                    } else if (response?.errorBody() != null) {
                        val error = gson.fromJson<GeneralResponse>(response.errorBody().string(), GeneralResponse::class.java!!)
                        if (error.message != null) {
                            AppUtils.showToast(findViewById<View>(android.R.id.content), error.message!!)
                        } else {
                            AppUtils.showToast(findViewById<View>(android.R.id.content), getString(R.string.somethingWrong))
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    AppUtils.showToast(findViewById<View>(android.R.id.content), getString(R.string.somethingWrong))
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                progressDialog?.dismiss()
                AppUtils.showToast(findViewById<View>(android.R.id.content), getString(R.string.somethingWrong))
            }
        })
    }

    fun addAdminApi(org_id: Int, dialog: Dialog) {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Please wait")
        progressDialog!!.setCancelable(false)
        progressDialog?.show()

        val mAddAdminRequest = AddAdminRequest()

        val mPrivileges = mAddAdminRequest.Privileges()
        mPrivileges.orgId = org_id

        if (dialog.rb_iqama.isChecked) {
            mPrivileges.orgPerm = dialog.rb_iqama.text.toString()
        } else {
            mPrivileges.orgPerm = dialog.rb_all.text.toString()
        }

        mAddAdminRequest.setFirstName(dialog.et_admin_fname.text.toString().trim())
        mAddAdminRequest.setLastName(dialog.et_admin_lname.text.toString().trim())
        mAddAdminRequest.setEmail(dialog.et_admin_email.text.toString().trim())
        mAddAdminRequest.setMobileNumber("+" + dialog.ccp_admin.getSelectedCountryCode() + "-" + dialog.et_admin_phone.text.toString().trim())
        mAddAdminRequest.setMobileNumberVerified(true)
        mAddAdminRequest.setPrivileges(mPrivileges)

        ApiClient.getClient(this)?.create(ApiInterface::class.java)?.addAdminApi(org_id, mAddAdminRequest)?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                progressDialog?.dismiss()
                try {
                    val gson = Gson()
                    if (response?.body() != null && response.isSuccessful()) {
                        val mGeneralResponse = gson.fromJson<GeneralResponse>(response.body().string(),
                                GeneralResponse::class.java!!)
                        AppUtils.showToast(findViewById<View>(android.R.id.content), mGeneralResponse.message!!)
                        dialog.dismiss()

                        if (AppUtils.isConnected(this@ListManageAdminActivity)) {
                            adminLisApi(org_id as Int)
                        } else {
                            AppUtils.showToast(findViewById<View>(android.R.id.content), getString(R.string.chk_network))
                        }
                    } else if (response?.errorBody() != null) {
                        val error = gson.fromJson<GeneralResponse>(response.errorBody().string(), GeneralResponse::class.java!!)
                        if (error.message != null) {
                            AppUtils.showToast(findViewById<View>(android.R.id.content), error.message!!)
                        } else {
                            AppUtils.showToast(findViewById<View>(android.R.id.content), getString(R.string.somethingWrong))
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    AppUtils.showToast(findViewById<View>(android.R.id.content), getString(R.string.somethingWrong))
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                progressDialog?.dismiss()
                AppUtils.showToast(findViewById<View>(android.R.id.content), getString(R.string.somethingWrong))
            }
        })
    }

    fun editAdminApi(admin_id: Int, dialog: Dialog) {
        Log.d("Admin id " + admin_id, " org id " + getIntent().extras.get("org_id"))
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Please wait")
        progressDialog!!.setCancelable(false)
        progressDialog?.show()

        val mAddAdminRequest = AddAdminRequest()

        val mPrivileges = mAddAdminRequest.Privileges()
        mPrivileges.orgId = getIntent().extras.get("org_id") as Int?

        if (dialog.rb_iqama.isChecked) {
            mPrivileges.orgPerm = dialog.rb_iqama.text.toString()
        } else {
            mPrivileges.orgPerm = dialog.rb_all.text.toString()
        }

        mAddAdminRequest.setFirstName(dialog.et_admin_fname.text.toString().trim())
        mAddAdminRequest.setLastName(dialog.et_admin_lname.text.toString().trim())
        mAddAdminRequest.setEmail(dialog.et_admin_email.text.toString().trim())
        mAddAdminRequest.setMobileNumber("+" + dialog.ccp_admin.getSelectedCountryCode() + "-" + dialog.et_admin_phone.text.toString().trim())
        mAddAdminRequest.setMobileNumberVerified(true)
        mAddAdminRequest.setPrivileges(mPrivileges)

        ApiClient.getClient(this)?.create(ApiInterface::class.java)?.editAdminApi(admin_id, mAddAdminRequest)?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                progressDialog?.dismiss()
                try {
                    val gson = Gson()
                    if (response?.body() != null && response.isSuccessful()) {
                        if (getIntent().extras.get("org_id") != null) {
                            val org_id = getIntent().extras.get("org_id")
                            if (AppUtils.isConnected(this@ListManageAdminActivity)) {
                                adminLisApi(org_id as Int)
                            } else {
                                AppUtils.showToast(findViewById<View>(android.R.id.content), getString(R.string.chk_network))
                            }
                        }
                        dialog.dismiss()

                    } else if (response?.errorBody() != null) {
                        val error = gson.fromJson<GeneralResponse>(response.errorBody().string(), GeneralResponse::class.java!!)
                        if (error.message != null) {
                            AppUtils.showToast(findViewById<View>(android.R.id.content), error.message!!)
                        } else {
                            AppUtils.showToast(findViewById<View>(android.R.id.content), getString(R.string.somethingWrong))
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    AppUtils.showToast(findViewById<View>(android.R.id.content), getString(R.string.somethingWrong))
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                progressDialog?.dismiss()
                AppUtils.showToast(findViewById<View>(android.R.id.content), getString(R.string.somethingWrong))
            }
        })
    }

    fun deleteAdminApi(org_id: Int) {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Please wait")
        progressDialog!!.setCancelable(false)
        progressDialog?.show()

        ApiClient.getClient(this)?.create(ApiInterface::class.java)?.deleteAdminApi(org_id)?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                progressDialog?.dismiss()
                try {
                    val gson = Gson()
                    if (response?.body() != null && response.isSuccessful()) {
                        if (getIntent().extras.get("org_id") != null) {
                            val org_id = getIntent().extras.get("org_id")
                            if (AppUtils.isConnected(this@ListManageAdminActivity)) {
                                adminLisApi(org_id as Int)
                            } else {
                                AppUtils.showToast(findViewById<View>(android.R.id.content), getString(R.string.chk_network))
                            }
                        }

                    } else if (response?.errorBody() != null) {
                        val error = gson.fromJson<GeneralResponse>(response.errorBody().string(), GeneralResponse::class.java!!)
                        if (error.message != null) {
                            AppUtils.showToast(findViewById<View>(android.R.id.content), error.message!!)
                        } else {
                            AppUtils.showToast(findViewById<View>(android.R.id.content), getString(R.string.somethingWrong))
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    AppUtils.showToast(findViewById<View>(android.R.id.content), getString(R.string.somethingWrong))
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                progressDialog?.dismiss()
                AppUtils.showToast(findViewById<View>(android.R.id.content), getString(R.string.somethingWrong))
            }
        })
    }
}