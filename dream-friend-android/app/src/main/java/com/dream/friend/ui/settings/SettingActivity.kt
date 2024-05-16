package com.dream.friend.ui.settings

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.billingclient.api.Purchase
import com.dream.friend.BuildConfig
import com.dream.friend.R
import com.dream.friend.common.Constants
import com.dream.friend.common.Utils.dialogLoading
import com.dream.friend.common.Utils.logoutOrDeleteAccountAlert
import com.dream.friend.common.Utils.openLoginPage
import com.dream.friend.common.Utils.showToast
import com.dream.friend.common.activityBindings
import com.dream.friend.common.startRotationAnimation
import com.dream.friend.data.model.CreateSubscriberRequest
import com.dream.friend.data.model.IncognitoModeRequest
import com.dream.friend.data.model.NormalPlan
import com.dream.friend.data.model.SubscribedPlan
import com.dream.friend.data.model.SubscriptionResponse
import com.dream.friend.data.network.Resource
import com.dream.friend.databinding.ActivitySettingBinding
import com.dream.friend.enums.Plan
import com.dream.friend.interfaces.SubscriptionDialogListener
import com.dream.friend.ui.bottomsheet.plan.PlanBottomSheetDialog
import com.dream.friend.ui.home.adapter.GalleryAdapter
import com.dream.friend.ui.login.LoginActivity
import com.dream.friend.ui.verify.email.UpdateOrVerifyEmailActivity
import com.dream.friend.ui.verify.phone.UpdateOrVerifyMobileNumberActivity
import com.dream.friend.ui.viewModel.HomeScreenViewModel
import com.dream.friend.ui.viewModel.UpdateUserDetailsViewModel
import com.dream.friend.ui.viewModel.UserLoginViewModel
import com.dream.friend.util.AppUtil.getCity
import com.dream.friend.util.InAppSubscriptionListener
import com.dream.friend.util.SubscriptionUtil
import kotlinx.android.synthetic.main.activity_setting.switchIncognito
import java.lang.Exception


class SettingActivity : AppCompatActivity(), View.OnClickListener {

    private val binding: ActivitySettingBinding by activityBindings(R.layout.activity_setting)
    private val viewModel: HomeScreenViewModel by viewModels()
    private val updateUserViewModel: UpdateUserDetailsViewModel by viewModels()
    private val viewModelUserLogin: UserLoginViewModel by viewModels()
    private val images = arrayListOf<String>()
    private val adapter = GalleryAdapter()
    lateinit var dialog: Dialog
    lateinit var premiumBottomSheetDialog: PlanBottomSheetDialog
    lateinit var plusBottomSheetDialog: PlanBottomSheetDialog
    var subscriptionResponse: SubscriptionResponse? = null
    var planId: String? = null
    var errorCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        dialog = dialogLoading()
        dialog.dismiss()
        viewModelUserLogin.getUser()?.userId?.let { it1 -> viewModel.getUserDetail(it1) }
        updateUserViewModel.getSubscriptions()
        viewModelUserLogin.getUser()?.email?.let {
            binding.tvEmail.text=it
        }
        viewModelUserLogin.getUser()?.phoneNumber?.let {
            binding.tvPhoneNumber.text=it
        }
        viewModelUserLogin.getUser()?.incognitoMode?.let {
            binding.switchIncognito.isChecked=it
        }
        viewModelUserLogin.getUser()?.city?.let {
            if(it!=""){
            binding.tvCurrentLocation.text=it
            }else{
                if(viewModelUserLogin.getUser()?.location?.coordinates!=null && viewModelUserLogin.getUser()?.location?.coordinates?.isNotEmpty()==true ) {
                    if(viewModelUserLogin.getUser()?.location?.coordinates!![0]!=null && viewModelUserLogin.getUser()?.location?.coordinates!![1]!=null) {
                        binding.tvCurrentLocation.text =getCity(viewModelUserLogin.getUser()?.location?.coordinates!![1],viewModelUserLogin.getUser()?.location?.coordinates!![0])
                    }
                }
            }
        }

        binding.switchIncognito.setOnCheckedChangeListener { buttonView, isChecked ->
            if(viewModelUserLogin.getUser()?.subscribption?.isPremiumSubscriber == true) {
                if (isChecked) {
                    viewModel.incognitoMode(IncognitoModeRequest(true))
                } else {
                    viewModel.incognitoMode(IncognitoModeRequest(false))
                }
            }else{
                openPremiumBottomSheet()
                binding.switchIncognito.isChecked=false
            }
        }
        setClickListeners()
        addObserver()


    }

    override fun onClick(v: View?) {
        when (v) {
            binding.ivBack->{finish()}
            binding.tvPhoneNumber -> {startActivity(Intent(this,UpdateOrVerifyMobileNumberActivity::class.java).putExtra("phone", binding.tvPhoneNumber.text.trim().toString()))}
            binding.tvEmail -> {  startActivity(Intent(this,UpdateOrVerifyEmailActivity::class.java).putExtra("email", binding.tvEmail.text.trim().toString()))}
            binding.tvCurrentLocation -> {  startActivityForResult(Intent(this, LocationSettingActivity::class.java),1000)}
//            binding.tvLanguage -> {}
            binding.tvRateUs -> {
                openPlayStoreForRating()
            }

            binding.tvHelpSupport -> {
                openUrl(Constants.URL_help_support)
            }

            binding.tvAboutUs -> {
                openUrl(Constants.URL_about_us)
            }

            binding.tvShare -> {
                shareApp()
            }

            binding.tvPrivacyPolicy -> {
                openUrl(Constants.URL_privacy_policy)
            }

            binding.tvTermsServices -> {
                openUrl(Constants.URL_terms_of_service)
            }

            binding.tvNotifications -> {
                startActivity(Intent(this, NotificationSettingActivity::class.java))
            }

            binding.tvBlockedUsers -> {
                startActivity(Intent(this, BlockedUsersActivity::class.java))
            }

            binding.btnLogout -> {
                logoutOrDeleteAccountAlert(
                    DialogInterface.OnClickListener { _, id ->
                        if (id == DialogInterface.BUTTON_POSITIVE) {
                            //logout api
                            viewModel.logout()
                        }
                    },
                    ActivityCompat.getDrawable(binding.root.context, R.drawable.drawable_logout_ic),
                    getString(R.string.logout),
                    getString(R.string.do_you_want_to_logout_the_app)
                )
            }

            binding.btnDeleteAccount -> {
                logoutOrDeleteAccountAlert(
                    DialogInterface.OnClickListener { _, id ->
                        if (id == DialogInterface.BUTTON_POSITIVE) {
                            updateUserViewModel.deleteUser()
                        }
                    },
                    ActivityCompat.getDrawable(binding.root.context, R.drawable.drawable_delete_ic),
                    getString(R.string.delete_account),
                    getString(R.string.do_you_want_to_delete_the_account)
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode==Activity.RESULT_OK && requestCode==1000){
            init()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    private fun addObserver() {
        updateUserViewModel.subscriptionResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
//                    dialog.dismiss()
                    subscriptionResponse = it.data
//                    binding.tvUpgradePremium.text = "Upgrade from ${
//                        subscriptionResponse?.subscriptionPlans?.get(0)?.planSchedule?.get(0)?.planPrice.toString()
//                    } INR"
//                    binding.tvUpgradePlus.text = "Upgrade from ${
//                        subscriptionResponse?.subscriptionPlans?.get(1)?.planSchedule?.get(0)?.planPrice.toString()
//                    } INR"
                }

                is Resource.Error -> {
//                    dialog.dismiss()

                }

                is Resource.Loading -> {
//                    dialog.show()
//                    dialog.findViewById<ImageView>(R.id.imgAnimation).startRotationAnimation()
                }

                is Resource.TokenRenew -> {
//                    dialog.dismiss()
                    if (it.isTokenExpire == true) {
                        openLoginPage()
                    }
                }
            }
        }
        viewModelUserLogin.subscribeResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    dialog.dismiss()
                    viewModelUserLogin.getUser()?.userId?.let { it1 -> viewModel.getUserDetail(it1) }
                    // requireActivity().showToast(it.data?.message.toString())
                    when (planId?.toLowerCase()) {
                        subscriptionResponse?.subscriptionPlans?.get(0)?.planSchedule?.get(0)?.planId?.toLowerCase(),
                        subscriptionResponse?.subscriptionPlans?.get(0)?.planSchedule?.get(1)?.planId?.toLowerCase(),
                        subscriptionResponse?.subscriptionPlans?.get(0)?.planSchedule?.get(2)?.planId?.toLowerCase() -> {
                            premiumBottomSheetDialog.dismiss()
                        }

                        subscriptionResponse?.subscriptionPlans?.get(1)?.planSchedule?.get(0)?.planId?.toLowerCase(),
                        subscriptionResponse?.subscriptionPlans?.get(1)?.planSchedule?.get(1)?.planId?.toLowerCase(),
                        subscriptionResponse?.subscriptionPlans?.get(1)?.planSchedule?.get(2)?.planId?.toLowerCase() -> {
                            plusBottomSheetDialog.dismiss()
                        }

//                        subscriptionResponse?.subscriptionPlans?.get(2)?.planSchedule?.get(0)?.planId?.toLowerCase(),
//                        subscriptionResponse?.subscriptionPlans?.get(2)?.planSchedule?.get(1)?.planId?.toLowerCase(),
//                        subscriptionResponse?.subscriptionPlans?.get(2)?.planSchedule?.get(2)?.planId?.toLowerCase() -> {
//                            addOnBottomSheetDialog.dismiss()
//                        }
//
//                        subscriptionResponse?.subscriptionPlans?.get(3)?.planSchedule?.get(0)?.planId?.toLowerCase(),
//                        subscriptionResponse?.subscriptionPlans?.get(3)?.planSchedule?.get(1)?.planId?.toLowerCase(),
//                        subscriptionResponse?.subscriptionPlans?.get(3)?.planSchedule?.get(2)?.planId?.toLowerCase() -> {
//                            addOnBottomSheetDialog.dismiss()
//                        }
                    }

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
                    if (it.isTokenExpire == true) {
                        openLoginPage()
                    }
                }
            }
        }
        viewModel.getUserDetailResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    it.data?.user?.let { it1 -> viewModelUserLogin.saveUser(it1) }
//                    setUserDetails()
//                    setBoost()
                }

                is Resource.Error -> {


                }

                is Resource.Loading -> {

                }

                is Resource.TokenRenew -> {

                }
            }

        }
        viewModel.logoutResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    if (response.data?.statusCode == 200) {
                        viewModelUserLogin.clearPreference(this)
                        response.data.message?.let {
//                            successFailureAlert(
//                                it,
//                                callback = { _: DialogInterface, i: Int ->
//                                    if (i == DialogInterface.BUTTON_POSITIVE) {
                                        val intent = Intent(this, LoginActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)
                                        finish()
//                                    }
//                                }
//                            )
                        }
                    } else {
                        response.data?.message?.let { showToast(it) }
                    }
                }

                is Resource.Error, is Resource.Loading -> {}
                is Resource.TokenRenew -> {
                    if (response.isTokenExpire == true) {
                        openLoginPage()
                    }
                }
            }
        }
        updateUserViewModel.deleteUserResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    viewModelUserLogin.clearPreference(this)
                    response.data?.message?.let {
                        showToast(it)
//                        successFailureAlert(
//                            it,
//                            title = "Success",
//                            callback = { _: DialogInterface, i: Int ->
//                                if (i == DialogInterface.BUTTON_POSITIVE) {
                                    val intent = Intent(this, LoginActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()
//                                }
//                            }
//                        )
                    }
                    viewModelUserLogin.clearPreference(this)
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }

                is Resource.Error -> {
//                    dialog.dismiss()
                    response.message?.let {
                        showToast(it)
//                        successFailureAlert(
//                            it,
//                            title = "Error",
//                            callback = { _: DialogInterface, i: Int ->
//                                if (i == DialogInterface.BUTTON_POSITIVE) {
                                    updateUserViewModel.deleteUser()
//                                }
//                            },
//                            "Try Again",
//                            true
//                        )
                    }
                }

                is Resource.Loading -> {
//                    dialog.show()
//                    dialog.findViewById<ImageView>(R.id.imgAnimation).startRotationAnimation()
                }

                is Resource.TokenRenew -> {
//                    dialog.dismiss()
                    if (response.isTokenExpire == true) {
                        openLoginPage()
                    }
                }
            }
        }
        viewModel.incognitoModeResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    if (response.data?.statusCode == 200) {
                            viewModelUserLogin.saveUser(response.data.user)
                           val msg= if(binding.switchIncognito.isChecked) "Incognito Mode Enabled" else  "Incognito Mode Disabled"
                            showToast(msg)
                    } else {
                        response.data?.message?.let { showToast(it) }
                    }
                }

                is Resource.Error, is Resource.Loading -> {}
                is Resource.TokenRenew -> {
                    if (response.isTokenExpire == true) {
                        openLoginPage()
                    }
                }
            }
        }
    }

    private fun removeObserver() {
        updateUserViewModel.subscriptionResponse.removeObservers(this)
        viewModelUserLogin.subscribeResponse.removeObservers(this)
        viewModel.getUserDetailResponse.removeObservers(this)
        viewModel.logoutResponse.removeObservers(this)
       updateUserViewModel.deleteUserResponse.removeObservers(this)
        viewModel.incognitoModeResponse.removeObservers(this)
    }

    private fun setClickListeners() {
        binding.tvPhoneNumber.setOnClickListener(this)
        binding.tvEmail.setOnClickListener(this)
        binding.tvCurrentLocation.setOnClickListener(this)
//        binding.tvLanguage.setOnClickListener(this)
        binding.tvRateUs.setOnClickListener(this)
        binding.tvHelpSupport.setOnClickListener(this)
        binding.tvAboutUs.setOnClickListener(this)
        binding.tvShare.setOnClickListener(this)
        binding.tvPrivacyPolicy.setOnClickListener(this)
        binding.tvTermsServices.setOnClickListener(this)
        binding.tvBlockedUsers.setOnClickListener(this)
        binding.tvNotifications.setOnClickListener(this)
        binding.btnLogout.setOnClickListener(this)
        binding.btnDeleteAccount.setOnClickListener(this)
        binding.ivBack.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeObserver()
    }

    private fun shareApp() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
        )
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    private fun openUrl(url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    private fun openPlayStoreForRating() {
        val uri: Uri = Uri.parse("market://details?id=$packageName")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            startActivity(goToMarket)
        } catch (e: Exception) {
            openUrl("http://play.google.com/store/apps/details?id=$packageName")
        }
    }

    fun openPremiumBottomSheet(){
        val productId="android_premium"
        subscriptionResponse.let {
            if (it?.subscriptionPlans?.isNotEmpty() == true) {
                premiumBottomSheetDialog = PlanBottomSheetDialog(
                    subscriptionResponse?.subscriptionPlans?.get(0)!!,
                    Plan.DREAM_FRIEND_PREMIUM.bgResource,
                    object : SubscriptionDialogListener {
                        override fun onClick(month: Int) {
                            errorCount = 0
                            when (month) {
                                1 -> initiateSubscription(productId, subscriptionResponse?.subscriptionPlans?.get(0)?.planSchedule?.get(0)?.planId.toString(),isPremiumPlan = true)
                                3 -> initiateSubscription(productId, subscriptionResponse?.subscriptionPlans?.get(0)?.planSchedule?.get(1)?.planId.toString(),isPremiumPlan = true)
                                6 -> initiateSubscription(productId, subscriptionResponse?.subscriptionPlans?.get(0)?.planSchedule?.get(2)?.planId.toString(),isPremiumPlan = true)
//                                        1->initiateSubscription(productId,"dreamonepremium")
//                                        3->initiateSubscription(productId,"dreamthreepremium")
//                                        6->initiateSubscription(productId,"dreamsixpremium")
                            }

                        }

                    })
                premiumBottomSheetDialog.show(
                    supportFragmentManager,
                    "PlanBottomSheet"
                )
            }
        }
    }
    fun initiateSubscription(productId:String,planId: String, isPremiumPlan:Boolean=false)=
        SubscriptionUtil(
        this,
        productId =productId,
        planId = planId,
        object : InAppSubscriptionListener {
            override fun onSuccess(purchases: Purchase) {
                if(isPremiumPlan) {
                    subscribePremiumPlan(
                        planId,
                        purchases.purchaseTime,
                        purchases.purchaseToken
                    )
                }else{
                    subscribePlusPlan(
                        planId,
                        purchases.purchaseTime,
                        purchases.purchaseToken
                    )
                }
            }

            override fun onError(msg: String) {
                showToast(msg)
            }
        })

    fun subscribePremiumPlan(planId: String, planStartDate: Long, purchaseToken: String) {
        this.planId = planId
        viewModelUserLogin.subscribe(
            CreateSubscriberRequest(
                userId = viewModelUserLogin.getUser()?.userId,
                subscribedPlan = SubscribedPlan(
                    premiumPlan = listOf(
                        NormalPlan(
                            planId = planId,
                            planStartDate = planStartDate,
                            purchaseToken = purchaseToken
                        )
                    )
                )
            )
        )
    }
    fun subscribePlusPlan(planId: String, planStartDate: Long, purchaseToken: String) {
        this.planId = planId
        viewModelUserLogin.subscribe(
            CreateSubscriberRequest(
                userId = viewModelUserLogin.getUser()?.userId,
                subscribedPlan = SubscribedPlan(
                    plusPlan = listOf(
                        NormalPlan(
                            planId = planId,
                            planStartDate = planStartDate,
                            purchaseToken = purchaseToken
                        )
                    )
                )
            )
        )
    }

}