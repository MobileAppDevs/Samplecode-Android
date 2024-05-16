package com.dream.friend.common

import android.app.Activity
import android.app.Dialog
import android.content.ContentUris
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.os.BuildCompat
import com.bumptech.glide.Glide
import com.dream.friend.R
import com.dream.friend.common.Constants.ExitMsg
import com.dream.friend.common.Utils.dialogLoading
import com.dream.friend.data.model.user.User
import com.dream.friend.databinding.DialogAlertBinding
import com.dream.friend.databinding.DialogProgressBinding
import com.dream.friend.databinding.PreviewDialogLayoutBinding
import com.dream.friend.ui.login.LoginActivity
import com.dream.friend.util.Extensions.gone
import com.dream.friend.util.Extensions.visible
import com.dream.myfirestorecharlibrary.Constants
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.imageview.ShapeableImageView
import com.yuyakaido.android.cardstackview.Duration
import java.lang.Long
import java.net.URISyntaxException
import java.text.SimpleDateFormat

object Utils {

    fun Context.showToast(message: String,duration:Int=Toast.LENGTH_SHORT) =
        Toast.makeText(this, message, duration).show()

    @androidx.annotation.OptIn(BuildCompat.PrereleaseSdkCheck::class)
    fun ComponentActivity.onBackPress(isEnable: Boolean = true) {
        if (BuildCompat.isAtLeastT()) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                exitAlert()
            }
        } else {
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(isEnable) {
                override fun handleOnBackPressed() {
                    exitAlert()
                }
            })
        }
    }

    fun Activity.dialogLoading(): Dialog =
        createDialogWithFullWidthAndHeight(DialogProgressBinding.inflate(LayoutInflater.from(this)).root).apply {
            setCancelable(false)
            create()
            show()
        }

    fun Activity.previewDialog(user:User):Dialog=previewDialogWithFullWidthAndHeight(PreviewDialogLayoutBinding.inflate(LayoutInflater.from(this)).root,user).apply {
        setCancelable(true)
        create()
        show()
    }

    fun Activity.logoutOrDeleteAccountAlert(
        callback: DialogInterface.OnClickListener? = null,
        icon: Drawable? = null,
        title: String,
        message: String,
        imageUrl: String? = null
    ) {
        val view = DialogAlertBinding.inflate(LayoutInflater.from(this))
        val dialog = createDialogWithFullWidth(view.root)
        dialog.setCancelable(false)

        view.tvTitle.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)
        view.tvTitle.text = title
        view.tvMsg.text = message

        if (imageUrl != null) {
            Glide.with(this)
                .load(imageUrl)
                .into(view.ivProfile)
            view.ivProfile.show()
        }

        view.tvOk.setOnClickListener {
            view.tvNo.background = null
            view.tvNo.setTextColor(resources.getColor(R.color.black, null))
            view.tvOk.setBackground(R.drawable.drawable_cr12cff655b_topright_bottomleft)
            view.tvOk.setTextColor(resources.getColor(R.color.white, null))
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    dialog.dismiss()
                    callback?.onClick(dialog, DialogInterface.BUTTON_POSITIVE)
                }, 200
            )
        }

        view.tvNo.setOnClickListener {
            dialog.dismiss()
            callback?.onClick(dialog, DialogInterface.BUTTON_NEGATIVE)
        }
    }

    fun Activity.exitAlert() {
        val view = DialogAlertBinding.inflate(LayoutInflater.from(this))
        val dialog = createDialogWithFullWidth(view.root)
        dialog.setCancelable(false)

        view.tvTitle.text = "Alert"
        view.tvMsg.text = ExitMsg

        view.tvOk.setOnClickListener {
            view.tvNo.background = null
            view.tvNo.setTextColor(resources.getColor(R.color.black, null))
            view.tvOk.setBackground(R.drawable.drawable_cr12cff655b_topright_bottomleft)
            view.tvOk.setTextColor(resources.getColor(R.color.white, null))
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    dialog.dismiss()
                    finish()
                }, 200
            )
        }

        view.tvNo.setOnClickListener {
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    dialog.dismiss()
                }, 200
            )
        }

        view.tvNo.show()
    }

   /* fun Activity.successFailureAlert(message: String, title: String = "", callback: DialogInterface.OnClickListener? = null, okText: String = "Ok", isCancel: Boolean = false) {
        val view = DialogAlertBinding.inflate(LayoutInflater.from(this))
        val dialog = createDialogWithFullWidth(view.root)
        dialog.setCancelable(false)

        view.tvTitle.text = title
        view.tvMsg.text = message

        view.tvOk.text = okText

        view.tvOk.setOnClickListener {
            if (isCancel) {
                view.tvNo.background = null
                view.tvNo.setTextColor(resources.getColor(R.color.black, null))
                view.tvOk.setBackground(R.drawable.drawable_cr12cff655b_topright_bottomleft)
                view.tvOk.setTextColor(resources.getColor(R.color.white, null))
            }

            Handler(Looper.getMainLooper()).postDelayed(
                {
                    dialog.dismiss()
                    callback?.onClick(dialog, DialogInterface.BUTTON_POSITIVE)
                }, 200
            )
        }

        if (isCancel) view.tvNo.show()
        else view.tvNo.hide()
        view.tvNo.setOnClickListener {
            dialog.dismiss()
            callback?.onClick(dialog, DialogInterface.BUTTON_NEGATIVE)
        }

        if (title.isBlank())
            view.tvTitle.hide()
    }
*/
    fun Activity.permissionAlert(
        message: String,
        title: String = "",
        cancelable: Boolean = false,
        successBtnText: String = "ok",
        cancelBtnText: String = "cancel",
        callback: DialogInterface.OnClickListener? = null,
    ) {
        val view = DialogAlertBinding.inflate(LayoutInflater.from(this))
        val dialog = createDialogWithFullWidth(view.root)
        dialog.setCancelable(cancelable)

        view.tvTitle.text = title
        view.tvMsg.text = message
        view.tvOk.text = successBtnText
        view.tvNo.text = cancelBtnText
        view.tvOk.setOnClickListener {
            dialog.dismiss()
            callback?.onClick(dialog, DialogInterface.BUTTON_POSITIVE)
        }
        view.tvNo.setOnClickListener {
            dialog.dismiss()
            callback?.onClick(dialog, DialogInterface.BUTTON_NEGATIVE)
        }

        view.tvNo.show()

        if (title.isBlank())
            view.tvTitle.hide()
    }

    fun Context.createDialogWithFullWidth(view: View): Dialog =
        Dialog(this).apply {
            setContentView(view)
            create()
            show()
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT)
            window?.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT))
        }

    fun Context.createDialogWithFullWidthAndHeight(view: View): Dialog =
        Dialog(this).apply {
            setContentView(view)
            create()
            show()
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT)
            window?.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT))
        }

    fun Context.previewDialogWithFullWidthAndHeight(view: View, user: User): Dialog =
        Dialog(this).apply {
            setContentView(view)
            if (user.images.size > 0)
                Glide.with(context)
                    .load(user.images[0])
                    .error(R.drawable.test_photo)
                    .placeholder(R.drawable.drawable_cr12topcffffff)
                    .into(view.findViewById<ShapeableImageView>(R.id.ivUserProfileImage))

            if (user.images.size > 1) {
                Glide.with(context)
                    .load(user.images[1])
                    .error(R.drawable.test_photo)
                    .placeholder(R.drawable.drawable_cr12topcffffff)
                    .into(view.findViewById<ShapeableImageView>(R.id.ivImage2))
                view.findViewById<ShapeableImageView>(R.id.ivImage2).visible()
            }else view.findViewById<ShapeableImageView>(R.id.ivImage2).gone()

            if (user.images.size > 2) {
                Glide.with(context)
                    .load(user.images[2])
                    .error(R.drawable.test_photo)
                    .placeholder(R.drawable.drawable_cr12topcffffff)
                    .into(view.findViewById<ShapeableImageView>(R.id.ivImage3))
                view.findViewById<ShapeableImageView>(R.id.ivImage3).visible()
            }else view.findViewById<ShapeableImageView>(R.id.ivImage3).gone()

            if (user.images.size > 3) {
                Glide.with(context)
                    .load(user.images[3])
                    .error(R.drawable.test_photo)
                    .placeholder(R.drawable.drawable_cr12topcffffff)
                    .into(view.findViewById<ShapeableImageView>(R.id.ivImage4))
                view.findViewById<ShapeableImageView>(R.id.ivImage4).visible()
            }else view.findViewById<ShapeableImageView>(R.id.ivImage4).gone()

            view.findViewById<TextView>(R.id.tvPersonName).text ="${user.userName},${user.age}"
            if(user.isProfileVerified){
                view.findViewById<TextView>(R.id.tvPersonName).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.new_ic_verified, 0)
            }
            if(user.city?.isNotEmpty()==true){
                view.findViewById<TextView>(R.id.tvLocation).visible()
                view.findViewById<TextView>(R.id.tvLocation).text="${user.userName}'s Location"
                view.findViewById<TextView>(R.id.tvPersonLocation).visible()
                view.findViewById<TextView>(R.id.tvLocationDesc).visible()
                view.findViewById<TextView>(R.id.tvPersonLocation).text= user.city!!.trim()
                view.findViewById<TextView>(R.id.tvLocationDesc).text= user.city!!.trim()
            }else{
                view.findViewById<TextView>(R.id.tvLocationDesc).gone()
                view.findViewById<TextView>(R.id.tvLocation).gone()
                view.findViewById<TextView>(R.id.tvPersonLocation).gone()
            }
            if(user.aboutMe.isNotEmpty()){
                view.findViewById<TextView>(R.id.tvAbout).visible()
                view.findViewById<TextView>(R.id.tvAboutDesc).visible()
                view.findViewById<TextView>(R.id.tvAboutDesc).text=user.aboutMe.trim()
            }else{
                view.findViewById<TextView>(R.id.tvAbout).gone()
                view.findViewById<TextView>(R.id.tvAboutDesc).gone()
            }
            if(user.height != 0){
                val chip = LayoutInflater.from(view.findViewById<ChipGroup>(R.id.myBasicChipGroup).context).inflate(
                    R.layout.chip_layout,
                    view.findViewById<ChipGroup>(R.id.myBasicChipGroup),
                    false
                ) as Chip
                chip.isCheckedIconVisible = true
                chip.isChecked=true
                chip.isClickable=false
                chip.setCheckedIconResource(R.drawable.new_ic_workout)
                chip.fontFamily(R.font.merriweather_sans_regular)
                chip.text = user.height.toString()
                view.findViewById<ChipGroup>(R.id.myBasicChipGroup).addView(chip)
                view.findViewById<ChipGroup>(R.id.tvMyBasic).visible()
                view.findViewById<ChipGroup>(R.id.myBasicChipGroup).visible()
            }
            if(user.interestIn != null){
                val chip = LayoutInflater.from(view.findViewById<ChipGroup>(R.id.myBasicChipGroup).context).inflate(
                    R.layout.chip_layout,
                    view.findViewById<ChipGroup>(R.id.myBasicChipGroup),
                    false
                ) as Chip
                chip.isCheckedIconVisible = true
                chip.isChecked=true
                chip.isClickable=false
                chip.setCheckedIconResource(R.drawable.ic_looking_for)
                chip.fontFamily(R.font.merriweather_sans_regular)
                chip.text = when (user.interestIn) {
                    1 -> "Men"
                    2 -> "Women"
                    3 -> "Everyone"
                    else -> "Men"
                }
                view.findViewById<ChipGroup>(R.id.myBasicChipGroup).addView(chip)
                view.findViewById<ChipGroup>(R.id.tvMyBasic).visible()
                view.findViewById<ChipGroup>(R.id.myBasicChipGroup).visible()
            }
            if(user.sexualOrientation.isNotEmpty()){
                user.sexualOrientation.mapIndexed { index, sexualOrientation ->
                    val chip = LayoutInflater.from(view.findViewById<ChipGroup>(R.id.myBasicChipGroup).context).inflate(
                        R.layout.chip_layout,
                        view.findViewById<ChipGroup>(R.id.myBasicChipGroup),
                        false
                    ) as Chip
                    chip.isCheckedIconVisible = true
                    chip.setCheckedIconResource(R.drawable.new_ic_sexual_orientation)
                    chip.isChecked=true
                    chip.fontFamily(R.font.merriweather_sans_regular)
                    chip.id = index
                    chip.isClickable=false
                    chip.text = sexualOrientation
                    view.findViewById<ChipGroup>(R.id.myBasicChipGroup).addView(chip)
                }
                view.findViewById<TextView>(R.id.tvMyBasic).visible()
                view.findViewById<ChipGroup>(R.id.myBasicChipGroup).visible()
            }
            if(user.education.isNotEmpty()){
                user.education.mapIndexed { index, education ->
                    val chip = LayoutInflater.from(view.findViewById<ChipGroup>(R.id.myBasicChipGroup).context).inflate(
                        R.layout.chip_layout,
                        view.findViewById<ChipGroup>(R.id.myBasicChipGroup),
                        false
                    ) as Chip
                    chip.isCheckedIconVisible = true
                    chip.setCheckedIconResource(R.drawable.new_ic_education)
                    chip.isChecked=true
                    chip.fontFamily(R.font.merriweather_sans_regular)
                    chip.id = index
                    chip.isClickable=false
                    chip.text = education
                    view.findViewById<ChipGroup>(R.id.myBasicChipGroup).addView(chip)
                }
                view.findViewById<TextView>(R.id.tvMyBasic).visible()
                view.findViewById<ChipGroup>(R.id.myBasicChipGroup).visible()
            }
            if(user.religion.isNotEmpty()){
                user.religion.mapIndexed { index, education ->
                    val chip = LayoutInflater.from(view.findViewById<ChipGroup>(R.id.myBasicChipGroup).context).inflate(
                        R.layout.chip_layout,
                        view.findViewById<ChipGroup>(R.id.myBasicChipGroup),
                        false
                    ) as Chip
                    chip.isCheckedIconVisible = true
                    chip.isChecked=true
                    chip.setCheckedIconResource(R.drawable.new_ic_religion)
                    chip.fontFamily(R.font.merriweather_sans_regular)
                    chip.id = index
                    chip.isClickable=false
                    chip.text = education
                    view.findViewById<ChipGroup>(R.id.myBasicChipGroup).addView(chip)
                }
                view.findViewById<TextView>(R.id.tvMyBasic).visible()
                view.findViewById<ChipGroup>(R.id.myBasicChipGroup).visible()
            }
            if(user.lifestyle?.workout?.isNotBlank()==true){
                val chip = LayoutInflater.from(view.findViewById<ChipGroup>(R.id.myBasicChipGroup).context).inflate(
                    R.layout.chip_layout,
                    view.findViewById<ChipGroup>(R.id.myBasicChipGroup),
                    false
                ) as Chip
                chip.isCheckedIconVisible = true
                chip.isChecked=true
                chip.isClickable=false
                chip.setCheckedIconResource(R.drawable.new_ic_workout)
                chip.fontFamily(R.font.merriweather_sans_regular)
                chip.text = user.lifestyle?.workout
                view.findViewById<ChipGroup>(R.id.myBasicChipGroup).addView(chip)
                view.findViewById<ChipGroup>(R.id.tvMyBasic).visible()
                view.findViewById<ChipGroup>(R.id.myBasicChipGroup).visible()
            }
            if(user.lifestyle?.smoking?.isNotBlank()==true){
                val chip = LayoutInflater.from(view.findViewById<ChipGroup>(R.id.myBasicChipGroup).context).inflate(
                    R.layout.chip_layout,
                    view.findViewById<ChipGroup>(R.id.myBasicChipGroup),
                    false
                ) as Chip
                chip.isCheckedIconVisible = true
                chip.isChecked=true
                chip.isClickable=false
                chip.setCheckedIconResource(R.drawable.new_ic_smoking)
                chip.fontFamily(R.font.merriweather_sans_regular)
                chip.text = user.lifestyle.smoking
                view.findViewById<ChipGroup>(R.id.myBasicChipGroup).addView(chip)
                view.findViewById<ChipGroup>(R.id.tvMyBasic).visible()
                view.findViewById<ChipGroup>(R.id.myBasicChipGroup).visible()
            }
            if(user.lifestyle?.drinking?.isNotBlank()==true){
                val chip = LayoutInflater.from(view.findViewById<ChipGroup>(R.id.myBasicChipGroup).context).inflate(
                    R.layout.chip_layout,
                    view.findViewById<ChipGroup>(R.id.myBasicChipGroup),
                    false
                ) as Chip
                chip.isCheckedIconVisible = true
                chip.isChecked=true
                chip.isClickable=false
                chip.fontFamily(R.font.merriweather_sans_regular)
                chip.text = user.lifestyle.drinking
                chip.setCheckedIconResource(R.drawable.new_ic_religion)
                view.findViewById<ChipGroup>(R.id.myBasicChipGroup).addView(chip)
                view.findViewById<TextView>(R.id.tvMyBasic).visible()
                view.findViewById<ChipGroup>(R.id.myBasicChipGroup).visible()
            }

            if(user.yourInterest.isNotEmpty()){
                user.yourInterest.mapIndexed { index, education ->
                    val chip = LayoutInflater.from(view.findViewById<ChipGroup>(R.id.interestsChipGroup).context).inflate(
                        R.layout.chip_layout,
                        view.findViewById<ChipGroup>(R.id.interestsChipGroup),
                        false
                    ) as Chip
                    chip.isCheckedIconVisible = true
                    chip.isChecked=true
                    chip.isCheckable=false
                    chip.isClickable=false
                    chip.setCheckedIconResource(R.drawable.new_ic_religion)
                    chip.fontFamily(R.font.merriweather_sans_regular)
                    chip.id = index
                    chip.text = education
                    view.findViewById<ChipGroup>(R.id.interestsChipGroup).addView(chip)
                }
                view.findViewById<TextView>(R.id.tvInterest).visible()
                view.findViewById<ChipGroup>(R.id.interestsChipGroup).visible()
            }else{
                view.findViewById<TextView>(R.id.tvInterest).gone()
                view.findViewById<ChipGroup>(R.id.interestsChipGroup).gone()
            }
            if(user.isRealTimeImageVerified){
                view.findViewById<TextView>(R.id.tvVerification).visible()
                view.findViewById<LinearLayoutCompat>(R.id.llVerification).visible()

                if (user.images.size > 0)
                    Glide.with(context)
                        .load(user.realtimeImage[0])
                        .error(R.drawable.test_photo)
                        .placeholder(R.drawable.drawable_bg_cr12cfff7f6)
                        .into(view.findViewById<ImageView>(R.id.sivUserProfile))
            }else{
                view.findViewById<TextView>(R.id.tvVerification).gone()
                view.findViewById<LinearLayoutCompat>(R.id.llVerification).gone()
            }

            create()
            show()
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT)
            window?.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT))
        }

    fun Context.openPermissionSettings(): Intent =
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:${packageName}")
        ).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
        }

    @Throws(URISyntaxException::class)
    fun Context.getPath(uri: Uri): String? {
        var myUri = uri
        var selection: String? = null
        var selectionArgs: Array<String>? = null
        if (DocumentsContract.isDocumentUri(applicationContext, myUri)) {
            if (isExternalStorageDocument(myUri)) {
                val docId = DocumentsContract.getDocumentId(myUri)
                val split = docId.split(":".toRegex()).toTypedArray()
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            } else if (isDownloadsDocument(myUri)) {
                val id = DocumentsContract.getDocumentId(myUri)
                myUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id)
                )
            } else if (isMediaDocument(myUri)) {
                val docId = DocumentsContract.getDocumentId(myUri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("image" == type) {
                    myUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
                selection = "_id=?"
                selectionArgs = arrayOf(split[1])
            }
        }
        if ("content".equals(myUri.scheme, ignoreCase = true)) {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            var cursor: Cursor? = null
            try {
                cursor =
                    contentResolver.query(myUri, projection, selection, selectionArgs, null)
                val index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                if (cursor.moveToFirst()) {
                    return cursor.getString(index)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor?.close()
            }
        } else if ("file".equals(myUri.scheme, ignoreCase = true)) {
            return myUri.path
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean =
        "com.android.externalstorage.documents" == uri.authority

    private fun isDownloadsDocument(uri: Uri): Boolean =
        "com.android.providers.downloads.documents" == uri.authority

    private fun isMediaDocument(uri: Uri): Boolean =
        "com.android.providers.media.documents" == uri.authority

    fun Context.isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }

    fun Activity.openLoginPage() =
        Intent(
            this,
            LoginActivity::class.java
        ).also {
            PreferenceHandler(this).clearSharePreferences(this)
            startActivity(it)
            finishAffinity()
        }

//    fun showKeyboard(con: Context, yourEditText: EditText) {
//        val imm = con.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
//        imm!!.showSoftInput(yourEditText, InputMethodManager.SHOW_IMPLICIT)
//    }

    fun hideKeyboard(con: Context, yourEditText: EditText) {
        val imm =
            con.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(yourEditText.windowToken, 0)
    }


    fun isCurrentDate(date:String)=date.equals(SimpleDateFormat(Constants.DATE_FORMAT).format(System.currentTimeMillis()).toString(),true)
}
