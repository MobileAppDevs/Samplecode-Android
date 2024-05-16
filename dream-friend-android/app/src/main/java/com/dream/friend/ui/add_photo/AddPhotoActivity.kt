package com.dream.friend.ui.add_photo

import android.Manifest
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.nileshp.multiphotopicker.photopicker.activity.PickImageActivity
import com.dream.friend.R
import com.dream.friend.common.*
import com.dream.friend.common.Constants.ProfileSetUpSuccessMsg
import com.dream.friend.common.Utils.dialogLoading
import com.dream.friend.common.Utils.getPath
import com.dream.friend.common.Utils.onBackPress
import com.dream.friend.common.Utils.openLoginPage
import com.dream.friend.common.Utils.openPermissionSettings
import com.dream.friend.common.Utils.permissionAlert
import com.dream.friend.common.Utils.showToast
import com.dream.friend.data.network.Resource
import com.dream.friend.databinding.ActivityAddPhotoBinding
import com.dream.friend.interfaces.AddImageClickListener
import com.dream.friend.interfaces.PhotoDeleteListener
import com.dream.friend.interfaces.PhotoGuideLineListener
import com.dream.friend.ui.bottomsheet.PhotoGuideLineBottomSheetDialog
import com.dream.friend.ui.bottomsheet.realtimeImage.DeleteAndDismissBottomSheet
import com.dream.friend.ui.home.HomeActivity
import com.dream.friend.ui.location_permissions.LocationPermissionActivity
import com.dream.friend.ui.viewModel.UpdateUserDetailsViewModel
import com.dream.friend.ui.viewModel.UserLoginViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class AddPhotoActivity : AppCompatActivity() ,View.OnClickListener{

    private val viewModelUserLogin: UserLoginViewModel by viewModels()
    private val viewModel: UpdateUserDetailsViewModel by viewModels()
    private val addPhotoBinding:ActivityAddPhotoBinding by activityBindings(R.layout.activity_add_photo)
    private lateinit var adapter: Add2PhotoAdapter
    private var imageList = arrayListOf<String>()
    private lateinit var dialog: Dialog
    private var size = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(addPhotoBinding.root)

        dialog = dialogLoading()
        dialog.dismiss()

        setAdapter()

        addPhotoBinding.btnContinueEnable.setOnClickListener(this)
//        addPhotoBinding.tivBack.setOnClickListener(this)
        addPhotoBinding.tvGuideline.setOnClickListener {
            val bottomSheet = PhotoGuideLineBottomSheetDialog(object:PhotoGuideLineListener{
                override fun onUploadBtnClicked() {
                    checkBeforeOpenGallery(6 - imageList.size)
                }
            })
            bottomSheet.show(supportFragmentManager, "ModalBottomSheet")
        }

        onBackPress()

        uploadUserImagesResponse()
    }

    private fun uploadUserImagesResponse() {
        viewModel.updateUserProfileImagesResponse.observe(this) { response ->
            when(response) {
                is Resource.Success -> {
                    response.data?.let {
                        if (response.data.user.images.size >= size) {
                            dialog.dismiss()
                            viewModelUserLogin.saveUser(it.user)
                            nextScr()
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
                is Resource.TokenRenew -> {
                    if (response.isTokenExpire == true) {
                        openLoginPage()
                    }
                }
            }
        }
    }

    private fun setAdapter() {
        adapter = Add2PhotoAdapter()
        addPhotoBinding.recyclerAddPhoto.adapter = adapter

        adapter.setOnAddImageListener(object : AddImageClickListener {
            override fun setOnAddImageItemListener() {
                checkBeforeOpenGallery(6 - imageList.size)
            }

            override fun setOnImageItemDeleteListener(item: String) {
                DeleteAndDismissBottomSheet(item,object : PhotoDeleteListener {
                    override fun onDeleteClicked(fileName: String) {
                        imageList.remove(item)
                        setImage()
                    }
                }).show(supportFragmentManager,"photoDeleteBottomSheet")
            }

            override fun setOnClickListener(position: Int) {}
        })
    }

    override fun onClick(id: View?) {
        val photos: ArrayList<MultipartBody.Part> = arrayListOf()
        var count = 0
        imageList.forEach {
            val file = File(it)
            val requestFile: RequestBody =
                file.asRequestBody("image/${file.extension}".toMediaTypeOrNull())
            if (count < 6) photos.add(MultipartBody.Part.createFormData("image", file.name, requestFile))
            count ++
        }
        when(id) {
            addPhotoBinding.btnContinueEnable -> {
                size = count
                photos.forEach {
                    viewModelUserLogin.getUser()?.userId?.let { userID ->
                        viewModel.userUserProfileImages(
                            userID,
                            it)
                    }
                }
            }
        }
    }

    private fun nextScr() {
        Intent(
            this,
            LocationPermissionActivity::class.java
        ).also {
            startActivity(it)
            finishAffinity()
        }
    }

    private fun checkBeforeOpenGallery(maxLimit: Int) {
        if (Build.VERSION_CODES.Q <= Build.VERSION.SDK_INT &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_MEDIA_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            readStoragePermissionRequest.launch(Manifest.permission.ACCESS_MEDIA_LOCATION)
        } else if (Build.VERSION_CODES.Q > Build.VERSION.SDK_INT && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            readStoragePermissionRequest.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            mInitGalleryOption(maxLimit)
        }
    }

    private fun launchGallery() {
        pickImageFromGalleryApiT.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val pickImageFromGalleryApiT =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            val file = File(it?.path.toString())
            val path = it?.let { it1 -> getPath(it1) }
            Log.d(
                "Image",
                file.absolutePath+
                        "\n$it" +
                        "\n$path"
            )
            if (path != null) {
                imageList.add(path)
                setImage()
            }
        }

    private val readStoragePermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                mInitGalleryOption(6-imageList.size)
            } else {
                permissionAlert(
                    message = "Storage permission is Required.",
                    title = "Permission",
                    successBtnText = "Request",
                    callback = { dialog: DialogInterface, i: Int ->
                        if (i == DialogInterface.BUTTON_POSITIVE)
                            permissionSettingRequest.launch(openPermissionSettings())
                        else dialog.dismiss()
                    }
                )
            }
        }

    private val permissionSettingRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            checkBeforeOpenGallery(6 - imageList.size)
        }

    private fun setImage() {
        adapter.setImages(imageList)
        if (imageList.size < 2) {
            addPhotoBinding.btnContinue.show()
            addPhotoBinding.btnContinueEnable.hide()
            addPhotoBinding.btnContinueEnable.disable()
        } else {
            addPhotoBinding.btnContinue.hide()
            addPhotoBinding.btnContinueEnable.show()
            addPhotoBinding.btnContinueEnable.enable()
        }
    }

    private fun mInitGalleryOption(maxLimit: Int) {
        if (Build.VERSION_CODES.TIRAMISU <= Build.VERSION.SDK_INT) launchGallery()
        else {
            val mIntent = Intent(this, PickImageActivity::class.java)
            mIntent.putExtra(PickImageActivity.KEY_LIMIT_MAX_IMAGE, maxLimit)
            mIntent.putExtra(PickImageActivity.KEY_LIMIT_MIN_IMAGE, 1)
            pickImageFromGalleryApiLessThenT.launch(mIntent)
        }
    }

    private val pickImageFromGalleryApiLessThenT =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val uris = it.data?.extras?.getStringArrayList(PickImageActivity.KEY_DATA_RESULT)
            Log.d("Images", "mSelected: $uris")
            uris?.forEach { uri ->
                imageList.add(uri)
            }
            setImage()
        }
}