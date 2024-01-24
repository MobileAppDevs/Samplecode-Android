package com.ongraph.whatsappclone.ui.home.subscreens.call.chatscreen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
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
import com.ongraph.whatsappclone.model.ChatOrGroupModel
import com.ongraph.whatsappclone.ui.home.FabButtons
import com.ongraph.whatsappclone.ui.theme.GreenTheme
import com.ongraph.whatsappclone.ui.theme.comfortaaBold
import com.ongraph.whatsappclone.ui.theme.comfortaaRegular
import kotlin.random.Random

@OptIn(ExperimentalPagerApi::class)
@Composable
fun StatusListScreen(navController: NavController, pagerState: PagerState) {
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val recentStatusList = mutableListOf<String>()
        val viewedStatusList = mutableListOf<String>()
        val mutedStatusList = mutableListOf<String>()
        for (i in 0..2) {
            recentStatusList.add("Recent Name $i")

        }
        for (i in 0..2) {
            viewedStatusList.add("Viewed Name $i")
        }
        for (i in 0..3) {
            mutedStatusList.add("Muted Name $i")

        }
        myStatusView()
        if (recentStatusList.size > 0) {
            Text(
                text = "Recent Updates", color = Color.Gray,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.fillMaxWidth()
            )
            recentStatusList.map {
                itemStatus(name = it.toString())
            }
        }
        if (viewedStatusList.size > 0) {
            Text(
                text = "Viewed Updates", color = Color.Gray,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.fillMaxWidth()
            )
            viewedStatusList.map {
                itemStatus(name = it.toString(), mode = "viewed")
            }
        }
        if (mutedStatusList.size > 0) {
            Text(
                text = "Muted Updates", color = Color.Gray,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.fillMaxWidth()
            )
            mutedStatusList.map {
                itemStatus(name = it, mode = "muted")
            }
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


@Composable
fun myStatusView() {
    Row(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(modifier = Modifier.wrapContentSize(), contentAlignment = Alignment.BottomEnd) {
            CircleImage()
            Image(
                painter = painterResource(id = R.drawable.ic_add_circle_outline_24),
                contentDescription = null,
                modifier = Modifier
                    .width(15.dp)
                    .height(15.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
            )
        }


        Spacer(modifier = Modifier.width(10.dp))
        Column(Modifier.weight(1F)) {
            Text(
                text = "My Status",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Tap to add status update", color = Color.LightGray,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun itemStatus(
    name: String = "My Status 1",
    time: String = "12 minutes ago",
    imageUrl: String = "https://picsum.photos/200/300",
    mode: String = "Recent"
) {

    Row(
        modifier = if (mode == "muted") {
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 10.dp, vertical = 10.dp)
                .alpha(0.3F)
        } else {
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 10.dp, vertical = 10.dp)
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleImage(
            imageUrl = imageUrl,
            strokeWidth = if (mode == "Recent"|| mode == "viewed") 2.dp else 0.dp,
            strokeColor = if (mode == "viewed") Color.Gray else GreenTheme,
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(Modifier.weight(1F)) {
            Text(
                text = name,
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily= comfortaaBold,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = time, color = Color.LightGray,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                fontFamily= comfortaaRegular,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@Preview
@Composable
fun MyStatusViewPreview() {
    myStatusView()
}

@Preview
@Composable
fun ItemStatusPreview() {
    itemStatus(mode = "Muted")
}

@OptIn(ExperimentalPagerApi::class)
@Preview
@Composable
fun StatusListFabButtonPreview() {
    FabButtons(pagerState = rememberPagerState(pageCount = 3, initialPage = 1), true)
}

@OptIn(ExperimentalPagerApi::class)
@Preview
@Composable
fun StatusListScreenPreview() {

    StatusListScreen(
        navController = NavController(LocalContext.current),
        rememberPagerState(pageCount = 3, initialPage = 1)
    )
}
