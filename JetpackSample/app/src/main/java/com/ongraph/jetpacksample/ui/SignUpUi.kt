package com.ongraph.jetpacksample.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.*
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
import com.ongraph.jetpacksample.ui.functions.SignUpFunctions
import com.ongraph.jetpacksample.ui.viewModel.SignUpViewModel
import com.ongraph.jetpacksample.ui.theme.BgGrey
import com.ongraph.jetpacksample.ui.theme.Blue
import com.ongraph.jetpacksample.ui.theme.Hint
import com.ongraph.jetpacksample.ui.theme.White
import com.ongraph.jetpacksample.ui.theme.textColor

/**
 * Composable function that represents the Sign-Up UI.
 *
 * This function displays a sign-up form with fields for first name, last name, email, password,
 * confirm password, and mobile number. It also includes a checkbox for enabling notifications
 * and buttons for submitting the form or navigating to the login screen.
 *
 * The form fields are handled using `remember` to maintain state during recomposition.
 *
 * Input validation occurs on sign-up submission. It checks that all required fields are filled
 * and that the password and confirm password match, with a minimum password length of 8 characters.
 *
 * @param navController The `NavController` used to navigate between screens.
 * @param context The `Context` of the current environment, used for displaying Toast messages.
 * @param signUpViewModel The `SignUpViewModel` for handling the sign-up logic.
 * */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpUi(navController: NavController, context: Context, signUpViewModel: SignUpViewModel?) {
    val checkedState = remember { mutableStateOf(true) }
    var firstname by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
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
                        //.background(BgGrey)
                    ) {
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                            //.background(BgGrey)
                            ,
                            value = firstname,
                            onValueChange = {
                                firstname = it
                            },
                            label = {
                                Text(
                                    text = "* First Name",
                                    color = textColor,
                                    fontFamily = FontFamily.Monospace
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            leadingIcon = {
                                Icon(painterResource(id = R.drawable.ic_user), "")
                            },
                            colors = TextFieldDefaults.textFieldColors(
//                                backgroundColor = BgGrey,
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
                        // .background(BgGrey)
                    ) {
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                            //.background(BgGrey)
                            ,
                            value = lastname,
                            onValueChange = {
                                lastname = it
                            },
                            label = {
                                Text(
                                    text = "* Last Name",
                                    color = textColor,
                                    fontFamily = FontFamily.Monospace
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            leadingIcon = {
                                Icon(painterResource(id = R.drawable.ic_user), "")
                            },
                            colors = TextFieldDefaults.textFieldColors(
//                                backgroundColor = BgGrey,
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
                        // .background(BgGrey)
                    ) {
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                            //.background(BgGrey)
                            ,
                            value = email,
                            onValueChange = {
                                email = it
                            },
                            label = {
                                Text(
                                    text = "* Email",
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
//                                backgroundColor = BgGrey,
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
                        // .background(BgGrey)
                    ) {
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                            //.background(BgGrey)
                            ,
                            value = password,
                            onValueChange = {
                                password = it
                            },
                            label = {
                                Text(
                                    text = "* Password",
                                    color = Hint,
                                    fontFamily = FontFamily.Monospace
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
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
//                                backgroundColor = BgGrey,
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
                        // .background(BgGrey)
                    ) {
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                            //.background(BgGrey)
                            ,
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                            },
                            label = {
                                Text(
                                    text = "* Confirm Password",
                                    color = Hint,
                                    fontFamily = FontFamily.Monospace
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            ),
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            leadingIcon = {
                                Icon(painterResource(id = R.drawable.ic_lock), null)
                            },
                            trailingIcon = {
                                val image =
                                    if (confirmPasswordVisible) painterResource(id = R.drawable.ic_eye_open)
                                    else painterResource(id = R.drawable.ic_eye_closed)
                                val description =
                                    if (confirmPasswordVisible) "hidePassword" else "showPassword"
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(image, description)
                                }
                            },
                            colors = TextFieldDefaults.textFieldColors(
//                                backgroundColor = BgGrey,
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
                        //.background(BgGrey)
                    ) {
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                            //.background(BgGrey)
                            ,
                            value = mobile,
                            onValueChange = {
                                mobile = it
                            },
                            label = {
                                Text(
                                    text = "Mobile Number",
                                    color = textColor,
                                    fontFamily = FontFamily.Monospace
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            leadingIcon = {
                                Icon(painterResource(id = R.drawable.ic_mobile), "")
                            },
                            colors = TextFieldDefaults.textFieldColors(
//                                backgroundColor = BgGrey,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "* Required Fields",
                        modifier = Modifier
                            .fillMaxWidth(),
                        textAlign = TextAlign.End,
                        fontSize = 14.sp,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row (
                        modifier = Modifier
                            .padding(horizontal = 13.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checkedState.value,
                            onCheckedChange = { checkedState.value = it },
                            colors = CheckboxDefaults.colors(Blue, Blue)
                        )
                        Text(text = "Send me notifications.", color = textColor)
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    Button(
                        onClick = {
                            if (checkFields(firstname, lastname, email, password, confirmPassword)) {
                                SignUpFunctions.signUpRequest(
                                    firstname,
                                    lastname,
                                    email,
                                    password,
                                    signUpViewModel
                                )
                            } else if (password.isNotBlank() && confirmPassword.isNotBlank()) {
                                if (password != confirmPassword)
                                    Toast.makeText(context, "Those passwords didn't matched", Toast.LENGTH_SHORT).show()
                                else if (password.length < 8)
                                    Toast.makeText(context, "Password must contains 8 characters", Toast.LENGTH_SHORT).show()
                            } else
                                Toast.makeText(context, "Enter all Required fields", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        colors = ButtonDefaults.buttonColors(Blue)
                    ) {
                        Text(
                            text = "Sign Up",
                            color = White,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(
                            onClick = {},
                            enabled = false
                        ) {
                            Text(
                                text = "Already have an account?",
                                fontSize = 14.sp,
                                color = textColor
                            )
                        }
                        TextButton(
                            onClick = {
                                navController.navigate("login")
                            },
                            colors = ButtonDefaults.buttonColors(White)
                        ) {
                            Text(
                                text = "Login",
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