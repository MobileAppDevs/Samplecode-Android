package com.ongraph.whatsappclone

import android.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ongraph.whatsappclone.common.Utils.showToast
import com.ongraph.whatsappclone.common.connectivity.base.ConnectivityProvider
import com.ongraph.whatsappclone.navigation.Navigation
import com.ongraph.whatsappclone.ui.theme.WhatsAppCloneTheme


class MainActivity : ComponentActivity(), ConnectivityProvider.ConnectivityStateListener {

    var backPressedTime: Long = 0
    lateinit var navController:NavHostController
    private val provider: ConnectivityProvider by lazy {
        ConnectivityProvider.createProvider(this@MainActivity)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhatsAppCloneTheme {
//                PersonalVoiceCallUi()
//                SplashScreen( )

                Surface(color = Color.White, modifier = Modifier.fillMaxSize()) {
                    navController= rememberNavController()
                    Navigation(navController)
                }
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
        if (navController.previousBackStackEntry== null || navController.currentBackStackEntry == null){
            if (backPressedTime + 3000 > System.currentTimeMillis()) {
                super.onBackPressed()
                finish()
            } else {
                showToast("Press back again to leave the app.")
            }
            backPressedTime = System.currentTimeMillis()
        }else{
            navController.popBackStack()
        }
    }
}
