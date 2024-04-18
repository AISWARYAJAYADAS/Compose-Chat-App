package com.example.composechatapp.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.navigation.NavController
import com.example.composechatapp.ChatViewModel
import com.example.composechatapp.CommonProgressBar
import com.example.composechatapp.CommonRow
import com.example.composechatapp.DestinationScreen
import com.example.composechatapp.TitleText
import com.example.composechatapp.navigateTo

@Composable
fun ChatListScreen(
    navController: NavController,
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
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(chats) { chat ->
                            val chatUser = if (chat.user1.userId == userData?.userId) {
                                chat.user2
                            } else {
                                chat.user1
                            }

                            CommonRow(
                                imageUrl = chatUser.imageUrl, name = chatUser.name
                            ) {

                                chat.chatId?.let {
                                    navigateTo(
                                        navController = navController,
                                        route = DestinationScreen.SingleChat.createRoute(id = it)
                                    )
                                }
                            }


                        }

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








