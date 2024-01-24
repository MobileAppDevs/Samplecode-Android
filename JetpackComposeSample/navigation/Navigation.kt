package com.ongraph.whatsappclone.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.accompanist.pager.ExperimentalPagerApi
import com.ongraph.whatsappclone.constants.screens.Screens.CHAT_SCREEN
import com.ongraph.whatsappclone.constants.screens.Screens.GROUP_VIDEO_CALL_SCREEN
import com.ongraph.whatsappclone.constants.screens.Screens.GROUP_VOICE_CALL_SCREEN
import com.ongraph.whatsappclone.constants.screens.Screens.HOME_SCREEN
import com.ongraph.whatsappclone.constants.screens.Screens.PERSONAL_VIDEO_CALL_SCREEN
import com.ongraph.whatsappclone.constants.screens.Screens.PERSONAL_VOICE_CALL_SCREEN
import com.ongraph.whatsappclone.constants.screens.Screens.SPLASH_SCREEN
import com.ongraph.whatsappclone.ui.home.HomeScreen
import com.ongraph.whatsappclone.ui.home.call.group.GroupVideoCallScreen
import com.ongraph.whatsappclone.ui.home.call.group.GroupVoiceCallScreen
import com.ongraph.whatsappclone.ui.home.call.group.PersonalVideoCallScreen
import com.ongraph.whatsappclone.ui.home.call.group.PersonalVoiceCallScreen
import com.ongraph.whatsappclone.ui.messageListScreen.MessageListScreen
import com.ongraph.whatsappclone.ui.splashscreen.SplashScreen

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Navigation(navController: NavController) {
    NavHost(navController = navController as NavHostController,
        startDestination = SPLASH_SCREEN) {
        composable(SPLASH_SCREEN) {
            SplashScreen(navController = navController)
        }

        // Home Screen
        composable(HOME_SCREEN) {
            HomeScreen(navController)
        }
        // chat Screen
        composable(CHAT_SCREEN) {
            MessageListScreen(navController)
        }
        // Personal voice call Screen
        composable(PERSONAL_VOICE_CALL_SCREEN) {
            PersonalVoiceCallScreen(navController)
        }
        // Personal voice call Screen
        composable(GROUP_VOICE_CALL_SCREEN) {
            GroupVoiceCallScreen(navController)
        }
        // Personal voice call Screen
        composable(PERSONAL_VIDEO_CALL_SCREEN) {
           PersonalVideoCallScreen(navController)
        }
        // Personal voice call Screen
        composable(GROUP_VIDEO_CALL_SCREEN) {
            GroupVideoCallScreen(navController)
        }
    }
}