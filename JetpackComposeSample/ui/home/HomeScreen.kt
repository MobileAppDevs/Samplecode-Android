package com.ongraph.whatsappclone.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.google.accompanist.pager.*
import com.ongraph.whatsappclone.R
import com.ongraph.whatsappclone.common.CircularButton
import com.ongraph.whatsappclone.ui.home.subscreens.call.chatscreen.CallListScreen
import com.ongraph.whatsappclone.ui.home.subscreens.call.chatscreen.ChatsListScreen
import com.ongraph.whatsappclone.ui.home.subscreens.call.chatscreen.StatusListScreen
import com.ongraph.whatsappclone.ui.theme.BgGreen
import com.ongraph.whatsappclone.ui.theme.GreenTheme
import com.ongraph.whatsappclone.ui.theme.textColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalUnitApi::class, ExperimentalComposeUiApi::class)
@ExperimentalPagerApi
@Composable
fun HomeScreen(navController: NavController) {
    val pagerState = rememberPagerState(pageCount = 3)
    Column(
        modifier = Modifier
            .background(Color.White)
            .onKeyEvent {
                if (it.key == Key.Escape) {
                    // Assuming you are using jetpack compose navigation
                    navController.popBackStack()
                }
                true
            }
    ) {
        TopTitle()
        Tabs(pagerState = pagerState)
        TabsContent(pagerState = pagerState, navController)
    }
}

@ExperimentalPagerApi
@Composable
fun Tabs(pagerState: PagerState) {
    val list = listOf(
        "CHATS",
        "STATUS",
        "CALLS"

    )
    val scope = rememberCoroutineScope()
    TabRow(
        modifier = Modifier.padding(horizontal = 10.dp),
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = Color.White,
        contentColor = GreenTheme,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                height = 3.dp,
                color = GreenTheme,
            )
        }
    ) {
        list.forEachIndexed { index, pair ->
            Tab(
                text = {
                    Text(
                        list[index],
                        fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.SemiBold,
                        fontFamily = if (pagerState.currentPage == index) FontFamily(Font(R.font.comfortaa_bold)) else FontFamily(
                            Font(R.font.comfortaa_regular)
                        ),
                        color = if (pagerState.currentPage == index) GreenTheme else textColor
                    )
                },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
}

@ExperimentalPagerApi
@Composable
fun TabsContent(pagerState: PagerState, navController: NavController) {
    HorizontalPager(state = pagerState) { page ->
        when (page) {
            0 -> ChatsListScreen(navController = navController,pagerState)
            1 -> StatusListScreen(navController = navController,pagerState)
            2 -> CallListScreen(navController = navController,pagerState)
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun FabButtons(
    pagerState: PagerState= rememberPagerState(pageCount = 3, initialPage = 0),
    visibility: Boolean = false,
    fabBtnRadius:Dp= 25.dp,
    iconHeight:Dp= 20.dp,
    iconWidth :Dp= 20.dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        Row(
            horizontalArrangement = Arrangement.End
        ) {
            when (pagerState.currentPage) {
                0 -> {
                    if (visibility) {
                        CircularButton(
                            icon = R.drawable.ic_message_24, bgColor = GreenTheme,
                            circleRadius = fabBtnRadius,
                            iconHeight = iconHeight,
                            iconWidth = iconWidth,
                            onClick = {

                            }
                        )
                    }
                }
                1 -> {
                    if (visibility) {
                        CircularButton(
                            icon = R.drawable.ic_edit_green_24, bgColor =BgGreen,
                            circleRadius = fabBtnRadius,
                            iconHeight = iconHeight,
                            iconWidth = iconWidth,
                            onClick = {

                            }
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        CircularButton(
                            icon = R.drawable.drawable_camera_white, bgColor = GreenTheme,
                            circleRadius = fabBtnRadius,
                            iconHeight = iconHeight,
                            iconWidth = iconWidth,
                            onClick = {

                            }
                        )
                    }
                }
                2 -> {
                    if (visibility) {
                        CircularButton(
                            icon = R.drawable.drawable_call_white, bgColor = GreenTheme,
                            circleRadius = fabBtnRadius,
                            iconHeight = iconHeight,
                            iconWidth = iconWidth,
                            onClick = {

                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = NavController(LocalContext.current))
}

@Composable
fun TopTitle(){
    Column(modifier = Modifier
        .padding(top = 30.dp, start = 20.dp, bottom = 10.dp)) {
        Text(text = "WhatsUp",
            color = Color.Black,
            fontSize = 20.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold
        )
    }
}