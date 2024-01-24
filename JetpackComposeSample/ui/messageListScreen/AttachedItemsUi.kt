package com.ongraph.whatsappclone.ui.messageListScreen

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ongraph.whatsappclone.R
import com.ongraph.whatsappclone.ui.theme.BgGreen
import com.ongraph.whatsappclone.ui.theme.BgGrey
import com.ongraph.whatsappclone.ui.theme.comfortaaBold

@Preview
@Composable
fun AttachedItemsUi(
    isDocument: Boolean = true,
    isCamera: Boolean = true,
    isGallery: Boolean = true,
    isAudio: Boolean = true,
    isLocation: Boolean = false,
    isContact: Boolean = false,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(BorderStroke(1.dp, BgGrey), RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Column  {
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                if (isDocument) Document()
                if (isCamera) Camera()
                if (isGallery) Gallery()
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.Center
            ) {
                if (isAudio) Audio()
                if (isLocation) Location()
                if (isContact) Contact()
            }
        }
    }
}

@Composable
private fun RowScope.Document() = Column(
        modifier = Modifier.weight(1F),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            modifier = Modifier
                .height(80.dp)
                .width(80.dp),
            onClick = {

            }) {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(80.dp))
                    .background(BgGreen)
            )
            Image(
                painter = painterResource(id = R.drawable.drawable_document_10c17d),
                contentDescription = null,
                modifier = Modifier.size(20.dp, 20.dp)
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        Text(text = "Document", color = Color.Black, fontFamily = comfortaaBold)
    }

@Composable
private fun RowScope.Camera() = Column(
        modifier = Modifier.weight(1F),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            modifier = Modifier
                .height(80.dp)
                .width(80.dp),
            onClick = {

            }) {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(80.dp))
                    .background(BgGreen)
            )
            Image(
                painter = painterResource(id = R.drawable.drawable_camera_10c17d),
                contentDescription = null,
                modifier = Modifier.size(20.dp, 20.dp)
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        Text(text = "Camera", color = Color.Black, fontFamily = comfortaaBold)
    }

@Composable
private fun RowScope.Gallery() = Column(
        modifier = Modifier.weight(1F),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            modifier = Modifier
                .height(80.dp)
                .width(80.dp),
            onClick = {

            }) {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(80.dp))
                    .background(BgGreen)
            )
            Image(
                painter = painterResource(id = R.drawable.drawable_gallery_10c17d),
                contentDescription = null,
                modifier = Modifier.size(20.dp, 20.dp)
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        Text(text = "Gallery", color = Color.Black, fontFamily = comfortaaBold)
    }

@Composable
private fun RowScope.Audio() = Column(
        modifier = Modifier.weight(1F),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            modifier = Modifier
                .height(80.dp)
                .width(80.dp),
            onClick = {

            }) {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(80.dp))
                    .background(BgGreen)
            )
            Image(
                painter = painterResource(id = R.drawable.drawable_audio_10c17d),
                contentDescription = null,
                modifier = Modifier.size(20.dp, 20.dp)
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        Text(text = "Audio", color = Color.Black, fontFamily = comfortaaBold)
    }

@Composable
private fun RowScope.Location() = Column(
        modifier = Modifier.weight(1F),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            modifier = Modifier
                .height(80.dp)
                .width(80.dp),
            onClick = {

            }) {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(80.dp))
                    .background(BgGreen)
            )
            Image(
                painter = painterResource(id = R.drawable.drawable_location_10c17d),
                contentDescription = null,
                modifier = Modifier.size(20.dp, 20.dp)
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        Text(text = "Location", color = Color.Black, fontFamily = comfortaaBold)
    }

@Composable
private fun RowScope.Contact() = Column(
        modifier = Modifier.weight(1F),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            modifier = Modifier
                .height(80.dp)
                .width(80.dp),
            onClick = {

            }) {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(80.dp))
                    .background(BgGreen)
            )
            Image(
                painter = painterResource(id = R.drawable.drawable_contact_10c17d),
                contentDescription = null,
                modifier = Modifier.size(20.dp, 20.dp)
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        Text(text = "Contact", color = Color.Black, fontFamily = comfortaaBold)
    }