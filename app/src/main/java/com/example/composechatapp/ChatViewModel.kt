package com.example.composechatapp

import android.content.Context
import android.icu.util.Calendar
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composechatapp.data.CHATS
import com.example.composechatapp.data.ChatData
import com.example.composechatapp.data.ChatUser
import com.example.composechatapp.data.Event
import com.example.composechatapp.data.MESSAGE
import com.example.composechatapp.data.Message
import com.example.composechatapp.data.STATUS
import com.example.composechatapp.data.Status
import com.example.composechatapp.data.USER_NODE
import com.example.composechatapp.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    val auth: FirebaseAuth,
    var db: FirebaseFirestore,
    val storage: FirebaseStorage
) : ViewModel() {
    var inProgress = mutableStateOf(false)
    val eventMutableState = mutableStateOf<Event<String>?>(null)
    val signIn = mutableStateOf(false)
    var userData = mutableStateOf<UserData?>(null)


    init {
        val currentUser = auth.currentUser
        signIn.value = currentUser != null
        currentUser?.uid?.let {
            viewModelScope.launch {
                getUserData(it)
            }
        }
    }


    fun loginIn(email: String, password: String) {
        if (email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "Please Fill all fields")
            return
        }
        inProgress.value = true
        viewModelScope.launch {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                signIn.value = result.user != null
                if (signIn.value) {
                    val userId = result.user?.uid
                    userId?.let { getUserData(it) }
                }
                inProgress.value = false
            } catch (e: Exception) {
                handleException(e, "Login Failed")
            }
        }
    }

    private suspend fun uploadImage(uri: Uri): Uri {
        return withContext(Dispatchers.IO) {
            val storageRef = storage.reference
            val uuid = UUID.randomUUID()
            val imageRef = storageRef.child("images/$uuid")
            val uploadTask = imageRef.putFile(uri).await()
            return@withContext uploadTask.storage.downloadUrl.await()
        }
    }

    fun uploadProfileImage(uri: Uri) {
        viewModelScope.launch {
            try {
                val imageUrl = uploadImage(uri)
                createOrUpdateProfile(imageUrl = imageUrl.toString())
            } catch (e: Exception) {
                handleException(e, "Failed to upload profile image")
            }
        }
    }

    fun signUp(name: String, number: String, email: String, password: String) {
        if (name.isEmpty() or number.isEmpty() or email.isEmpty()) {
            handleException(customMessage = "Please Fill All Fields")
            return
        }
        inProgress.value = true
        viewModelScope.launch {
            try {
                val querySnapshot = db.collection(USER_NODE)
                    .whereEqualTo("number", number)
                    .get()
                    .await()
                if (querySnapshot.isEmpty) {
                    val result = auth.createUserWithEmailAndPassword(email, password).await()
                    signIn.value = result.user != null
                    if (signIn.value) {
                        createOrUpdateProfile(name, number)
                    }
                } else {
                    handleException(customMessage = "Number Already Exists")
                }
                inProgress.value = false
            } catch (e: Exception) {
                handleException(e, "Sign Up Failed")
                inProgress.value = false
            }
        }
    }


    fun createOrUpdateProfile(
        name: String? = null,
        number: String? = null,
        imageUrl: String? = null
    ) {
        val uid = auth.currentUser?.uid

        uid?.let { userId ->
            viewModelScope.launch {
                inProgress.value = true

                try {
                    val updatedUserData = UserData(
                        userId = userId,
                        name = name ?: userData.value?.name,
                        number = number ?: userData.value?.number,
                        imageUrl = imageUrl ?: userData.value?.imageUrl
                    )

                    val documentSnapshot = db.collection(USER_NODE).document(userId).get().await()

                    if (documentSnapshot.exists()) {
                        // Update user data if the document exists
                        val fieldsToUpdate = updatedUserData.toMap()
                            .filterValues { it != null } // Filter out null values
                        db.collection(USER_NODE).document(userId).update(fieldsToUpdate).await()
                    } else {
                        // Create new user data if the document doesn't exist
                        db.collection(USER_NODE).document(userId).set(updatedUserData).await()
                    }

                    // Data updated successfully
                    inProgress.value = false
                    getUserData(userId)
                } catch (e: Exception) {
                    // Failed to update/create user data
                    handleException(e, "Failed to update/create user data")
                }
            }
        }
    }

    private suspend fun getUserData(uid: String) {
        inProgress.value = true
        try {
            val documentSnapshot = db.collection(USER_NODE)
                .document(uid)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                val user = documentSnapshot.toObject<UserData>()
                userData.value = user
                populateChats() // Call populateChats() after userData is updated
                populateStatues()
            } else {
                userData.value = null // No user found
            }
        } catch (e: Exception) {
            handleException(e, "Cannot Retrieve User")
        } finally {
            inProgress.value = false
        }
    }



    fun handleException(exception: Exception? = null, customMessage: String = "") {
        Log.e("Compose Chat App", "Compose Chat App Exception : ", exception)
        exception?.printStackTrace()
        val errorMessage = exception?.localizedMessage ?: ""
        val message = if (customMessage.isNullOrEmpty()) errorMessage else customMessage
        eventMutableState.value = Event(message)
        inProgress.value = false


    }

    fun logout() {
        auth.signOut()
        signIn.value = false
        userData.value = null
        depopulateMessage()
        currentChatMessageListener = null
        eventMutableState.value = Event("Logged Out")
    }


    // Chat Screen

    var inProcessChats = mutableStateOf(false)
    var chats = mutableStateOf<List<ChatData>>(listOf())

    fun onAddChat(number: String) {
        if (number.isEmpty() or !number.isDigitsOnly()) {
            handleException(customMessage = "Number Must be contain digits only")
        } else {
            db.collection(CHATS).where(
                Filter.or(
                    Filter.and(
                        Filter.equalTo("user1.number", number),
                        Filter.equalTo("user2.number", userData.value?.number)
                    ),
                    Filter.and(
                        Filter.equalTo("user1.number", userData.value?.number),
                        Filter.equalTo("user2.number", number)
                    )

                )
            ).get().addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    db.collection(USER_NODE).whereEqualTo("number", number)
                        .get().addOnSuccessListener { userQuerySnapshot ->
                            if (userQuerySnapshot.isEmpty) {
                                handleException(customMessage = "Number not Found")
                            } else {
                                val chatPartner = userQuerySnapshot.toObjects<UserData>()[0]
                                val id = db.collection(CHATS).document().id
                                val chat = ChatData(
                                    chatId = id,
                                    ChatUser(
                                        userData.value?.userId,
                                        userData.value?.name,
                                        userData.value?.imageUrl,
                                        userData.value?.number

                                    ),
                                    ChatUser(
                                        chatPartner.userId,
                                        chatPartner.name,
                                        chatPartner.imageUrl,
                                        chatPartner.number
                                    )
                                )

                                db.collection(CHATS).document(id).set(chat).addOnSuccessListener {
                                    // Update the chats list immediately after adding a new chat
                                    val updatedChats = chats.value.toMutableList()
                                    updatedChats.add(chat)
                                    chats.value = updatedChats.toList()
                                }.addOnFailureListener {
                                    handleException(it)
                                }

                            }
                        }
                        .addOnFailureListener {
                            handleException(it)
                        }
                } else {
                    handleException(customMessage = "Chat already exists")
                }
            }
        }

    }


    // Populate Chats
    private suspend fun populateChats() {
        inProcessChats.value = true
        try {
            val querySnapshot = db.collection(CHATS).where(
                Filter.or(
                    Filter.equalTo("user1.userId", userData.value?.userId),
                    Filter.equalTo("user2.userId", userData.value?.userId)
                )
            ).get().await()

            val chatList = querySnapshot.documents.mapNotNull {
                it.toObject<ChatData>()
            }
            chats.value = chatList
        } catch (e: Exception) {
            handleException(e, "Failed to populate chats")
        } finally {
            inProcessChats.value = false
        }
    }

    //single chat send reply
    fun onSendReply(chatID: String, message: String) {
        val time = Calendar.getInstance().time.toString()
        val msg = Message(sendBy = userData.value?.userId, message = message, timeStamp = time)

        viewModelScope.launch {
            try {
                db.collection(CHATS)
                    .document(chatID)
                    .collection(MESSAGE)
                    .document()
                    .set(msg)
                    .await()
                // Optionally, you can perform any post-operation actions here
            } catch (e: Exception) {
                // Handle any exceptions
                handleException(e, "Failed to send message")
            }
        }
    }

    // populate messages between 2 user
    val chatMessages = mutableStateOf<List<Message>>(listOf())
    val inProgressSingleChatMessage = mutableStateOf(false)
    var currentChatMessageListener: ListenerRegistration? = null

    fun populateMessages(chatId: String) {
        inProgressSingleChatMessage.value = true
        currentChatMessageListener = db.collection(CHATS).document(chatId).collection(MESSAGE)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    handleException(error)
                }
                if (value != null) {
                    chatMessages.value = value.documents.mapNotNull {
                        it.toObject<Message>()
                    }.sortedBy { it.timeStamp }
                    inProgressSingleChatMessage.value = false
                }
            }
    }

    fun depopulateMessage() {
        chatMessages.value = listOf()
        currentChatMessageListener = null
    }


    // Status Screen
    val statusList = mutableStateOf<List<Status>>(listOf())
    val inProgressStatus = mutableStateOf(false)


    fun uploadStatus(uri: Uri?) {
        uri?.let { imageUri ->
            viewModelScope.launch {
                try {
                    inProgressStatus.value = true
                    val imageUrl = uploadImage(imageUri)
                    createStatus(imageUrl.toString())
                } catch (e: Exception) {
                    handleException(e, "Failed to upload status image")
                } finally {
                    inProgressStatus.value = false
                }
            }
        } ?: handleException(customMessage = "Image URI is null")
    }

    private suspend fun createStatus(imageUrl: String) {
        val userId = userData.value?.userId ?: return // Ensure userId is available

        val newStatus = Status(
            user = ChatUser(
                userId = userId,
                name = userData.value?.name,
                imageUrl = userData.value?.imageUrl,
                number = userData.value?.number
            ),
            imageUrl = imageUrl,
            timeStamp = System.currentTimeMillis()
        )

        db.collection(STATUS).add(newStatus).await()
    }

    private fun populateStatues() {
        val twentyFourHoursInMillis = 24 * 60 * 60 * 1000 // 24 hours in milliseconds
        val currentTime = System.currentTimeMillis()
        val twentyFourHoursAgo = currentTime - twentyFourHoursInMillis

        val currentConnection = mutableListOf<String>() // Define currentConnection here

        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId)
            )
        ).addSnapshotListener { chatSnapshot, error ->
            if (error != null) {
                handleException(error)
                return@addSnapshotListener
            }
            chatSnapshot?.let { chats ->
                currentConnection.clear() // Clear previous connections
                chats.forEach { chat ->
                    chat.getString("user1.userId")?.let { currentConnection.add(it) }
                    chat.getString("user2.userId")?.let { currentConnection.add(it) }
                }

                db.collection(STATUS)
                    .whereIn("user.userId", currentConnection) // Filter by user IDs
                    .whereGreaterThan("timeStamp", twentyFourHoursAgo) // Filter by timestamp
                    .addSnapshotListener { statusSnapshot, error ->
                        if (error != null) {
                            handleException(error)
                            return@addSnapshotListener
                        }
                        statusSnapshot?.let { statuses ->
                            val updatedStatusList = mutableListOf<Status>()
                            statuses.forEach { status ->
                                status.toObject<Status>().let { updatedStatusList.add(it) }
                            }
                            statusList.value = updatedStatusList
                        }
                    }
            }
        }
    }






//    private fun populateStatues() {
//     //   inProgressStatus.value = true
//
//        val twentyFourHoursInMillis = 24 * 60 * 60 * 1000 // 24 hours in milliseconds
//        val currentTime = System.currentTimeMillis()
//        val twentyFourHoursAgo = currentTime - twentyFourHoursInMillis
//
//        db.collection(CHATS).where(
//            Filter.or(
//                Filter.equalTo("user1.userId", userData.value?.userId),
//                Filter.equalTo("user2.userId", userData.value?.userId)
//            )
//        ).addSnapshotListener { value, error ->
//            if (error != null) {
//                handleException(error)
//                return@addSnapshotListener
//            }
//            if (value != null) {
//                val currentConnection = arrayListOf(userData.value?.userId)
//                val chats = value.toObjects<ChatData>()
//                chats.forEach { chat ->
//                    if (chat.user1.userId == userData.value?.userId) {
//                        currentConnection.add(chat.user2.userId)
//                    } else {
//                        currentConnection.add(chat.user1.userId)
//                    }
//                }
//
//                db.collection(STATUS)
//                    .whereIn("user.userId", currentConnection) // Filter by user IDs
//                    .whereGreaterThan("timeStamp", twentyFourHoursAgo) // Filter by timestamp
//                    .addSnapshotListener { value, error ->
//
//                        if (error != null) {
//                            handleException(error)
//                            return@addSnapshotListener
//                        }
//
//                        if (value != null) {
//                            statusList.value = value.toObjects()
//                        }
//                        // Always hide the progress bar after fetching statuses
//                      //  inProgressStatus.value = false
//                    }
//            }
//        }
//    }






}