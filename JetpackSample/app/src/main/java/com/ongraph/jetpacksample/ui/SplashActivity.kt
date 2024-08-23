package com.ongraph.jetpacksample.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ongraph.jetpacksample.ui.theme.JetpackSampleTheme
import com.ongraph.jetpacksample.MainActivity
import com.ongraph.jetpacksample.R
import com.ongraph.jetpacksample.common.PreferenceHandler
import com.ongraph.jetpacksample.ui.theme.Blue

/**
 * SplashActivity displays a splash screen when the app is launched.
 * It checks user login status and navigates to the appropriate activity after a delay.
 * */
@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SetContent()
        }

        val pref = PreferenceHandler(this)

        // Delay navigation by 3000 milliseconds (3 seconds)
        Handler(mainLooper).postDelayed({
            if (pref.readBoolean(pref.hasLogin, false)) {
                Intent(this, HomeActivity::class.java).apply {
                    startActivity(this)
                    finish()
                }
            } else {
                Intent(this, MainActivity::class.java).apply {
                    startActivity(this)
                    finish()
                }
            }
        }, 3000)
    }

    /**
     * Preview composable function to display the splash screen content.
     */
    @Preview
    @Composable
    private fun SetContent() {
        JetpackSampleTheme {
            SplashUi(
                modifier = Modifier.fillMaxSize().background(Blue)
            )
        }
    }
}

/**
 * Composable function to display the splash screen UI.
 *
 * @param modifier Modifier to adjust the appearance and layout of the UI element.
 */
@Composable
fun SplashUi(modifier: Modifier = Modifier) {
    Box (modifier = modifier) {
        Image(
            painterResource(id = R.drawable.top_white_hanger_foreground),
            null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.wrapContentSize()
                .size(200.dp, 180.dp)
                .padding(top = 26.dp)
                .align(Alignment.Center)
        )
    }
}