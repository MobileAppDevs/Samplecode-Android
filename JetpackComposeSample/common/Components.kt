package com.ongraph.whatsappclone.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ongraph.whatsappclone.R
import com.ongraph.whatsappclone.ui.theme.BgGreen
import com.ongraph.whatsappclone.ui.theme.GreenTheme
import com.ongraph.whatsappclone.ui.theme.comfortaaBold
@Composable
fun CircleImage(
    imageUrl: String = "https://picsum.photos/200/300",
    placeholder: Painter = painterResource(R.drawable.logo),
    width: Dp = 50.dp,
    height: Dp = 50.dp,
    strokeWidth:Dp=0.dp,
    strokeColor:Color= GreenTheme
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        placeholder = placeholder,
        contentDescription = "",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(100.dp)).border(BorderStroke(strokeWidth, color = strokeColor),RoundedCornerShape(100.dp))
    )
}

@Composable
fun CircularButton(
    icon: Int = R.drawable.ic_circular_cross, bgColor: Color = BgGreen,
    circleRadius: Dp=35.dp,
    iconWidth:Dp=40.dp,
    iconHeight:Dp=40.dp,
    onClick:()->Unit,
) = Column(
    modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally
) {
    IconButton(modifier = Modifier
        .height(circleRadius*2)
        .width(circleRadius*2), onClick =onClick) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(80.dp))
                .background(bgColor)
        )
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier
                .width(iconWidth)
                .height(iconHeight)
        )
    }
}

@Composable
fun RowScope.CallUserItem(name: String) = Column(
    modifier = Modifier.weight(1F), horizontalAlignment = Alignment.CenterHorizontally
) {
    CircleImage(
        placeholder = painterResource(id = R.drawable.default_profile),
        width = 120.dp,
        height = 120.dp
    )
    Spacer(modifier = Modifier.height(5.dp))
    Text(
        text = name,
        color = Color.Black,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = comfortaaBold,
        textAlign = TextAlign.Center
    )
}
