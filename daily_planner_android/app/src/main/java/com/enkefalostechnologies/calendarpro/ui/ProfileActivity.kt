package com.enkefalostechnologies.calendarpro.ui

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.SubscriptionStatus
import com.android.billingclient.api.Purchase
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.constant.Constants
import com.enkefalostechnologies.calendarpro.databinding.ActivityProfileBinding
import com.enkefalostechnologies.calendarpro.router.Router.navigateToLoginScreen
import com.enkefalostechnologies.calendarpro.ui.base.BaseActivity
import com.enkefalostechnologies.calendarpro.ui.bottomSheet.SubscribedBottomSheet
import com.enkefalostechnologies.calendarpro.ui.bottomSheet.SubscriptionBottomSheet
import com.enkefalostechnologies.calendarpro.ui.viewModel.ProfileActivityViewModel
import com.enkefalostechnologies.calendarpro.util.AppUtil.isNetworkAvailable
import com.enkefalostechnologies.calendarpro.util.AppUtil.showToast
import com.enkefalostechnologies.calendarpro.util.Extension.close
import com.enkefalostechnologies.calendarpro.util.Extension.gone
import com.enkefalostechnologies.calendarpro.util.Extension.setImageFromUrl
import com.enkefalostechnologies.calendarpro.util.Extension.visible
import com.enkefalostechnologies.calendarpro.util.StorageUtil
import com.enkefalostechnologies.calendarpro.util.SubscriptionBottomSheetListener
import com.enkefalostechnologies.calendarpro.util.isCameraPermissionAllowed
import com.enkefalostechnologies.calendarpro.util.isMediaPermissionIsAllowed
import java.io.File
import java.util.Date
import java.util.UUID


class ProfileActivity : BaseActivity<ActivityProfileBinding>(R.layout.activity_profile) {


    val viewModel: ProfileActivityViewModel by viewModels { ProfileActivityViewModel.Factory }
    var clNameOpen = false

    private var imageUri: Uri? = null
    private val selectedCameraRequestCode = 1008
    private val requestCode = 2009
    override fun onViewBindingCreated() {
        setStatusBarColor()
        binding.clEmail.gone()
        binding.llEtEmailFilled.isEnabled = false
        if (!clNameOpen) {
            binding.clName.gone()
        }
        binding.llEtNameFilled.isEnabled = false
        setListeners()
        setProfileData()
    }

    override fun addObserver() {
        viewModel.userData.observe(this, Observer { user ->
            viewModel.user = user
            setProfileData()
        })
        viewModel.userAttributes.observe(this, Observer { user ->
            viewModel.fetchUserData(getUserEmail())
            setProfileData()
        })
        viewModel.loginSession.observe(this, Observer {
            if (it) {
                viewModel.fetchUserAttributes()
            }
            viewModel.isLoggedIn = it
            viewModel.user = null
        })
    }

    override fun removeObserver() {

    }


    fun setListeners() {
        binding.ivBack.setOnClickListener { finish() }
        binding.btnUpdate.setOnClickListener(this)
        binding.btnChangePassword.setOnClickListener(this)
        binding.ivSetting.setOnClickListener(this)
        binding.btnUpgrade.setOnClickListener(this)
        binding.ivCamera.setOnClickListener(this)
        binding.tvMySubscription.setOnClickListener(this)
        binding.tvEditEmail.setOnClickListener {
            binding.clEmail.visible()
            binding.clFilledEmail.gone()
        }
        binding.tvEditName.setOnClickListener {
            clNameOpen = true
            binding.clName.visible()
            binding.clFilledName.gone()
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.btnUpdate -> {
                if (!isNetworkAvailable()) {
                    showToast("Please check your internet connection.")
                }else {
                    if (!binding.clFilledName.isVisible) {
                        if (binding.etName.text.toString().trim().isBlank()) {
                            showToast(Constants.TOAST_ENTER_NAME)
                        } else {
                            amplifyDataModelUtil.updateUser(
                                binding.etName.text.toString(),
                                binding.etEmail.text.toString(), {
                                    runOnUiThread {
                                        // showToast(Constants.TOAST_PROFILE_UPDATED_SUCCESSFULLY)
                                        binding.clName.gone()
                                        binding.clFilledName.visible()
                                        setProfileData()

                                    }
                                }, {
                                    runOnUiThread {
                                        showToast(Constants.TOAST_FAILED_TO_UPDATE_PROFILE)
                                    }

                                })
                        }
                    } else {
                        if (binding.etName.text.toString().trim().isBlank()) {
                            showToast(com.enkefalostechnologies.calendarpro.constant.Constants.TOAST_ENTER_NAME)
                        } else if (binding.etEmail.text.toString().trim().isBlank()) {
                            showToast(com.enkefalostechnologies.calendarpro.constant.Constants.TOAST_ENTER_EMAIL)
                        }
                    }
                }
            }

            binding.btnChangePassword -> {
                if (!isNetworkAvailable()) {
                    showToast("Please check your internet connection.")
                } else startActivity(Intent(this, ChangePassword::class.java))
            }

            binding.ivSetting -> {
                val i = Intent(this, HomeActivity::class.java)
                i.putExtra("screen", "profile")
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            }

            binding.btnUpgrade -> {
                if (!isNetworkAvailable()) {
                    showToast("Please check your internet connection.")
                }else if (isUserLoggedIn()) {
                    SubscriptionBottomSheet(
                        getUserEmail(),
                        amplifyDataModelUtil, object : SubscriptionBottomSheetListener {
                            override fun onPurchaseSuccess(
                                purchases: Purchase,
                                validUpTo: Date,
                                subscriptionType: com.amplifyframework.datastore.generated.model.SubscriptionType
                            ) {
                                setSubscriptionStatus(SubscriptionStatus.ACTIVE)
                                saveSubscribedPlan(subscriptionType)
                                showToast("Subscribed Successfully.")
                               setProfileData()
                            }

                            override fun onClosed() {

                            }

                            override fun onError(error: String) {
                                showToast(error)
                            }

                        }
                    ).show(
                        supportFragmentManager, "Subscription bottom-sheet"
                    )
                } else {
                    navigateToLoginScreen()
                }
            }

            binding.tvMySubscription -> {
                if (!isNetworkAvailable()) {
                    showToast("Please check your internet connection.")
                }else {
                    SubscribedBottomSheet(
                        getUserEmail(),
                        amplifyDataModelUtil,
                        object : SubscriptionBottomSheetListener {
                            override fun onPurchaseSuccess(
                                purchases: Purchase,
                                validUpTo: Date,
                                subscriptionType: com.amplifyframework.datastore.generated.model.SubscriptionType
                            ) {
                                setSubscriptionStatus(SubscriptionStatus.ACTIVE)
                                saveSubscribedPlan(subscriptionType)
                                showToast("Subscribed Successfully.")
                                setProfileData()
                            }

                            override fun onClosed() {

                            }

                            override fun onError(error: String) {
                                showToast(error)
                            }

                        }).show(supportFragmentManager, "gcgvhvh")
                }
            }

            binding.ivCamera -> {
                mCheckCameraPermissionAndOpenCamera()
            }

        }
    }

    private var mGetContent: ActivityResultLauncher<Uri> = registerForActivityResult<Uri, Boolean>(
        ActivityResultContracts.TakePicture(),
        object : ActivityResultCallback<Boolean?> {

            override fun onActivityResult(result: Boolean?) {
              if (result == true){
                  val file = File(getRealPathFromURI(imageUri))
                  val filename = "${UUID.randomUUID()}.${file.extension}"
                  Amplify.Storage.uploadFile(filename, file,
                      {
                          //val url="https://dailyplanner2707f971afff4d3d96da7d9ba27d2df484701-prod.s3.eu-north-1.amazonaws.com/"+it.key
                          val url =
                              "https://dailyplanner2707f971afff4d3d96da7d9ba27d2df482147-staging.s3.eu-north-1.amazonaws.com/" + it.key
                          amplifyDataModelUtil.updateUserPicUrl(getUserEmail() , url, {
                              amplifyDataModelUtil.deletePhoto(getUserEmail())
                              runOnUiThread {
                                  dialog.close()
                                  binding.ivProfile.setImageFromUrl(url)
                                  setUserPicUrl(url)
                                  // showToast(Constants.TOAST_PROFILE_UPDATED_SUCCESSFULLY)
                              }
                          }, {
                              runOnUiThread {
                                  dialog.close()
                                  showToast(Constants.TOAST_FAILURE)
                              }
                          })
                      },
                      {
                          runOnUiThread {
                              dialog.close()
                              showToast(Constants.TOAST_FAILURE)
                          }
                      }
                  )
              }
            }
        })

    private fun mCheckCameraPermissionAndOpenCamera() {
        if (isCameraPermissionAllowed()){
            openCamera()
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                mediaStoragePermission.launch(arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                    Manifest.permission.CAMERA
                ))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                mediaStoragePermission.launch(arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.CAMERA
                ))
            } else {
                mediaStoragePermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA))
            }
        }
    }


    private fun openCamera(){
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "upload")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val file = File(imageUri.toString())
        if (file.exists()) file.delete()
        mGetContent.launch(imageUri)
    }
    private val mediaStoragePermission = (this as ComponentActivity).registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
        val granted = results.entries.all { it.value }
        if (granted){
            openCamera()
        }else{
            openPermissionSettings()
        }
    }

    private fun openPermissionSettings() {
        startActivity(StorageUtil.openPermissionSettings(this))
    }

    private fun getRealPathFromURI(uri: Uri?): String {
        var path = ""
        if (contentResolver != null) {
            val cursor: Cursor? = contentResolver.query(uri!!, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }

    private fun setStatusBarColor() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.color_D3F26A)
    }

    private fun setProfileData() {
        if(isNetworkAvailable()) {
            amplifyDataModelUtil.fetchUserByEmail(getUserEmail(), {
                runOnUiThread {
                    while (it.hasNext()) {
                        val user = it.next()
                        if (user.name.isNotBlank()) {
                            if (!binding.clName.isVisible) {
                                binding.etName.setText(user.name)
                            }
                            binding.etNameFilled.setText(user.name)
                            setUserName(user.name)
                        }
                        if (user.email.isNotBlank()) {
                            binding.etEmail.setText(user.email)
                            binding.etEmailFilled.setText(user.email)
                            setUserEmail(user.email)
                        }
                        if (user.picUrl.isNotBlank()) {
                            binding.ivProfile.setImageFromUrl(user.picUrl)
                            setUserPicUrl(user.picUrl)
                        } else {
                            binding.ivProfile.setImageFromUrl("${Constants.AVTAR_API}${user.name}")
                        }
                        if (user.isSocialLoggedIn) {
                            binding.btnChangePassword.gone()
                            binding.tvEditEmail.gone()
                            binding.etEmailFilled.isEnabled = false
                        } else {
                            binding.btnChangePassword.visible()
                            binding.tvEditEmail.gone()
                            binding.etEmailFilled.isEnabled = false

                        }
                        if (user.subscriptionStatus == SubscriptionStatus.ACTIVE) {
                            binding.ivPremium.visible()
                        } else binding.ivPremium.gone()
                        if (user.subscriptionStatus != SubscriptionStatus.NONE) {
                            if (user.subscriptionStatus != SubscriptionStatus.ACTIVE) {
                                binding.btnUpgrade.visible()
                            } else binding.btnUpgrade.gone()
                            binding.tvMySubscription.visible()
                            binding.llLine.visible()
                        } else {
                            binding.tvMySubscription.gone()
                            binding.llLine.gone()
                        }
                        binding.tvEditEmail.gone()
                        binding.etEmailFilled.isEnabled = false

                    }
                }

            }, {})
        }else{
            if (getUserName().isNotEmpty()) {
                if (!binding.clName.isVisible) {
                    binding.etName.setText(getUserName())
                }
                binding.etNameFilled.setText(getUserName())
                setUserName(getUserName())
            }
            if (getUserEmail().isNotBlank()) {
                binding.etEmail.setText(getUserEmail())
                binding.etEmailFilled.setText(getUserEmail())
                setUserEmail(getUserEmail())
            }
            if (getUserPicUrl().isNotBlank()) {
                binding.ivProfile.setImageFromUrl(getUserPicUrl())
                setUserPicUrl(getUserPicUrl())
            } else {
                binding.ivProfile.setImageFromUrl("${Constants.AVTAR_API}${getUserName()}")
            }
            if (isSocialLoggedIn()) {
                binding.btnChangePassword.gone()
                binding.tvEditEmail.gone()
                binding.etEmailFilled.isEnabled = false
            } else {
                binding.btnChangePassword.visible()
                binding.tvEditEmail.gone()
                binding.etEmailFilled.isEnabled = false
            }
            if (getSubscriptionStatus() == SubscriptionStatus.ACTIVE) {
                binding.ivPremium.visible()
            } else binding.ivPremium.gone()

            if (getSubscriptionStatus() != SubscriptionStatus.NONE) {
                if (getSubscriptionStatus() != SubscriptionStatus.ACTIVE) {
                    binding.btnUpgrade.visible()
                } else binding.btnUpgrade.gone()
                binding.tvMySubscription.visible()
                binding.llLine.visible()
            } else {
                binding.tvMySubscription.gone()
                binding.llLine.gone()
            }
            binding.tvEditEmail.gone()
            binding.etEmailFilled.isEnabled = false
        }
        binding.btnUpdate.visible()
        binding.ivProfile.visible()
        binding.ivCamera.visible()
        binding.clFilledEmail.visible()
        if (binding.clName.isVisible) {
            binding.clFilledName.gone()
        } else {
            binding.clFilledName.visible()
        }
    }


}