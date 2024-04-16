package com.example.composechatapp.Screens

import androidx.annotation.ColorLong
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.composechatapp.DestinationScreen
import com.example.composechatapp.R
import com.example.composechatapp.navigateTo


enum class BottomNavigationItem(val icon: Int, val navDestination: DestinationScreen) {

    CHATLIST(icon = R.drawable.comments, navDestination = DestinationScreen.ChatList),
    STATUSLIST(icon = R.drawable.status, navDestination = DestinationScreen.StatusList),
    PROFILE(icon = R.drawable.user, navDestination = DestinationScreen.Profile)

}


@Composable
fun BottomNavigationMenu(
    selectedItem: BottomNavigationItem,
    navController: NavController
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(4.dp)
            .background(Color.White)
    ) {

        for (item in BottomNavigationItem.entries) {
            Image(
                painter = painterResource(id = item.icon), contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .padding(4.dp)
                    .weight(1f)
                    .clickable {
                        navigateTo(navController = navController, route = item.navDestination.route)
                    },
                colorFilter = if (item == selectedItem) ColorFilter.tint(color = Color(0xFF009688)) else ColorFilter.tint(
                    color = Color.Gray
                )
            )
        }

    }


}