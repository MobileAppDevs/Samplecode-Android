package com.ongraph.whatsappclone.ui.home.call.group

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ongraph.whatsappclone.common.CallButtonLayout
import com.ongraph.whatsappclone.common.GridUserLayout
import com.ongraph.whatsappclone.ui.theme.*


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PersonalVoiceCallScreen(navController: NavController?) {

    var timer by remember {
        mutableStateOf("Ringing...")
    }
    Handler(Looper.getMainLooper()).postDelayed({
         timer="04:37 mins"
    }, 3000)

    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize().onKeyEvent {
                if(it.key == Key.Escape) {
                    // Assuming you are using jetpack compose navigation
                    navController?.popBackStack()
                }
                true
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        val userList = mutableListOf<String>()
        userList.add("Rupesh Deshmukh")
//        userList.add("Chanchal Deshmukh")
//        userList.add("Nisha Saini")
//        userList.add("Priya")
//        userList.add("Pankaj sir")
        GridUserLayout(userList = userList, marginHorizontal = 20.dp)
        Text(
            text = timer,
            fontSize = 14.sp,
            fontWeight=FontWeight.Normal,
            textAlign=TextAlign.Center,
            color=Color.DarkGray,
            fontFamily = comfortaaRegular,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        )
    }
    CallButtonLayout(
        btnEndCallVisibility = true,
        btnNormalCallVisibility = timer == "Ringing...",
        btnSpeakerVisibility=timer != "Ringing...",
        navController = navController
    )
}

@Preview
@Composable
fun PersonalVoiceCallScreenPreview() {
    PersonalVoiceCallScreen(null)
}











