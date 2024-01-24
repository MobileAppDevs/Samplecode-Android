package com.ongraph.whatsappclone.ui.messageListScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ongraph.whatsappclone.R
import com.ongraph.whatsappclone.common.CircleImage
import com.ongraph.whatsappclone.constants.screens.Screens
import com.ongraph.whatsappclone.ui.theme.BgGreen
import com.ongraph.whatsappclone.ui.theme.comfortaaBold

@Composable
fun TopActionBarUi(
    personOrGroupName: String = "",
    onBackClick: () -> Unit,
    itemCount: Int = 0,
    navController: NavController
): MutableState<Boolean> {
    val isDeleteStatus = remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick
        ) {
            Image(
                painter = painterResource(
                    id = R.drawable.drawable_left_arrow_10c17d
                ),
                contentDescription = null
            )
        }

        if (itemCount <= 0) {
            CircleImage(
                placeholder = painterResource(id = R.drawable.default_profile),
                width = 30.dp,
                height = 30.dp
            )
        }

        TextButton(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1F),
            onClick = {

            }) {
            Text(
                text = if (itemCount <= 0) personOrGroupName else "$itemCount",
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = comfortaaBold
            )
        }

        if (itemCount > 0) isDeleteStatus.value = onMessageSelected(itemCount).value
        else OnMessageNotSelected(
            onVoiceCallIconClick = {
                navController.navigate(Screens.PERSONAL_VOICE_CALL_SCREEN)
            },
            onVideoCallIconClick = {
                navController.navigate(Screens.PERSONAL_VIDEO_CALL_SCREEN)
            }
        )

        IconButton(
            modifier = Modifier
                .height(30.dp)
                .width(30.dp),
            onClick = {

            }) {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
                    .background(BgGreen)
            )
            Image(
                painter = painterResource(id = R.drawable.drawable_nav_dots_10c17d),
                contentDescription = null,
                modifier = Modifier.size(15.dp, 15.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))
    }

    return isDeleteStatus
}

@Composable
private fun OnMessageNotSelected(
    onVoiceCallIconClick: () -> Unit,
    onVideoCallIconClick: () -> Unit,
    visibilityVoiceCallIcon: Boolean = true,
    visibilityVideoCallIcon: Boolean = true,
) {
    if (visibilityVoiceCallIcon) {
        IconButton(
            modifier = Modifier
                .height(30.dp)
                .width(30.dp),
            onClick = onVoiceCallIconClick
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
                    .background(BgGreen)
            )
            Image(
                painter = painterResource(id = R.drawable.drawable_call_10c17d),
                contentDescription = null,
                modifier = Modifier.size(20.dp, 20.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
    }
    if (visibilityVideoCallIcon) {
        IconButton(
            modifier = Modifier
                .height(30.dp)
                .width(30.dp),
            onClick = onVideoCallIconClick
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
                    .background(BgGreen)
            )
            Image(
                painter = painterResource(id = R.drawable.drawable_video_10c17d),
                contentDescription = null,
                modifier = Modifier.size(20.dp, 20.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))
    }
}

@Preview
@Composable
private fun onMessageSelected(itemCount: Int? = 1): MutableState<Boolean> {
    val isDeleteStatus = remember { mutableStateOf(false) }
    if (itemCount == 1) {
        IconButton(
            modifier = Modifier
                .height(30.dp)
                .width(30.dp),
            onClick = {

            }) {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
                    .background(BgGreen)
            )
            Image(
                painter = painterResource(id = R.drawable.drawable_backward_arrow_10c17d),
                contentDescription = null,
                modifier = Modifier.size(20.dp, 20.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))
    }

    IconButton(
        modifier = Modifier
            .height(30.dp)
            .width(30.dp),
        onClick = {

        }) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp))
                .background(BgGreen)
        )
        Image(
            painter = painterResource(id = R.drawable.drawable_star_10c17d),
            contentDescription = null,
            modifier = Modifier.size(20.dp, 20.dp)
        )
    }

    Spacer(modifier = Modifier.width(10.dp))

    IconButton(
        modifier = Modifier
            .height(30.dp)
            .width(30.dp),
        onClick = {

        }) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp))
                .background(BgGreen)
        )
        Image(
            painter = painterResource(id = R.drawable.drawable_copy_10c17d),
            contentDescription = null,
            modifier = Modifier.size(20.dp, 20.dp)
        )
    }

    Spacer(modifier = Modifier.width(10.dp))

    IconButton(
        modifier = Modifier
            .height(30.dp)
            .width(30.dp),
        onClick = {

        }) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp))
                .background(BgGreen)
        )
        Image(
            painter = painterResource(id = R.drawable.drawable_bin_10c17d),
            contentDescription = null,
            modifier = Modifier.size(20.dp, 20.dp)
                .clickable {
                    isDeleteStatus.value = !isDeleteStatus.value
                }
        )
    }

    Spacer(modifier = Modifier.width(10.dp))

    IconButton(
        modifier = Modifier
            .height(30.dp)
            .width(30.dp),
        onClick = {

        }) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp))
                .background(BgGreen)
        )
        Image(
            painter = painterResource(id = R.drawable.drawable_forward_arrow_10c17d),
            contentDescription = null,
            modifier = Modifier.size(20.dp, 20.dp)
        )
    }

    Spacer(modifier = Modifier.width(10.dp))

    return isDeleteStatus
}