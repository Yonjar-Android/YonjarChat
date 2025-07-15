package com.example.yonjarchat.domain

data class MessageDomain(
    val messageId: String,
    val chatId: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val timestamp: Long,
    val isSeen: Boolean
)
