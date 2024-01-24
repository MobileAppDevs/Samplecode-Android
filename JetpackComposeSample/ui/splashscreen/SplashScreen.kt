package com.ongraph.whatsappclone.ui.splashscreen

import android.graphics.Paint.Align
import android.view.animation.OvershootInterpolator
import android.window.SplashScreen
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.navigation.NavController
import com.ongraph.whatsappclone.R
import com.ongraph.whatsappclone.constants.screens.Screens.HOME_SCREEN
import com.ongraph.whatsappclone.ui.theme.GreenTheme
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(navController: NavController?) {
    val logoScale = remember {
        androidx.compose.animation.core.Animatable(1f)
    }

    // AnimationEffect
    LaunchedEffect(key1 = true) {
        logoScale.animateTo(
            targetValue = 4f,
            animationSpec = tween(
                durationMillis = 2000,
                easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                })
        )
        delay(3000L)
        navController?.popBackStack()
        navController?.navigate(HOME_SCREEN)
    }

    // Image
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column() {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(logoScale.value)
            )
            Spacer(modifier = Modifier.height(logoScale.value * 15.dp))
            Text(
                text = "Whatsapp",
                fontSize = (logoScale.value * 10).sp,
                fontWeight= FontWeight.SemiBold,
                color = GreenTheme,
                textAlign=TextAlign.Center,
                modifier = Modifier.align(CenterHorizontally)
            )
        }


    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    SplashScreen(null)
}


