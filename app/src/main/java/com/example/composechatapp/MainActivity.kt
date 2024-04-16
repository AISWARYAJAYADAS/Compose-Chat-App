package com.example.composechatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composechatapp.Screens.ChatListScreen
import com.example.composechatapp.Screens.LoginScreen
import com.example.composechatapp.Screens.ProfileScreen
import com.example.composechatapp.Screens.SignUpScreen
import com.example.composechatapp.Screens.StatusScreen
import dagger.hilt.android.AndroidEntryPoint


sealed class DestinationScreen(var route: String) {
    object SignUp : DestinationScreen("signup")
    object Login : DestinationScreen("login")
    object Profile : DestinationScreen("profile")
    object ChatList : DestinationScreen("chatList")
    object SingleChat : DestinationScreen("singleChat/{chatId}") {
        fun createRoute(id: String) = "singlechat/$id"
    }

    object StatusList : DestinationScreen("statusList")
    object SingleStatus : DestinationScreen("singleStatus/{userId}") {
        fun createRoute(id: String) = "singleStatus/$id"
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatAppNavigation()

        }
    }

    @Composable
    fun ChatAppNavigation() {
        val navController = rememberNavController()
        val vm = hiltViewModel<ChatViewModel>()
        val chatListViewModel = hiltViewModel<ChatListViewModel>()
        NavHost(
            navController = navController,
            startDestination = DestinationScreen.SignUp.route
        ) {
            composable(DestinationScreen.SignUp.route) {
                SignUpScreen(navController = navController, viewModel = vm)
            }
            composable(DestinationScreen.Login.route) {
                LoginScreen(navController = navController, viewModel = vm)
            }
            composable(DestinationScreen.ChatList.route) {
                ChatListScreen(
                    navController = navController,
                    chatListViewModel = chatListViewModel,
                    viewModel = vm
                )
            }
            composable(DestinationScreen.StatusList.route) {
                StatusScreen(navController = navController, viewModel = vm)
            }
            composable(DestinationScreen.Profile.route) {
                ProfileScreen(navController = navController, viewModel = vm)
            }


        }
    }
}

