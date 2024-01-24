package com.ongraph.jetpackloginsample.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ongraph.jetpackloginsample.ui.viewModel.LoginViewModel
import com.ongraph.jetpackloginsample.ui.viewModel.SignUpViewModel

@Composable
fun Context.MainUi(loginViewModel: LoginViewModel?, signUpViewModel: SignUpViewModel?) {
    Column(modifier = Modifier.fillMaxSize()) {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = "login"
        ) {
            composable("login") { LoginUi(navController = navController, this@MainUi, loginViewModel) }
            composable("sign-up") { SignUpUi(navController = navController, this@MainUi, signUpViewModel) }
        }
    }
}