package com.ongraph.whatsappclone.ui.home.call.group

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ongraph.whatsappclone.common.CallButtonLayout
import com.ongraph.whatsappclone.common.GridUserLayout


@Composable
fun GroupVoiceCallScreen(navController: NavController?) {

    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {

        val userList = mutableListOf<String>()
        userList.add("Rupesh Deshmukh")
        userList.add("Chanchal Deshmukh")
        userList.add("Nisha Saini")
        userList.add("Priya")
        userList.add("Pankaj sir")
        GridUserLayout(userList = userList, marginHorizontal = 20.dp)
    }
    CallButtonLayout(
        btnEndCallVisibility = true,
        btnNormalCallVisibility = true,
        navController = navController
    )
}
@Preview
@Composable
fun GroupVoiceCallScreenPreview() {
    GroupVoiceCallScreen(null)
}











