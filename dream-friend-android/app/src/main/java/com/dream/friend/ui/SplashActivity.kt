package com.dream.friend.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import com.dream.friend.R
import com.dream.friend.common.Constants
import com.dream.friend.common.activityBindings
import com.dream.friend.common.connectivity.base.ConnectivityProvider
import com.dream.friend.databinding.ActivitySplashBinding
import com.dream.friend.ui.home.HomeActivity
import com.dream.friend.ui.login.LoginActivity
import com.dream.friend.ui.onboarding.CreateProfileActivity
import com.dream.friend.ui.viewModel.UserLoginViewModel
import com.dream.friend.util.AppUtil.hideStatusBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity(),
    ConnectivityProvider.ConnectivityStateListener {

    private val splashBinding: ActivitySplashBinding by activityBindings(R.layout.activity_splash)
    private val viewModel: UserLoginViewModel by viewModels()

    private val provider: ConnectivityProvider by lazy { ConnectivityProvider.createProvider(this) }
    private var snackBar: Snackbar? = null

    companion object {
        fun ConnectivityProvider.NetworkState.hasInternet(): Boolean {
            return (this as? ConnectivityProvider.NetworkState.ConnectedState)?.hasInternet == true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusBar()
        setContentView(splashBinding.root)
        val view = findViewById<View>(android.R.id.content)
        if (view != null) {
            snackBar =
                Snackbar.make(view, Constants.NoInternetConnection, Snackbar.LENGTH_INDEFINITE)
        }

        val cls = if (viewModel.getUser() == null) LoginActivity::class.java
        else if (viewModel.getUser()?.isNew == true) CreateProfileActivity::class.java
        else HomeActivity::class.java

        Handler(Looper.getMainLooper()).postDelayed({ splashBinding.progress.progress += 10; Handler(Looper.getMainLooper()).postDelayed({ splashBinding.progress.progress += 10; Handler(Looper.getMainLooper()).postDelayed({ splashBinding.progress.progress += 10;Handler(Looper.getMainLooper()).postDelayed({ splashBinding.progress.progress += 10;Handler(Looper.getMainLooper()).postDelayed({ splashBinding.progress.progress += 10;Handler(Looper.getMainLooper()).postDelayed({ splashBinding.progress.progress += 10; Handler(Looper.getMainLooper()).postDelayed({ splashBinding.progress.progress += 10; Handler(Looper.getMainLooper()).postDelayed({ splashBinding.progress.progress += 10; Handler(Looper.getMainLooper()).postDelayed({ splashBinding.progress.progress += 10; Handler(Looper.getMainLooper()).postDelayed({ splashBinding.progress.progress += 10
            startActivity(cls) }, 300) }, 300) }, 300) }, 300) }, 300)}, 300) }, 300) }, 300) }, 300) }, 300)
    }

    private fun startActivity(cls: Class<out AppCompatActivity>) {
        Intent(
            this,
            cls
        ).also {
            startActivity(it)
            finish()
        }
    }

    override fun onStateChange(state: ConnectivityProvider.NetworkState) {
        val hasInternet = state.hasInternet()
        if (snackBar != null) {
            if (hasInternet) {
                snackBar!!.dismiss()
            } else {
                snackBar!!.show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        provider.addListener(this)
    }

    override fun onStop() {
        super.onStop()
        provider.removeListener(this)
    }
}