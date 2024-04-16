package com.example.composechatapp.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.composechatapp.ChatViewModel
import com.example.composechatapp.CommonProgressBar

@Composable
fun StatusScreen(navController: NavController, viewModel: ChatViewModel) {

    val inProgress = viewModel.inProgress.value

    Scaffold(
        bottomBar = {
            BottomNavigationMenu(
                selectedItem = BottomNavigationItem.STATUSLIST,
                navController = navController
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(text = "Status Screen", fontSize = 30.sp)

            }
        }


    }

}
