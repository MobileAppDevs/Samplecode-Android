package com.enkefalostechnologies.calendarpro.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.DataStoreChannelEventName
import com.amplifyframework.datastore.DataStoreException
import com.amplifyframework.datastore.events.NetworkStatusEvent
import com.amplifyframework.datastore.events.OutboxStatusEvent
import com.amplifyframework.datastore.generated.model.Tasks
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.syncengine.OutboxMutationEvent
import com.amplifyframework.hub.HubChannel
import com.amplifyframework.hub.HubEvent
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.constant.Constants
import com.enkefalostechnologies.calendarpro.constant.StorageConstant
import com.enkefalostechnologies.calendarpro.databinding.FragmentSettingBinding
import com.enkefalostechnologies.calendarpro.databinding.MsgDialog2Binding
import com.enkefalostechnologies.calendarpro.databinding.MsgDialogBinding
import com.enkefalostechnologies.calendarpro.router.Router.navigateToVerifyEmailScreen
import com.enkefalostechnologies.calendarpro.ui.HomeActivity
import com.enkefalostechnologies.calendarpro.ui.base.BaseFragment
import com.enkefalostechnologies.calendarpro.ui.country_selection.CountrySelectionActivity
import com.enkefalostechnologies.calendarpro.ui.viewModel.fragment.SettingFragmentViewModel
import com.enkefalostechnologies.calendarpro.util.AmplifyUtil.handleExceptions
import com.enkefalostechnologies.calendarpro.util.AppUtil.isNetworkAvailable
import com.enkefalostechnologies.calendarpro.util.AppUtil.isNotificationPermissionAvailable
import com.enkefalostechnologies.calendarpro.util.AppUtil.openUrl
import com.enkefalostechnologies.calendarpro.util.AppUtil.requestPermission
import com.enkefalostechnologies.calendarpro.util.AppUtil.showToast
import com.enkefalostechnologies.calendarpro.util.Extension.close
import com.enkefalostechnologies.calendarpro.util.Extension.getVersionName
import com.enkefalostechnologies.calendarpro.util.Extension.gone
import com.enkefalostechnologies.calendarpro.util.Extension.removeLastChar
import com.enkefalostechnologies.calendarpro.util.Extension.visible
import com.enkefalostechnologies.calendarpro.util.MsgAlertDialogListener
import com.enkefalostechnologies.calendarpro.util.dialog.messageAlertDialog
import com.enkefalostechnologies.calendarpro.util.dialog.messageAlertDialog2


class SettingFragment : BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting),
    View.OnClickListener {
    private val MY_PERMISSION_REQUEST_NOTIFICATION = 123
    private val MY_PERMISSION_REQUEST_REMINDER = 1234
    var countriess: String = ""
    val viewModel: SettingFragmentViewModel by viewModels { SettingFragmentViewModel.Factory }

    private var policyListener: ClickableSpan = object : ClickableSpan() {
        override fun onClick(textView: View) {
            requireActivity().openUrl(Constants.PRIVACY_POLICY_URL)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = true
        }
    }
    var isDataSynced: Boolean = true

    override fun setupViews() {
        dialog.close()
        binding.switchReminder.isChecked =
            isReminderOn() && requireActivity().isNotificationPermissionAvailable()
        binding.switchNotification.isChecked =
            isNotificationOn() && requireActivity().isNotificationPermissionAvailable()
        setSpannable()
        setSwitchListeners()
        setProfileData()
        binding.tvVersion.text = "version ${requireActivity().getVersionName()}"
//        viewModel.checkSessionValue()

//        val slideUp: Animation = AnimationUtils.loadAnimation(requireActivity(), R.anim.fall_down)
//            binding.llNotification.startAnimation(slideUp)
//            binding.llReminders.startAnimation(slideUp)
//            binding.linearLayoutCompat.startAnimation(slideUp)
//            binding.btnLogout.startAnimation(slideUp)
    }

    override fun setupListeners() {
        binding.btnLogout.setOnClickListener(this)
        binding.tvCountries.setOnClickListener(this)
        binding.tvEmailSupport.setOnClickListener(this)

    }

    override fun fetchInitialData() {

    }

    fun setProfileData() {
        binding.btnLogout.gone()
        binding.linearLayoutCompat.gone()
//        binding.linearLayoutCompat6.gone()
//        val query =
//            if (isUserLoggedIn()) Where.matches(User.EMAIL.eq(getUserEmail())) else Where.matches(
//                User.DEVICE_ID.eq(deviceId())
//            )
//        Amplify.DataStore.query(User::class.java, query, {
//            requireActivity().runOnUiThread {
//                if (it.hasNext()) {
//                    val user = it.next()
//                    binding.tvCountries.text = "ADD"
//                    user.countrySelected?.let {
//                        if (user.countrySelected != "") {
//                            if (user.countrySelected.contains(",")) {
//                                binding.tvCountries.text = user.countrySelected.removeLastChar()
//                            } else {
//                                binding.tvCountries.text = user.countrySelected
//                            }
//
//                        }
//                    }
//                    binding.linearLayoutCompat6.visible()
//                }else{
//                    binding.tvCountries.text = "ADD"
//                    binding.linearLayoutCompat6.visible()
//                }
//            }
//        }, {
//            requireActivity().runOnUiThread {
//                binding.tvCountries.text = "ADD"
//                binding.linearLayoutCompat6.visible()
//                requireActivity().handleExceptions(it)
//            }
//        })
        binding.tvCountries.text = "ADD"
        if (getCountries().contains(",")) {
            binding.tvCountries.text = getCountries().removeLastChar()
        } else {
            binding.tvCountries.text = if(getCountries() == "") "ADD" else getCountries()
        }
        binding.linearLayoutCompat6.visible()

        if (isUserLoggedIn()) {
            if (getUserEmail().isNotBlank()) {
                binding.tvEmail.text = getUserEmail()
                binding.tvEmail.visible()
            }
            if (isUserEmailVerified()) {
                binding.tvVerify.text = getString(R.string.verified)
                binding.tvVerify.setOnClickListener(null)
            } else {
                binding.tvVerify.text = getString(R.string.verify)
                binding.tvVerify.setOnClickListener(this)
            }


            binding.switchReminder.isChecked =
                isReminderOn() && requireActivity().isNotificationPermissionAvailable()
            binding.switchNotification.isChecked =
                isNotificationOn() && requireActivity().isNotificationPermissionAvailable()
//            preferenceManager.writeBoolean(StorageConstant.IS_REMINDER_ON,user.isReminderEnabled ?: true &&  requireActivity().isNotificationPermissionAvailable())
//            preferenceManager.writeBoolean(StorageConstant.IS_NOTIFICATION_ON,user.isNotificationEnabled ?: true && requireActivity().isNotificationPermissionAvailable())
            binding.btnLogout.visible()
            binding.linearLayoutCompat.visible()
        } else {
            binding.switchReminder.isChecked =
                isReminderOn() && requireActivity().isNotificationPermissionAvailable()
            binding.switchNotification.isChecked =
                isNotificationOn() && requireActivity().isNotificationPermissionAvailable()
            binding.btnLogout.gone()
            binding.linearLayoutCompat.gone()
        }
    }


    //    val userDataObserver=Observer<User> { user ->
//        viewModel.user=user
//
//        setRemoteData(user)
//        dialog.close()
//    }
//    val userAttributes=Observer<com.enkefalostechnologies.calendarpro.model.User> { user ->
//        viewModel.fetchUserData(user.userId)
//    }
//    val loginSessionObserver=Observer<Boolean> {
//        dialog.close()
//        if (it) {
////            //dialog.visible()
//            viewModel.fetchUserAttributes()
//        }
//        viewModel.isLoggedIn = it
//        viewModel.user=null
//    }
    val signoutObserver = Observer<Boolean> { isSignedOut ->
        if (isSignedOut) {
//            Amplify.DataStore.clear({
            requireActivity().runOnUiThread {
                clearLocalStorage(requireActivity())
                (requireActivity() as HomeActivity).signOut()
                dialog.close()
            }
//            }, {
//                requireActivity().runOnUiThread {
//                    requireActivity().handleExceptions(it)
//                }
//            })
        } else {
            dialog.close()
        }
    }
    val dataSyncObserver = Observer<Boolean> { isSynced ->
        isDataSynced = isSynced
    }
    val onError = Observer<DataStoreException> {
        dialog.close()
    }

    override fun setupObservers() {
//        viewModel.userData.observe(this,userDataObserver)
//        viewModel.userAttributes.observe(this,userAttributes)
//        viewModel.loginSession.observe(this,loginSessionObserver)
        viewModel.signOutConfirmation().observe(this, signoutObserver)
        viewModel.onError.observe(this, onError)
        viewModel.isDataSynced.observe(this, dataSyncObserver)
    }

    override fun removeObserver() {
//        viewModel.userData.removeObserver( userDataObserver)
//        viewModel.userAttributes.removeObserver(userAttributes)
//        viewModel.loginSession.removeObserver( loginSessionObserver)
        viewModel.signOutConfirmation().removeObserver(signoutObserver)
        viewModel.onError.removeObserver(onError)
        viewModel.isDataSynced.removeObserver(dataSyncObserver)

    }


    override fun onClick(p0: View?) {
        when (p0) {
            binding.btnLogout -> {
                if (!requireActivity().isNetworkAvailable()) {
                    requireActivity().showToast("Please check your internet connection.")
                } else {
                    if (!isDataSynced) {
                        requireActivity().runOnUiThread {
                            /*** sync is in progress **/
                            val d = requireActivity().messageAlertDialog2(
                                MsgDialog2Binding.inflate(LayoutInflater.from(requireActivity())),
                                icon = R.drawable.ic_sync,
                                iconVisible = true,
                                msg = "Please wait while your data is syncing. This may take a moment. Alternatively, you can try logging out again in few minutes. Thank you for your patience.",
                                btnText = "Try later",
                                listener = object : MsgAlertDialogListener {
                                    override fun onDoneClicked() {}
                                })
                            d.show()
                        }
                    } else {
                        requireActivity().runOnUiThread {
                            viewModel.signOut()
                            dialog.visible()
                        }
                    }
                }
            }

            binding.tvEmailSupport -> {
                sendEmail()
            }

            binding.tvVerify -> {
                if (!requireActivity().isNetworkAvailable()) {
                    requireActivity().showToast("Please check your internet connection.")
                } else {
                    requireActivity().navigateToVerifyEmailScreen(binding.tvEmail.text.toString())
                }
            }

            binding.tvCountries -> {
                val query =
                    if (isUserLoggedIn()) Where.matches(User.EMAIL.eq(getUserEmail())) else Where.matches(
                        User.DEVICE_ID.eq(deviceId())
                    )
                Amplify.DataStore.query(User::class.java, query, {
                    requireActivity().runOnUiThread {
                        if (it.hasNext()) {
                            val user = it.next()
                            user.countrySelected?.let {
                                if (user.countrySelected != "") {
                                  val ids=  if (user.countrySelected.contains(",")) {
                                       user.countrySelected.removeLastChar()
                                    } else {
                                       user.countrySelected
                                    }
                                    setCountries(ids)
                                    startActivity(
                                        Intent(requireActivity(), CountrySelectionActivity::class.java)
                                            .putExtra("from", "setting")
                                            .putExtra("ids", ids)
                                    )

                                }else{
                                    setCountries("ADD")
                                    startActivity(
                                        Intent(requireActivity(), CountrySelectionActivity::class.java)
                                            .putExtra("from", "setting")
                                            .putExtra("ids", "")
                                    )
                                }
                            }

                        }else{
                            binding.tvCountries.text = "ADD"
                            binding.linearLayoutCompat6.visible()
                        }
                    }
                }, {
                    requireActivity().runOnUiThread {
                        binding.tvCountries.text = "ADD"
                        binding.linearLayoutCompat6.visible()
                        requireActivity().handleExceptions(it)
                    }
                })

            }

        }
    }

    private fun setSpannable() {
        val ss = SpannableStringBuilder(binding.tvPrivacyPolicy.text.toString())

        val text1 = "privacy policy"
        ss.setSpan(
            policyListener, ss.toString().indexOf(text1),
            ss.toString().indexOf(text1) + text1.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        ss.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.color_255CCB)),
            ss.toString().indexOf(text1),
            ss.toString().indexOf(text1) + text1.length,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )

        binding.tvPrivacyPolicy.text = ss
        binding.tvPrivacyPolicy.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun sendEmail() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(binding.tvEmailSupport.text.toString()))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Help & Support")
        intent.type = "message/rfc822"
        intent.setPackage("com.google.android.gm");
        startActivity(Intent.createChooser(intent, "Choose an Email client :"))
    }


//    fun setRemoteData(user:User){
//            if (user.email.isNotBlank()) {
//                binding.tvEmail.text = user.email
//                binding.tvEmail.visible()
//            }
//            if (user.isEmailVerified) {
//                binding.tvVerify.text = getString(R.string.verified)
//                binding.tvVerify.setOnClickListener(null)
//            } else {
//                binding.tvVerify.text = getString(R.string.verify)
//                binding.tvVerify.setOnClickListener(this)
//            }
//            binding.switchReminder.isChecked = user.isReminderEnabled ?: false && requireActivity().isNotificationPermissionAvailable()
//            binding.switchNotification.isChecked =
//                user.isNotificationEnabled ?: false && requireActivity().isNotificationPermissionAvailable()
//        binding.btnLogout.visible()
//        binding.linearLayoutCompat.visible()
//
//        if(isUserLoggedIn()) {
//            amplifyDataModelUtil.fetchUserByUserId(
//                getUserId(),
//                {
//                    requireActivity().runOnUiThread {
//                        while (it.hasNext()) {
//                            val user = it.next()
//                            if (user.email.isNotBlank()) {
//                                binding.tvEmail.text = user.email
//                                binding.tvEmail.visible()
//                            }
//                            if (user.isEmailVerified) {
//                                binding.tvVerify.text = getString(R.string.verified)
//                                binding.tvVerify.setOnClickListener(null)
//                            } else {
//                                binding.tvVerify.text = getString(R.string.verify)
//                                binding.tvVerify.setOnClickListener(this)
//                            }
//                            binding.switchReminder.isChecked = user.isReminderEnabled ?: false && requireActivity().isNotificationPermissionAvailable()
//                            binding.switchNotification.isChecked = user.isNotificationEnabled ?: false &&  requireActivity().isNotificationPermissionAvailable()
//                            preferenceManager.writeBoolean(StorageConstant.IS_REMINDER_ON,user.isReminderEnabled ?: true &&  requireActivity().isNotificationPermissionAvailable())
//                            preferenceManager.writeBoolean(StorageConstant.IS_NOTIFICATION_ON,user.isNotificationEnabled ?: true && requireActivity().isNotificationPermissionAvailable())
//
//                        }
//                        binding.btnLogout.visible()
//                        binding.linearLayoutCompat.visible()
//                    }
//
//                },
//                {
//                    requireActivity().runOnUiThread {
//                        binding.linearLayoutCompat.gone()
//                        binding.btnLogout.gone()
//                    }
//                })
//        }else{
//            binding.switchReminder.isChecked = preferenceManager.readBoolean(StorageConstant.IS_REMINDER_ON,requireActivity().isNotificationPermissionAvailable())
//            binding.switchNotification.isChecked =preferenceManager.readBoolean(StorageConstant.IS_NOTIFICATION_ON,requireActivity().isNotificationPermissionAvailable())
//            binding.linearLayoutCompat.gone()
//            binding.btnLogout.gone()
//        }
//
//    }

    private fun setSwitchListeners() {
        binding.switchReminder.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                if (requireActivity().isNotificationPermissionAvailable()) {
                    if (isUserLoggedIn()) {
                        setIsReminderOn(isChecked)
                        amplifyDataModelUtil.setUserReminder(
                            getUserEmail(),
                            deviceId(),
                            isChecked,
                            {
                                requireActivity().runOnUiThread {
                                    dialog.close()
//                                    requireActivity().showToast(
//                                        "Reminder ${if (isChecked) "enabled" else "disabled"} successfully.",
//                                        Toast.LENGTH_SHORT
//                                    )
                                }
                            },
                            {
                                requireActivity().runOnUiThread {
                                    requireActivity().handleExceptions(
                                        it
                                    )
                                }
                            })
                    } else {
                        setIsReminderOn(isChecked)
                    }
                } else {
                    requestPermission(
                        Manifest.permission.POST_NOTIFICATIONS,
                        MY_PERMISSION_REQUEST_REMINDER
                    )
                }
            }
        }
        binding.switchNotification.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                if (requireActivity().isNotificationPermissionAvailable()) {
                    if (isUserLoggedIn()) {
//                        //dialog.visible()
                        setIsNotificationOn(isChecked)
                        amplifyDataModelUtil.setUserNotification(
                            getUserEmail(),
                            deviceId(),
                            isChecked,
                            {
                                requireActivity().runOnUiThread {
//                                    dialog.close()
//                                    requireActivity().showToast(
//                                        "Notification ${if (isChecked) "enabled" else "disabled"} successfully.",
//                                        Toast.LENGTH_SHORT
//                                    )
                                }
                            },
                            {
                                requireActivity().runOnUiThread {
                                    requireActivity().handleExceptions(
                                        it
                                    )
                                }
                            })
                    } else {
                        setIsNotificationOn(isChecked)
//                        requireActivity().showToast(
//                            "Notification ${if (isChecked) "enabled" else "disabled"} successfully.",
//                            Toast.LENGTH_SHORT
//                        )
                    }
                } else {
                    requestPermission(
                        Manifest.permission.POST_NOTIFICATIONS,
                        MY_PERMISSION_REQUEST_NOTIFICATION
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSION_REQUEST_NOTIFICATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                binding.switchNotification.isChecked = true
                if (isUserLoggedIn()) {
                    //dialog.visible()
                    preferenceManager.writeBoolean(StorageConstant.IS_NOTIFICATION_ON, true)
                    amplifyDataModelUtil.setUserNotification(
                        getUserEmail(),
                        deviceId(),
                        true,
                        {
                            requireActivity().runOnUiThread {
                                dialog.close()
                                // requireActivity().showToast("Notification ${if (true) "enabled" else "disabled"} successfully.",Toast.LENGTH_SHORT)
                            }
                        },
                        {
                            requireActivity().runOnUiThread {
                                requireActivity().handleExceptions(
                                    it
                                )
                            }
                        })
                } else {
                    binding.switchNotification.isChecked = true
                    setIsNotificationOn(true)
                }
            } else {
                binding.switchNotification.isChecked = false
                requireActivity().messageAlertDialog(
                    MsgDialogBinding.inflate(LayoutInflater.from(requireActivity())),
                    title = "Notification permission required.",
                    msg = "Notification permission required to get Notification updates on time.",
                    btnText = "Request Permission",
                    negativeBtnText = "Dismiss",
                    listener = object : MsgAlertDialogListener {
                        override fun onDoneClicked() {
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            val uri = Uri.fromParts("package", requireActivity().packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }
                    }).show()
            }
        } else if (requestCode == MY_PERMISSION_REQUEST_REMINDER) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                binding.switchReminder.isChecked = true
                if (isUserLoggedIn()) {
                    preferenceManager.writeBoolean(StorageConstant.IS_REMINDER_ON, true)
                    amplifyDataModelUtil.setUserReminder(
                        getUserEmail(),
                        deviceId(),
                        true,
                        {
                            requireActivity().runOnUiThread {
                                dialog.close()
//                                requireActivity().showToast(
//                                    "Reminder ${if (true) "enabled" else "disabled"} successfully.",
//                                    Toast.LENGTH_SHORT
//                                )
                            }
                        },
                        {
                            requireActivity().runOnUiThread {
                                requireActivity().handleExceptions(
                                    it
                                )
                            }
                        })
                } else {
                    setIsReminderOn(true)
                }
            } else {
                binding.switchReminder.isChecked = false
                requireActivity().messageAlertDialog(
                    MsgDialogBinding.inflate(LayoutInflater.from(requireActivity())),
                    title = "Notification permission required.",
                    msg = "Notification permission required to get Notification updates on time.",
                    btnText = "Request Permission",
                    listener = object : MsgAlertDialogListener {
                        override fun onDoneClicked() {
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            val uri = Uri.fromParts("package", requireActivity().packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }
                    }).show()
            }
        }
    }


    override fun onPause() {
        super.onPause()
        Log.i("Lifecycle", "SettingFragment -> onPause()")
    }

    override fun onResume() {
        Log.i("Lifecycle", "SettingFragment -> onResume()")
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        Log.i("Lifecycle", "SettingFragment -> onStop()")
    }

    override fun onDestroy() {
        Log.i("Lifecycle", "SettingFragment -> onDestroy()")
        super.onDestroy()
    }
}