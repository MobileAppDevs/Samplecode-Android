package com.ongraph.jetpacksample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.ongraph.jetpacksample.common.Utils.showToast
import com.ongraph.jetpacksample.common.connectivity.base.ConnectivityProvider
import com.ongraph.jetpacksample.ui.BackgroundUi
import com.ongraph.jetpacksample.ui.MainUi
import com.ongraph.jetpacksample.ui.functions.LoginFunctions.responseLogin
import com.ongraph.jetpacksample.ui.functions.SignUpFunctions.responseSignUp
import com.ongraph.jetpacksample.ui.theme.JetpackSampleTheme
import com.ongraph.jetpacksample.ui.viewModel.LoginViewModel
import com.ongraph.jetpacksample.ui.viewModel.SignUpViewModel

class MainActivity : ComponentActivity(), ConnectivityProvider.ConnectivityStateListener {

    private var loginViewModel: LoginViewModel? = null
    private var signUpViewModel: SignUpViewModel? = null

    private val provider: ConnectivityProvider by lazy {
        ConnectivityProvider.createProvider(this@MainActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        signUpViewModel = ViewModelProvider(this)[SignUpViewModel::class.java]

        setContent {
            JetpackSampleTheme {
                Column(modifier = Modifier.fillMaxSize()) {
                    BackgroundUi()
                }
                MainUi(loginViewModel, signUpViewModel)
//                TelebuBottomSheetUi()
            }
        }

        responseLogin(loginViewModel, this)
        responseSignUp(signUpViewModel, this)
    }

    override fun onStart() {
        super.onStart()
        provider.addListener(this)
    }

    override fun onStop() {
        super.onStop()
        provider.removeListener(this)
    }

    override fun onStateChange(state: ConnectivityProvider.NetworkState) {
        val hasInternet = state.hasInternet()
        if (!hasInternet) {
            showToast("No Internet Connection")
        }
    }

    companion object {
        fun ConnectivityProvider.NetworkState.hasInternet(): Boolean {
            return (this as? ConnectivityProvider.NetworkState.ConnectedState)?.hasInternet == true
        }
    }

    @Deprecated("Deprecated in Java", ReplaceWith("finish()"))
    override fun onBackPressed() {
        //super.onBackPressed()
        finish()
    }
}