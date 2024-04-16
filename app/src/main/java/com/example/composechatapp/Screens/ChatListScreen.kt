package com.example.composechatapp.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.composechatapp.ChatListViewModel
import com.example.composechatapp.ChatViewModel
import com.example.composechatapp.CommonProgressBar
import com.example.composechatapp.TitleText

@Composable
fun ChatListScreen(
    navController: NavController,
    chatListViewModel: ChatListViewModel,
    viewModel: ChatViewModel
) {

    val inProgress = viewModel.inProcessChats.value

    val chats = viewModel.chats.value
    val userData = viewModel.userData.value
    val showDialog = remember {
        mutableStateOf(false)
    }
    val onFabClick: () -> Unit = { showDialog.value = true }
    val onDismiss: () -> Unit = { showDialog.value = false }
    val onAddChat: (String) -> Unit = {
        viewModel.onAddChat(it)
        showDialog.value = false
    }

    Scaffold(
        bottomBar = {
            BottomNavigationMenu(
                selectedItem = BottomNavigationItem.CHATLIST,
                navController = navController
            )
        },
        floatingActionButton = {
            FAB(
                showDialog = showDialog.value,
                onFabClick = onFabClick,
                onDismiss = onDismiss,
                onAddChat = onAddChat

            )
        }
    ) {
        if (inProgress) {
            CommonProgressBar()
        } else {

            Column(
                Modifier
                    .padding(it)
                    .padding(16.dp)
                    .fillMaxSize(),

                ) {

                TitleText(text = "Chats")

                if (chats.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("No Chats Available")
                    }
                }

            }
        }


    }

}

@Composable
fun FAB(
    showDialog: Boolean,
    onFabClick: () -> Unit,
    onDismiss: () -> Unit,
    onAddChat: (String) -> Unit
) {
    val addChatNumber = remember {
        mutableStateOf("")
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                onDismiss.invoke()
                addChatNumber.value = ""
            },
            confirmButton = {
                Button(
                    onClick = { onAddChat(addChatNumber.value) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009688)),
                ) {
                    Text(text = "Add Chat")
                }
            },
            title = { Text(text = "Add Chat") },
            text = {
                OutlinedTextField(
                    value = addChatNumber.value,
                    onValueChange = { addChatNumber.value = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        )
    }


    FloatingActionButton(
        onClick = { onFabClick() },
        containerColor = Color(0xFF009688),
        // shape = CircleShape,
        modifier = Modifier.padding(bottom = 40.dp)
    ) {

        Icon(imageVector = Icons.Rounded.Add, contentDescription = null, tint = Color.White)
    }

}








