package com.ongraph.whatsappclone.ui.messageListScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ongraph.whatsappclone.ui.theme.BgGrey
import com.ongraph.whatsappclone.ui.theme.GreenTheme
import com.ongraph.whatsappclone.ui.theme.comfortaaBold

@Preview
@Composable
fun DeleteMessageScreen(): MutableState<Boolean?> {
    val isDeleteStatus: MutableState<Boolean?> = remember { mutableStateOf(null) }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
            .fillMaxWidth()
            .background(Color.White),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .size(50.dp, 1.dp)
                    .background(BgGrey)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Delete 0 message?",
                fontWeight = FontWeight.SemiBold,
                fontFamily = comfortaaBold,
                color = Color.Black
            )

            Row(
                modifier = Modifier.padding(vertical = 30.dp)
                    .padding(horizontal = 20.dp)
                    .clickable {
                        isDeleteStatus.value = false
                    }
            ) {
                TextButton(
                    onClick = {},
                    modifier = Modifier
                        .border(2.dp, GreenTheme, RoundedCornerShape(50.dp))
                        .background(Color.White)
                        .weight(1F)
                ) {
                    Text(text = "Cancel", color = GreenTheme)
                }

                Spacer(modifier = Modifier.width(20.dp).padding(horizontal = 20.dp))

                TextButton(
                    onClick = {},
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(GreenTheme)
                        .weight(1F)
                        .clickable {
                            isDeleteStatus.value = true
                        }
                ) {
                    Text(text = "Yes, delete for me", color = Color.White)
                }
            }
        }
    }

    return isDeleteStatus
}