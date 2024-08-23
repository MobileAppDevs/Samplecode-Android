package com.ongraph.jetpacksample.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ongraph.jetpacksample.ui.viewModel.LoginViewModel
import com.ongraph.jetpacksample.ui.viewModel.SignUpViewModel

/**
 * Composable function that represents the main UI of the app, handling navigation between different screens.
 *
 * This function sets up a `NavHost` using Jetpack Compose's Navigation component. It defines two destinations:
 * 1. "login" - Renders the `LoginUi` composable for the login screen.
 * 2. "sign-up" - Renders the `SignUpUi` composable for the sign-up screen.
 *
 * The `Column` serves as the container for the navigation host, which fills the entire screen.
 * The navigation is controlled by `NavController`, which is remembered across recompositions using `rememberNavController()`.
 *
 * @param loginViewModel The ViewModel responsible for handling login-related logic.
 * @param signUpViewModel The ViewModel responsible for handling sign-up-related logic.
 */
@Composable
fun Context.MainUi(loginViewModel: LoginViewModel?, signUpViewModel: SignUpViewModel?) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Remember the NavController instance to manage navigation between screens
        val navController = rememberNavController()

        // Define the navigation host with the start destination as "login"
        NavHost(
            navController = navController,
            startDestination = "login"
        ) {
            composable("login") { LoginUi(navController = navController, this@MainUi, loginViewModel) }
            composable("sign-up") { SignUpUi(navController = navController, this@MainUi, signUpViewModel) }
        }
    }
}