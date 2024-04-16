package com.example.composechatapp.Screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.composechatapp.ChatViewModel
import com.example.composechatapp.CommonDivider
import com.example.composechatapp.CommonImage
import com.example.composechatapp.CommonProgressBar
import com.example.composechatapp.DestinationScreen
import com.example.composechatapp.R
import com.example.composechatapp.navigateTo


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ChatViewModel
) {

    val inProgress = viewModel.inProgress.value

    val userData = viewModel.userData.value
    var name by rememberSaveable {
        mutableStateOf(userData?.name ?: "")
    }
    var number by rememberSaveable {
        mutableStateOf(userData?.number ?: "")
    }

    Scaffold(
        bottomBar = {
            BottomNavigationMenu(
                selectedItem = BottomNavigationItem.PROFILE,
                navController = navController
            )
        }
    ) {
        if (inProgress) {
            CommonProgressBar()
        } else {
            ProfileContent(
                modifier = Modifier
                    .padding(8.dp),
                onBack = {
                    navigateTo(
                        navController = navController,
                        route = DestinationScreen.ChatList.route
                    )
                },
                onSave = {
                    viewModel.createOrUpdateProfile(
                        name = name,
                        number = number,
                    )
                    Log.d("Test", "gggggkkkk")
                },
                viewModel = viewModel,
                onLogOut = {
                    viewModel.logout()
                    navigateTo(navController = navController, route = DestinationScreen.Login.route)
                },
                onNameChange = { name = it },
                onNumberChange = { number = it },
                name = name,
                number = number
            )
        }
    }
}

@Composable
fun ProfileContent(
    modifier: Modifier,
    viewModel: ChatViewModel,
    onBack: () -> Unit,
    onSave: () -> Unit,
    name: String,
    number: String,
    onNameChange: (String) -> Unit,
    onNumberChange: (String) -> Unit,
    onLogOut: () -> Unit
) {
    val imageUrl = viewModel.userData.value?.imageUrl

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Back",
                modifier = Modifier.clickable { onBack.invoke() },
                color = Color(0xFF009688),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Save",
                modifier = Modifier.clickable { onSave.invoke() },
                color = Color(0xFF009688),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        CommonDivider()
        ProfileImage(imageUrl = imageUrl, viewModel = viewModel)
        CommonDivider()

        OutlinedTextField(
            value = name,
            onValueChange = { onNameChange(it) },
            label = { Text(text = "Name") },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            textStyle = TextStyle(color = Color(0xFF009688)),
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Filled.Person, contentDescription = "Name", modifier = Modifier.size(18.dp))
            }
        )

        OutlinedTextField(
            value = number,
            onValueChange = { onNumberChange(it) },
            label = { Text(text = "Number") },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            textStyle = TextStyle(color = Color(0xFF009688)),
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Filled.Phone, contentDescription = "Number", modifier = Modifier.size(18.dp))
            }
        )

        CommonDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Log Out",
                modifier = Modifier
                    .clickable { onLogOut() }
                    .padding(8.dp),
                color = Color(0xFF009688),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}



//@Composable
//fun ProfileContent(
//    modifier: Modifier,
//    viewModel: ChatViewModel,
//    onBack: () -> Unit,
//    onSave: () -> Unit,
//    name: String,
//    number: String,
//    onNameChange: (String) -> Unit,
//    onNumberChange: (String) -> Unit,
//    onLogOut: () -> Unit
//) {
//    val imageUrl = viewModel.userData.value?.imageUrl
//
//    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(
//                text = "Back",
//                modifier = Modifier.clickable { onBack.invoke() },
//                color = Color(0xFF009688),
//                style = MaterialTheme.typography.bodyMedium
//            )
//            Text(
//                text = "Save",
//                modifier = Modifier.clickable { onSave.invoke() },
//                color = Color(0xFF009688),
//                style = MaterialTheme.typography.bodyMedium
//            )
//        }
//        CommonDivider()
//        ProfileImage(imageUrl = imageUrl, viewModel = viewModel)
//        CommonDivider()
//
//        OutlinedTextField(
//            value = name,
//            onValueChange = { onNameChange(it) },
//            label = { Text(text = "Name") },
//            modifier = Modifier
//                .padding(8.dp)
//                .fillMaxWidth(),
//            textStyle = TextStyle(color = Color(0xFF009688)),
//            singleLine = true,
////            leadingIcon = {
////                Icon(Icons.Filled.Person, contentDescription = "Name", modifier = Modifier.size(18.dp))
////            }
//        )
//
//        OutlinedTextField(
//            value = number,
//            onValueChange = { onNumberChange(it) },
//            label = { Text(text = "Number") },
//            modifier = Modifier
//                .padding(8.dp)
//                .fillMaxWidth(),
//            textStyle = TextStyle(color = Color(0xFF009688)),
//            singleLine = true,
////            leadingIcon = {
////                Icon(Icons.Filled.Phone, contentDescription = "Number", modifier = Modifier.size(18.dp))
////            }
//        )
//
//        CommonDivider()
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            horizontalArrangement = Arrangement.Center
//        ) {
//
//            Text(
//                text = "Log Out",
//                modifier = Modifier
//                    .clickable { onLogOut() }
//                    .padding(8.dp),
//                color = Color(0xFF009688), // Set text color to 0xFF009688
//                style = MaterialTheme.typography.bodyLarge,
//                fontSize = 24.sp,
//                fontWeight = FontWeight.Bold
//            )
//
//        }
//    }
//}


@Composable
fun ProfileImage(imageUrl: String?, viewModel: ChatViewModel) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let { viewModel.uploadProfileImage(uri) }
        }
    )

    Box(
        modifier = Modifier
            .height(intrinsicSize = IntrinsicSize.Min)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable {
                    launcher.launch("image/*")
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = CircleShape,
                elevation = CardDefaults.elevatedCardElevation(8.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .size(150.dp)
            ) {
                CommonImage(
                    data = imageUrl,
                    placeholder = painterResource(R.drawable.placeholder), // Placeholder image
                    error = painterResource(R.drawable.placeholder), // Error image
                    contentDescription = null, // Description of the content for accessibility
                    contentScale = ContentScale.FillBounds, // Scale type for the image content
                    modifier = Modifier.fillMaxSize() // Modifier for additional customization
                )
            }
            Text(text = "Change Profile Picture")
        }

        if (viewModel.inProgress.value) {
            CommonProgressBar()
        }
    }
}

