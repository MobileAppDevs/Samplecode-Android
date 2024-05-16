package com.dream.friend.ui.settings

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.dream.friend.R
import com.dream.friend.common.*
import com.dream.friend.common.Utils.dialogLoading
import com.dream.friend.common.Utils.getPath
import com.dream.friend.common.Utils.openLoginPage
import com.dream.friend.common.Utils.openPermissionSettings
import com.dream.friend.common.Utils.permissionAlert
import com.dream.friend.common.Utils.previewDialog
import com.dream.friend.common.Utils.showToast
import com.dream.friend.data.model.EditProfileReq
import com.dream.friend.data.model.Lifestyle
import com.dream.friend.data.model.LifestyleReq
import com.dream.friend.data.model.user.Location
import com.dream.friend.data.model.user.User
import com.dream.friend.data.network.Resource
import com.dream.friend.databinding.ActivityCreateProfileBinding
import com.dream.friend.databinding.ActivityEditProfileBinding
import com.dream.friend.enums.RealtimeImageVerificationStatus
import com.dream.friend.interfaces.AddImageClickListener
import com.dream.friend.interfaces.ChipGroupListener
import com.dream.friend.interfaces.HeightListener
import com.dream.friend.interfaces.NameListener
import com.dream.friend.interfaces.PhotoDeleteListener
import com.dream.friend.interfaces.PhotoVerificationListener
import com.dream.friend.ui.add_photo.Add2PhotoAdapter
import com.dream.friend.ui.bottomsheet.editProfile.BsChipGroupDialog
import com.dream.friend.ui.bottomsheet.editProfile.BsHeightDialog
import com.dream.friend.ui.bottomsheet.editProfile.BsNameDialog
import com.dream.friend.ui.bottomsheet.realtimeImage.DeleteAndDismissBottomSheet
import com.dream.friend.ui.bottomsheet.realtimeImage.RealTimeImageFailedBottomSheetDialog
import com.dream.friend.ui.bottomsheet.realtimeImage.RealTimeImageSubmitBottomSheetDialog
import com.dream.friend.ui.bottomsheet.realtimeImage.RealTimeImageTakePhotoBottomSheetDialog
import com.dream.friend.ui.home.activities.GalleryActivity
import com.dream.friend.ui.viewModel.HomeScreenViewModel
import com.dream.friend.ui.viewModel.UpdateUserDetailsViewModel
import com.dream.friend.ui.viewModel.UserLoginViewModel
import com.dream.friend.util.Extensions.gone
import com.dream.friend.util.Extensions.visible
import com.dream.friend.util.KeyboardWatcher
import com.nileshp.multiphotopicker.photopicker.activity.PickImageActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat


class EditProfileActivity : AppCompatActivity(), View.OnClickListener,
    KeyboardWatcher.OnKeyboardToggleListener {
    var selectedSexualOrientationId = arrayListOf<Int>()
    var selectedEducationId = arrayListOf<Int>()
    var selectedReligionId = arrayListOf<Int>()
    var selectedInterestsId = arrayListOf<Int>()
    private val binding: ActivityEditProfileBinding by activityBindings(R.layout.activity_edit_profile)
    private lateinit var dialog: Dialog
    private var uName = ""
    var isUpdateButtonVisible = false
    var drinking: String? = null
    var smoking: String? = null
    var workout: String? = null
    var lookingFor: Int? = null
    var gender: String? = null
    var name: String? = null
    var about: String? = null
    var height: Int? = null
    var lat: Double? = null
    var long: Double? = null
    var isAboutChanged = false
    private val viewModelUserLogin: UserLoginViewModel by viewModels()
    private val viewModel: UpdateUserDetailsViewModel by viewModels()
    private val homeViewModel: HomeScreenViewModel by viewModels()
    lateinit var realTimeImageTakePhotoBottomSheetDialog: RealTimeImageTakePhotoBottomSheetDialog
     var realTimeImageSubmitBottomSheetDialog: RealTimeImageSubmitBottomSheetDialog?=null
     var realTimeImageFailedBottomSheetDialog: RealTimeImageFailedBottomSheetDialog?=null
    private lateinit var user: User
    private val adapterImages = Add2PhotoAdapter()
    private var imageList = arrayListOf<String>()
    private var isImageChange = false
    lateinit var imageUri: Uri

    //    private lateinit var dialogProfileScreenBinding: DialogProfileScreenBinding
    private lateinit var profileDialog: Dialog

    private var city: String? = null
    private var keyboardWatcher: KeyboardWatcher? = null

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        dialogProfileScreenBinding = DialogProfileScreenBinding.inflate(LayoutInflater.from(this))
//        profileDialog = createDialogWithFullWidth(dialogProfileScreenBinding.root)
//        profileDialog.setCancelable(false)
//        val window = profileDialog.window
//        val wlp = window?.attributes
//        wlp?.gravity = Gravity.BOTTOM
//        window?.attributes = wlp
//        profileDialog.dismiss()

        keyboardWatcher = KeyboardWatcher(this);
        keyboardWatcher?.setListener(this);

        dialog = dialogLoading()
        dialog.dismiss()


        setSavedProfileData()


//        user.yourInterest.map {
//            selectedInterestsId.add(it.toInt())
//        }
//        user.religion.map {
//            selectedReligionId.add(it.toInt())
//        }
//        user.sexualOrientation.map {
//            selectedSexualOrientationId.add(it.toInt())
//        }
//        user.education.map {
//            selectedEducationId.add(it.toInt())
//        }
//        user.religion.map {
//            selectedReligionId.add(it.toInt())
//        }
        binding.ivBack.setOnClickListener(this)
        binding.tvPreview.setOnClickListener(this)
        binding.tvHeight.setOnClickListener(this)
        binding.tvName.setOnClickListener(this)
        binding.tvLookingFor.setOnClickListener(this)
        binding.tvName.setOnClickListener(this)
        binding.tvSexualOrientation.setOnClickListener(this)
        binding.tvEducation.setOnClickListener(this)
        binding.tvReligion.setOnClickListener(this)
        binding.tvDrinking.setOnClickListener(this)
        binding.tvSmoking.setOnClickListener(this)
        binding.tvWorkout.setOnClickListener(this)
        binding.tvInterests.setOnClickListener(this)
        binding.etCity.setOnClickListener(this)
        binding.tvGender.setOnClickListener(this)
        binding.tvDob.setOnClickListener(this)


        binding.recyclerAddPhoto.adapter = adapterImages
        setImageAdapter()
        addObserver()
    }

    fun addObserver() {
        viewModel.updateUserProfileImagesResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    dialog.dismiss()
                    response.data?.let {
                        viewModelUserLogin.saveUser(it.user)
                        imageList.clear()
                        imageList.addAll(it.user.images)
                        setImage()
                        isImageChange = false
                    }
                }

                is Resource.Error -> dialog.dismiss()
                is Resource.Loading -> {
                    dialog.show()
                    dialog.findViewById<ImageView>(R.id.imgAnimation).startRotationAnimation()
                }

                is Resource.TokenRenew -> {
                    dialog.dismiss()
                    if (response.isTokenExpire == true) {
                        openLoginPage()
                    }
                }
            }
        }
        viewModel.updateUserDetailsResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    dialog.dismiss()
                    it.data?.user?.let { user ->
                        viewModelUserLogin.saveUser(user)
                        setSavedProfileData()
                    }
//                    successFailureAlert(
//                        ProfileSetUpSuccessMsg,
//                        callback = { _: DialogInterface, i: Int ->
//                            if (i == DialogInterface.BUTTON_POSITIVE)
//                                finish()
//                        }
//                    )
                }

                is Resource.Error -> dialog.dismiss()
                is Resource.Loading -> {
                    dialog.show()
                    dialog.findViewById<ImageView>(R.id.imgAnimation).startRotationAnimation()
                }

                is Resource.TokenRenew -> {
                    dialog.dismiss()
                    if (it.isTokenExpire == true) {
                        openLoginPage()
                    }
                }
            }
        }
        viewModel.updateLifestyleResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    dialog.dismiss()
                    it.data?.user?.let { user ->
                        viewModelUserLogin.saveUser(user)
                        setSavedProfileData()
                    }
//                    successFailureAlert(
//                        ProfileSetUpSuccessMsg,
//                        callback = { _: DialogInterface, i: Int ->
//                            if (i == DialogInterface.BUTTON_POSITIVE)
//                                finish()
//                        }
//                    )
                }

                is Resource.Error -> dialog.dismiss()
                is Resource.Loading -> {
                    dialog.show()
                    dialog.findViewById<ImageView>(R.id.imgAnimation).startRotationAnimation()
                }

                is Resource.TokenRenew -> {
                    dialog.dismiss()
                    if (it.isTokenExpire == true) {
                        openLoginPage()
                    }
                }
            }
        }
        viewModelUserLogin.deleteImageResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    dialog.dismiss()
                    response.data?.user?.let {
                        viewModelUserLogin.saveUser(it)
                        imageList.clear()
                        imageList.addAll(it.images)
                    }
                    isImageChange = true
                    setImage()
                }

                is Resource.Error -> {
                    dialog.dismiss()
                }

                is Resource.Loading -> {
                    dialog.show()
                    dialog.findViewById<ImageView>(R.id.imgAnimation).startRotationAnimation()
                }

                is Resource.TokenRenew -> {
                    dialog.dismiss()
                    if (response.isTokenExpire == true) {
                        openLoginPage()
                    }
                }
            }
        }
        viewModel.realtimeImageResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    dialog.dismiss()
                    response.data?.user?.let { viewModelUserLogin.saveUser(it) }
                    showToast("Image Sent for Verification.")
                    realTimeImageSubmitBottomSheetDialog?.dismiss()
                    setSavedProfileData()
                    //setMyProfileVerificationStatus()

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
                    dialog.dismiss()
                    if (response.isTokenExpire == true) {
                        openLoginPage()
                    }
                }
            }
        }
        homeViewModel.educationResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    dialog.dismiss()
                    if (response.data?.education?.isNotEmpty() == true) {
                        val list = arrayListOf<String>()
                        val selectedList = arrayListOf<String>()
                        response.data.education.map {
                            list.add(it.name)
                        }
                        user.education.map {
                            selectedList.add(it)
                        }
                        BsChipGroupDialog(
                            title = "Education",
                            list,
                            selectedList,
                            false,
                            object : ChipGroupListener {
                                override fun onApplyBtnClicked(selectedChips: ArrayList<String>) {
                                    // visibleUpdateBtn()
                                    selectedEducationId.clear()
                                    var txt = if (selectedChips.isNotEmpty()) "" else "Add"
                                    selectedChips.map { edu ->
                                        if (list.contains(edu)) {
                                            txt += "$edu,"
                                            val e =
                                                response.data.education.filter { it.name == edu }
                                            if (e.isNotEmpty()) {
                                                selectedEducationId.add(e[0].educationId)
                                            }
                                        }
                                    }

                                    val sb = StringBuffer(txt)
                                    if (selectedChips.isNotEmpty()) {
                                        sb.deleteCharAt(sb.length - 1)
                                    }
                                    binding.tvEducation.text = sb
                                    if (binding.tvEducation.text.toString().equals("Add", true)) {
                                        binding.tvEducation.setTextAndDrawableColor(
                                            R.drawable.new_ic_next,
                                            R.color.color_949494
                                        )
                                    } else binding.tvEducation.setTextAndDrawableColor(
                                        R.drawable.new_ic_next,
                                        R.color.color_161616
                                    )
                                    user.userId?.let { uid ->
                                        viewModel.updateUserDetails(
                                            uid, EditProfileReq(
                                                name = null,//if (name.isNullOrBlank()) null else name,
                                                gender = null,// if (gender.isNullOrBlank()) null else gender,
                                                aboutMe = null,//if(about.isNullOrBlank()) null else about,
                                                //  birthDate =if(binding.tvDob.text.toString().isNullOrBlank()) null else binding.tvDob.text.toString(),
                                                height = null,//if (height == 0) null else height,
                                                birthDate = null,//if (dob.isNullOrBlank()) null else dob.toString(),
                                                interestIn = null,// if (lookingFor == 0) 0 else lookingFor,
                                                sexualOrientation = null,// if (selectedSexualOrientationId.size==0) null  else selectedSexualOrientationId ,
                                                education = if (selectedEducationId.size == 0) null else selectedEducationId,
                                                religion = null,//if (selectedReligionId.size == 0) null else selectedReligionId,
                                                lifestyle = null,
//                                    Lifestyle(
//                                        workout = if (workout.isNullOrBlank()) null else workout,
//                                        drinking = if (drinking.isNullOrBlank()) null else drinking,
//                                        smoking = if (smoking.isNullOrBlank()) null else smoking,
//                                    ),
                                                yourInterest = null,// if (selectedInterestsId.size == 0) null else selectedInterestsId,
                                                city = null,// city,
                                                location = null,//if(lat!=null && long!=null) Location(lat,long) else null
                                            )
                                        )
                                        dialog.show()
                                    }

                                }
                            }).show(supportFragmentManager, "educationBottomSheet")
                    } else {
                        response.message?.let { showToast(it) }
                    }
                }

                is Resource.Error -> {
                    dialog.dismiss()
                    response.message?.let { showToast(it) }
                }

                is Resource.Loading -> {
                    dialog.show()
                    dialog.findViewById<ImageView>(R.id.imgAnimation)
                        .startRotationAnimation()
                }

                is Resource.TokenRenew -> {
                    dialog.dismiss()
                    if (response.isTokenExpire == true) {
                        openLoginPage()
                    }
                }
            }
        }
        homeViewModel.sexualOrientationResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    dialog.dismiss()
                    if (response.data?.sexualOrientations?.isNotEmpty() == true) {
                        val list = arrayListOf<String>()
                        val selectedList = arrayListOf<String>()
                        response.data.sexualOrientations.map {
                            list.add(it.name)
                        }
                        user.sexualOrientation.map {
                            selectedList.add(it)
                        }
                        BsChipGroupDialog(
                            title = "Sexual Orientation",
                            list,
                            selectedList,
                            false,
                            object : ChipGroupListener {
                                override fun onApplyBtnClicked(selectedChips: ArrayList<String>) {
                                    // visibleUpdateBtn()
                                    selectedSexualOrientationId?.clear()
                                    var txt = if (selectedChips.isNotEmpty()) "" else "Add"
                                    selectedChips.map { so ->
                                        if (list.contains(so)) {
                                            txt += "$so,"
                                            val e =
                                                response.data.sexualOrientations.filter { it.name == so }
                                            if (e.isNotEmpty()) {
                                                selectedSexualOrientationId?.add(e[0].orientationId)
                                            }
                                        }
                                    }
                                    val sb = StringBuffer(txt)
                                    if (selectedChips.isNotEmpty()) {
                                        sb.deleteCharAt(sb.length - 1)
                                    }
                                    binding.tvSexualOrientation.text = sb
                                    if (binding.tvSexualOrientation.text.toString()
                                            .equals("Add", true)
                                    ) {
                                        binding.tvSexualOrientation.setTextAndDrawableColor(
                                            R.drawable.new_ic_next,
                                            R.color.color_949494
                                        )
                                    } else binding.tvSexualOrientation.setTextAndDrawableColor(
                                        R.drawable.new_ic_next,
                                        R.color.color_161616
                                    )
                                    user.userId?.let { uid ->
                                        viewModel.updateUserDetails(
                                            uid, EditProfileReq(
                                                name = null,//if (name.isNullOrBlank()) null else name,
                                                gender = null,// if (gender.isNullOrBlank()) null else gender,
                                                aboutMe = null,//if(about.isNullOrBlank()) null else about,
                                                //  birthDate =if(binding.tvDob.text.toString().isNullOrBlank()) null else binding.tvDob.text.toString(),
                                                height = null,//if (height == 0) null else height,
                                                birthDate = null,//if (dob.isNullOrBlank()) null else dob.toString(),
                                                interestIn = null,// if (lookingFor == 0) 0 else lookingFor,
                                                sexualOrientation = if (selectedSexualOrientationId.size == 0) null else selectedSexualOrientationId,
                                                education = null,// if (selectedEducationId.size == 0) null else selectedEducationId,
                                                religion = null,//if (selectedReligionId.size == 0) null else selectedReligionId,
                                                lifestyle = null,
//                                    Lifestyle(
//                                        workout = if (workout.isNullOrBlank()) null else workout,
//                                        drinking = if (drinking.isNullOrBlank()) null else drinking,
//                                        smoking = if (smoking.isNullOrBlank()) null else smoking,
//                                    ),
                                                yourInterest = null,// if (selectedInterestsId.size == 0) null else selectedInterestsId,
                                                city = null,// city,
                                                location = null,//if(lat!=null && long!=null) Location(lat,long) else null
                                            )
                                        )
                                        dialog.show()
                                    }
                                }

                            }).show(
                            supportFragmentManager,
                            "sexualOrientationBottomSheet"
                        )
                    } else {
                        response.message?.let { showToast(it) }
                    }
                }

                is Resource.Error -> {
                    dialog.dismiss()
                    response.message?.let { showToast(it) }
                }

                is Resource.Loading -> {
                    dialog.show()
                    dialog.findViewById<ImageView>(R.id.imgAnimation)
                        .startRotationAnimation()
                }

                is Resource.TokenRenew -> {
                    dialog.dismiss()
                    if (response.isTokenExpire == true) {
                        openLoginPage()
                    }
                }
            }
        }
        homeViewModel.interestsResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    dialog.dismiss()
                    if (response.data?.hobbies?.isNotEmpty() == true) {
                        val list = arrayListOf<String>()
                        val selectedList = arrayListOf<String>()
                        response.data.hobbies.map {
                            list.add(it.name)
                        }
                        user.yourInterest.map {
                            selectedList.add(it)
                        }
                        BsChipGroupDialog(
                            title = "Interests",
                            list,
                            selectedList,
                            true,
                            object : ChipGroupListener {
                                override fun onApplyBtnClicked(selectedChips: ArrayList<String>) {
                                    // visibleUpdateBtn()
                                    selectedInterestsId.clear()
                                    var txt =
                                        if (selectedChips.isNotEmpty()) "" else "Add"
                                    selectedChips.map { so ->
                                        if (list.contains(so)) {
                                            txt += "$so,"
                                            val e = response.data.hobbies.filter { it.name == so }
                                            if (e.isNotEmpty()) {
                                                selectedInterestsId.add(e[0].hobbiesId)
                                            }
                                        }
                                    }
                                    val sb = StringBuffer(txt)
                                    if (selectedChips.isNotEmpty()) {
                                        sb.deleteCharAt(sb.length - 1)
                                    }
                                    binding.tvInterests.text = sb
                                    if (binding.tvInterests.text.toString().equals("Add", true)) {
                                        binding.tvInterests.setTextAndDrawableColor(
                                            R.drawable.new_ic_next,
                                            R.color.color_949494
                                        )
                                    } else binding.tvInterests.setTextAndDrawableColor(
                                        R.drawable.new_ic_next,
                                        R.color.color_161616
                                    )
                                    user.userId?.let { uid ->
                                        viewModel.updateUserDetails(
                                            uid, EditProfileReq(
                                                name = null,//if (name.isNullOrBlank()) null else name,
                                                gender = null,// if (gender.isNullOrBlank()) null else gender,
                                                aboutMe = null,//if(about.isNullOrBlank()) null else about,
                                                //  birthDate =if(binding.tvDob.text.toString().isNullOrBlank()) null else binding.tvDob.text.toString(),
                                                height = null,//if (height == 0) null else height,
                                                birthDate = null,//if (dob.isNullOrBlank()) null else dob.toString(),
                                                interestIn = null,// if (lookingFor == 0) 0 else lookingFor,
                                                sexualOrientation = null,// if (selectedSexualOrientationId.size==0) null  else selectedSexualOrientationId ,
                                                education = null,// if (selectedEducationId.size == 0) null else selectedEducationId,
                                                religion = null,//if (selectedReligionId.size == 0) null else selectedReligionId,
                                                lifestyle = null,
//                                    Lifestyle(
//                                        workout = if (workout.isNullOrBlank()) null else workout,
//                                        drinking = if (drinking.isNullOrBlank()) null else drinking,
//                                        smoking = if (smoking.isNullOrBlank()) null else smoking,
//                                    ),
                                                yourInterest = if (selectedInterestsId.size == 0) null else selectedInterestsId,
                                                city = null,// city,
                                                location = null,//if(lat!=null && long!=null) Location(lat,long) else null
                                            )
                                        )
                                        dialog.show()
                                    }
                                }

                            }).show(supportFragmentManager, "interestsBottomSheet")
                    } else {
                        response.message?.let { showToast(it) }
                    }
                }

                is Resource.Error -> {
                    dialog.dismiss()
                    response.message?.let { showToast(it) }
                }

                is Resource.Loading -> {
                    dialog.show()
                    dialog.findViewById<ImageView>(R.id.imgAnimation)
                        .startRotationAnimation()
                }

                is Resource.TokenRenew -> {
                    dialog.dismiss()
                    if (response.isTokenExpire == true) {
                        openLoginPage()
                    }
                }
            }
        }
        homeViewModel.religionResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    dialog.dismiss()
                    if (response.data?.religion?.isNotEmpty() == true) {
                        val list = arrayListOf<String>()
                        val selectedList = arrayListOf<String>()
                        response.data.religion.map {
                            list.add(it.name)
                        }
                        user.religion?.map {
                            selectedList.add(it)
                        }
                        BsChipGroupDialog(
                            title = "Religion",
                            list,
                            selectedList,
                            false,
                            object : ChipGroupListener {
                                override fun onApplyBtnClicked(selectedChips: ArrayList<String>) {
                                    //visibleUpdateBtn()

                                    selectedReligionId.clear()
                                    var txt =
                                        if (selectedChips.isNotEmpty()) "" else "Add"
                                    selectedChips.map { so ->
                                        if (list.contains(so)) {
                                            txt += "$so,"
                                            val e = response.data.religion.filter { it.name == so }
                                            if (e.isNotEmpty()) {
                                                selectedReligionId.add(e[0].religionId)
                                            }
                                        }
                                    }
                                    val sb = StringBuffer(txt)
                                    if (selectedChips.isNotEmpty()) {
                                        sb.deleteCharAt(sb.length - 1)
                                    }
                                    binding.tvReligion.text = sb
                                    if (binding.tvReligion.text.toString().equals("Add", true)) {
                                        binding.tvReligion.setTextAndDrawableColor(
                                            R.drawable.new_ic_next,
                                            R.color.color_949494
                                        )
                                    } else binding.tvReligion.setTextAndDrawableColor(
                                        R.drawable.new_ic_next,
                                        R.color.color_161616
                                    )
                                    user.userId?.let { uid ->
                                        viewModel.updateUserDetails(
                                            uid, EditProfileReq(
                                                name = null,//if (name.isNullOrBlank()) null else name,
                                                gender = null,// if (gender.isNullOrBlank()) null else gender,
                                                aboutMe = null,//if(about.isNullOrBlank()) null else about,
                                                //  birthDate =if(binding.tvDob.text.toString().isNullOrBlank()) null else binding.tvDob.text.toString(),
                                                height = null,//if (height == 0) null else height,
                                                birthDate = null,//if (dob.isNullOrBlank()) null else dob.toString(),
                                                interestIn = null,// if (lookingFor == 0) 0 else lookingFor,
                                                sexualOrientation = null,// if (selectedSexualOrientationId.size==0) null  else selectedSexualOrientationId ,
                                                education = null,// if (selectedEducationId.size == 0) null else selectedEducationId,
                                                religion = if (selectedReligionId.size == 0) null else selectedReligionId,
                                                lifestyle = null,
//                                    Lifestyle(
//                                        workout = if (workout.isNullOrBlank()) null else workout,
//                                        drinking = if (drinking.isNullOrBlank()) null else drinking,
//                                        smoking = if (smoking.isNullOrBlank()) null else smoking,
//                                    ),
                                                yourInterest = null,// if (selectedInterestsId.size == 0) null else selectedInterestsId,
                                                city = null,// city,
                                                location = null,//if(lat!=null && long!=null) Location(lat,long) else null
                                            )
                                        )
                                        dialog.show()
                                    }
                                }
                            }).show(supportFragmentManager, "religionBottomSheet")

                    } else {
                        response.message?.let { showToast(it) }
                    }
                }

                is Resource.Error -> {
                    dialog.dismiss()
                    response.message?.let { showToast(it) }
                }

                is Resource.Loading -> {
                    dialog.show()
                    dialog.findViewById<ImageView>(R.id.imgAnimation)
                        .startRotationAnimation()
                }

                is Resource.TokenRenew -> {
                    dialog.dismiss()
                    if (response.isTokenExpire == true) {
                        openLoginPage()
                    }
                }
            }
        }
    }

    fun removeObserver() {
        viewModel.realtimeImageResponse.removeObservers(this)
        viewModel.updateLifestyleResponse.removeObservers(this)
        viewModelUserLogin.deleteImageResponse.removeObservers(this)
        viewModel.updateUserProfileImagesResponse.removeObservers(this)
        homeViewModel.educationResponse.removeObservers(this)
        homeViewModel.sexualOrientationResponse.removeObservers(this)
        homeViewModel.interestsResponse.removeObservers(this)
        homeViewModel.religionResponse.removeObservers(this)
    }


    private fun setImageAdapter() {
        setImage()

        adapterImages.setOnAddImageListener(object : AddImageClickListener {
            override fun setOnAddImageItemListener() {
                checkBeforeOpenGallery()
            }

            override fun setOnImageItemDeleteListener(item: String) {

                DeleteAndDismissBottomSheet(item, object : PhotoDeleteListener {
                    override fun onDeleteClicked(fileName: String) {
                        imageList.remove(item)
                        viewModelUserLogin.deleteImage(
                            user.userId!!,
                            item.replace(
                                "http://24.199.110.55:7800/uploads/",
                                ""
                            )
                        )
                    }
                }).show(supportFragmentManager, "photoDeleteBottomSheet")

            }

            override fun setOnClickListener(position: Int) {
                openGallery(imageList, position)
            }
        })
    }

    companion object {
        var dob: String? = null
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.tvPreview -> {
                viewModelUserLogin.getUser()?.let { previewDialog(it) }
            }

            binding.tvVerified -> {
                if (getProfileVerificationStatus() == RealtimeImageVerificationStatus.VERIFY_NOW) {
                    /** user realtime image not uploaded yet **/
                    realTimeImageTakePhotoBottomSheetDialog =
                        RealTimeImageTakePhotoBottomSheetDialog(object : PhotoVerificationListener {
                            override fun openCamera() {
                                requestCameraPermissionAndOpenCameraForFirstTime()
                            }

                            override fun uploadPhoto() {

                            }
                        })
                    realTimeImageTakePhotoBottomSheetDialog.show(
                        supportFragmentManager,
                        "ModalBottomSheet"
                    )
                } else if (getProfileVerificationStatus() == RealtimeImageVerificationStatus.FAILED) {
                    /** Rejected by Admin i.e failed case **/
                    realTimeImageFailedBottomSheetDialog =
                        RealTimeImageFailedBottomSheetDialog(user.realtimeImage[0],
                            object : PhotoVerificationListener {
                                override fun openCamera() {
                                    requestCameraPermissionAndOpenCameraForReject()
                                }

                                override fun uploadPhoto() {

                                }
                            })
                    realTimeImageFailedBottomSheetDialog?.show(
                        supportFragmentManager,
                        "ModalBottomSheet"
                    )
                }
            }


            binding.ivBack -> onBackPressedDispatcher.onBackPressed()

            binding.tvName -> {
                BsNameDialog("Name", name, object : NameListener {
                    override fun onApplyBtnClicked(names: String) {
                        name = names
                        user.userId?.let { uid ->
                            viewModel.updateUserDetails(
                                uid, EditProfileReq(
                                    name = if (name.isNullOrBlank()) null else name,
                                    gender = null,// if (gender.isNullOrBlank()) null else gender,
                                    aboutMe = null,//if(about.isNullOrBlank()) null else about,
                                    //  birthDate =if(binding.tvDob.text.toString().isNullOrBlank()) null else binding.tvDob.text.toString(),
                                    height = null,//if (height == 0) null else height,
                                    birthDate = null,//if (dob.isNullOrBlank()) null else dob.toString(),
                                    interestIn = null,// if (lookingFor == 0) 0 else lookingFor,
                                    sexualOrientation = null,// if (selectedSexualOrientationId.size==0) null  else selectedSexualOrientationId ,
                                    education = null,// if (selectedEducationId.size == 0) null else selectedEducationId,
                                    religion = null,//if (selectedReligionId.size == 0) null else selectedReligionId,
                                    lifestyle = null,
//                                    Lifestyle(
//                                        workout = if (workout.isNullOrBlank()) null else workout,
//                                        drinking = if (drinking.isNullOrBlank()) null else drinking,
//                                        smoking = if (smoking.isNullOrBlank()) null else smoking,
//                                    ),
                                    yourInterest = null,// if (selectedInterestsId.size == 0) null else selectedInterestsId,
                                    city = null,// city,
                                    location = null,//if(lat!=null && long!=null) Location(lat,long) else null
                                )
                            )
                            dialog.show()
                        }
//                        visibleUpdateBtn()
//
//                        binding.tvName.text = names.trim()
                    }
                }).show(supportFragmentManager, "nameBottomSheet")
            }

            binding.tvGender -> {
                val selectedList = arrayListOf<String>()
                user.gender?.let {
                    selectedList.add(
                        it
                    )
                }
                val list = arrayListOf(
                    getString(R.string.opt_man),
                    getString(R.string.opt_woman),
                    getString(R.string.opt_other)
                )
                BsChipGroupDialog(
                    title = "Gender",
                    list,
                    selectedList,
                    false,
                    object : ChipGroupListener {
                        override fun onApplyBtnClicked(selectedChips: ArrayList<String>) {
                            //  visibleUpdateBtn()
                            gender = null
                            var txt = if (selectedChips.isNotEmpty()) "" else "Add"
                            selectedChips.map {
                                if (list.contains(it)) {
                                    txt += "$it,"
                                    gender = it
                                }
                            }
                            val sb = StringBuffer(txt)
                            if (selectedChips.isNotEmpty()) {
                                sb.deleteCharAt(sb.length - 1)
                            }
                            binding.tvGender.text = sb

                            user.userId?.let { uid ->
                                viewModel.updateUserDetails(
                                    uid, EditProfileReq(
                                        name = null,//if (name.isNullOrBlank()) null else name,
                                        gender = if (gender.isNullOrBlank()) null else gender,
                                        aboutMe = null,//if(about.isNullOrBlank()) null else about,
                                        //  birthDate =if(binding.tvDob.text.toString().isNullOrBlank()) null else binding.tvDob.text.toString(),
                                        height = null,//if (height == 0) null else height,
                                        birthDate = null,//if (dob.isNullOrBlank()) null else dob.toString(),
                                        interestIn = null,// if (lookingFor == 0) 0 else lookingFor,
                                        sexualOrientation = null,// if (selectedSexualOrientationId.size==0) null  else selectedSexualOrientationId ,
                                        education = null,// if (selectedEducationId.size == 0) null else selectedEducationId,
                                        religion = null,//if (selectedReligionId.size == 0) null else selectedReligionId,
                                        lifestyle = null,
//                                    Lifestyle(
//                                        workout = if (workout.isNullOrBlank()) null else workout,
//                                        drinking = if (drinking.isNullOrBlank()) null else drinking,
//                                        smoking = if (smoking.isNullOrBlank()) null else smoking,
//                                    ),
                                        yourInterest = null,// if (selectedInterestsId.size == 0) null else selectedInterestsId,
                                        city = null,// city,
                                        location = null,//if(lat!=null && long!=null) Location(lat,long) else null
                                    )
                                )
                                dialog.show()
                            }
                        }
                    }).show(supportFragmentManager, "genderBottomSheet")
            }

            binding.tvHeight -> {
                BsHeightDialog("Height", height, object : HeightListener {
                    override fun onApplyBtnClicked(heightInCm: Int) {
                        // visibleUpdateBtn()
                        height = heightInCm
                        binding.tvHeight.text = "${heightInCm.toString()} CM"
                        user.userId?.let { uid ->
                            viewModel.updateUserDetails(
                                uid, EditProfileReq(
                                    name = null,//if (name.isNullOrBlank()) null else name,
                                    gender = null,// if (gender.isNullOrBlank()) null else gender,
                                    aboutMe = null,//if(about.isNullOrBlank()) null else about,
                                    //  birthDate =if(binding.tvDob.text.toString().isNullOrBlank()) null else binding.tvDob.text.toString(),
                                    height = if (height == 0) null else height,
                                    birthDate = null,//if (dob.isNullOrBlank()) null else dob.toString(),
                                    interestIn = null,// if (lookingFor == 0) 0 else lookingFor,
                                    sexualOrientation = null,// if (selectedSexualOrientationId.size==0) null  else selectedSexualOrientationId ,
                                    education = null,// if (selectedEducationId.size == 0) null else selectedEducationId,
                                    religion = null,//if (selectedReligionId.size == 0) null else selectedReligionId,
                                    lifestyle = null,
//                                    Lifestyle(
//                                        workout = if (workout.isNullOrBlank()) null else workout,
//                                        drinking = if (drinking.isNullOrBlank()) null else drinking,
//                                        smoking = if (smoking.isNullOrBlank()) null else smoking,
//                                    ),
                                    yourInterest = null,// if (selectedInterestsId.size == 0) null else selectedInterestsId,
                                    city = null,// city,
                                    location = null,//if(lat!=null && long!=null) Location(lat,long) else null
                                )
                            )
                            dialog.show()
                        }
                    }

                }).show(supportFragmentManager, "heightBottomSheet")
            }

            binding.tvLookingFor -> {
                val selectedList = arrayListOf<String>()
                user.interestIn?.let {
                    selectedList.add(
                        when (it) {
                            1 -> getString(R.string.chip_men)
                            2 -> getString(R.string.chip_women)
                            3 -> getString(R.string.chip_everyone)
                            else -> ""
                        }
                    )
                }
                val list = arrayListOf(
                    getString(R.string.chip_men),
                    getString(R.string.chip_women),
                    getString(R.string.chip_everyone)
                )
                BsChipGroupDialog(
                    title = "Looking For",
                    list,
                    selectedList,
                    false,
                    object : ChipGroupListener {
                        override fun onApplyBtnClicked(selectedChips: ArrayList<String>) {
                            // visibleUpdateBtn()
                            lookingFor = 0
                            var txt = if (selectedChips.isNotEmpty()) "" else "Add"
                            selectedChips.map {
                                if (list.contains(it)) {
                                    txt += "$it,"
                                    lookingFor = when (it) {
                                        getString(R.string.chip_men) -> 1
                                        getString(R.string.chip_women) -> 2
                                        getString(R.string.chip_everyone) -> 3
                                        else -> 0
                                    }
                                }
                            }

                            val sb = StringBuffer(txt)
                            if (selectedChips.isNotEmpty()) {
                                sb.deleteCharAt(sb.length - 1)
                            }
                            binding.tvLookingFor.text = sb
                            if (binding.tvLookingFor.text.toString().equals("Add", true)) {
                                binding.tvLookingFor.setTextAndDrawableColor(
                                    R.drawable.new_ic_next,
                                    R.color.color_949494
                                )
                            } else binding.tvLookingFor.setTextAndDrawableColor(
                                R.drawable.new_ic_next,
                                R.color.color_161616
                            )
                            user.userId?.let { uid ->
                                viewModel.updateUserDetails(
                                    uid, EditProfileReq(
                                        name = null,//if (name.isNullOrBlank()) null else name,
                                        gender = null,// if (gender.isNullOrBlank()) null else gender,
                                        aboutMe = null,//if(about.isNullOrBlank()) null else about,
                                        //  birthDate =if(binding.tvDob.text.toString().isNullOrBlank()) null else binding.tvDob.text.toString(),
                                        height = null,//if (height == 0) null else height,
                                        birthDate = null,//if (dob.isNullOrBlank()) null else dob.toString(),
                                        interestIn = if (lookingFor == 0) 0 else lookingFor,
                                        sexualOrientation = null,// if (selectedSexualOrientationId.size==0) null  else selectedSexualOrientationId ,
                                        education = null,// if (selectedEducationId.size == 0) null else selectedEducationId,
                                        religion = null,//if (selectedReligionId.size == 0) null else selectedReligionId,
                                        lifestyle = null,
//                                    Lifestyle(
//                                        workout = if (workout.isNullOrBlank()) null else workout,
//                                        drinking = if (drinking.isNullOrBlank()) null else drinking,
//                                        smoking = if (smoking.isNullOrBlank()) null else smoking,
//                                    ),
                                        yourInterest = null,// if (selectedInterestsId.size == 0) null else selectedInterestsId,
                                        city = null,// city,
                                        location = null,//if(lat!=null && long!=null) Location(lat,long) else null
                                    )
                                )
                                dialog.show()
                            }
                        }
                    }).show(supportFragmentManager, "lookingForBottomSheet")
            }

            binding.tvSexualOrientation -> {
                homeViewModel.getSexualOrientations(Constants.BASE_URL + Constants.UserSexualOrientations)
                dialog.show()
            }

            binding.tvEducation -> {
                homeViewModel.getEducations()
                dialog.show()
            }

            binding.tvReligion -> {
                homeViewModel.getReligions()
                dialog.show()
            }

            binding.tvWorkout -> {
                val selectedList = arrayListOf<String>()
                user.lifestyle?.workout?.let {
                    selectedList.add(
                        it
                    )
                }

                val list = arrayListOf(
                    getString(R.string.chip_everyday),
                    getString(R.string.chip_often),
                    getString(R.string.chip_sometimes)
                )
                BsChipGroupDialog(
                    title = "Workout",
                    list,
                    selectedList,
                    false,
                    object : ChipGroupListener {
                        override fun onApplyBtnClicked(selectedChips: ArrayList<String>) {
                            // visibleUpdateBtn()
                            workout = null
                            var txt = if (selectedChips.isNotEmpty()) "" else "Add"
                            selectedChips.map {
                                if (list.contains(it)) {
                                    txt += "$it,"
                                    workout = it
                                }
                            }
                            val sb = StringBuffer(txt)
                            if (selectedChips.isNotEmpty()) {
                                sb.deleteCharAt(sb.length - 1)
                            }
                            binding.tvWorkout.text = sb
                            if (binding.tvWorkout.text.toString().equals("Add", true)) {
                                binding.tvWorkout.setTextAndDrawableColor(
                                    R.drawable.new_ic_next,
                                    R.color.color_949494
                                )
                            } else binding.tvWorkout.setTextAndDrawableColor(
                                R.drawable.new_ic_next,
                                R.color.color_161616
                            )
                            user.userId?.let { uid ->
                                viewModel.updateLifestyle(
                                    uid,
                                    lifestyleReq = LifestyleReq(Lifestyle(
                                        workout = if (workout.isNullOrBlank()) null else workout,
                                        drinking = null,// if (drinking.isNullOrBlank()) null else drinking,
                                        smoking = null,//if (smoking.isNullOrBlank()) null else smoking,
                                    ))

                                )
                                dialog.show()
                            }
                        }
                    }).show(supportFragmentManager, "WorkoutBottomSheet")
            }

            binding.tvSmoking -> {
                val selectedList = arrayListOf<String>()
                user.lifestyle?.smoking?.let {
                    selectedList.add(
                        it
                    )
                }
                val list = arrayListOf(
                    getString(R.string.chip_socially),
                    getString(R.string.chip_never),
                    getString(R.string.chip_regularly)
                )
                BsChipGroupDialog(
                    title = "Smoking",
                    list,
                    selectedList,
                    false,
                    object : ChipGroupListener {
                        override fun onApplyBtnClicked(selectedChips: ArrayList<String>) {
                            // visibleUpdateBtn()
                            smoking = null
                            var txt =
                                if (selectedChips.isNotEmpty()) "" else "Add"
                            selectedChips.map {
                                if (list.contains(it)) {
                                    txt += "$it,"
                                    smoking = it
                                }
                            }
                            val sb = StringBuffer(txt)
                            if (selectedChips.isNotEmpty()) {
                                sb.deleteCharAt(sb.length - 1)
                            }
                            binding.tvSmoking.text = sb
                            if (binding.tvSmoking.text.toString().equals("Add", true)) {
                                binding.tvSmoking.setTextAndDrawableColor(
                                    R.drawable.new_ic_next,
                                    R.color.color_949494
                                )
                            } else binding.tvSmoking.setTextAndDrawableColor(
                                R.drawable.new_ic_next,
                                R.color.color_161616
                            )
                            user.userId?.let { uid ->
                                viewModel.updateLifestyle(
                                    uid,
                                    lifestyleReq = LifestyleReq(Lifestyle(
                                        workout = null,//if (workout.isNullOrBlank()) null else workout,
                                        drinking = null,// if (drinking.isNullOrBlank()) null else drinking,
                                        smoking = if (smoking.isNullOrBlank()) null else smoking,
                                    ))

                                )
                                dialog.show()
                            }
                        }
                    }).show(supportFragmentManager, "smokingBottomSheet")
            }

            binding.tvDrinking -> {
                val selectedList = arrayListOf<String>()

                user.lifestyle?.drinking?.let {
                    selectedList.add(
                        it
                    )
                }
                val list = arrayListOf(
                    getString(R.string.chip_socially),
                    getString(R.string.chip_never)
                )
                BsChipGroupDialog(
                    title = "Drinking",
                    list,
                    selectedList,
                    false,
                    object : ChipGroupListener {
                        override fun onApplyBtnClicked(selectedChips: ArrayList<String>) {
                            // visibleUpdateBtn()
                            drinking = null
                            var txt =
                                if (selectedChips.isNotEmpty()) "" else "Add"
                            selectedChips.map {
                                if (list.contains(it)) {
                                    txt += "$it,"
                                    drinking = it
                                }
                            }
                            val sb = StringBuffer(txt)
                            if (selectedChips.isNotEmpty()) {
                                sb.deleteCharAt(sb.length - 1)
                            }
                            binding.tvDrinking.text = sb
                            if (binding.tvDrinking.text.toString().equals("Add", true)) {
                                binding.tvDrinking.setTextAndDrawableColor(
                                    R.drawable.new_ic_next,
                                    R.color.color_949494
                                )
                            } else binding.tvDrinking.setTextAndDrawableColor(
                                R.drawable.new_ic_next,
                                R.color.color_161616
                            )

                            user.userId?.let { uid ->
                                viewModel.updateLifestyle(
                                    uid,
                                    lifestyleReq = LifestyleReq(
                                        Lifestyle(
                                            workout = null,//if (workout.isNullOrBlank()) null else workout,
                                            drinking = if (drinking.isNullOrBlank()) null else drinking,
                                            smoking = null,//if (smoking.isNullOrBlank()) null else smoking,
                                        )
                                    )

                                )
                                dialog.show()
                            }
                        }
                    }).show(supportFragmentManager, "drinkingBottomSheet")
            }

            binding.tvInterests -> {
                homeViewModel.getInterests()
                dialog.show()
            }

            binding.etCity -> editCityLauncher.launch(
                Intent(
                    this,
                    LocationSettingActivity::class.java
                )
            )

            binding.tvDob ->
                DatePicker(
                    ActivityCreateProfileBinding.inflate(LayoutInflater.from(this)),
                    binding
                ).show(supportFragmentManager, "calendar")

//            binding.btnUpdate -> {
//                user.userId?.let { uid ->
//                    viewModel.updateUserDetails(
//                        uid, EditProfileReq(
//                            name = if (name.isNullOrBlank()) null else name,
//                            gender = if (gender.isNullOrBlank()) null else gender,
//                            aboutMe = if(about.isNullOrBlank()) null else about,
//                            //  birthDate =if(binding.tvDob.text.toString().isNullOrBlank()) null else binding.tvDob.text.toString(),
//                            height = if (height == 0) null else height,
//                            birthDate = if (dob.isNullOrBlank()) null else dob.toString(),
//                            interestIn = if (lookingFor == 0) 0 else lookingFor,
//                            sexualOrientation = if (selectedSexualOrientationId.size==0) null  else selectedSexualOrientationId ,
//                            education = if (selectedEducationId.size == 0) null else selectedEducationId,
//                            religion = if (selectedReligionId.size == 0) null else selectedReligionId,
//                            lifestyle = Lifestyle(
//                                workout = if (workout.isNullOrBlank()) null else workout,
//                                drinking = if (drinking.isNullOrBlank()) null else drinking,
//                                smoking = if (smoking.isNullOrBlank()) null else smoking,
//                            ),
//                            yourInterest = if (selectedInterestsId.size == 0) null else selectedInterestsId,
//                            city = city,
//                            location =if(lat!=null && long!=null) Location(lat,long) else null
//                        )
//                    )
//                    dialog.show()
//                }
//            }
        }
    }

    private val editCityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            city = if (it.resultCode == RESULT_OK) it.data?.getStringExtra("city") else null
            lat = if (it.resultCode == RESULT_OK && it.data?.getDoubleExtra(
                    "lat",
                    0.00
                ) != 0.00
            ) it.data?.getDoubleExtra("lat", 0.00) else null
            long = if (it.resultCode == RESULT_OK && it.data?.getDoubleExtra(
                    "long",
                    0.00
                ) != 0.00
            ) it.data?.getDoubleExtra("long", 0.00) else null
            if (!city.isNullOrBlank()) {
                binding.etCity.text = city
            }
            user.userId?.let { uid ->
                viewModel.updateUserDetails(
                    uid, EditProfileReq(
                        name = null,//if (name.isNullOrBlank()) null else name,
                        gender = null,// if (gender.isNullOrBlank()) null else gender,
                        aboutMe = null,//if(about.isNullOrBlank()) null else about,
                        //  birthDate =if(binding.tvDob.text.toString().isNullOrBlank()) null else binding.tvDob.text.toString(),
                        height = null,//if (height == 0) null else height,
                        birthDate = null,//if (dob.isNullOrBlank()) null else dob.toString(),
                        interestIn = null,//if (lookingFor == 0) 0 else lookingFor,
                        sexualOrientation = null,// if (selectedSexualOrientationId.size==0) null  else selectedSexualOrientationId ,
                        education = null,// if (selectedEducationId.size == 0) null else selectedEducationId,
                        religion = null,//if (selectedReligionId.size == 0) null else selectedReligionId,
                        lifestyle = null,//
//                        // Lifestyle(
//                            workout =null,// if (workout.isNullOrBlank()) null else workout,
//                            drinking =null,// if (drinking.isNullOrBlank()) null else drinking,
//                            smoking =null,//if (smoking.isNullOrBlank()) null else smoking,
//                        ),
                        yourInterest = null,// if (selectedInterestsId.size == 0) null else selectedInterestsId,
                        city = city,
                        location = if (lat != null && long != null) Location(lat, long) else null
                    )
                )
                dialog.show()
            }
            // binding.btnUpdate.visible()
            setSavedProfileData()
        }

    private fun checkBeforeOpenGallery() {
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
            mInitGalleryOption()
        }
    }

    private val readStoragePermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                mInitGalleryOption()
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
            checkBeforeOpenGallery()
        }

    private fun mInitGalleryOption() {
        if (Build.VERSION_CODES.TIRAMISU <= Build.VERSION.SDK_INT)
            pickImageFromGalleryApiT.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        else {
            val mIntent = Intent(this, PickImageActivity::class.java)
            mIntent.putExtra(PickImageActivity.KEY_LIMIT_MAX_IMAGE, 6 - user.images.size)
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
                uploadImage(uri)
            }
            isImageChange = true
            setImage()
        }

    private val pickImageFromGalleryApiT =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            val file = File(it?.path.toString())
            val path = it?.let { it1 -> getPath(it1) }
            Log.d(
                "Image",
                file.absolutePath +
                        "\n$it" +
                        "\n$path"
            )
            if (path != null) {
                uploadImage(path)
                imageList.add(path)
                isImageChange = true
                setImage()
            }
        }

    private fun uploadImage(path: String) {
        val file = File(path)
        val requestFile: RequestBody =
            file.asRequestBody("image/${file.extension}".toMediaTypeOrNull())

        viewModel.userUserProfileImages(
            user.userId.toString(),
            MultipartBody.Part.createFormData("image", file.name, requestFile)
        )
    }

    private fun setImage() {
        adapterImages.setImages(imageList)
    }

    private fun openGallery(images: java.util.ArrayList<String>, position: Int) {
        Intent(this, GalleryActivity::class.java).also {
            it.putExtra("images", images)
            it.putExtra("position", position)
            startActivity(it)
        }
    }

    fun requestCameraPermissionAndOpenCameraForFirstTime() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                1001
            )
        } else {
            openMobileCamera(1001)
        }
    }
    fun requestCameraPermissionAndOpenCameraForReject() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                1002
            )
        } else {
            openMobileCamera(1002)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openMobileCamera(1001)
            } else {
                showToast("Camera permission denied.")
            }
        }
        if (requestCode == 1002) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openMobileCamera(1002)
            } else {
                showToast("Camera permission denied.")
            }
        }
    }

    private fun openMobileCamera(requestCode:Int) {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "upload")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
        // videoUri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //  val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        val file = File(imageUri.toString())
        if (file.exists()) file.delete()
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        //  takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri)
        val chooserIntent = Intent(Intent.ACTION_CHOOSER)
        val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
        val intentArray = arrayOf(takePictureIntent)
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Capture Image")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
        startActivityForResult(chooserIntent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001 && resultCode == RESULT_OK) {
            val finalFile = File(getRealPathFromURI(imageUri))
            if(realTimeImageTakePhotoBottomSheetDialog.isVisible){
                realTimeImageTakePhotoBottomSheetDialog.dismiss()
            }
            realTimeImageSubmitBottomSheetDialog =
                RealTimeImageSubmitBottomSheetDialog(imageUri,
                    object : PhotoVerificationListener {
                        override fun openCamera() {
                            openMobileCamera(1001)
                        }

                        override fun uploadPhoto() {
                            uploadRealtimePhoto(finalFile)
                        }

                    })
            realTimeImageSubmitBottomSheetDialog?.show(supportFragmentManager, "SubmitDialog")
        }
        if (requestCode == 1002 && resultCode == RESULT_OK) {
            val finalFile = File(getRealPathFromURI(imageUri))
            if(realTimeImageFailedBottomSheetDialog?.isVisible==true){
                realTimeImageFailedBottomSheetDialog?.dismiss()
            }

            realTimeImageSubmitBottomSheetDialog =
                RealTimeImageSubmitBottomSheetDialog(imageUri,
                    object : PhotoVerificationListener {
                        override fun openCamera() {
                            openMobileCamera(1002)
                        }

                        override fun uploadPhoto() {
                            uploadRealtimePhoto(finalFile)
                        }

                    })
            realTimeImageSubmitBottomSheetDialog?.show(supportFragmentManager, "SubmitDialog")
        }
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

    fun uploadRealtimePhoto(file: File) {
        val requestFile: RequestBody =
            file.asRequestBody("image/${file.extension}".toMediaTypeOrNull())
        viewModel.uploadRealtimeImage(
            viewModelUserLogin.getUser()?.userId.toString(),
            MultipartBody.Part.createFormData("realtimeImage", file.name, requestFile)
        )
        dialog.show()
    }

    private fun setMyProfileVerificationStatus() {
        if (user.isRealTimeImageVerified && user.realtimeImage.isNotEmpty()) {
            //user verified
            binding.tvVerified.text = "Verified"
            var drawable = getDrawable(R.drawable.new_ic_next)
            drawable = DrawableCompat.wrap(drawable!!)
            DrawableCompat.setTint(drawable, getColor(R.color.color_01A4FF))
            binding.tvVerified.compoundDrawablePadding = 0
            binding.tvVerified.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            binding.tvVerified.textColor(R.color.color_01A4FF)
        } else if (user.imageProcessing == false && user.realtimeImage.isEmpty()) {
            // user realtime image not uploaded yet
            binding.tvVerified.text = "Verify Now"
            binding.tvVerified.setOnClickListener(this)
            var drawable = getDrawable(R.drawable.new_ic_next)
            drawable = DrawableCompat.wrap(drawable!!)
            DrawableCompat.setTint(drawable, getColor(R.color.color_949494))
            binding.tvVerified.compoundDrawablePadding = 10
            binding.tvVerified.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
            binding.tvVerified.textColor(R.color.color_949494)
        } else if (user.imageProcessing == true && user.realtimeImage.isNotEmpty()) {
            //user uploaded image and waiting for approval
            binding.tvVerified.text = "Processing..."
            binding.tvVerified.textColor(R.color.color_FFC629)
            var drawable = getDrawable(R.drawable.new_ic_next)
            drawable = DrawableCompat.wrap(drawable!!)
            DrawableCompat.setTint(drawable, getColor(R.color.color_FFC629))
            binding.tvVerified.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        } else {
            binding.tvVerified.text = "failed"
            binding.tvVerified.setOnClickListener(this)
            var drawable = getDrawable(R.drawable.new_ic_next)
            drawable = DrawableCompat.wrap(drawable!!)
            DrawableCompat.setTint(drawable, getColor(R.color.color_ED1C1D))
            binding.tvVerified.compoundDrawablePadding = 10
            binding.tvVerified.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
            binding.tvVerified.textColor(R.color.color_ED1C1D)
        }
    }

    private fun getProfileVerificationStatus(): RealtimeImageVerificationStatus {
        return if (user.isRealTimeImageVerified && user.realtimeImage.isNotEmpty()) {
            //user verified
            RealtimeImageVerificationStatus.VERIFIED
        } else if (user.imageProcessing == false && user.realtimeImage.isEmpty()) {
            // user realtime image not uploaded yet
            RealtimeImageVerificationStatus.VERIFY_NOW
        } else if (user.imageProcessing == true && user.realtimeImage.isNotEmpty()) {
            //user uploaded image and waiting for approval
            RealtimeImageVerificationStatus.PROCESSING
        } else {
            RealtimeImageVerificationStatus.FAILED
        }
    }

    override fun onResume() {
        super.onResume()
        setSavedProfileData()
    }

    override fun onDestroy() {
        keyboardWatcher!!.destroy()
        super.onDestroy()
        removeObserver()
    }

//    fun visibleUpdateBtn() {
//        binding.btnUpdate.visible()
//        binding.btnUpdate.setOnClickListener(this)
//    }

    fun disableUpdateBtn() {
//        binding.btnUpdate.gone()
//        binding.btnUpdate.setOnClickListener(null)
    }

    private fun setSavedProfileData() {
        user = viewModelUserLogin.getUser()!!
        setMyProfileVerificationStatus()
        name = user.userName
        height = user.height
        gender = user.gender
        drinking = user.lifestyle?.drinking
        smoking = user.lifestyle?.smoking
        workout = user.lifestyle?.workout
        imageList.clear()
        user.images.forEach { imageList.add(it) }
        setImage()
        uName = "${user.userName}"
        binding.tvName.text = "${user.userName}"
        val date = SimpleDateFormat("yyyy-mm-dd").parse(user.birthDate.toString())
            ?.let { SimpleDateFormat("dd/MMM/yyyy").format(it) }
        user.height.let {
            binding.tvHeight.text = "$it CM"
            binding.tvHeight.setTextColor(getColor(R.color.color_161616))
        }
        /** setDob **/
        binding.tvDob.text = "$date"
        /** set About **/
        binding.etAboutMe.setText(user.aboutMe)
        binding.etAboutMe.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(binding.etAboutMe.text.toString().length<=300) {
                    about = binding.etAboutMe.text.toString()
                    isAboutChanged = true
                }else{
                    isAboutChanged = false
                    showToast("Only 300 Characters are allowed.")
                }
//                if(binding.etAboutMe.text.isNullOrBlank()){
//                    disableUpdateBtn()
//                }else{
//                    visibleUpdateBtn()
//                }
            }

            override fun afterTextChanged(s: Editable?) {
                // about=binding.etAboutMe.text.toString()
            }
        })
        /** Set Gender **/
        binding.tvGender.text = user.gender
        /** set City **/
        if (!user.city.isNullOrBlank()) binding.etCity.text = user.city


        /** set lifeStyle **/
        user.lifestyle?.drinking?.let {
            binding.tvDrinking.text = it
        }
        user.lifestyle?.smoking?.let {
            binding.tvSmoking.text = it
        }
        user.lifestyle?.workout?.let {
            binding.tvWorkout.text = it
        }
        user.education.let { educations ->
            var education = if (educations.isEmpty()) "Add " else ""
            educations.map {
                education += "$it,"
            }
            val sb = StringBuffer(education)
            if (education.isNotEmpty()) {
                sb.deleteCharAt(sb.length - 1)
            }
            binding.tvEducation.text = sb
        }
        user.sexualOrientation.let { sexualOrientations ->
            var sexualOrientation = if (sexualOrientations.isEmpty()) "Add " else ""
            sexualOrientations.map {
                sexualOrientation += "$it,"
            }
            val sb = StringBuffer(sexualOrientation)
            if (sexualOrientation.isNotEmpty()) {
                sb.deleteCharAt(sb.length - 1)
            }
            binding.tvSexualOrientation.text = sb
        }
        user.interestIn.let {
            binding.tvLookingFor.text = when (it) {
                1 -> getString(R.string.chip_men)
                2 -> getString(R.string.chip_women)
                3 -> getString(R.string.chip_everyone)
                else -> "Add"
            }
        }
        user.religion.let { religions ->
            var religion = if (religions.isEmpty()) "Add " else ""
            religions.map {
                religion += "$it,"
            }
            val sb = StringBuffer(religion)
            if (religion.isNotEmpty()) {
                sb.deleteCharAt(sb.length - 1)
            }
            binding.tvReligion.text = sb
        }
        user.yourInterest.let { interests ->
            var interest = if (interests.isEmpty()) "Add " else ""
            interests.map {
                interest += "$it,"
            }
            val sb = StringBuffer(interest)
            if (interest.isNotEmpty()) {
                sb.deleteCharAt(sb.length - 1)
            }
            binding.tvInterests.text = sb
        }

        if (binding.tvHeight.text.toString().equals("0 CM", true)) {
            binding.tvHeight.setTextAndDrawableColor(R.drawable.new_ic_next, R.color.color_949494)
        } else binding.tvHeight.setTextAndDrawableColor(
            R.drawable.new_ic_next,
            R.color.color_161616
        )

        if (binding.tvReligion.text.toString().equals("Add", true)) {
            binding.tvReligion.setTextAndDrawableColor(R.drawable.new_ic_next, R.color.color_949494)
        } else binding.tvReligion.setTextAndDrawableColor(
            R.drawable.new_ic_next,
            R.color.color_161616
        )

        if (binding.tvLookingFor.text.toString().equals("Add", true)) {
            binding.tvLookingFor.setTextAndDrawableColor(
                R.drawable.new_ic_next,
                R.color.color_949494
            )
        } else binding.tvLookingFor.setTextAndDrawableColor(
            R.drawable.new_ic_next,
            R.color.color_161616
        )

        if (binding.tvEducation.text.toString().equals("Add", true)) {
            binding.tvEducation.setTextAndDrawableColor(
                R.drawable.new_ic_next,
                R.color.color_949494
            )
        } else binding.tvEducation.setTextAndDrawableColor(
            R.drawable.new_ic_next,
            R.color.color_161616
        )

        if (binding.tvWorkout.text.toString().equals("Add", true)) {
            binding.tvWorkout.setTextAndDrawableColor(R.drawable.new_ic_next, R.color.color_949494)
        } else binding.tvWorkout.setTextAndDrawableColor(
            R.drawable.new_ic_next,
            R.color.color_161616
        )

        if (binding.tvSmoking.text.toString().equals("Add", true)) {
            binding.tvSmoking.setTextAndDrawableColor(R.drawable.new_ic_next, R.color.color_949494)
        } else binding.tvSmoking.setTextAndDrawableColor(
            R.drawable.new_ic_next,
            R.color.color_161616
        )

        if (binding.tvDrinking.text.toString().equals("Add", true)) {
            binding.tvDrinking.setTextAndDrawableColor(R.drawable.new_ic_next, R.color.color_949494)
        } else binding.tvDrinking.setTextAndDrawableColor(
            R.drawable.new_ic_next,
            R.color.color_161616
        )

        if (binding.tvInterests.text.toString().equals("Add", true)) {
            binding.tvInterests.setTextAndDrawableColor(
                R.drawable.new_ic_next,
                R.color.color_949494
            )
        } else binding.tvInterests.setTextAndDrawableColor(
            R.drawable.new_ic_next,
            R.color.color_161616
        )

        if (binding.tvSexualOrientation.text.toString().equals("Add", true)) {
            binding.tvSexualOrientation.setTextAndDrawableColor(
                R.drawable.new_ic_next,
                R.color.color_949494
            )
        } else binding.tvSexualOrientation.setTextAndDrawableColor(
            R.drawable.new_ic_next,
            R.color.color_161616
        )


    }

    @SuppressLint("ResourceAsColor")
    private fun TextView.setTextAndDrawableColor(@DrawableRes res: Int, @ColorRes colorId: Int) {
        var drawable = getDrawable(res)
        drawable = DrawableCompat.wrap(drawable!!)
        DrawableCompat.setTint(drawable, getColor(colorId))
        this.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
        this.textColor(colorId)
    }

    override fun onKeyboardShown(keyboardSize: Int) {
    }

    override fun onKeyboardClosed() {
        if (isAboutChanged) {
            user.userId?.let { uid ->
                viewModel.updateUserDetails(
                    uid, EditProfileReq(
                        aboutMe = about,
                        height = null,//if (height == 0) null else height,
                    )
                )
                dialog.show()
            }
        }
    }


}