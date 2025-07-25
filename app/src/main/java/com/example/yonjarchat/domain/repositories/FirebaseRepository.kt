package com.example.yonjarchat.domain.repositories

import android.content.Context
import android.net.Uri
import com.example.yonjarchat.domain.models.MessageModel
import com.example.yonjarchat.domain.models.User
import com.example.yonjarchat.domain.models.UserChatModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration

interface FirebaseRepository {
    suspend fun registerUser(
        email: String, password: String, username: String
    ): String

    fun loginUser(email: String, password: String, onResult: (String) -> Unit)

    suspend fun forgotPassword(email: String): String

    fun signOut(): String

    suspend fun getUsers(): List<User>

    suspend fun getUserId(id: String): User?

    suspend fun updateUsername(id: String, username: String, onResult: (String) -> Unit)

    suspend fun sendMessage(senderId: String, receiverId: String, content: String)

    suspend fun getMessages(
        user1: String, user2: String,
        lastVisible: DocumentSnapshot?,
        onResult: (List<MessageModel>, DocumentSnapshot?) -> Unit
    ): ListenerRegistration

    suspend fun updatePicture(
        id: String, image: Uri,
        context: Context,
        onResult: (String) -> Unit
    )

    suspend fun sendPicture(
        senderId: String,
        receiverId: String,
        image: Uri,
        context: Context,
        onResult: (String) -> Unit
    )

    suspend fun getChats(context: Context,onResult: (List<UserChatModel>) -> Unit): ListenerRegistration


}