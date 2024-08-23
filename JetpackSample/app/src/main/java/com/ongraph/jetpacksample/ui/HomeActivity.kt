package com.ongraph.jetpacksample.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ongraph.jetpacksample.ui.theme.JetpackSampleTheme
import com.ongraph.jetpacksample.R
import com.ongraph.jetpacksample.ui.theme.White

/**
 * home screen to display data after login or registration
 * */
class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            showContent()
        }
    }

    /**
     * Composable function to display the main content of the home screen.
     *
     * - The UI is wrapped in the `JetpackSampleTheme` to apply the app's theme.
     * - A `Scaffold` is used to provide a basic structure for the screen, with `innerPadding` available for further customization.
     * - The `BackgroundUi()` composable is displayed at the back to create the background visuals.
     * - The `HomeUi()` composable is layered on top, providing the main UI elements for the home screen.
     */
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Preview
    @Composable
    private fun showContent() {
        JetpackSampleTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Column(modifier = Modifier.fillMaxSize()) {
                    BackgroundUi()
                }
                HomeUi(
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    }
}

/**
 * Composable function for rendering the UI components specific to the home screen.
 *
 * - The main layout is a `Box`, allowing content to be overlaid on top of each other.
 * - The `Text` composable displays a centered "Home screen" title at the top of the screen with padding.
 * - An `Image` composable is placed just below the title, aligned to the top center, showing a decorative image.
 *
 * @param modifier A `Modifier` to customize the layout, with a default empty modifier.
 */
@Composable
fun HomeUi(modifier: Modifier = Modifier) {
    Box (modifier = modifier) {

        Text(
            text = "Home screen",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 20.dp),
            color = White,
            fontSize = 40.sp
        )

        Image(
            painterResource(id = R.drawable.top_white_hanger_foreground),
            null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .wrapContentSize()
                .padding(top = 26.dp)
                .align(Alignment.TopCenter)
        )
    }
}