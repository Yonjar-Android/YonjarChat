package com.example.yonjarchat.domain.models

data class ChatDomain(
    val lastMessage: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val arrayOfUsers: List<String> = emptyList()
)
