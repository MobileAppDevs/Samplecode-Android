package com.ongraph.whatsappclone.ui.home.subscreens.call.chatscreen

import android.telecom.InCallService.VideoCall
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.ongraph.whatsappclone.R
import com.ongraph.whatsappclone.common.CircleImage
import com.ongraph.whatsappclone.ui.home.FabButtons
import com.ongraph.whatsappclone.ui.theme.BgGreen
import com.ongraph.whatsappclone.ui.theme.comfortaaBold
import com.ongraph.whatsappclone.ui.theme.comfortaaRegular
import kotlin.random.Random
import kotlin.random.Random.Default.nextInt

@OptIn(ExperimentalPagerApi::class)
@Composable
fun CallListScreen(navController: NavController, pagerState: PagerState) {
    val callList = mutableListOf<String>()
    for (i in 0..10) {
        callList.add("Call Name $i")
    }
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        callList.map {
            val callType = mutableListOf("VoiceCall", "VideoCall")
            itemCall(
                name = it.toString(),
                isIncomingCall = Random.nextBoolean(),
                callType = callType[Random.nextInt(callType.size)]
            )
        }
    }
    FabButtons(
        pagerState = pagerState,
        visibility = true,
        modifier = Modifier
            .fillMaxSize()
            .padding(end = 20.dp, bottom = 20.dp)
    )
}

@Preview
@Composable
fun ItemCallPreview() {
    itemCall()
}

@Composable
fun itemCall(
    name: String = "Call 1",
    time: String = "12 minutes ago",
    imageUrl: String = "https://picsum.photos/200/300",
    isIncomingCall: Boolean = false,
    callType: String = "VoiceCall"
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleImage(
            imageUrl = imageUrl
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(Modifier.weight(1F)) {
            Text(
                text = name,
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = comfortaaBold,
                modifier = Modifier.fillMaxWidth()
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = if (isIncomingCall) R.drawable.drawable_incoming_red else R.drawable.ic_outgoing_green_call_icon_24),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp, 14.dp)
                )
                Text(
                    text = time, color = Color.LightGray,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    fontFamily = comfortaaRegular,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        IconButton(
            modifier = Modifier
                .height(35.dp)
                .width(35.dp),
            onClick = {

            }) {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
                    .background(BgGreen)
            )
            Image(
                painter = painterResource(id = if (callType == "VoiceCall") R.drawable.drawable_call_10c17d else R.drawable.drawable_video_10c17d),
                contentDescription = null,
                modifier = Modifier.size(25.dp, 25.dp)
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Preview
@Composable
fun CallListFabButtonPreview() {
    FabButtons(pagerState = rememberPagerState(pageCount = 3, initialPage = 2), true)
}

@OptIn(ExperimentalPagerApi::class)
@Preview
@Composable
fun CallListScreenPreview() {
    val pager = rememberPagerState(pageCount = 3, initialPage = 2)
    CallListScreen(navController = NavController(LocalContext.current), pager)
}
