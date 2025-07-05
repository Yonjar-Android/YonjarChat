package com.example.yonjarchat.domain.repositories

interface FirebaseRepository {
    suspend fun registerUser(
        email: String, password: String, username: String
    ):String

    fun loginUser(email: String, password: String, onResult: (String) -> Unit)

    fun forgotPassword(email: String): String

    fun signOut(): String



}