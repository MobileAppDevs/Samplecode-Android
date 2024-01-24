package com.ongraph.whatsappclone.ui.home.subscreens.call.chatscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.ongraph.whatsappclone.constants.screens.Screens.CHAT_SCREEN
import com.ongraph.whatsappclone.model.ChatOrGroupModel
import com.ongraph.whatsappclone.ui.home.FabButtons
import kotlin.random.Random

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ChatsListScreen(navController: NavController, pagerState: PagerState) {
    val list =mutableListOf<ChatOrGroupModel>()
    for (i in 0..20) {
        val isGroup= Random.nextBoolean()
        val title=if(isGroup) "group $i" else "personal Chat $i"
        list.add(
            ChatOrGroupModel(
                title = title,
                lastMessage = "last message $i...",
                isGroup =isGroup,
                dpUrl = "",
                time =""
            )
        )
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        LazyColumn {
            items(list) { item ->
                item.isGroup?.let {
                    chatItem(
                        title=item.title,
                        lastMessage=item.lastMessage,
                        dpUrl="https://picsum.photos/200/300",
                        isGroup= it,
                        navController=navController
                    )
                }
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
fun chatItem(
    title:String,
    lastMessage:String,
    dpUrl:String="",
    isGroup:Boolean,
    navController: NavController
) {
    Row(
        Modifier.clickable {
                navController.navigate(CHAT_SCREEN)
        }
    ) {
        Column(
            Modifier
                .padding(10.dp)
                .align(Alignment.CenterVertically)
        ) {
            CircleImage(
                imageUrl = dpUrl,
                placeholder = painterResource(id = R.drawable.default_profile)
            )
        }
        Column(
            Modifier
                .padding(vertical = 20.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = title, color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold, fontStyle = FontStyle.Italic, modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = lastMessage, color = Color.LightGray,
                fontWeight = FontWeight.Normal,
                fontSize=14.sp,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Preview
@Composable
fun ChatListFabButtonPreview(){
    FabButtons(pagerState = rememberPagerState(pageCount = 3, initialPage = 0),true)
}
@OptIn(ExperimentalPagerApi::class)
@Preview
@Composable
fun ChatListScreenPreview() {
    val pager = rememberPagerState(pageCount = 3, initialPage = 0)
    ChatsListScreen(navController = NavController(LocalContext.current), pager)
}