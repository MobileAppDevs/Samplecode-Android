package com.ongraph.jetpacksample.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.ongraph.jetpacksample.R

/**
 * Composable function to display a background UI using two images arranged vertically in a column layout.
 *
 * The UI consists of two images:
 * 1. A top image that occupies a larger portion of the screen (weight of 1.2).
 * 2. A bottom image that occupies a smaller portion of the screen (weight of 1.0).
 *
 * The `fillMaxSize()` modifier ensures that both images fill the available space of their respective containers.
 * The `weight()` modifier determines the relative space allocation for each image within the column.
 *
 * Both images use the `ContentScale.Crop` to ensure they fill their containers while maintaining their aspect ratios.
 *
 * The function is annotated with `@Composable` to indicate that it's a composable function, and `@Preview`
 * to allow previewing the UI in Android Studio.
 */
@Composable
@Preview
fun BackgroundUi() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painterResource(id = R.drawable.top_img),
            null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .weight(1.2F)
        )
        Image(
            painterResource(id = R.drawable.bottom_img),
            null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .weight(1F)
        )
    }
}