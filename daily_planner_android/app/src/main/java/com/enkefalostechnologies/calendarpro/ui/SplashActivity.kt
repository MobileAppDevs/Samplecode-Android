package com.enkefalostechnologies.calendarpro.ui

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Observer
import com.amplifyframework.datastore.generated.model.SubscriptionStatus
import com.amplifyframework.datastore.generated.model.SubscriptionType
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchaseHistoryRecord
import com.enkefalostechnologies.calendarpro.MainActivity
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.constant.Constants.FIREBASE_EVENT_APP_INSTALLS
import com.enkefalostechnologies.calendarpro.constant.StorageConstant
import com.enkefalostechnologies.calendarpro.databinding.ActivitySplashBinding
import com.enkefalostechnologies.calendarpro.router.Router.navigateToGetStartedContactPermissionScreen
import com.enkefalostechnologies.calendarpro.router.Router.navigateToGetStartedDisplayOverlayPermissionScreen
import com.enkefalostechnologies.calendarpro.router.Router.navigateToHome
import com.enkefalostechnologies.calendarpro.ui.base.BaseActivity
import com.enkefalostechnologies.calendarpro.ui.viewModel.ProfileActivityViewModel
import com.enkefalostechnologies.calendarpro.ui.viewModel.SplashActivityViewModel
import com.enkefalostechnologies.calendarpro.util.AmplifyDataModelUtil
import com.enkefalostechnologies.calendarpro.util.AmplifyUtil
import com.enkefalostechnologies.calendarpro.util.AppUtil.hideStatusBar
import com.enkefalostechnologies.calendarpro.util.AppUtil.isNetworkAvailable
import com.enkefalostechnologies.calendarpro.util.AppUtil.showToast
import com.enkefalostechnologies.calendarpro.util.Extension.gone
import com.enkefalostechnologies.calendarpro.util.Extension.visible
import com.enkefalostechnologies.calendarpro.util.SubscriptionCheckUtil
import com.enkefalostechnologies.calendarpro.util.SubscriptionHelper
import com.enkefalostechnologies.calendarpro.util.SubscriptionHelper.getNextRenewingDate
import com.google.firebase.analytics.FirebaseAnalytics
import java.util.Date


class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {
    val viewModel: SplashActivityViewModel by viewModels { SplashActivityViewModel.Factory }
    override fun onCreate(savedInstanceState: Bundle?) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S_V2) {
            val splashScreen = installSplashScreen()
            super.onCreate(savedInstanceState)
            splashScreen.setKeepOnScreenCondition { false }
        } else {
            super.onCreate(savedInstanceState)
        }


    }

    override fun onViewBindingCreated() {
        hideStatusBar()
        if(!isNewInstall()){
            Log.d("newUser","isNewInstall()=>${isNewInstall()}")
            if (isUserLoggedIn()) {
                when (getSubscriptionStatus()) {
                    SubscriptionStatus.EXPIRED, SubscriptionStatus.CANCELED -> {
                        /** check plan is renewal case**/
                        checkForRenewal()
                    }

                    SubscriptionStatus.ACTIVE -> {
                        /*** check for expired case  **/
                        checkForPlanExpiration()
                    }

                    else -> performSplashAction()
                }
            } else performSplashAction()
        }
        //startActivity(Intent(this,MainActivity::class.java))
    }


    val isInitialSyncStartedObserver = Observer<Boolean> { isSyncStarted ->
        binding.tvSyncStatus.visible()
        Log.d("splashcheck", "isInitialSyncStarted=>$isSyncStarted")
    }
    val isInitialSyncCompletedObserver = Observer<Boolean> { isSyncCompleted ->
        Log.d("splashcheck", "isInitialSyncCompleted=>$isSyncCompleted")
        if(isNewInstall()) {
            Log.d("newUser","isNewInstall()=>${isNewInstall()}")
            if (isUserLoggedIn()) {
                when (getSubscriptionStatus()) {
                    SubscriptionStatus.EXPIRED, SubscriptionStatus.CANCELED -> {
                        /** check plan is renewal case**/
                        checkForRenewal()
                    }

                    SubscriptionStatus.ACTIVE -> {
                        /*** check for expired case  **/
                        checkForPlanExpiration()
                    }

                    else -> performSplashAction()
                }
            } else performSplashAction()
        }
    }
    val initialSyncingModelObserver = Observer<String> {model ->
    }

    override fun addObserver() {
        viewModel.isInitialSyncStarted.observe(this, isInitialSyncStartedObserver)
        viewModel.isInitialSyncCompleted.observe(this, isInitialSyncCompletedObserver)
        viewModel.initialSyncingModel.observe(this, initialSyncingModelObserver)
    }

    override fun removeObserver() {
        viewModel.isInitialSyncStarted.removeObserver(isInitialSyncStartedObserver)
        viewModel.isInitialSyncCompleted.removeObserver(isInitialSyncCompletedObserver)
        viewModel.initialSyncingModel.removeObserver(initialSyncingModelObserver)
    }

    override fun onClick(p0: View?) {

    }

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean {

        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    fun getProductId(subscriptionType: SubscriptionType) = when (subscriptionType) {
        SubscriptionType.MONTHLY -> com.enkefalostechnologies.calendarpro.SubscriptionType.MONTHLY.productId
        SubscriptionType.YEARLY -> com.enkefalostechnologies.calendarpro.SubscriptionType.YEARLY.productId
        else -> ""
    }

    private fun hasDisplayOverlayPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                return false
            }
        }
        return true // on lower OS versions granted during apk installation
    }

    override fun onDestroy() {
        if (isNewInstall()) {
            val params = Bundle()
            params.putString("device_id", deviceId())
            params.putString("user_id", getUserEmail())
            firebaseAnalytics.logEvent(FIREBASE_EVENT_APP_INSTALLS, params)
            setNewInstall(false)
        }
        super.onDestroy()
    }


    fun checkForPlanExpiration() {
        amplifyDataModelUtil.getUserDetails(getUserEmail(), {
            runOnUiThread {
                val user = it.next()
                if (Date().after(user.subscriptionValidUpTo.toDate())) {
                    /** plan is Expired check for renewal case **/
                    checkForRenewal()
                } else {
                    /** plan is active **/
                    runOnUiThread {
                        performSplashAction()
                    }
                }
            }
        }, {
            runOnUiThread {
                performSplashAction()
            }
        })
    }

    fun checkForPlanCancellation() {
        if (isNetworkAvailable()) {
            SubscriptionCheckUtil(this@SplashActivity,
                object : SubscriptionCheckUtil.SubscriptionStateListener {
                    override fun onPurchaseHistoryFetched(purchases: MutableList<Purchase>) {
                        val filteredData = purchases.filter {
                            it.packageName == packageName && it.purchaseState == Purchase.PurchaseState.PURCHASED && it.products.contains(
                                getProductId(getSubscribedPlan())
                            )
                        }
                        if (filteredData.isNotEmpty()) {
                            /*** renewal success save new expiry date ***/
                            updatePlanPlanRenewed(
                                filteredData[0].purchaseTime.getNextRenewingDate(
                                    getSubscribedPlan()
                                )
                            )
                        } else {
                            /*** confirms plan expired **/
                            updatePlanExpiredToDatabase()
                        }
                    }

                    override fun onError() {
                        /** save plan expired to database **/
                        updatePlanExpiredToDatabase()
                    }

                }).establishConnectionForCheckingSubscription()
        } else {
            /*** save plan expired to database**/
            updatePlanExpiredToDatabase()
        }
    }

    fun checkForRenewal() {
        if (isNetworkAvailable()) {
            SubscriptionCheckUtil(this@SplashActivity,
                object : SubscriptionCheckUtil.SubscriptionStateListener {
                    override fun onPurchaseHistoryFetched(purchases: MutableList<Purchase>) {
                        val filteredData = purchases.filter {
                            it.packageName == packageName && it.purchaseState == Purchase.PurchaseState.PURCHASED && it.products.contains(
                                getProductId(getSubscribedPlan())
                            )
                        }
                        if (filteredData.isNotEmpty()) {
                            /*** renewal success save new expiry date ***/
                            updatePlanPlanRenewed(
                                filteredData[0].purchaseTime.getNextRenewingDate(
                                    getSubscribedPlan()
                                )
                            )
                        } else {
                            /*** confirms plan expired **/
                            updatePlanExpiredToDatabase()
                        }
                    }

                    override fun onError() {
                        /** save plan expired to database **/
                        updatePlanExpiredToDatabase()
                    }

                }).establishConnectionForCheckingSubscription()
        } else {
            /*** save plan expired to database**/
            updatePlanExpiredToDatabase()
        }
    }


    fun updatePlanExpiredToDatabase() {
        amplifyDataModelUtil.setSubscriptionStatus(
            getUserEmail(),
            SubscriptionStatus.EXPIRED,
            {
                runOnUiThread {
                    setSubscriptionStatus(SubscriptionStatus.EXPIRED)
                    performSplashAction()
                }
            },
            {
                runOnUiThread {
                    performSplashAction()
                }
            })
    }

    fun updatePlanPlanRenewed(newExpiryDate: Date) {
        amplifyDataModelUtil.setSubscriptionStatus(
            getUserEmail(),
            SubscriptionStatus.ACTIVE,
            newExpiryDate,
            {
                runOnUiThread {
                    setSubscriptionStatus(SubscriptionStatus.ACTIVE)
                    performSplashAction()
                }
            },
            {
                runOnUiThread {
                    performSplashAction()
                }
            })
    }


    private fun performSplashAction() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.tvSyncStatus.gone()
            if (
                !hasPermissions(this, Manifest.permission.READ_PHONE_STATE) ||
                !hasPermissions(this, Manifest.permission.CALL_PHONE) ||
                !hasPermissions(this, Manifest.permission.ANSWER_PHONE_CALLS) ||
                !hasDisplayOverlayPermission()
            ) {
                navigateToGetStartedContactPermissionScreen()
            } else {
                navigateToHome(binding.imageView).also { finish() }
            }

        }, 2000)
    }

}