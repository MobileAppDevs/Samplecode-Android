package com.ongraph.jetpackloginsample.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.google.accompanist.pager.*
import com.ongraph.jetpackloginsample.R
import com.ongraph.jetpackloginsample.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
@Preview
@Composable
fun TelebuBottomSheetUi() {
    var audioVisible by rememberSaveable { mutableStateOf(false) }
    var speakerVisible by rememberSaveable { mutableStateOf(false) }
    var videoVisible by rememberSaveable { mutableStateOf(false) }
    val scaffoldState = rememberBottomSheetScaffoldState()
    BottomSheetScaffold(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        scaffoldState = scaffoldState,
        sheetPeekHeight = 90.dp,
        sheetContent = {

            LazyColumn {

                // the first item that is visible
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(White),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp, 12.dp))
                                .background(BottomSheetColor)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(
                                    modifier = Modifier
                                        .padding(top = 8.dp)
                                        .width(24.dp)
                                        .height(2.dp)
                                        .background(White)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(40.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    IconButton(
                                        onClick = { audioVisible = !audioVisible },
                                        modifier = Modifier.weight(1F)
                                    ) {
                                        Image(
                                            painter = painterResource(
                                                id = if (audioVisible) R.drawable.ic_speaker_inactive else R.drawable.ic_speaker_active
                                            ),
                                            contentDescription = null
                                        )
                                    }
                                    IconButton(
                                        onClick = { speakerVisible = !speakerVisible },
                                        modifier = Modifier.weight(1F)
                                    ) {
                                        Image(
                                            painter = painterResource(
                                                id = if (speakerVisible) R.drawable.ic_mic_mute else R.drawable.ic_unmute
                                            ),
                                            contentDescription = null
                                        )
                                    }
                                    IconButton(
                                        onClick = { videoVisible = !videoVisible },
                                        modifier = Modifier.weight(1F)
                                    ) {
                                        Image(
                                            painter = painterResource(
                                                id = if (videoVisible) R.drawable.ic_video_on else R.drawable.ic_video_disabled
                                            ),
                                            contentDescription = null
                                        )
                                    }
                                    IconButton(
                                        onClick = {},
                                        modifier = Modifier.weight(1F)
                                    ) {
                                        Image(
                                            painter = painterResource(
                                                id = if (videoVisible) R.drawable.ic_camera_flip_enabled else R.drawable.ic_camera_flip_disabled
                                            ),
                                            contentDescription = null
                                        )
                                    }
                                    IconButton(
                                        onClick = {},
                                        modifier = Modifier.weight(1F)
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_call_end),
                                            contentDescription = null
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }

                // remaining items
                item {
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .background(BottomSheetColor)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .height(45.dp)
                                        .clip(RoundedCornerShape(45.dp))
                                        .border(1.dp, itemStrokeBg, RoundedCornerShape(45.dp))
                                        .background(itemBg)
                                        .weight(1F),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_recording),
                                            contentDescription = null
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = "Record Meeting",
                                            textAlign = TextAlign.Center,
                                            color = White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Box(
                                    modifier = Modifier
                                        .height(45.dp)
                                        .clip(RoundedCornerShape(45.dp))
                                        .border(1.dp, itemStrokeBg, RoundedCornerShape(45.dp))
                                        .background(itemBg)
                                        .weight(1F),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_lock_open_12),
                                            contentDescription = null
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "Lock Room",
                                            textAlign = TextAlign.Center,
                                            color = White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                IconButton(
                                    onClick = {},
                                    modifier = Modifier
                                        .height(45.dp)
                                        .width(45.dp)
                                        .clip(RoundedCornerShape(45.dp))
                                        .background(White)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_info_icon),
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        TabLayout()

                        Spacer(modifier = Modifier
                            .height(1.dp)
                            .background(Color(0xFF797979)))
                    }
                }
            }
        },
    ) {

    }
}


@ExperimentalPagerApi
@Composable
fun TabLayout() {
    val pagerState = rememberPagerState(pageCount = 2)
    Column {
        Tabs(pagerState = pagerState)
        Spacer(modifier = Modifier.background(Color(0xFF797979)).fillMaxWidth().height(1.dp))
        TabsContent(pagerState = pagerState)
    }
}

@ExperimentalPagerApi
@Composable
fun Tabs(pagerState: PagerState) {
    val list = listOf(
        "Participants",
        "Notifications")
    val scope = rememberCoroutineScope()
    TabRow(
        modifier = Modifier.heightIn(max = 45.dp),
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = BottomSheetColor,
        contentColor = Color.White,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                height = 2.dp,
                color = Color(0xFF3166DE)
            )
        }
    ) {
        list.forEachIndexed { index, _ ->
            Tab(
                text = {
                    Text(
                        list[index],
                        color = if (pagerState.currentPage == index) Color.White else Color.LightGray,
                        fontWeight = FontWeight.Bold
                    ) },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(page = index)
                    }
                }
            )
        }
    }
}

@ExperimentalPagerApi
@Composable
fun TabsContent(pagerState: PagerState) {
    HorizontalPager(state = pagerState) {
            page ->
        when (page) {
            0 -> TabContentScreen(data = "Participants page")
            1 -> TabContentScreen(data = "Lock room page")
        }
    }
}

@Composable
fun TabContentScreen(data: String) {
    Column(
        modifier = Modifier.fillMaxSize().background(BottomSheetColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = data,
            style = MaterialTheme.typography.h5,
            color = White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}