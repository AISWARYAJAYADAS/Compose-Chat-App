package com.example.composechatapp.Screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.composechatapp.ChatViewModel
import com.example.composechatapp.CommonDivider
import com.example.composechatapp.CommonProgressBar
import com.example.composechatapp.CommonRow
import com.example.composechatapp.DestinationScreen
import com.example.composechatapp.TitleText
import com.example.composechatapp.navigateTo
import kotlinx.coroutines.launch


@Composable
fun StatusScreen(navController: NavController, viewModel: ChatViewModel) {

    val inProgress = viewModel.inProgressStatus.value
    val userData = viewModel.userData.value

    val statusList = viewModel.statusList.value
    val myStatusList = statusList.filter {
        it.user.userId == userData?.userId
    }
    val otherStatusList = statusList.filter {
        it.user.userId != userData?.userId
    }

    // Launch image picker when FAB is clicked
    val statusImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let { viewModel.uploadStatus(uri) }
        }
    )

    Scaffold(
        bottomBar = {
            BottomNavigationMenu(
                selectedItem = BottomNavigationItem.STATUSLIST,
                navController = navController
            )
        },
        floatingActionButton = {
            FAB(onFabClick = {
                statusImageLauncher.launch("image/*")
            })
        }
    ) {
        if (inProgress) {
            CommonProgressBar()
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                TitleText(text = "Status")
                if (statusList.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "No Statuses available")
                    }
                } else {
                    // Display user's own status
                    myStatusList.firstOrNull()?.let { myStatus ->
                        CommonRow(
                            imageUrl = myStatus.user.imageUrl,
                            name = myStatus.user.name
                        ) {
                            navigateTo(
                                navController = navController,
                                route = DestinationScreen.SingleStatus.createRoute(
                                    myStatus.user.userId ?: ""
                                )
                            )
                        }
                        CommonDivider()
                    }

                    // Display other users' statuses
                    val uniqueUsers = otherStatusList.map { it.user }.toSet().toList()
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(uniqueUsers) { user ->
                            CommonRow(
                                imageUrl = user.imageUrl,
                                name = user.name
                            ) {
                                navigateTo(
                                    navController = navController,
                                    route = DestinationScreen.SingleStatus.createRoute(
                                        user.userId ?: ""
                                    )
                                )
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
    onFabClick: () -> Unit
) {
    FloatingActionButton(
        onClick = { onFabClick.invoke() },
        containerColor = Color(0xFF009688),
        modifier = Modifier.padding(bottom = 40.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Edit,
            contentDescription = "Add Status",
            tint = Color.White
        )
    }
}


