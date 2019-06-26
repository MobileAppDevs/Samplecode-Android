package com.ongraph.muslimhub.ui.activities

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.ongraph.muslimhub.R
import com.ongraph.muslimhub.ui.adapters.AddImagesAdapter
import com.ongraph.muslimhub.ui.callbacks.AppCallBackListner
import com.ongraph.muslimhub.ui.model.ApiInterface
import com.ongraph.muslimhub.ui.model.response.GeneralResponse
import com.ongraph.muslimhub.ui.rest.ApiClient
import com.ongraph.muslimhub.ui.utils.AppUtils
import com.ongraph.muslimhub.ui.utils.GlobalVariable
import kotlinx.android.synthetic.main.activity_add_announcment.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class AddAnnouncementActivity : FragmentActivity() {

    private var selectedImageList = ArrayList<String>()
    private var progressDialog: ProgressDialog? = null
    private var adapter: AddImagesAdapter? = null
    private var orgId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_announcment)

        initViews()
    }

    private fun initViews() {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Please wait")
        progressDialog!!.setCancelable(false)

        imgBack.setOnClickListener {
            finish()
        }

        if (intent.extras != null && intent.extras.get("org_id") != null) {
            orgId = intent.extras.get("org_id") as Int
            Log.e("ORG id", " $orgId")
        } else {
            Log.e("ORG id", "empty ")
        }

        uploadLayout.setOnClickListener({
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkPermissionAndOpenImagePicker()
            } else {
                pickMultipleImageFromGallery()
            }
        })

        rvOrgsImgs.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapter = AddImagesAdapter(selectedImageList, this, object : AppCallBackListner.ItemClick {
            override fun onResult(mObject: Int?) {
                if (mObject == -1) {
                    uploadLayout.visibility = View.VISIBLE
                    rvOrgsImgs.visibility = View.GONE
                } else {
                    uploadLayout.performClick()
                }
            }
        })
        adapter!!.isEditingMode(false)
        rvOrgsImgs.adapter = adapter

        btn_save.setOnClickListener({
            progressDialog?.show()
            btn_save.isEnabled = false
            if (validate()) {
                Handler().postDelayed({
                    /*if (orgId != null)
                        callAddOrgAnouncementApi()
                    else*/
                    callAddAnouncementApi()
                }, 2000)
            } else {
                progressDialog?.dismiss()
                btn_save.isEnabled = true
            }
        })
        btn_cancel.setOnClickListener({
            finish()
        })
    }

    private fun validate(): Boolean {
        if (TextUtils.isEmpty(et_title.getText().toString().trim())) {
            et_title.setError(getString(R.string.valid_title))
            return false
        }
        if (TextUtils.isEmpty(et_desc.getText().toString().trim())) {
            et_desc.setError(getString(R.string.valid_desc))
            return false
        }
        return true
    }

    fun setImageListData() {
        uploadLayout.visibility = View.GONE
        rvOrgsImgs.visibility = View.VISIBLE
        adapter!!.notifyDataSetChanged()
    }

    private fun checkPermissionAndOpenImagePicker() {
        val hasWriteExternalPermission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (hasWriteExternalPermission == PackageManager.PERMISSION_GRANTED) {
            pickMultipleImageFromGallery()
        } else {
            addPermission()
        }
    }

    private fun addPermission() {
        val hasWriteExternalPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val hasReadExternalPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        val permissions = ArrayList<String>()

        if (hasWriteExternalPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (hasReadExternalPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (!permissions.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions.toTypedArray(), 100)
            } else {
                pickMultipleImageFromGallery()
            }
        } else {
            pickMultipleImageFromGallery()
        }
    }

    private fun openAnnouncementListActivity() {
        startActivity(Intent(this, AnnouncmentListActivity::class.java))
        finish()
    }

    private fun pickMultipleImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GlobalVariable.PICK_IMAGE_MULTIPLE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 100) {
            val hasReadExternalPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            val hasWriteExternalPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (hasReadExternalPermission == PackageManager.PERMISSION_GRANTED && hasWriteExternalPermission == PackageManager.PERMISSION_GRANTED) {
                pickMultipleImageFromGallery()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, requestCode, data)

        if (requestCode == GlobalVariable.PICK_IMAGE_MULTIPLE) {
            if (data != null) {
                if (data.getClipData() != null) {
                    val count: Int = data.clipData.getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                    for (x in 0 until count) {
                        val imageUri: Uri = data.clipData.getItemAt(x).getUri();
                        Log.d("imageUri- " + x, imageUri.toString())
                        if (selectedImageList.size >= 6) {
                            AppUtils.showToast(findViewById<View>(android.R.id.content), getString(R.string.max_6))
                            break
                        } else
                            selectedImageList.add(imageUri.toString())
                    }
                } else {
                    if (data != null) {
                        val contentURI = data.data
                        if (selectedImageList.size < 6) {
                            selectedImageList.add(contentURI.toString())
                        }
                    }
                }
                setImageListData()
            }
        }
    }

    private fun callAddAnouncementApi() {
        val mImageList: ArrayList<MultipartBody.Part> = ArrayList()
        for (i in 0 until selectedImageList.size) {
            try {
                val uri: Uri = Uri.parse(selectedImageList.get(i));
                val bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri)
                val file = AppUtils.getImageFile(bitmap, "image_" + UUID.randomUUID() + ".png")
                val requestFile: RequestBody = RequestBody.create(MediaType.parse("image/png"), file)
                val imageFile = MultipartBody.Part.createFormData("image" + (i + 1), file.name, requestFile);
                mImageList.add(imageFile)
            } catch (e: IOException) {
                e.printStackTrace()
                AppUtils.showToast(findViewById<View>(android.R.id.content), "Failed!")
            }
        }
        val titleBody: RequestBody = RequestBody.create(MediaType.parse("text"), et_title.text.toString().trim())
        val descBody: RequestBody = RequestBody.create(MediaType.parse("text"), et_desc.text.toString().trim())

        ApiClient.getClient(this)?.create(ApiInterface::class.java)?.addAnnouncement(
                titleBody, descBody, mImageList)?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                progressDialog?.dismiss()
                try {
                    val gson = Gson()
                    if (response?.body() != null && response.isSuccessful()) {
                        AppUtils.showToast(findViewById<View>(android.R.id.content), "Image uploaded")
                        btn_save.isEnabled = true
                        openAnnouncementListActivity()
                    } else if (response?.errorBody() != null) {
                        val error = gson.fromJson<GeneralResponse>(response.errorBody().string(), GeneralResponse::class.java)
                        if (error.message != null) {
                            AppUtils.showToast(findViewById<View>(android.R.id.content), error.message!!)
                        } else {
                            AppUtils.showToast(findViewById<View>(android.R.id.content), getString(R.string.somethingWrong))
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    btn_save.isEnabled = true
                    AppUtils.showToast(findViewById<View>(android.R.id.content), getString(R.string.somethingWrong))
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                progressDialog?.dismiss()
                t!!.message
                btn_save.isEnabled = true
                AppUtils.showToast(findViewById<View>(android.R.id.content), getString(R.string.somethingWrong))
            }
        })
    }

    private fun callAddOrgAnouncementApi() {
        val mImageList: ArrayList<MultipartBody.Part> = ArrayList()
        for (i in 0 until selectedImageList.size) {
            try {
                val uri: Uri = Uri.parse(selectedImageList.get(i));
                val bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri)
                val file = AppUtils.getImageFile(bitmap, "image_" + UUID.randomUUID() + ".png")
                val requestFile: RequestBody = RequestBody.create(MediaType.parse("image/png"), file)
                val imageFile = MultipartBody.Part.createFormData("image" + (i + 1), file.name, requestFile);
                mImageList.add(imageFile)
            } catch (e: IOException) {
                e.printStackTrace()
                AppUtils.showToast(findViewById<View>(android.R.id.content), "Failed!")
            }
        }
        val orgId: RequestBody = RequestBody.create(MediaType.parse("id"), orgId.toString())
        val titleBody: RequestBody = RequestBody.create(MediaType.parse("text"), et_title.text.toString().trim())
        val descBody: RequestBody = RequestBody.create(MediaType.parse("text"), et_desc.text.toString().trim())

        ApiClient.getClient(this)?.create(ApiInterface::class.java)?.addOrgAnnouncement(
                orgId, titleBody, descBody, mImageList)?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                progressDialog?.dismiss()
                try {
                    val gson = Gson()
                    if (response?.body() != null && response.isSuccessful()) {
                        AppUtils.showToast(findViewById<View>(android.R.id.content), "Image uploaded")
                        btn_save.isEnabled = true
                        openAnnouncementListActivity()
                    } else if (response?.errorBody() != null) {
                        val error = gson.fromJson<GeneralResponse>(response.errorBody().string(), GeneralResponse::class.java)
                        if (error.message != null) {
                            AppUtils.showToast(findViewById<View>(android.R.id.content), error.message!!)
                        } else {
                            AppUtils.showToast(findViewById<View>(android.R.id.content), getString(R.string.somethingWrong))
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    btn_save.isEnabled = true
                    AppUtils.showToast(findViewById<View>(android.R.id.content), getString(R.string.somethingWrong))
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                progressDialog?.dismiss()
                t!!.message
                btn_save.isEnabled = true
                AppUtils.showToast(findViewById<View>(android.R.id.content), getString(R.string.somethingWrong))
            }
        })
    }
}
