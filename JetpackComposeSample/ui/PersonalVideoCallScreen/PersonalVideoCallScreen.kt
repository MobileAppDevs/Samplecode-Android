package com.ongraph.whatsappclone.ui.home.call.group

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ongraph.whatsappclone.R
import com.ongraph.whatsappclone.common.CallButtonLayout
import com.ongraph.whatsappclone.common.GridUserLayout
import com.ongraph.whatsappclone.ui.theme.*


@Composable
fun PersonalVideoCallScreen(navController: NavController) {
    var timer by remember {
        mutableStateOf("Ringing...")
    }
    Handler(Looper.getMainLooper()).postDelayed({
        timer="04:37 mins"
    }, 3000)
    AsyncImage(model = ImageRequest.Builder(LocalContext.current)
        .data("https://picsum.photos/200/300")
        .crossfade(true)
        .build() ,
        placeholder = painterResource(id = R.drawable.default_profile),
        contentDescription = "",
        contentScale = ContentScale.FillBounds,
        modifier = Modifier.fillMaxSize().blur(16.dp)
    )
    Column(
        modifier = Modifier
            .fillMaxSize(),
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
        btnVideoCallVisibility = timer == "Ringing...",
        btnSpeakerVisibility=timer != "Ringing...",
        navController = navController
    )
}











