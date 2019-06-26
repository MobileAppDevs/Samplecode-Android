package com.ongraph.muslimhub.ui.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.google.gson.Gson
import com.ongraph.muslimhub.R
import com.ongraph.muslimhub.ui.adapters.AnnounceMentAdpater
import com.ongraph.muslimhub.ui.callbacks.AppCallBackListner
import com.ongraph.muslimhub.ui.model.ApiInterface
import com.ongraph.muslimhub.ui.model.response.AnnouncementListResponse
import com.ongraph.muslimhub.ui.model.response.GeneralResponse
import com.ongraph.muslimhub.ui.rest.ApiClient
import com.ongraph.muslimhub.ui.utils.AppDialogs
import com.ongraph.muslimhub.ui.utils.AppUtils
import com.ongraph.muslimhub.ui.utils.SharedPrefsHelper
import kotlinx.android.synthetic.main.activity_feed_announcment.*
import kotlinx.android.synthetic.main.layout_image_header.*
import okhttp3.ResponseBody
import java.util.*

class AnnouncmentListActivity : AppCompatActivity() {

    private var mAdapter: AnnounceMentAdpater? = null
    private var mAnnouncementList: ArrayList<AnnouncementListResponse.Data> = ArrayList()
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_announcment)
        initViews()
    }

    private fun initViews() {
        rv_announcement!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = AnnounceMentAdpater(mAnnouncementList, this, object : AppCallBackListner.DeleteCallback {
            override fun onResult(mObject: Any?, id: Int?) {
                if (mObject != null) {
                    val intent = Intent(this@AnnouncmentListActivity, EditAnnouncementActivity::class.java)
                    intent.putExtra("Form_Data_Announcement", mObject as AnnouncementListResponse.Data)
                    startActivity(intent)
                } else if (id != null) {
                    AppDialogs.showAlertDialog(this@AnnouncmentListActivity, "", getString(R.string.delete_announcement),
                            true, object : AppCallBackListner.DialogCallback {
                        override fun onClickPositiveButton() {
                            if (AppUtils.isConnected(this@AnnouncmentListActivity)) {
                                deleteMHAnnouncement(id)
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
        rv_announcement!!.adapter = mAdapter

        if (!SharedPrefsHelper.getInstance().loginData.token.isNullOrEmpty()) {
            if (SharedPrefsHelper.getInstance().loginData.isSuperuser!!)
                iv_add.visibility = View.VISIBLE
            else
                iv_add.visibility = View.GONE
        } else {
            iv_add.visibility = View.GONE
        }

        iv_add!!.setOnClickListener {
            startActivity(Intent(this, AddAnnouncementActivity::class.java))
        }
        iv_bck.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })

        iv_bck.setImageResource(R.mipmap.search_move)
        ll_header!!.visibility = View.GONE

        callAnnouncementListApi()
    }

    private fun callAnnouncementListApi() {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Please wait")
        progressDialog!!.setCancelable(false)
        progressDialog?.show()

        ApiClient.getClient(this)?.create(ApiInterface::class.java)?.getMhAnnouncementLisApi()?.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: retrofit2.Call<ResponseBody>?, response: retrofit2.Response<ResponseBody>?) {
                progressDialog?.dismiss()
                try {
                    val gson = Gson()
                    if (response?.body() != null && response.isSuccessful) {
                        val mAnnouncementListResponse = gson.fromJson<AnnouncementListResponse>(
                                response.body().string(), AnnouncementListResponse::class.java!!)

                        mAnnouncementList!!.clear()
                        mAnnouncementList!!.addAll(mAnnouncementListResponse.getData() as ArrayList<AnnouncementListResponse.Data>)
                        mAdapter!!.notifyDataSetChanged()
                        mAdapter!!.refreshAdapter(mAnnouncementList)
                        /*containerView.rv_eid_salah.adapter!!.notifyDataSetChanged()*/

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

            override fun onFailure(call: retrofit2.Call<ResponseBody>?, t: Throwable?) {
                progressDialog?.dismiss()
                AppUtils.showToast(findViewById<View>(android.R.id.content), getString(R.string.somethingWrong))
            }
        })
    }

    fun deleteMHAnnouncement(id: Int) {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Please wait")
        progressDialog!!.setCancelable(false)
        progressDialog?.show()

        ApiClient.getClient(this)?.create(ApiInterface::class.java)?.deleteMHAnnouncement(id)?.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: retrofit2.Call<ResponseBody>?, response: retrofit2.Response<ResponseBody>?) {
                progressDialog?.dismiss()
                try {
                    val gson = Gson()
                    if (response?.body() != null && response.isSuccessful) {
                        val response = gson.fromJson<GeneralResponse>(response.body().string(), GeneralResponse::class.java!!)
                        AppUtils.showToast(findViewById<View>(android.R.id.content), response.message!!)
                        callAnnouncementListApi()
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

            override fun onFailure(call: retrofit2.Call<ResponseBody>?, t: Throwable?) {
                progressDialog?.dismiss()
                AppUtils.showToast(findViewById<View>(android.R.id.content), getString(R.string.somethingWrong))
            }
        })
    }
}
