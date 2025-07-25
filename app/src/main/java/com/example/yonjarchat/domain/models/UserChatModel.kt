package com.example.yonjarchat.domain.models

data class UserChatModel(
    val uid: String = "",
    val username: String = "",
    val lastMessage: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val imageUrl: String = ""
)
