package com.example.composechatapp.Screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.composechatapp.ChatViewModel
import com.example.composechatapp.CommonDivider
import com.example.composechatapp.CommonProfileImage
import com.example.composechatapp.R
import com.example.composechatapp.data.Message
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


@Composable
fun SingleChatScreen(navController: NavController, viewModel: ChatViewModel, chatId: String) {


    var reply by rememberSaveable { mutableStateOf(" ") }
    val onSendReply = {
        viewModel.onSendReply(
            chatID = chatId,
            message = reply
        )
        reply = ""
    }
    var chatMessage = viewModel.chatMessages
    val myUser = viewModel.userData.value
    val currentChat = viewModel.chats.value.first { it.chatId == chatId }
    val chatUser =
        if (myUser?.userId == currentChat.user1.userId) currentChat.user2 else currentChat.user1

    LaunchedEffect(key1 = Unit) {
        viewModel.populateMessages(chatId)

    }

    BackHandler {
        viewModel.depopulateMessage()

    }

    Column {
        //Text(text = "chat Id $chatId")

        ChatHeader(
            name = chatUser.name ?: "",
            imageUrl = chatUser.imageUrl ?: "",
            onBackClicked = {
                navController.popBackStack()
                viewModel.depopulateMessage()
            }
        )

        MessageBox(
            modifier = Modifier.weight(1f),
            chatMessages = chatMessage.value ,
            currentUserId = myUser?.userId ?: ""
        )

        ReplyBox(
            reply = reply,
            onReplyChange = { reply = it },
            onSendReply = { onSendReply.invoke() }
        )
    }
}

@Composable
fun ReplyBox(
    reply: String,
    onReplyChange: (String) -> Unit,
    onSendReply: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        CommonDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // TextField(value = reply, onValueChange = onReplyChange, maxLines = 3)

            OutlinedTextField(
                value = reply,
                onValueChange = onReplyChange,
                maxLines = 3,
            )

            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009688)),
                onClick = { onSendReply.invoke() },
            ) {
                Text(text = "Send")
            }

        }

    }

}

@Composable
fun ChatHeader(
    name: String,
    imageUrl: String,
    onBackClicked: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Icon(
            imageVector = Icons.Rounded.ArrowBack,
            contentDescription = null,
            modifier = Modifier
                .clickable { onBackClicked.invoke() }
                .padding(8.dp)
        )

        CommonProfileImage(
            data = imageUrl,
            placeholder = painterResource(id = R.drawable.placeholder),
            error = painterResource(id = R.drawable.placeholder),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.padding(8.dp)
        )

        Text(
            text = name,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )


    }

}


@Composable
fun MessageBox(
    modifier: Modifier,
    chatMessages: List<Message>,
    currentUserId: String
) {

    // Sort messages based on timestamp (latest message at the top)
    val sortedMessages = chatMessages.sortedByDescending { it.timeStamp }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
    ) {
        items(sortedMessages) { msg ->
            val isCurrentUser = (msg.sendBy == currentUserId)
            val backgroundColor =
                if (isCurrentUser) Color(0xFF2AAA8A) else Color(0xFF899499)
            val contentColor = if (isCurrentUser) Color.White else Color.Black
            val borderRadius = if (isCurrentUser) RoundedCornerShape(8.dp, 0.dp, 8.dp, 8.dp) else RoundedCornerShape(0.dp, 8.dp, 8.dp, 8.dp)
            val alignment = if (isCurrentUser) Alignment.End else Alignment.Start

            val formattedDateTime = msg.timeStamp?.let { parseAndFormatDateTime(it) } ?: ""

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = alignment
            ) {
                Box(
                    modifier = Modifier
                        .background(color = backgroundColor, shape = borderRadius)
                        .padding(8.dp)
                ) {
                    Text(
                        text = msg.message ?: "",
                        color = contentColor
                    )
                }
                Text(
                    text = formattedDateTime,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

private fun parseAndFormatDateTime(dateString: String): String {
    val format = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
    val date: Date = format.parse(dateString) ?: return ""
    val timestamp = date.time
    return formatDateTime(timestamp)
}

private fun formatDateTime(timestamp: Long): String {
    val instant = Instant.ofEpochMilli(timestamp)
    val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
    return formatter.format(localDateTime)
}






