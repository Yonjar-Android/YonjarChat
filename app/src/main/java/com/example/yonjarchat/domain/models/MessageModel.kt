package com.example.yonjarchat.domain.models

data class MessageModel(
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
)