package com.ongraph.jetpacksample.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ongraph.jetpacksample.R
import com.ongraph.jetpacksample.common.Utils.checkFields
import com.ongraph.jetpacksample.ui.functions.LoginFunctions
import com.ongraph.jetpacksample.ui.viewModel.LoginViewModel
import com.ongraph.jetpacksample.ui.theme.BgGrey
import com.ongraph.jetpacksample.ui.theme.Blue
import com.ongraph.jetpacksample.ui.theme.Hint
import com.ongraph.jetpacksample.ui.theme.White
import com.ongraph.jetpacksample.ui.theme.textColor

/**
 * Composable function that represents the login screen UI.
 *
 * The UI includes:
 * 1. A logo or image at the top.
 * 2. Text fields for entering email and password.
 * 3. A "Forgot Password?" link.
 * 4. A login button that triggers the login process.
 * 5. A sign-up option for users who don't have an account.
 *
 * The layout uses Jetpack Compose to structure the UI components in a `Column` with centered alignment.
 *
 * @OptIn(ExperimentalMaterial3Api::class) is used to opt into using experimental APIs from Material 3.
 *
 * @param navController Used for navigation between screens.
 * @param context The current context, used for displaying a Toast message.
 * @param loginViewModel The ViewModel responsible for handling login-related logic.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginUi(navController: NavController, context: Context, loginViewModel: LoginViewModel?) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painterResource(id = R.drawable.top_white_hanger_foreground),
            null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(200.dp, 180.dp)
                .padding(top = 26.dp)
        )

        Box(
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 25.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(40.dp))
                .background(BgGrey)
        ) {
            Box(
                modifier = Modifier
                    .padding(bottom = 10.dp, end = 5.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(36.dp))
                    .background(White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                            ,
                            value = email,
                            onValueChange = {
                                email = it
                            },
                            label = {
                                Text(
                                    text = "email",
                                    color = textColor,
                                    fontFamily = FontFamily.Monospace
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            leadingIcon = {
                                Icon(painterResource(id = R.drawable.ic_email), "")
                            },
                            colors = TextFieldDefaults.textFieldColors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                            ,
                            value = password,
                            onValueChange = {
                                password = it
                            },
                            label = {
                                Text(
                                    text = "password",
                                    color = Hint,
                                    fontFamily = FontFamily.Monospace
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            leadingIcon = {
                                Icon(painterResource(id = R.drawable.ic_lock), null)
                            },
                            trailingIcon = {
                                val image =
                                    if (passwordVisible) painterResource(id = R.drawable.ic_eye_open)
                                    else painterResource(id = R.drawable.ic_eye_closed)
                                val description =
                                    if (passwordVisible) "hidePassword" else "showPassword"
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(image, description)
                                }
                            },
                            colors = TextFieldDefaults.textFieldColors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            )
                        )
                    }
                    Text(
                        text = "Forgot Password?",
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.End,
                        fontSize = 11.sp,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Button(
                        onClick = {
                            if (checkFields(email, password)) {
                                LoginFunctions.loginRequest(email, password, loginViewModel)
                            } else
                                Toast.makeText(context, "Enter all fields", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        colors = ButtonDefaults.buttonColors(Blue)
                    ) {
                        Text(
                            text = "Login",
                            color = White,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(
                            onClick = {},
                            enabled = false
                        ) {
                            Text(
                                text = "Don't have an account?",
                                fontSize = 14.sp,
                                color = textColor
                            )
                        }
                        TextButton(
                            onClick = {
                                navController.navigate("sign-up")
                            },
                            colors = ButtonDefaults.buttonColors(White)
                        ) {
                            Text(
                                text = "Sign Up",
                                fontSize = 14.sp,
                                color = Blue
                            )
                        }
                    }
                }
            }
        }
    }
}