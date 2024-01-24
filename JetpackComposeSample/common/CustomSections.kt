package com.ongraph.whatsappclone.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ongraph.whatsappclone.R
import com.ongraph.whatsappclone.ui.theme.BgGreen
import com.ongraph.whatsappclone.ui.theme.BgRed

@Composable
fun CallButtonLayout(
    btnVideoCallVisibility: Boolean = false,
    btnNormalCallVisibility: Boolean = false,
    btnSpeakerVisibility: Boolean = false,
    btnEndCallVisibility: Boolean = false,
    navController: NavController?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Row(
            horizontalArrangement = Arrangement.Center
        ) {

            if (btnEndCallVisibility) {
                CircularButton(
                    icon = R.drawable.cross, bgColor = BgRed,
                    onClick={
                        navController?.popBackStack()
                    }
                )
            }
            if (btnNormalCallVisibility) {
                Spacer(modifier = Modifier.width(20.dp))
                CircularButton(
                    icon = R.drawable.drawable_call_10c17d, bgColor = BgGreen,
                    onClick = {

                    }
                )
            }
            if (btnSpeakerVisibility) {
                Spacer(modifier = Modifier.width(20.dp))
                CircularButton(
                    icon = R.drawable.ic_speaker, bgColor = BgGreen,
                    onClick = {

                    }
                )
            }
            if (btnVideoCallVisibility) {
                Spacer(modifier = Modifier.width(20.dp))
                CircularButton(
                    icon = R.drawable.drawable_video_10c17d, bgColor = BgGreen,
                    onClick = {

                    }
                )
            }
        }
    }


}

@Composable
fun GridUserLayout(
    userList: MutableList<String>,
    marginHorizontal: Dp = 10.dp,
    marginVertical: Dp = 10.dp,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = marginHorizontal, vertical = marginVertical),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (userList.size) {
            1 -> {
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    CallUserItem(name = userList[0])
                }
            }
            2 -> {
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    CallUserItem(name = userList[0])
                    CallUserItem(name = userList[1])
                }
            }
            3 -> {
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    CallUserItem(name = userList[0])
                    CallUserItem(name = userList[1])
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    CallUserItem(name = userList[2])
                }
            }
            4 -> {
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    CallUserItem(name = userList[0])
                    CallUserItem(name = userList[1])
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    CallUserItem(name = userList[2])
                    CallUserItem(name = userList[3])
                }
            }
            else -> {
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    CallUserItem(name = userList[0])
                    CallUserItem(name = userList[1])
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    CallUserItem(name = userList[2])
                    CallUserItem(name = userList[3])
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "and ${userList.size - 4} others...",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.DarkGray
                )
            }
        }
    }
}