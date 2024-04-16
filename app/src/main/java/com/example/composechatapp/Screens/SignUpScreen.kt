package com.example.composechatapp.Screens

import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.composechatapp.ChatViewModel
import com.example.composechatapp.CheckSignedIn
import com.example.composechatapp.CommonProgressBar
import com.example.composechatapp.DestinationScreen
import com.example.composechatapp.R
import com.example.composechatapp.navigateTo

@Composable
fun SignUpScreen(navController: NavController, viewModel: ChatViewModel) {

    CheckSignedIn(viewModel = viewModel, navController = navController)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val nameState = remember {
                mutableStateOf(TextFieldValue())
            }
            val numberState = remember {
                mutableStateOf(TextFieldValue())
            }
            val emailState = remember {
                mutableStateOf(TextFieldValue())
            }
            val passwordState = remember {
                mutableStateOf(TextFieldValue())
            }

            val passwordVisible = remember { mutableStateOf(false) }

            val focus = LocalFocusManager.current

            Image(
                painter = painterResource(id = R.drawable.chat),
                contentDescription = "",
                modifier = Modifier
                    .width(200.dp)
                    .padding(top = 16.dp)
                    .padding(8.dp)
            )

            Text(
                text = "Sign Up",
                fontSize = 30.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )

            OutlinedTextField(
                value = nameState.value,
                onValueChange = { nameState.value = it },
                label = { Text(text = "Name") },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )

            OutlinedTextField(
                value = numberState.value,
                onValueChange = { numberState.value = it },
                label = { Text(text = "Number") },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )

            OutlinedTextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                label = { Text(text = "Email") },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )

            OutlinedTextField(
                value = passwordState.value,
                onValueChange = { passwordState.value = it },
                label = { Text(text = "Password") },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible.value = !passwordVisible.value },
                        modifier = Modifier.size(32.dp)
                        ) {
                        if (passwordVisible.value) {
                            Icon(
                                painter = painterResource(id = R.drawable.eye),
                                contentDescription = "Hide password",
                                tint = Color(0xFF009688),
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.view),
                                contentDescription = "Show password",
                                tint = Color(0xFF009688)
                            )
                        }
                    }
                }
            )


            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009688)),
                onClick = {
                    viewModel.signUp(
                        nameState.value.text,
                        numberState.value.text,
                        emailState.value.text,
                        passwordState.value.text
                    )
                },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "SIGN UP")
            }

            Text(
                text = "Already a User ? Go to Login - >",
                color = Color.Blue,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        navigateTo(
                            navController = navController,
                            route = DestinationScreen.Login.route
                        )
                    }
            )


        }

    }

    if (viewModel.inProgress.value) {
        CommonProgressBar()
    }

}