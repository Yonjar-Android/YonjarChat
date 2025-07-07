package com.example.yonjarchat.domain.models

data class Message(
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
)