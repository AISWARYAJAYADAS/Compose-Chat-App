package com.example.composechatapp

import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.example.composechatapp.data.ChatData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(

) : ViewModel() {


//    var inProcessChats = mutableStateOf(false)
//    var chats = mutableStateOf<List<ChatData>>(listOf())
//
//    fun onAddChat(number: String) {
//        if (number.isEmpty() or !number.isDigitsOnly()){
//           // handleException()
//        }
//        else {
//           // db.
//        }
//
//    }
}