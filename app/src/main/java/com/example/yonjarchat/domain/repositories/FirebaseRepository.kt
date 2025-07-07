package com.example.yonjarchat.domain.repositories

import com.example.yonjarchat.domain.MessageDomain
import com.example.yonjarchat.domain.models.User

interface FirebaseRepository {
    suspend fun registerUser(
        email: String, password: String, username: String
    ):String

    fun loginUser(email: String, password: String, onResult: (String) -> Unit)

    fun forgotPassword(email: String): String

    fun signOut(): String

    suspend fun getUsers(): List<User>

    suspend fun getUserId(id: String): User?

    suspend fun sendMessage(senderId: String, receiverId: String, content: String)

    suspend fun getMessages(senderId: String, receiverId: String): List<MessageDomain>




}